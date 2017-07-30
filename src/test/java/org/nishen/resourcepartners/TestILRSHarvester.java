package org.nishen.resourcepartners;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.junit.BeforeClass;
import org.junit.Test;
import org.nishen.resourcepartners.entity.ObjectFactory;
import org.nishen.resourcepartners.harvesters.ILRSHarvester;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Module;

import static org.junit.Assert.fail;

public class TestILRSHarvester
{
	private static final Logger log = LoggerFactory.getLogger(TestILRSHarvester.class);

	private static Injector injector = null;

	private static ILRSHarvester harvester = null;
	
	private static ObjectFactory of = null;

	@BeforeClass
	public static void setup()
	{
		of = new ObjectFactory();

		// list for injector modules
		List<Module> modules = new ArrayList<Module>();

		// module (main configuration)
		modules.add(new ResourcePartnerModule());

		// create the injector
		log.debug("creating injector");
		injector = Guice.createInjector(modules);

		harvester = injector.getInstance(ILRSHarvester.class);
	}

	@Test
	public void testILRSHarvest()
	{
		log.debug("running test: {}", Arrays.asList(new Throwable().getStackTrace()).get(0).getMethodName());
		try
		{
			harvester.harvest();
			
			// assertThat(actual, equalTo(expected));
		}
		catch (Exception e)
		{
			fail(e.getMessage());
		}
	}
}
