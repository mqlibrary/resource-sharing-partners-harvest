package org.nishen.resourcepartners;

import static org.hamcrest.Matchers.greaterThan;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.junit.BeforeClass;
import org.junit.Test;
import org.nishen.resourcepartners.dao.LaddDAO;
import org.nishen.resourcepartners.entity.ElasticSearchPartner;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Module;

public class TestLaddDAO
{
	private static final Logger log = LoggerFactory.getLogger(TestLaddDAO.class);

	private static Injector injector = null;

	private static LaddDAO laddDAO = null;

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

		laddDAO = injector.getInstance(LaddDAO.class);
	}

	@Test
	public void testGetData()
	{
		log.debug("running test: {}", Arrays.asList(new Throwable().getStackTrace()).get(0).getMethodName());
		try
		{
			Map<String, ElasticSearchPartner> laddPartners = laddDAO.getData();

			assertThat(laddPartners.size(), greaterThan(650));
		}
		catch (Exception e)
		{
			fail(e.getMessage());
		}
	}
}
