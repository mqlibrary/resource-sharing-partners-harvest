package org.nishen.resourcepartners;

import static org.junit.Assert.fail;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.junit.BeforeClass;
import org.junit.Test;
import org.nishen.resourcepartners.dao.ElasticSearchDAO;
import org.nishen.resourcepartners.entity.ElasticSearchPartner;
import org.nishen.resourcepartners.util.DataUtils;
import org.nishen.resourcepartners.util.JaxbUtil;
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
			ElasticSearchPartner actual = elastic.getPartner("NMQU").get();
			log.debug("{}", actual.toString());
			// assertThat(actual, equalTo(expected));
		}
		catch (Exception e)
		{
			fail(e.getMessage());
		}
	}

	@Test(expected = javax.ws.rs.NotFoundException.class)
	public void testGetPartnerDoesNotExist()
	{
		log.debug("running test: {}", Arrays.asList(new Throwable().getStackTrace()).get(0).getMethodName());

		elastic.getPartner("THOR");
	}

	@Test
	public void testGetPartners()
	{
		log.debug("running test: {}", Arrays.asList(new Throwable().getStackTrace()).get(0).getMethodName());
		try
		{
			Map<String, ElasticSearchPartner> p = elastic.getPartners();
			log.debug("{}", p.toString());
			// assertThat(actual, equalTo(expected));
		}
		catch (Exception e)
		{
			fail(e.getMessage());
		}
	}

	@Test
	public void testGetUnmarshalledElasticPartner()
	{
		log.debug("running test: {}", Arrays.asList(new Throwable().getStackTrace()).get(0).getMethodName());
		try
		{
			String datafile = "target/test-classes/data/elastic-partner.json";
			byte[] data = DataUtils.loadFile(datafile);

			String json = new String(data, "UTF-8");
			log.debug("json:\n{}", json);
			ElasticSearchPartner e = JaxbUtil.get(json, ElasticSearchPartner.class);
			log.debug("unmarshalled:\n{}", e);
		}
		catch (Exception e)
		{
			fail(e.getMessage());
		}
	}
}
