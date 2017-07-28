package org.nishen.resourcepartners.util;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.Properties;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.Form;
import javax.ws.rs.core.MediaType;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.safety.Whitelist;
import org.nishen.resourcepartners.entity.Address;
import org.nishen.resourcepartners.entity.Address.Country;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.Inject;
import com.google.inject.Provider;
import com.google.inject.name.Named;

public class ILRSScraperUtil
{
	private static final Logger log = LoggerFactory.getLogger(ILRSScraperUtil.class);

	private static final String REGEX = "(.+) +(ACT|NT|NSW|VIC|TAS|QLD|WA|SA) +(\\d{4})";
	
	private static final String REGEX2 = "<P><B>(\\w+) address:</B>\\s*<BR>(.*)</P>";

	private static final Pattern p = Pattern.compile(REGEX2, Pattern.CASE_INSENSITIVE | Pattern.DOTALL);

	private Provider<WebTarget> webTargetProvider;

	@Inject
	private ILRSScraperUtil(@Named("app.config") final Properties config,
	                        @Named("ws.url.ilrs") Provider<WebTarget> webTargetProvider)
	{
		this.webTargetProvider = webTargetProvider;

		log.debug("initialised ilrsscraperutil");
	}

	public Optional<Address> getAddress(String nuc)
	{
		String page = getPage(nuc);

		try
		{
			log.debug("getting postal");
			return Optional.of(getAddressFromPage(page, "Postal address"));
		}
		catch (AlternateAddressTypeException aate)
		{
			log.debug("alternate address type exception");
			try
			{
				log.debug("getting main address");
				return Optional.of(getAddressFromPage(page, "Main address"));
			}
			catch (Exception ie)
			{
				return Optional.empty();
			}
		}
		catch (Exception e)
		{
			return Optional.empty();
		}
	}

	public Optional<Address> getAddress(String nuc, String addressType)
	{
		String page = getPage(nuc);

		try
		{
			return Optional.of(getAddressFromPage(page, addressType));
		}
		catch (Exception e)
		{
			return Optional.empty();
		}
	}

	public String getPage(String nuc)
	{
		WebTarget ilrsTarget = webTargetProvider.get();
		WebTarget t = ilrsTarget.path("apps").path("ilrs").path("/").queryParam("action", "IlrsSearch");

		Form form = new Form();
		form = form.param("nuc", nuc).param("term", "").param("termType", "Keyword").param("state", "All")
		           .param("dosearch", "Search").param("chunk", "20");

		String result = null;

		try
		{
			result = t.request(MediaType.TEXT_HTML).post(Entity.form(form), String.class);

			Document doc = Jsoup.parse(result);
			String cleanPage = Jsoup.clean(doc.toString(), Whitelist.basic());
			Matcher m = p.matcher(cleanPage);
			m.find();
			log.trace("\n{}", cleanPage);
		}
		catch (Exception e)
		{
			log.error("unable to acquire page: {}", t.getUri().toString());
			return result;
		}

		return result;
	}

	public Address getAddressFromPage(String page, String addressType) throws Exception
	{
		Address address = new Address();

		List<String> addr = new ArrayList<String>();
		try (Scanner scanner = new Scanner(page))
		{
			String line = scanner.nextLine();
			while (!line.startsWith(addressType + ":") && scanner.hasNextLine())
				line = scanner.nextLine();

			if (!line.startsWith(addressType + ":"))
				throw new Exception("unable to parse page");

			int lineCount = 0;
			while (!line.startsWith(" Same as") && !line.equals(" AUSTRALIA") && lineCount < 10
			       && scanner.hasNextLine())
			{
				line = scanner.nextLine();
				addr.add(line.trim());
				lineCount++;
			}

			if (lineCount == 10)
				throw new Exception("unable to parse page");

			if (line.startsWith(" Same as"))
			{
				String alternateAddressType = line.replace(" Same as ", "");

				log.debug("getting an alternate address type: {}", alternateAddressType);

				throw new AlternateAddressTypeException(alternateAddressType);
			}
		}
		catch (NoSuchElementException | IllegalStateException e)
		{
			log.error("error parsing page: {}", e.getMessage(), e);
			throw new Exception("unable to parse page");
		}

		List<String> left = new ArrayList<String>();
		Collections.reverse(addr);
		for (String s : addr)
		{
			Matcher m = p.matcher(s);

			if ("australia".equals(s.toLowerCase()))
			{
				Country c = new Country();
				c.setValue("AUS");
				c.setDesc("Australia");
				address.setCountry(c);
			}
			else if (m.find())
			{
				address.setCity(m.group(1).trim());
				address.setStateProvince(m.group(2).trim());
				address.setPostalCode(m.group(3).trim());
			}
			else
			{
				left.add(s);
			}
		}

		if (left.size() == 0)
			return address;

		Collections.reverse(left);

		address.setLine1(left.get(0));
		left.remove(0);
		if (left.size() == 0)
			return address;

		address.setLine2(left.get(0));
		left.remove(0);
		if (left.size() == 0)
			return address;

		address.setLine3(left.get(0));
		left.remove(0);
		if (left.size() == 0)
			return address;

		address.setLine4(left.get(0));
		left.remove(0);
		if (left.size() == 0)
			return address;

		address.setLine5(left.get(0));
		left.remove(0);

		return address;
	}
}
