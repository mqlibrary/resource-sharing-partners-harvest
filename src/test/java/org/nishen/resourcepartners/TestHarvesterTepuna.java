package org.nishen.resourcepartners;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.junit.BeforeClass;
import org.junit.Test;
import org.nishen.resourcepartners.dao.TepunaDAOImpl;
import org.nishen.resourcepartners.entity.ResourcePartner;
import org.nishen.resourcepartners.entity.ResourcePartnerAddress;
import org.nishen.resourcepartners.harvesters.Harvester;
import org.nishen.resourcepartners.harvesters.HarvesterTepuna;
import org.nishen.resourcepartners.model.Address;
import org.nishen.resourcepartners.model.Address.Country;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Module;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

public class TestHarvesterTepuna
{
	private static final Logger log = LoggerFactory.getLogger(TestHarvesterTepuna.class);

	private static Injector injector = null;

	private static Harvester harvester = null;

	@BeforeClass
	public static void setupClass()
	{
		// list for injector modules
		List<Module> modules = new ArrayList<Module>();

		// module (main configuration)
		modules.add(new ResourcePartnerModule());

		// create the injector
		log.debug("creating injector");
		injector = Guice.createInjector(modules);

		harvester = injector.getInstance(HarvesterTepuna.class);
	}

	@Test
	public void testHarvesterTepuna()
	{
		log.debug("running test: {}", Arrays.asList(new Throwable().getStackTrace()).get(0).getMethodName());

		try
		{
			Map<String, ResourcePartner> changed = harvester.harvest();
			log.debug("changed count: {}", changed.size());
		}
		catch (Exception e)
		{
			fail(e.getMessage());
			log.error("{}", e.getMessage(), e);
		}
	}

	@Test
	public void testHarvesterTepunaPartners()
	{
		log.debug("running test: {}", Arrays.asList(new Throwable().getStackTrace()).get(0).getMethodName());

		try
		{
			TepunaDAOImpl dao = injector.getInstance(TepunaDAOImpl.class);
			Map<String, ResourcePartner> partners = dao.getData();
			log.debug("partner count: {}", partners.size());
			partners.values().stream().limit(3).forEach(System.out::println);
		}
		catch (Exception e)
		{
			fail(e.getMessage());
			log.error("{}", e.getMessage(), e);
		}
	}

	@Test
	public void testHarvesterTepunaAddress1()
	{
		log.debug("running test: {}", Arrays.asList(new Throwable().getStackTrace()).get(0).getMethodName());

		try
		{
			String addressText =
			        "41 Islington Street\n" + "Turnbull Thompson Park\n" + "Invercargill 9810\n" + "New Zealand";
			TepunaDAOImpl dao = injector.getInstance(TepunaDAOImpl.class);
			ResourcePartnerAddress actual = dao.getAddress(addressText);

			Country country = new Country();
			country.setDesc("New Zealand");
			country.setValue("NZL");

			Address expected = new Address();
			expected.setLine1("41 Islington Street");
			expected.setLine2("Turnbull Thompson Park");
			expected.setCity("Invercargill");
			expected.setPostalCode("9810");
			expected.setCountry(country);

			log.debug("address: {}", actual);

			assertEquals(expected, actual);
		}
		catch (Exception e)
		{
			fail(e.getMessage());
			log.error("{}", e.getMessage(), e);
		}
	}

	@Test
	public void testHarvesterTepunaAddress2()
	{
		log.debug("running test: {}", Arrays.asList(new Throwable().getStackTrace()).get(0).getMethodName());

		try
		{
			String addressText = "59-63 Huia Street\n" + "Taumarunui 3920";
			TepunaDAOImpl dao = injector.getInstance(TepunaDAOImpl.class);
			ResourcePartnerAddress actual = dao.getAddress(addressText);

			Address expected = new Address();
			expected.setLine1("59-63 Huia Street");
			expected.setCity("Taumarunui");
			expected.setPostalCode("3920");

			log.debug("address: {}", actual);

			assertEquals(expected, actual);
		}
		catch (Exception e)
		{
			fail(e.getMessage());
			log.error("{}", e.getMessage(), e);
		}
	}

	@Test
	public void testHarvesterTepunaAddress3()
	{
		log.debug("running test: {}", Arrays.asList(new Throwable().getStackTrace()).get(0).getMethodName());

		try
		{
			String addressText = "8A Miri Rd Auckland\n" + "Rothesay Bay\n" + "Auckland 630";
			TepunaDAOImpl dao = injector.getInstance(TepunaDAOImpl.class);
			ResourcePartnerAddress actual = dao.getAddress(addressText);

			Address expected = new Address();
			expected.setLine1("8A Miri Rd Auckland");
			expected.setLine2("Rothesay Bay");
			expected.setCity("Auckland");
			expected.setPostalCode("630");

			log.debug("address: {}", actual);

			assertEquals(expected, actual);
		}
		catch (Exception e)
		{
			fail(e.getMessage());
			log.error("{}", e.getMessage(), e);
		}
	}

	@Test
	public void testHarvesterTepunaAddress4()
	{
		log.debug("running test: {}", Arrays.asList(new Throwable().getStackTrace()).get(0).getMethodName());

		try
		{
			String addressText = "Le-Papaigalagala Campus\n" + "Apia\n" + "Samoa";
			TepunaDAOImpl dao = injector.getInstance(TepunaDAOImpl.class);
			ResourcePartnerAddress actual = dao.getAddress(addressText);

			Country country = new Country();
			country.setDesc("Samoa");
			country.setValue("WSM");

			Address expected = new Address();
			expected.setLine1("Le-Papaigalagala Campus");
			expected.setLine2("Apia");
			expected.setCountry(country);

			log.debug("address: {}", actual);

			assertEquals(expected, actual);
		}
		catch (Exception e)
		{
			fail(e.getMessage());
			log.error("{}", e.getMessage(), e);
		}
	}

}
