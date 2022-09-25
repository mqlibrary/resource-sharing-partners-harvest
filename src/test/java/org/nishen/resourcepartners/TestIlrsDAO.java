package org.nishen.resourcepartners;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.junit.BeforeClass;
import org.junit.Test;
import org.nishen.resourcepartners.dao.IlrsDAO;
import org.nishen.resourcepartners.entity.ResourcePartnerAddress;
import org.nishen.resourcepartners.util.DataUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Module;

import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;

import static org.junit.Assert.fail;

public class TestIlrsDAO
{
	private static final Logger log = LoggerFactory.getLogger(TestIlrsDAO.class);

	private static Injector injector = null;

	private static IlrsDAO ilrsDAO = null;

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

		ilrsDAO = injector.getInstance(IlrsDAO.class);
	}

	@Test
	public void testIlrsGetPage()
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
	public void testGetAddressFromPage01()
	{
		log.debug("running test: {}", Arrays.asList(new Throwable().getStackTrace()).get(0).getMethodName());
		try
		{
			// String nuc = "NMQU";
			// String page = ilrsDAO.getPage(nuc);
			String page = new String(DataUtil.loadFile("target/test-classes/data/ilrs-data-nmqu-raw.html"), "UTF-8");

			Map<String, ResourcePartnerAddress> addresses = ilrsDAO.getAddressesFromPage(page);

			ResourcePartnerAddress actual = null;
			ResourcePartnerAddress expected = null;

			expected = new ResourcePartnerAddress();
			expected.setLine1("Balaclava Rd, cnr Epping Rd");
			expected.setCity("NORTH RYDE");
			expected.setStateProvince("NSW");
			expected.setPostalCode("2109");
			expected.setCountry("Australia");

			actual = addresses.get("main");

			assertThat(actual, equalTo(expected));

			expected = new ResourcePartnerAddress();
			expected.setLine1("Building C3C");
			expected.setLine2("Macquarie Drive");
			expected.setCity("MACQUARIE UNIVERSITY");
			expected.setStateProvince("NSW");
			expected.setPostalCode("2109");
			expected.setCountry("Australia");

			actual = addresses.get("postal");

			assertThat(actual, equalTo(expected));

			expected = new ResourcePartnerAddress();
			expected.setLine1("Same as Postal address");

			actual = addresses.get("billing");

			assertThat(actual, equalTo(expected));
		}
		catch (Exception e)
		{
			e.printStackTrace(System.out);
			fail(e.getMessage());
		}
	}

	@Test
	public void testGetAddressFromPage02()
	{
		log.debug("running test: {}", Arrays.asList(new Throwable().getStackTrace()).get(0).getMethodName());
		try
		{
			String page = new String(DataUtil.loadFile("target/test-classes/data/ilrs-data-aaar-raw.html"), "UTF-8");

			Map<String, ResourcePartnerAddress> addresses = ilrsDAO.getAddressesFromPage(page);

			ResourcePartnerAddress actual = null;
			ResourcePartnerAddress expected = null;

			expected = new ResourcePartnerAddress();
			expected.setLine1("Ground Floor");
			expected.setLine2("National Archives Building / Queen Victoria Terrace");
			expected.setCity("PARKES");
			expected.setStateProvince("ACT");
			expected.setPostalCode("2600");
			expected.setCountry("Australia");

			actual = addresses.get("main");

			assertThat(actual, equalTo(expected));

			expected = new ResourcePartnerAddress();
			expected.setLine1("PO Box 7425");
			expected.setCity("CANBERRA MAIL CENTRE");
			expected.setStateProvince("ACT");
			expected.setPostalCode("2610");
			expected.setCountry("Australia");

			actual = addresses.get("postal");

			assertThat(actual, equalTo(expected));

			expected = new ResourcePartnerAddress();
			expected.setLine1("ATTN: Librarian");
			expected.setLine2("Same as Postal address");

			actual = addresses.get("billing");

			assertThat(actual, equalTo(expected));
		}
		catch (Exception e)
		{
			e.printStackTrace(System.out);
			fail(e.getMessage());
		}
	}

	@Test
	public void getEmailFromPage()
	{
		log.debug("running test: {}", Arrays.asList(new Throwable().getStackTrace()).get(0).getMethodName());

		try
		{
			String page = new String(DataUtil.loadFile("target/test-classes/data/ilrs-data-nmqu-raw.html"), "UTF-8");

			String expected = "lib.ill@mq.edu.au";

			String actual = ilrsDAO.getEmailFromPage(page).orElse(null);
			log.debug("actual: {}", actual);

			assertThat(actual, equalTo(expected));
		}
		catch (Exception e)
		{
			log.debug("{}", e.getMessage(), e);
			fail(e.getMessage());
		}
	}

	@Test
	public void getPhoneIllFromPage()
	{
		log.debug("running test: {}", Arrays.asList(new Throwable().getStackTrace()).get(0).getMethodName());

		try
		{
			String page = new String(DataUtil.loadFile("target/test-classes/data/ilrs-data-nmqu-raw.html"), "UTF-8");

			String expected = "02 9850 7514";

			String actual = ilrsDAO.getPhoneIllFromPage(page).orElse(null);
			log.debug("actual: {}", actual);

			assertThat(actual, equalTo(expected));
		}
		catch (Exception e)
		{
			log.debug("{}", e.getMessage(), e);
			fail(e.getMessage());
		}
	}

	@Test
	public void getPhoneFaxFromPage()
	{
		log.debug("running test: {}", Arrays.asList(new Throwable().getStackTrace()).get(0).getMethodName());

		try
		{
			String page = new String(DataUtil.loadFile("target/test-classes/data/ilrs-data-nmqu-raw.html"), "UTF-8");

			String expected = "02 9850 6516";

			String actual = ilrsDAO.getPhoneFaxFromPage(page).orElse(null);
			log.debug("actual: {}", actual);

			assertThat(actual, equalTo(expected));
		}
		catch (Exception e)
		{
			log.debug("{}", e.getMessage(), e);
			fail(e.getMessage());
		}
	}
}
