package org.nishen.resourcepartners;

import static org.junit.Assert.fail;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.junit.BeforeClass;
import org.junit.Test;
import org.nishen.resourcepartners.dao.ElasticSearchDAO;
import org.nishen.resourcepartners.entity.ElasticSearchPartner;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Module;

public class TestElasticSearchDAO
{
	private static final Logger log = LoggerFactory.getLogger(TestElasticSearchDAO.class);

	private static Injector injector = null;

	private static ElasticSearchDAO elastic = null;

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

		elastic = injector.getInstance(ElasticSearchDAO.class);
	}

	@Test
	public void testGetPartner()
	{
		log.debug("running test: {}", Arrays.asList(new Throwable().getStackTrace()).get(0).getMethodName());
		try
		{
			ElasticSearchPartner p = elastic.getPartner("NMQU");
			log.debug("{}", p.toString());
			// assertThat(actual, equalTo(expected));
		}
		catch (Exception e)
		{
			fail(e.getMessage());
		}
	}
}
