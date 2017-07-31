package org.nishen.resourcepartners.harvesters;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import org.nishen.resourcepartners.dao.ElasticSearchDAO;
import org.nishen.resourcepartners.dao.ILRSScraperDAO;
import org.nishen.resourcepartners.entity.Address;
import org.nishen.resourcepartners.entity.ElasticSearchPartner;
import org.nishen.resourcepartners.entity.ElasticSearchPartnerAddress;
import org.nishen.resourcepartners.util.JaxbUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.Inject;

public class ILRSHarvesterImpl implements ILRSHarvester
{
	private static final Logger log = LoggerFactory.getLogger(ILRSHarvesterImpl.class);

	private static final SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ");

	private static final int THREADS = 6;

	private static final int BLOCKING_QUEUE_SIZE = 10;

	private ILRSScraperDAO ilrs;

	private ElasticSearchDAO elastic;

	@Inject
	public ILRSHarvesterImpl(ILRSScraperDAO ilrs, ElasticSearchDAO elastic)
	{
		this.ilrs = ilrs;
		this.elastic = elastic;

		log.debug("instantiated class: {}", this.getClass().getName());
	}

	@Override
	public void harvest()
	{
		List<String> nucs = new ArrayList<String>();
		nucs.add("AACOM");
		nucs.add("AAGD");
		nucs.add("NFML");
		nucs.add("NMQU");
		nucs.add("NPRK");
		nucs.add("QTDD");
		nucs.add("VMLT");
		nucs.add("XPFD");

		BlockingQueue<Runnable> queue = new ArrayBlockingQueue<>(BLOCKING_QUEUE_SIZE);
		ExecutorService executor = new ThreadPoolExecutor(THREADS, THREADS, 0L, TimeUnit.MILLISECONDS, queue,
		                                                  new ThreadPoolExecutor.CallerRunsPolicy());

		Map<String, Future<Map<String, Address>>> results = new HashMap<String, Future<Map<String, Address>>>();

		for (String nuc : nucs)
			results.put(nuc, executor.submit(new Harvester(nuc)));

		executor.shutdown();

		List<ElasticSearchPartner> partners = new ArrayList<ElasticSearchPartner>();
		for (String nuc : results.keySet())
		{
			try
			{
				List<ElasticSearchPartnerAddress> partnerAddresses = new ArrayList<ElasticSearchPartnerAddress>();

				Map<String, Address> addresses = results.get(nuc).get();
				for (String type : addresses.keySet())
				{
					ElasticSearchPartnerAddress a = new ElasticSearchPartnerAddress();
					a.setAddressType(type);
					a.setAddressDetail(addresses.get(type));

					partnerAddresses.add(a);
				}

				ElasticSearchPartner p = new ElasticSearchPartner();
				p.setNuc(nuc);
				p.setStatus("ACTIVE");
				p.setUpdated(sdf.format(new Date()));
				p.setDataSource("ILRS");
				p.setAddresses(partnerAddresses);

				partners.add(p);

				log.debug("{}", JaxbUtil.formatElasticSearchPartner(p));
			}
			catch (Exception e)
			{
				log.error("error: {}", e.getMessage());
			}
		}

		try
		{
			elastic.saveEntities(partners);
		}
		catch (Exception e)
		{
			log.error("unable to save partners: {}", e.getMessage(), e);
		}
	}

	private class Harvester implements Callable<Map<String, Address>>
	{
		private String nuc;

		public Harvester(String nuc)
		{
			this.nuc = nuc;
		}

		public Map<String, Address> call() throws Exception
		{
			Map<String, Address> addresses = null;
			try
			{
				String page = ilrs.getPage(nuc);
				addresses = ilrs.getAddressFromPage(page);
				for (String k : addresses.keySet())
				{
					if (log.isDebugEnabled())
					{
						String result = JaxbUtil.formatAddress(addresses.get(k));
						log.debug("result:\n{}", result);
					}
				}
			}
			catch (Exception e)
			{
				log.error("failed to obtain addresses: {}", e.getMessage(), e);
			}

			return addresses;
		}
	}
}
