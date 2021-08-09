package org.nishen.resourcepartners;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;
import org.nishen.resourcepartners.entity.ElasticSearchPartner;
import org.nishen.resourcepartners.harvesters.Harvester;
import org.nishen.resourcepartners.harvesters.HarvesterIlrs;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Module;

import static org.junit.Assert.fail;

@Ignore
public class TestHarvesterIlrs
{
	private static final Logger log = LoggerFactory.getLogger(TestHarvesterIlrs.class);

	private static Injector injector = null;

	private static Harvester harvester = null;

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

		harvester = injector.getInstance(HarvesterIlrs.class);
	}

	@Test
	public void testILRSHarvest()
	{
		log.debug("running test: {}", Arrays.asList(new Throwable().getStackTrace()).get(0).getMethodName());
		try
		{
			Map<String, ElasticSearchPartner> partners = harvester.harvest();
			for (String nuc : partners.keySet())
				log.debug("{}:\n{}", nuc, partners.get(nuc));

			// assertThat(actual, equalTo(expected));
		}
		catch (Exception e)
		{
			fail(e.getMessage());
		}
	}
}
