package org.nishen.resourcepartners;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.junit.BeforeClass;
import org.junit.Test;
import org.nishen.resourcepartners.entity.Address;
import org.nishen.resourcepartners.entity.Address.Country;
import org.nishen.resourcepartners.entity.ObjectFactory;
import org.nishen.resourcepartners.util.DataUtils;
import org.nishen.resourcepartners.util.ILRSScraperUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Module;

import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;

import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.CoreMatchers.equalTo;

public class TestILRSScraperUtil
{
	private static final Logger log = LoggerFactory.getLogger(TestILRSScraperUtil.class);

	private static Injector injector = null;

	private static ILRSScraperUtil scraper = null;

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

			assertThat(page, containsString("[Australian Interlibrary Resource Sharing (ILRS) Directory]"));

			log.debug("{}", page);
		}
		catch (Exception e)
		{
			fail(e.getMessage());
		}
	}

	@Test
	public void testScraperFindAddress()
	{
		log.debug("running test: {}", Arrays.asList(new Throwable().getStackTrace()).get(0).getMethodName());
		try
		{
			String datafile = "target/test-classes/data/irls-data-nmqu.html";
			String page = new String(DataUtils.loadFile(datafile), "UTF-8");
			Map<String, Address> addresses = scraper.getAddressFromPage(page);

			Address actual = null;
			Address expected = null;

			Country country = of.createAddressCountry();
			country.setValue("AUS");
			country.setDesc("Australia");

			expected = of.createAddress();
			expected.setLine1("Balaclava Rd, cnr Epping Rd");
			expected.setLine2("NORTH RYDE");
			expected.setStateProvince("NSW");
			expected.setPostalCode("2109");
			expected.setCountry(country);

			actual = addresses.get("main");

			assertThat(actual, equalTo(expected));

			expected = of.createAddress();
			expected.setLine1("Building C3C");
			expected.setLine2("Macquarie Drive");
			expected.setLine3("MACQUARIE UNIVERSITY");
			expected.setStateProvince("NSW");
			expected.setPostalCode("2109");
			expected.setCountry(country);

			actual = addresses.get("postal");

			assertThat(actual, equalTo(expected));

			expected = of.createAddress();
			expected.setLine1("Same as Postal address");

			actual = addresses.get("billing");
		}
		catch (Exception e)
		{
			fail(e.getMessage());
		}
	}
}
