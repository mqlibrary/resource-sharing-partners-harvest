package org.nishen.resourcepartners.harvesters;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import org.nishen.resourcepartners.dao.ElasticSearchDAO;
import org.nishen.resourcepartners.dao.IlrsDAO;
import org.nishen.resourcepartners.entity.ElasticSearchChangeRecord;
import org.nishen.resourcepartners.entity.ElasticSearchPartner;
import org.nishen.resourcepartners.entity.ElasticSearchPartnerAddress;
import org.nishen.resourcepartners.model.Address;
import org.nishen.resourcepartners.util.JaxbUtilModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.Inject;

public class HarvesterIlrs implements Harvester
{
	private static final Logger log = LoggerFactory.getLogger(HarvesterIlrs.class);

	private static final int THREADS = 6;

	private static final int BLOCKING_QUEUE_SIZE = 10;

	private IlrsDAO ilrs;

	private ElasticSearchDAO elastic;

	@Inject
	public HarvesterIlrs(IlrsDAO ilrs, ElasticSearchDAO elastic)
	{
		this.ilrs = ilrs;
		this.elastic = elastic;

		log.debug("instantiated class: {}", this.getClass().getName());
	}

	@Override
	public Map<String, ElasticSearchPartner> harvest() throws IOException
	{
		Map<String, ElasticSearchPartner> esPartners = elastic.getPartners();

		BlockingQueue<Runnable> queue = new ArrayBlockingQueue<>(BLOCKING_QUEUE_SIZE);
		ExecutorService executor = new ThreadPoolExecutor(THREADS, THREADS, 0L, TimeUnit.MILLISECONDS, queue,
		                                                  new ThreadPoolExecutor.CallerRunsPolicy());

		Map<String, Future<Map<String, Address>>> results = new HashMap<String, Future<Map<String, Address>>>();
		for (String nuc : esPartners.keySet())
			results.put(nuc, executor.submit(new Harvester(nuc)));

		executor.shutdown();

		Map<String, ElasticSearchPartner> ilrsPartners = new TreeMap<String, ElasticSearchPartner>();

		for (String nuc : results.keySet())
		{
			try
			{
				Map<String, ElasticSearchPartnerAddress> ilrsAddresses =
				        new HashMap<String, ElasticSearchPartnerAddress>();

				Map<String, Address> addresses = results.get(nuc).get();
				for (String type : addresses.keySet())
				{
					ElasticSearchPartnerAddress address = new ElasticSearchPartnerAddress();
					address.setAddressStatus("active");
					address.setAddressType(type);
					address.setAddressDetail(addresses.get(type));

					ilrsAddresses.put(type, address);
				}

				ElasticSearchPartner ep = esPartners.get(nuc);
				ElasticSearchPartner ilrsPartner = new ElasticSearchPartner();
				ilrsPartner.setNuc(ep.getNuc());
				ilrsPartner.setEnabled(ep.isEnabled());
				ilrsPartner.setName(ep.getName());
				ilrsPartner.setStatus(ep.getStatus());
				ilrsPartner.setEmailMain(ep.getEmailMain());
				ilrsPartner.setEmailIll(ep.getEmailIll());
				ilrsPartner.setPhoneMain(ep.getPhoneMain());
				ilrsPartner.setPhoneIll(ep.getPhoneIll());
				ilrsPartner.setSuspensionStart(ep.getSuspensionStart());
				ilrsPartner.setSuspensionEnd(ep.getSuspensionEnd());
				ilrsPartner.setUpdated(ep.getUpdated());
				ilrsPartner.setAddresses(new ArrayList<ElasticSearchPartnerAddress>(ilrsAddresses.values()));

				ilrsPartners.put(ilrsPartner.getNuc(), ep);
			}
			catch (Exception e)
			{
				log.error("error: {}", e.getMessage(), e);
			}
		}

		return ilrsPartners;
	}

	@Override
	public Map<String, ElasticSearchPartner> update(Map<String, ElasticSearchPartner> partners,
	                                                Map<String, ElasticSearchPartner> latest,
	                                                List<ElasticSearchChangeRecord> changes)
	{
		return null;
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
				addresses = ilrs.getAddressesFromPage(page);
				for (String k : addresses.keySet())
				{
					if (log.isDebugEnabled())
					{
						String result = JaxbUtilModel.format(addresses.get(k));
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
