package org.nishen.resourcepartners;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.junit.BeforeClass;
import org.junit.Test;
import org.nishen.resourcepartners.dao.OutlookDAO;
import org.nishen.resourcepartners.util.DataUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Module;

import static org.junit.Assert.fail;

public class TestOutlookDAO
{
	private static final Logger log = LoggerFactory.getLogger(TestOutlookDAO.class);

	private static Injector injector = null;

	private static OutlookDAO outlookDAO = null;

	private static ObjectMapper mapper = null;

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

		outlookDAO = injector.getInstance(OutlookDAO.class);

		mapper = new ObjectMapper();
	}

	@Test
	public void testGetFolders()
	{
		log.debug("running test: {}", Arrays.asList(new Throwable().getStackTrace()).get(0).getMethodName());

		try
		{
			String folderId = outlookDAO.getProcessedFolderId();
			log.debug("folderId: {}", folderId);
		}
		catch (Exception e)
		{
			fail(e.getMessage());
		}
	}

	@Test
	public void testGetMessages()
	{
		log.debug("running test: {}", Arrays.asList(new Throwable().getStackTrace()).get(0).getMethodName());

		try
		{
			Map<String, String> messages = outlookDAO.getMessages();
			for (String m : messages.keySet())
				log.debug("id: {}, message:\n{}", m, messages.get(m));
		}
		catch (Exception e)
		{
			fail(e.getMessage());
		}
	}

	@Test
	public void testMoveMessages()
	{
		log.debug("running test: {}", Arrays.asList(new Throwable().getStackTrace()).get(0).getMethodName());

		try
		{
			Map<String, String> messages = outlookDAO.getMessages();
			outlookDAO.markMessagesProcessed(messages);
		}
		catch (Exception e)
		{
			fail(e.getMessage());
		}
	}

	@Test
	public void testProcessResponse()
	{
		log.debug("running test: {}", Arrays.asList(new Throwable().getStackTrace()).get(0).getMethodName());

		try
		{
			String response = new String(DataUtils.loadFile("target/test-classes/data/outlook-response.json"), "UTF-8");
			log.debug("response:\n{}", response);

			JsonNode root = mapper.readTree(response);
			log.debug("next: {}", root.get("@odata.nextLink"));

			for (JsonNode entry : root.get("value"))
			{
				log.debug("\n{}", entry.get("Body").get("Content").asText().replace("\\r\\n", "\n"));
			}
		}
		catch (Exception e)
		{
			fail(e.getMessage());
		}
	}
}
