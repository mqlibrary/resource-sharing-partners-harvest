package org.nishen.resourcepartners.dao;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.Callable;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.xml.bind.JAXBElement;

import org.nishen.resourcepartners.ResourcePartnerSynchroniserException;
import org.nishen.resourcepartners.model.ObjectFactory;
import org.nishen.resourcepartners.model.Partner;
import org.nishen.resourcepartners.model.Partners;
import org.nishen.resourcepartners.util.JaxbUtilModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.Inject;
import com.google.inject.Provider;
import com.google.inject.name.Named;

public class AlmaDAOImpl implements AlmaDAO
{
	private static final Logger log = LoggerFactory.getLogger(AlmaDAOImpl.class);

	private static final int DEFAULT_THREADS = 12;

	private static final int LIMIT = 100;

	private static final ObjectFactory of = new ObjectFactory();

	private Config config;

	private Client client;

	private String apiurl;

	private String apikey;

	@Inject
	public AlmaDAOImpl(ConfigFactory configFactory, @Named("ws.alma") Provider<Client> clientProvider)
	{
		this.config = configFactory.fetch("ALMA");

		this.apiurl = config.get("apiurl").orElse("https://api-ap.hosted.exlibrisgroup.com/almaws/v1");

		this.apikey = config.get("apikey").orElseThrow(() -> new RuntimeException("ALMA apikey not found"));

		this.client = clientProvider.get();

		log.debug("instantiated class: {}", this.getClass().getName());
	}

	@Override
	public ConcurrentMap<String, Partner> getPartners() throws ResourcePartnerSynchroniserException
	{
		ConcurrentMap<String, Partner> partnerMap = fetchPartners();
		return partnerMap;
	}

	@Override
	public Optional<Partner> getPartner(String nuc) throws ResourcePartnerSynchroniserException
	{
		return Optional.ofNullable(fetchPartner(nuc));
	}

	public void savePartner(Partner p) throws ResourcePartnerSynchroniserException
	{
		Map<String, Partner> partners = new HashMap<String, Partner>();
		partners.put(p.getPartnerDetails().getCode(), p);

		savePartners(partners);
	}

	public void savePartners(Map<String, Partner> partners) throws ResourcePartnerSynchroniserException
	{
		WebTarget t = client.target(apiurl).path("partners");

		List<Future<Partner>> partnerUpdates = new ArrayList<Future<Partner>>();

		try
		{
			ExecutorService executor = Executors.newFixedThreadPool(DEFAULT_THREADS);

			ConcurrentMap<String, Partner> almaPartners = getPartners();

			for (String nuc : partners.keySet())
			{
				boolean update = almaPartners.keySet().contains(nuc);
				partnerUpdates.add(executor.submit(new SavePartner(t, partners.get(nuc), update)));
			}

			executor.shutdown();

			for (Future<Partner> future : partnerUpdates)
			{
				Partner partner = future.get();
				log.debug("partner: {}", JaxbUtilModel.formatPretty(partner));
			}
		}
		catch (Exception e)
		{
			throw new ResourcePartnerSynchroniserException(e);
		}
	}

	private ConcurrentMap<String, Partner> fetchPartners() throws ResourcePartnerSynchroniserException
	{
		ConcurrentMap<String, Partner> partnerMap = new ConcurrentHashMap<String, Partner>();

		long offset = 0;
		long total = -1;
		long count = 0;

		WebTarget t = client.target(apiurl).path("partners").queryParam("limit", LIMIT);

		try
		{
			ExecutorService executor = Executors.newFixedThreadPool(DEFAULT_THREADS);

			log.debug("getAlmaPartners [count/total/offset]: {}/{}/{}", count, total, offset);
			Future<Partners> initial = executor.submit(new FetchPartners(t, offset));
			Partners partners = initial.get();
			total = partners.getTotalRecordCount();
			offset += LIMIT;
			count += partners.getPartner().size();

			for (Partner p : partners.getPartner())
				partnerMap.put(p.getPartnerDetails().getCode(), p);

			List<Future<Partners>> partial = new ArrayList<Future<Partners>>();
			while (count < total)
			{
				log.debug("getAlmaPartners [count/total/offset]: {}/{}/{}", count, total, offset);
				partial.add(executor.submit(new FetchPartners(t, offset)));

				offset += LIMIT;
				count += partners.getPartner().size();
			}

			for (Future<Partners> future : partial)
			{
				partners = future.get();
				for (Partner p : partners.getPartner())
					partnerMap.put(p.getPartnerDetails().getCode(), p);
			}

			executor.shutdown();
		}
		catch (ExecutionException ee)
		{
			log.error("execution failed: {}", ee.getMessage(), ee);
			throw new ResourcePartnerSynchroniserException(ee.getMessage());
		}
		catch (InterruptedException ie)
		{
			log.error("execution interrupted: {}", ie.getMessage(), ie);
		}

		return partnerMap;
	}

	private Partner fetchPartner(String nuc)
	{
		WebTarget t = client.target(apiurl).path("partner").path(nuc);
		Partner partner = t.request(MediaType.APPLICATION_XML)
		                   .accept(MediaType.APPLICATION_XML)
		                   .header("Authorization", "apikey " + apikey)
		                   .get(Partner.class);
		return partner;
	}

	private class FetchPartners implements Callable<Partners>
	{
		private WebTarget target;

		private long offset;

		public FetchPartners(WebTarget target, long offset)
		{
			this.target = target;
			this.offset = offset;
		}

		@Override
		public Partners call() throws Exception
		{
			String m = MediaType.APPLICATION_XML;

			WebTarget t = target.queryParam("limit", LIMIT).queryParam("offset", offset);
			Partners partners = t.request(m).accept(m).header("Authorization", "apikey " + apikey).get(Partners.class);

			log.debug("fetchResourcePartners [offset]: {}", offset);

			return partners;
		}
	}

	private class SavePartner implements Callable<Partner>
	{

		private WebTarget target;

		private Partner partner;

		private boolean update;

		public SavePartner(WebTarget target, Partner partner, boolean update)
		{
			this.target = target;
			this.partner = partner;
			this.update = update;
		}

		@Override
		public Partner call() throws Exception
		{
			String m = MediaType.APPLICATION_XML;

			String action = update ? "updating" : "creating";
			log.debug("{} partner[{}]: {}", action, partner.getPartnerDetails().getCode(),
			          partner.getPartnerDetails().getName());

			Partner result = null;

			JAXBElement<Partner> p = of.createPartner(partner);

			String code = partner.getPartnerDetails().getCode();
			try
			{
				if (update)
				{
					result = target.path(code)
					               .request(m)
					               .header("Authorization", "apikey " + apikey)
					               .put(Entity.entity(p, m), Partner.class);
				}
				else
				{
					result = target.request(m)
					               .header("Authorization", "apikey " + apikey)
					               .post(Entity.entity(p, m), Partner.class);
				}
			}
			catch (Exception e)
			{
				log.error("error adding partner:\n{}", result, e);
			}

			log.debug("result:\n{}", result);

			return result;
		}
	}
}
