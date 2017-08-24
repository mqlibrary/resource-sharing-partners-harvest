package org.nishen.resourcepartners.harvesters;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
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

import javax.ws.rs.NotFoundException;

import org.nishen.resourcepartners.SkipHarvestException;
import org.nishen.resourcepartners.dao.Config;
import org.nishen.resourcepartners.dao.ConfigFactory;
import org.nishen.resourcepartners.dao.ElasticSearchDAO;
import org.nishen.resourcepartners.dao.IlrsDAO;
import org.nishen.resourcepartners.entity.ElasticSearchChangeRecord;
import org.nishen.resourcepartners.entity.ElasticSearchPartner;
import org.nishen.resourcepartners.entity.ElasticSearchPartnerAddress;
import org.nishen.resourcepartners.model.Address;
import org.nishen.resourcepartners.util.JaxbUtil;
import org.nishen.resourcepartners.util.JaxbUtilModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.Inject;

/**
 * @author nishen
 *
 */
public class HarvesterIlrs implements Harvester
{
	private static final Logger log = LoggerFactory.getLogger(HarvesterIlrs.class);

	private static final String SOURCE_SYSTEM = "ILRS";

	private static final String NZ_NUC_PREFIX = "NLNZ";

	private static final SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ");

	private static final String DEFAULT_DAYS_BETWEEN = "7";

	private static final int THREADS = 3;

	private static final int BLOCKING_QUEUE_SIZE = 3;

	private Config config;

	private IlrsDAO ilrs;

	private ElasticSearchDAO elastic;

	@Inject
	public HarvesterIlrs(ConfigFactory configFactory, IlrsDAO ilrs, ElasticSearchDAO elastic)
	{
		this.config = configFactory.create(SOURCE_SYSTEM);
		log.debug("config[{}]: {}", SOURCE_SYSTEM, config.getAll());
		this.ilrs = ilrs;
		this.elastic = elastic;

		log.debug("instantiated class: {}", this.getClass().getName());
	}

	@Override
	public String getSource()
	{
		return SOURCE_SYSTEM;
	}

	@Override
	public Map<String, ElasticSearchPartner> harvest() throws IOException, SkipHarvestException
	{
		config.set("last_run_attempt", sdf.format(new Date()));
		if (!shouldHarvestRun())
			throw new SkipHarvestException();

		Map<String, ElasticSearchPartner> esPartners = elastic.getPartners();

		BlockingQueue<Runnable> queue = new ArrayBlockingQueue<>(BLOCKING_QUEUE_SIZE);
		ExecutorService executor = new ThreadPoolExecutor(THREADS, THREADS, 0L, TimeUnit.MILLISECONDS, queue,
		                                                  new ThreadPoolExecutor.CallerRunsPolicy());

		Map<String, Future<String>> results = new HashMap<String, Future<String>>();
		for (String nuc : esPartners.keySet())
			if (!nuc.startsWith(NZ_NUC_PREFIX))
				results.put(nuc, executor.submit(new Harvester(nuc)));

		executor.shutdown();

		Map<String, ElasticSearchPartner> ilrsPartners = new TreeMap<String, ElasticSearchPartner>();

		for (String nuc : results.keySet())
		{
			try
			{
				String page = results.get(nuc).get();

				if (page == null)
				{
					log.warn("could not obtain ilrs page for: {}", nuc);
					continue;
				}

				Map<String, Address> addresses = ilrs.getAddressesFromPage(page);
				if (log.isDebugEnabled())
					for (String k : addresses.keySet())
						log.debug("result:\n{}", JaxbUtilModel.format(addresses.get(k)));

				String emailIll = ilrs.getEmailFromPage(page).orElse(null);
				String phoneIll = ilrs.getPhoneIllFromPage(page).orElse(null);
				String phoneFax = ilrs.getPhoneFaxFromPage(page).orElse(null);

				ElasticSearchPartner ep = esPartners.get(nuc);
				ElasticSearchPartner ilrsPartner = new ElasticSearchPartner();
				ilrsPartner.setNuc(ep.getNuc());
				ilrsPartner.setEnabled(ep.isEnabled());
				ilrsPartner.setName(ep.getName());
				ilrsPartner.setStatus(ep.getStatus());
				ilrsPartner.setEmailMain(ep.getEmailMain());
				ilrsPartner.setEmailIll(emailIll);
				ilrsPartner.setPhoneMain(null);
				ilrsPartner.setPhoneIll(phoneIll);
				ilrsPartner.setPhoneFax(phoneFax);
				ilrsPartner.getSuspensions().addAll(ep.getSuspensions());
				for (String type : addresses.keySet())
				{
					ElasticSearchPartnerAddress address = new ElasticSearchPartnerAddress();
					address.setAddressStatus("active");
					address.setAddressType(type);
					address.setAddressDetail(addresses.get(type));

					ilrsPartner.getAddresses().add(address);
				}
				ilrsPartner.setUpdated(ep.getUpdated());

				ilrsPartners.put(ilrsPartner.getNuc(), ilrsPartner);
			}
			catch (Exception e)
			{
				log.error("error: {}", e.getMessage(), e);
			}
		}

		config.set("last_run", sdf.format(new Date()));

		return ilrsPartners;
	}

	@Override
	public Map<String, ElasticSearchPartner> update(Map<String, ElasticSearchPartner> partners,
	                                                Map<String, ElasticSearchPartner> latest,
	                                                List<ElasticSearchChangeRecord> changes)
	{
		Map<String, ElasticSearchPartner> updated = new HashMap<String, ElasticSearchPartner>();

		for (String nuc : latest.keySet())
		{
			ElasticSearchPartner l = latest.get(nuc);
			ElasticSearchPartner p = partners.get(nuc);

			boolean requiresUpdate = false;

			if (!compareStrings(p.getEmailMain(), l.getEmailMain()))
			{
				changes.add(new ElasticSearchChangeRecord(SOURCE_SYSTEM, nuc, "email_main", p.getEmailMain(),
				                                          l.getEmailMain()));
				p.setEmailMain(l.getEmailMain());
				requiresUpdate = true;
			}

			if (!compareStrings(p.getEmailIll(), l.getEmailIll()))
			{
				changes.add(new ElasticSearchChangeRecord(SOURCE_SYSTEM, nuc, "email_ill", p.getEmailIll(),
				                                          l.getEmailIll()));
				p.setEmailIll(l.getEmailIll());
				requiresUpdate = true;
			}

			if (!compareStrings(p.getPhoneMain(), l.getPhoneMain()))
			{
				changes.add(new ElasticSearchChangeRecord(SOURCE_SYSTEM, nuc, "phone_main", p.getPhoneMain(),
				                                          l.getPhoneMain()));
				p.setPhoneMain(l.getPhoneMain());
				requiresUpdate = true;
			}

			if (!compareStrings(p.getPhoneIll(), l.getPhoneIll()))
			{
				changes.add(new ElasticSearchChangeRecord(SOURCE_SYSTEM, nuc, "phone_ill", p.getPhoneIll(),
				                                          l.getPhoneIll()));
				p.setPhoneIll(l.getPhoneIll());
				requiresUpdate = true;
			}

			if (!compareStrings(p.getPhoneFax(), l.getPhoneFax()))
			{
				changes.add(new ElasticSearchChangeRecord(SOURCE_SYSTEM, nuc, "phone_fax", p.getPhoneFax(),
				                                          l.getPhoneFax()));
				p.setPhoneFax(l.getPhoneFax());
				requiresUpdate = true;
			}

			Map<String, ElasticSearchPartnerAddress> pAddresses = new HashMap<String, ElasticSearchPartnerAddress>();
			for (ElasticSearchPartnerAddress ea : p.getAddresses())
				pAddresses.put(ea.getAddressType(), ea);

			for (ElasticSearchPartnerAddress la : l.getAddresses())
			{
				ElasticSearchPartnerAddress pa = pAddresses.remove(la.getAddressType());
				if (pa == null)
				{
					changes.add(new ElasticSearchChangeRecord(SOURCE_SYSTEM, nuc, "address:" + la.getAddressType(),
					                                          null, JaxbUtil.format(la)));
					p.getAddresses().add(la);
					requiresUpdate = true;
				}
				else if (!la.equals(pa))
				{
					changes.add(new ElasticSearchChangeRecord(SOURCE_SYSTEM, nuc, "address:" + la.getAddressType(),
					                                          JaxbUtil.format(pa), JaxbUtil.format(la)));
					p.getAddresses().remove(pa);
					p.getAddresses().add(la);
					requiresUpdate = true;
				}
			}

			for (String type : pAddresses.keySet())
			{
				changes.add(new ElasticSearchChangeRecord(SOURCE_SYSTEM, nuc, "address:" + type + ":status", "active",
				                                          "inactive"));
				pAddresses.get(type).setAddressStatus("inactive");
				requiresUpdate = true;
			}

			if (requiresUpdate)
			{
				p.setUpdated(sdf.format(new Date()));
				updated.put(nuc, p);
				log.debug("to be updated[{}]: {}", nuc, JaxbUtil.format(p));
			}
		}

		return updated;
	}

	private boolean shouldHarvestRun()
	{
		int daysBetween = Integer.parseInt(config.get("days_between_update").orElse(DEFAULT_DAYS_BETWEEN));

		Calendar lastRun = Calendar.getInstance();
		lastRun.set(2017, Calendar.JANUARY, 1);

		if (config.get("last_run").isPresent())
		{
			try
			{
				lastRun.setTime(sdf.parse(config.get("last_run").get()));
			}
			catch (ParseException pe)
			{
				lastRun.set(2017, Calendar.JANUARY, 1);
			}
		}

		Calendar checkRun = Calendar.getInstance();
		checkRun.add(Calendar.DAY_OF_MONTH, -daysBetween);

		return checkRun.after(lastRun);
	}

	private class Harvester implements Callable<String>
	{
		private String nuc;

		public Harvester(String nuc)
		{
			this.nuc = nuc;
		}

		public String call()
		{
			String page = null;

			try
			{
				page = ilrs.getPage(nuc);
			}
			catch (NotFoundException nfe)
			{
				log.warn("failed to obtain ilrs page [{}]: {}", nuc, nfe.getMessage());
			}
			catch (Exception e)
			{
				log.error("failed to obtain ilrs page: {}", e.getMessage());
			}

			return page;
		}
	}
}
