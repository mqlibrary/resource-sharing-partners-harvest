package org.nishen.resourcepartners;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.junit.BeforeClass;
import org.junit.Test;
import org.nishen.resourcepartners.dao.Config;
import org.nishen.resourcepartners.dao.ConfigFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Module;

import static org.junit.Assert.fail;

public class TestConfig
{
	private static final Logger log = LoggerFactory.getLogger(TestConfig.class);

	private static Injector injector = null;

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
	}

	@Test
	public void testFetchConfig()
	{
		log.debug("running test: {}", Arrays.asList(new Throwable().getStackTrace()).get(0).getMethodName());

		try
		{
			ConfigFactory factory = injector.getInstance(ConfigFactory.class);
			Config config = factory.fetch("OUTLOOK");
			log.debug("config: {}", config.getAll());
			// assertThat(actual, equalTo(expected));
		}
		catch (Exception e)
		{
			fail(e.getMessage());
		}
	}

	@Test
	public void testSetConfig()
	{
		log.debug("running test: {}", Arrays.asList(new Throwable().getStackTrace()).get(0).getMethodName());

		try
		{
			ConfigFactory factory = injector.getInstance(ConfigFactory.class);
			Config config = factory.fetch("OUTLOOK");
			log.debug("config: {}", config.getAll());
			config.set("nish", "was here");
			// assertThat(actual, equalTo(expected));
		}
		catch (Exception e)
		{
			fail(e.getMessage());
		}
	}
}
