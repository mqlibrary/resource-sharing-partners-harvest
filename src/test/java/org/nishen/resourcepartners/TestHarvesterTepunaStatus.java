package org.nishen.resourcepartners;

import static org.junit.Assert.fail;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.BeforeClass;
import org.junit.Test;
import org.nishen.resourcepartners.entity.ElasticSearchPartner;
import org.nishen.resourcepartners.harvesters.Harvester;
import org.nishen.resourcepartners.harvesters.HarvesterTepunaStatus;
import org.nishen.resourcepartners.util.DataUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Module;

public class TestHarvesterTepunaStatus
{
	private static final Logger log = LoggerFactory.getLogger(TestHarvesterTepunaStatus.class);

	private static Injector injector = null;

	private static HarvesterTepunaStatus harvester = null;

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

		harvester = injector.getInstance(HarvesterTepunaStatus.class);
	}

	@Test
	public void testGetSuspensions()
	{
		log.debug("running test: {}", Arrays.asList(new Throwable().getStackTrace()).get(0).getMethodName());

		try
		{
			Map<String, String> messages = new HashMap<String, String>();
			messages.put("msg01", new String(DataUtils.loadFile("target/test-classes/data/message01.txt"), "UTF-8"));
			messages.put("msg02", new String(DataUtils.loadFile("target/test-classes/data/message02.txt"), "UTF-8"));
			harvester.getSuspensions(messages);
		}
		catch (Exception e)
		{
			fail(e.getMessage());
			log.error("{}", e.getMessage(), e);
		}
	}

	@Test
	public void testHarvesterTepunaStatus()
	{
		log.debug("running test: {}", Arrays.asList(new Throwable().getStackTrace()).get(0).getMethodName());

		try
		{
			Map<String, ElasticSearchPartner> changed = harvester.harvest();
			if (changed != null)
				log.debug("changed count: {}", changed.size());
		}
		catch (Exception e)
		{
			fail(e.getMessage());
			log.error("{}", e.getMessage(), e);
		}
	}
}
