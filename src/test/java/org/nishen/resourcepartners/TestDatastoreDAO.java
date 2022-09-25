package org.nishen.resourcepartners;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.junit.BeforeClass;
import org.junit.Test;
import org.nishen.resourcepartners.dao.DatastoreDAO;
import org.nishen.resourcepartners.entity.ResourcePartner;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Module;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.greaterThan;
import static org.hamcrest.Matchers.notNullValue;

import static org.junit.Assert.fail;

public class TestDatastoreDAO
{
	private static final Logger log = LoggerFactory.getLogger(TestDatastoreDAO.class);

	private static DatastoreDAO datastoreDAO;

	@BeforeClass
	public static void setup()
	{
		// list for injector modules
		List<Module> modules = new ArrayList<Module>();

		// module (main configuration)
		modules.add(new ResourcePartnerModule());

		// create the injector
		log.debug("creating injector");
		Injector injector = Guice.createInjector(modules);

		datastoreDAO = injector.getInstance(DatastoreDAO.class);
	}

	@Test
	public void testFetchPartner()
	{
		log.debug("running test: {}", Arrays.asList(new Throwable().getStackTrace()).get(0).getMethodName());

		try
		{
			ResourcePartner p = datastoreDAO.getPartner("NLRL").get();
			assertThat(p, is(notNullValue()));
			log.debug("partner: {}", p);
		}
		catch (Exception e)
		{
			fail(e.getMessage());
		}
	}

	@Test
	public void testFetchPartners()
	{
		log.debug("running test: {}", Arrays.asList(new Throwable().getStackTrace()).get(0).getMethodName());

		try
		{
			Map<String, ResourcePartner> partners = datastoreDAO.getPartners();
			assertThat(partners.size(), is(greaterThan(0)));
			log.debug("partners: {}", partners);
		}
		catch (Exception e)
		{
			fail(e.getMessage());
		}
	}

	@Test
	public void testSavePartner()
	{
		log.debug("running test: {}", Arrays.asList(new Throwable().getStackTrace()).get(0).getMethodName());

		try
		{
			ResourcePartner p = datastoreDAO.getPartner("NLNZ:MP").get();
			assertThat(p, is(notNullValue()));
			log.debug("partner: {}", p);
			p.setNuc("AAATEST:NUC");
			datastoreDAO.addEntity(p);
		}
		catch (Exception e)
		{
			log.debug("{}", e.getMessage(), e);
			fail(e.getMessage());
		}
	}

}
