package org.nishen.resourcepartners;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.junit.BeforeClass;
import org.junit.Test;
import org.nishen.resourcepartners.dao.LaddDAOImpl;
import org.nishen.resourcepartners.entity.ResourcePartner;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Module;

import static org.junit.Assert.fail;

public class TestHarvesterLaddPartners
{
	private static final Logger log = LoggerFactory.getLogger(TestHarvesterLaddPartners.class);

	private static Injector injector = null;

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
	}

	@Test
	public void testHarvesterLaddPartners()
	{
		log.debug("running test: {}", Arrays.asList(new Throwable().getStackTrace()).get(0).getMethodName());
		try
		{
			LaddDAOImpl dao = injector.getInstance(LaddDAOImpl.class);
			Map<String, ResourcePartner> partners = dao.getData();
			partners.values().stream().limit(3).forEach(System.out::println);
		}
		catch (Exception e)
		{
			fail(e.getMessage());
			log.error("{}", e.getMessage(), e);
		}
	}

}
