package org.nishen.resourcepartners;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.junit.BeforeClass;
import org.junit.Test;
import org.nishen.resourcepartners.dao.TepunaDAO;
import org.nishen.resourcepartners.entity.ElasticSearchPartner;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Module;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.greaterThan;

import static org.junit.Assert.fail;

public class TestTepunaDAO
{
	private static final Logger log = LoggerFactory.getLogger(TestTepunaDAO.class);

	private static Injector injector = null;

	private static TepunaDAO tepunaDAO = null;

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

		tepunaDAO = injector.getInstance(TepunaDAO.class);
	}

	@Test
	public void testGetData()
	{
		log.debug("running test: {}", Arrays.asList(new Throwable().getStackTrace()).get(0).getMethodName());
		try
		{
			Map<String, ElasticSearchPartner> partners = tepunaDAO.getData();

			for (String nuc : partners.keySet())
				log.debug("{}:\n{}", nuc, partners.get(nuc));

			assertThat(partners.size(), greaterThan(400));
		}
		catch (Exception e)
		{
			log.error("{}", e.getMessage(), e);
			fail(e.getMessage());
		}
	}
}
