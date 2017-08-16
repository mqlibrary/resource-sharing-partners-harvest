package org.nishen.resourcepartners;

import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.junit.BeforeClass;
import org.junit.Test;
import org.nishen.resourcepartners.dao.IlrsDAO;
import org.nishen.resourcepartners.model.Address;
import org.nishen.resourcepartners.model.Address.Country;
import org.nishen.resourcepartners.model.ObjectFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Module;

public class TestIlrsDAO
{
	private static final Logger log = LoggerFactory.getLogger(TestIlrsDAO.class);

	private static Injector injector = null;

	private static IlrsDAO ilrsDAO = null;

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

		ilrsDAO = injector.getInstance(IlrsDAO.class);
	}

	@Test
	public void testScraperPageFecth()
	{
		log.debug("running test: {}", Arrays.asList(new Throwable().getStackTrace()).get(0).getMethodName());
		try
		{
			String nuc = "NMQU";
			String page = ilrsDAO.getPage(nuc);

			assertThat(page, containsString("[Australian Interlibrary Resource Sharing (ILRS) Directory]"));
		}
		catch (Exception e)
		{
			fail(e.getMessage());
		}
	}

	@Test
	public void testScraperFindAddress00()
	{
		log.debug("running test: {}", Arrays.asList(new Throwable().getStackTrace()).get(0).getMethodName());
		try
		{
			String nuc = "NMQU";
			String page = ilrsDAO.getPage(nuc);

			Map<String, Address> addresses = ilrsDAO.getAddressesFromPage(page);

			Address actual = null;
			Address expected = null;

			Country country = of.createAddressCountry();
			country.setValue("AUS");
			country.setDesc("Australia");

			expected = of.createAddress();
			expected.setLine1("Balaclava Rd, cnr Epping Rd");
			expected.setCity("NORTH RYDE");
			expected.setStateProvince("NSW");
			expected.setPostalCode("2109");
			expected.setCountry(country);

			actual = addresses.get("main");

			assertThat(actual, equalTo(expected));

			expected = of.createAddress();
			expected.setLine1("Building C3C");
			expected.setLine2("Macquarie Drive");
			expected.setCity("MACQUARIE UNIVERSITY");
			expected.setStateProvince("NSW");
			expected.setPostalCode("2109");
			expected.setCountry(country);

			actual = addresses.get("postal");

			assertThat(actual, equalTo(expected));

			expected = of.createAddress();
			expected.setLine1("Same as Postal address");

			actual = addresses.get("billing");

			assertThat(actual, equalTo(expected));
		}
		catch (Exception e)
		{
			fail(e.getMessage());
		}
	}
}
