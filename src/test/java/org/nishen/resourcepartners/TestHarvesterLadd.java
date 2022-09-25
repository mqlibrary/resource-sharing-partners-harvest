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
import org.nishen.resourcepartners.dao.DatastoreDAO;
import org.nishen.resourcepartners.entity.ResourcePartner;
import org.nishen.resourcepartners.entity.ResourcePartnerAddress;
import org.nishen.resourcepartners.entity.ResourcePartnerSuspension;
import org.nishen.resourcepartners.harvesters.Harvester;
import org.nishen.resourcepartners.harvesters.HarvesterLadd;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Module;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.greaterThanOrEqualTo;

import static org.junit.Assert.fail;

public class TestHarvesterLadd
{
	private static final Logger log = LoggerFactory.getLogger(TestHarvesterLadd.class);

	private static Injector injector = null;

	private static Harvester harvester = null;

	private static DatastoreDAO elastic = null;

	private static ResourcePartner partner = null;

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

		elastic = injector.getInstance(DatastoreDAO.class);

		Calendar c = Calendar.getInstance();
		c.clear();
		c.set(Calendar.YEAR, 2017);
		c.set(Calendar.MONTH, Calendar.JULY);
		c.set(Calendar.DAY_OF_MONTH, 21);

		ResourcePartnerAddress ea = new ResourcePartnerAddress();
		ea.setAddressType("main");
		ea.setLine1("101 Test Street");
		ea.setLine2("Test Area");
		ea.setCity("Testville");
		ea.setPostalCode("5555");
		ea.setCountry("AUS");

		partner = new ResourcePartner();
		partner.setNuc("TEST");
		partner.setName("Test Organisation");

		ResourcePartnerSuspension suspension = new ResourcePartnerSuspension();
		suspension.setSuspensionStatus(ResourcePartnerSuspension.NOT_SUSPENDED);
		suspension.setSuspensionStart(null);
		suspension.setSuspensionEnd(null);

		partner.getSuspensions().add(suspension);
	}

	@Before
	public void setup() throws Exception
	{
		// elastic.addEntity(partner);
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
			Map<String, ResourcePartner> laddPartners = harvester.harvest();

			assertThat(laddPartners.size(), greaterThanOrEqualTo(500));
		}
		catch (Exception e)
		{
			fail(e.getMessage());
			log.error("{}", e.getMessage(), e);
		}
	}
}
