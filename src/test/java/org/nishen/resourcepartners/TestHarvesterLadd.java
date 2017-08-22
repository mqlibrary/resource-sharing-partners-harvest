package org.nishen.resourcepartners;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;
import java.util.Map;

import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.nishen.resourcepartners.dao.ElasticSearchDAO;
import org.nishen.resourcepartners.entity.ElasticSearchPartner;
import org.nishen.resourcepartners.entity.ElasticSearchPartnerAddress;
import org.nishen.resourcepartners.entity.ElasticSearchSuspension;
import org.nishen.resourcepartners.harvesters.Harvester;
import org.nishen.resourcepartners.harvesters.HarvesterLadd;
import org.nishen.resourcepartners.model.Address;
import org.nishen.resourcepartners.model.Address.Country;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Module;

import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;

import static org.hamcrest.Matchers.contains;

public class TestHarvesterLadd
{
	private static final Logger log = LoggerFactory.getLogger(TestHarvesterLadd.class);

	private static Injector injector = null;

	private static Harvester harvester = null;

	private static ElasticSearchDAO elastic = null;

	private static ElasticSearchPartner partner = null;

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

		harvester = injector.getInstance(HarvesterLadd.class);

		elastic = injector.getInstance(ElasticSearchDAO.class);

		Calendar c = Calendar.getInstance();
		c.clear();
		c.set(Calendar.YEAR, 2017);
		c.set(Calendar.MONTH, Calendar.JULY);
		c.set(Calendar.DAY_OF_MONTH, 21);

		Country country = new Country();
		country.setDesc("Australia");
		country.setValue("AUS");

		Address a = new Address();
		a.setLine1("101 Test Street");
		a.setLine2("Test Area");
		a.setCity("Testville");
		a.setPostalCode("5555");
		a.setCountry(country);

		ElasticSearchPartnerAddress ea = new ElasticSearchPartnerAddress();
		ea.setAddressType("main");
		ea.setAddressDetail(a);

		partner = new ElasticSearchPartner();
		partner.setNuc("TEST");
		partner.setName("Test Organisation");

		ElasticSearchSuspension suspension = new ElasticSearchSuspension();
		suspension.setSuspensionStatus(ElasticSearchSuspension.NOT_SUSPENDED);
		suspension.setSuspensionStart(null);
		suspension.setSuspensionEnd(null);

		partner.getSuspensions().add(suspension);

		try
		{
			elastic.addEntity(partner);
		}
		catch (Exception e)
		{
			log.error("unable to add entity: {}", e.getMessage(), e);
		}
	}

	@Before
	public void setup() throws Exception
	{
		elastic.addEntity(partner);
	}

	@After
	public void teardown() throws Exception
	{
		elastic.delEntity(partner);
	}

	@Test
	public void testHarvesterLadd()
	{
		log.debug("running test: {}", Arrays.asList(new Throwable().getStackTrace()).get(0).getMethodName());
		try
		{
			Map<String, ElasticSearchPartner> changed = harvester.harvest();
			for (String nuc : changed.keySet())
				log.debug("{}: {}", nuc, changed.get(nuc));

			assertThat(changed.values(), contains(partner));
		}
		catch (Exception e)
		{
			fail(e.getMessage());
			log.error("{}", e.getMessage(), e);
		}
	}
}
