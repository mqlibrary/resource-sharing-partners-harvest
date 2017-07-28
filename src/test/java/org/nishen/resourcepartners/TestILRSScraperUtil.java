package org.nishen.resourcepartners;

import static org.junit.Assert.fail;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.junit.BeforeClass;
import org.junit.Test;
import org.nishen.resourcepartners.util.ILRSScraperUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Module;

public class TestILRSScraperUtil
{
	private static final Logger log = LoggerFactory.getLogger(TestILRSScraperUtil.class);

	private static Injector injector = null;

	private static ILRSScraperUtil scraper = null;

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

		scraper = injector.getInstance(ILRSScraperUtil.class);
	}

	@Test
	public void testScraperPageFecth()
	{
		log.debug("running test: {}", Arrays.asList(new Throwable().getStackTrace()).get(0).getMethodName());
		try
		{
			String nuc = "NMQU";
			String page = scraper.getPage(nuc);

			// Address address = scraper.getAddress(nuc).orElse(null);

			log.debug("{}", page);
		}
		catch (Exception e)
		{
			fail(e.getMessage());
		}
	}
}
