package org.nishen.resourcepartners;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;
import java.util.Map;

import org.junit.BeforeClass;
import org.junit.Test;
import org.nishen.resourcepartners.dao.DatastoreDAO;
import org.nishen.resourcepartners.entity.ResourcePartner;
import org.nishen.resourcepartners.entity.ResourcePartnerAddress;
import org.nishen.resourcepartners.entity.ResourcePartnerSuspension;
import org.nishen.resourcepartners.util.DataUtil;
import org.nishen.resourcepartners.util.JaxbUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Module;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.greaterThan;

import static org.junit.Assert.fail;

public class TestElasticSearchDAO
{
	private static final Logger log = LoggerFactory.getLogger(TestElasticSearchDAO.class);

	private static Injector injector = null;

	private static DatastoreDAO elastic = null;

	@BeforeClass
	public static void setup()
	{
		// list for injector modules
		List<Module> modules = new ArrayList<Module>();

		// module (main configuration)
		modules.add(new ResourcePartnerModule());

		// create the injector
		log.debug("creating injector");
		injector = Guice.createInjector(modules);

		elastic = injector.getInstance(DatastoreDAO.class);
	}

	@Test
	public void testGetPartner()
	{
		log.debug("running test: {}", Arrays.asList(new Throwable().getStackTrace()).get(0).getMethodName());
		try
		{
			// add data
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

			ResourcePartner expected = new ResourcePartner();
			expected.setNuc("TEST");
			expected.setName("Test Organisation");

			ResourcePartnerSuspension suspension = new ResourcePartnerSuspension();
			suspension.setSuspensionStatus(ResourcePartnerSuspension.NOT_SUSPENDED);
			suspension.setSuspensionStart(null);
			suspension.setSuspensionEnd(null);

			expected.getSuspensions().add(suspension);

			elastic.addEntity(expected);

			ResourcePartner actual = elastic.getPartner("TEST").get();
			log.debug("{}", actual.toString());
			assertThat(actual, equalTo(expected));

			elastic.delEntity(expected);
		}
		catch (Exception e)
		{
			fail(e.getMessage());
		}
	}

	@Test
	public void testGetPartnerDoesNotExist()
	{
		log.debug("running test: {}", Arrays.asList(new Throwable().getStackTrace()).get(0).getMethodName());

		try
		{
			ResourcePartner partner = elastic.getPartner("TEST").orElse(null);
			assertThat(partner, equalTo(null));
		}
		catch (IOException ioe)
		{
			fail(ioe.getMessage());
		}
	}

	@Test
	public void testGetPartners()
	{
		log.debug("running test: {}", Arrays.asList(new Throwable().getStackTrace()).get(0).getMethodName());
		try
		{
			Map<String, ResourcePartner> p = elastic.getPartners();

			for (String nuc : p.keySet())
				log.debug("{}:\n{}", nuc, JaxbUtil.format(p.get(nuc)));

			assertThat(p.size(), greaterThan(0));
		}
		catch (Exception e)
		{
			fail(e.getMessage());
		}
	}

	@Test
	public void testGetUnmarshalledElasticPartner()
	{
		log.debug("running test: {}", Arrays.asList(new Throwable().getStackTrace()).get(0).getMethodName());
		try
		{
			String datafile = "target/test-classes/data/elastic-partner.json";
			byte[] data = DataUtil.loadFile(datafile);

			String json = new String(data, "UTF-8");
			log.debug("json:\n{}", json);
			ResourcePartner e = JaxbUtil.get(json, ResourcePartner.class);
			log.debug("unmarshalled:\n{}", e);
		}
		catch (Exception e)
		{
			fail(e.getMessage());
		}
	}
}
