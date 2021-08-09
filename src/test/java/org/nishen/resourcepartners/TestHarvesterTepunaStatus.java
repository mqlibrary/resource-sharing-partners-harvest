package org.nishen.resourcepartners;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;
import org.nishen.resourcepartners.entity.ElasticSearchPartner;
import org.nishen.resourcepartners.harvesters.HarvesterTepunaStatus;
import org.nishen.resourcepartners.util.DataUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Module;

import static org.junit.Assert.fail;

@Ignore
public class TestHarvesterTepunaStatus
{
	private static final Logger log = LoggerFactory.getLogger(TestHarvesterTepunaStatus.class);

	private static ObjectMapper mapper = null;

	private static Injector injector = null;

	private static HarvesterTepunaStatus harvester = null;

	@BeforeClass
	public static void setupClass()
	{
		mapper = new ObjectMapper();

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
			JsonNode node = null;
			Map<String, JsonNode> messages = new HashMap<String, JsonNode>();

			// node = mapper.readTree(DataUtils.loadFile("target/test-classes/data/message01.txt"));
			// messages.put("NLNZ:HP", node);

			node = mapper.readTree(DataUtils.loadFile("target/test-classes/data/message02.txt"));
			messages.put("NLNZ:NPM", node);

			node = mapper.readTree(DataUtils.loadFile("target/test-classes/data/message03.txt"));
			messages.put("NLNZ:WHP", node);

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
