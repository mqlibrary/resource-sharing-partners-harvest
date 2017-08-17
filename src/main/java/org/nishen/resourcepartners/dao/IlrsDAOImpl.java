package org.nishen.resourcepartners.dao;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.ws.rs.ClientErrorException;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.Form;
import javax.ws.rs.core.MediaType;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.safety.Whitelist;
import org.nishen.resourcepartners.model.Address;
import org.nishen.resourcepartners.model.Address.Country;
import org.nishen.resourcepartners.model.ObjectFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.Inject;
import com.google.inject.Provider;
import com.google.inject.name.Named;

public class IlrsDAOImpl implements IlrsDAO
{
	private static final Logger log = LoggerFactory.getLogger(IlrsDAOImpl.class);

	private static final String REGEX_ADDRESS = "<P><B>(\\w+) address:</B>\\s*<BR>(.*?)</P>";

	private static final String REGEX_EMAIL = "<B>ILL email:</B>\\s*<A HREF=\"mailto:(.*?)\"\\s*>";

	private static final String REGEX_PHONE_ILL = "<B>ILL phone:</B>\\s*(.*?)\\s*<BR>";

	private static final String REGEX_PHONE_FAX = "<B>ILL fax:</B>\\s*(.*?)\\s*<BR>";

	private static final Pattern pAddress = Pattern.compile(REGEX_ADDRESS, Pattern.CASE_INSENSITIVE | Pattern.DOTALL);

	private static final Pattern pEmail = Pattern.compile(REGEX_EMAIL, Pattern.CASE_INSENSITIVE);

	private static final Pattern pPhoneIll = Pattern.compile(REGEX_PHONE_ILL, Pattern.CASE_INSENSITIVE);

	private static final Pattern pPhoneFax = Pattern.compile(REGEX_PHONE_FAX, Pattern.CASE_INSENSITIVE);

	private static List<String> states = new ArrayList<String>();
	static
	{
		states.add("nsw");
		states.add("new south wales");
		states.add("qld");
		states.add("queensland");
		states.add("vic");
		states.add("victoria");
		states.add("sa");
		states.add("south australia");
		states.add("wa");
		states.add("western australia");
		states.add("nt");
		states.add("northern territory");
		states.add("act");
		states.add("australian capital territory");
		states.add("tas");
		states.add("tasmania");
	}

	private Provider<WebTarget> webTargetProvider;

	private ObjectFactory of = null;

	@Inject
	public IlrsDAOImpl(@Named("ws.ilrs") Provider<WebTarget> webTargetProvider)
	{
		this.webTargetProvider = webTargetProvider;

		this.of = new ObjectFactory();

		log.debug("instantiated class: {}", this.getClass().getName());
	}

	@Override
	public String getPage(String nuc) throws ClientErrorException
	{
		WebTarget ilrsTarget = webTargetProvider.get();
		WebTarget t = ilrsTarget.path("apps").path("ilrs").queryParam("action", "IlrsSearch");

		log.debug("target: {}", t.getUri());

		Form form = new Form();
		form = form.param("nuc", nuc).param("term", "").param("termType", "Keyword").param("state", "All")
		           .param("dosearch", "Search").param("chunk", "20");

		String result = null;

		result = t.request(MediaType.TEXT_HTML).post(Entity.form(form), String.class);

		Document doc = Jsoup.parse(result);
		String cleanPage = Jsoup.clean(doc.toString(), Whitelist.basic());
		log.trace("\n{}", cleanPage);

		return result;
	}

	@Override
	public Map<String, Address> getAddressesFromPage(String page)
	{
		Map<String, Address> addresses = new HashMap<String, Address>();

		Matcher m = pAddress.matcher(page);
		while (m.find())
		{
			String type = m.group(1);
			String addr = m.group(2);

			List<String> cleanAddr = cleanAddress(addr);
			log.debug("type: {}", type.toLowerCase());
			for (String s : cleanAddr)
			{
				log.debug("addr: {}", s);
			}

			addresses.put(type.toLowerCase(), extractAddress(cleanAddr));
		}

		return addresses;
	}

	@Override
	public Optional<String> getEmailFromPage(String page)
	{
		String email = null;
		Matcher m = pEmail.matcher(page);
		if (m.find())
		{
			email = m.group(1);
			log.debug("found ill email: {}", email);
		}

		return Optional.ofNullable(email);
	}

	@Override
	public Optional<String> getPhoneIllFromPage(String page)
	{
		String phoneIll = null;
		Matcher m = pPhoneIll.matcher(page);
		if (m.find())
		{
			phoneIll = m.group(1);
			log.debug("found ill phone: {}", phoneIll);
		}

		return Optional.ofNullable(phoneIll);
	}

	@Override
	public Optional<String> getPhoneFaxFromPage(String page)
	{
		String phoneFax = null;
		Matcher m = pPhoneFax.matcher(page);
		if (m.find())
		{
			phoneFax = m.group(1);
			log.debug("found ill fax: {}", phoneFax);
		}

		return Optional.ofNullable(phoneFax);
	}

	private List<String> cleanAddress(String address)
	{
		List<String> result = null;
		try
		{
			String a = null;
			a = URLDecoder.decode(address, "UTF-8");
			a = a.replaceAll("^\\s*\n\\s*", "");
			a = a.replaceAll("\\w*&nbsp;\\w*", "\n");
			a = a.replaceAll("<BR>\\s", "\n");
			a = a.replaceAll("\\s*\n", "\n");
			a = a.replaceAll("\n\\s*", "\n");
			a = a.replaceAll("\n\\s*$", "");

			result = Arrays.asList(a.split("\n"));
		}
		catch (UnsupportedEncodingException e)
		{
			log.error("error cleaning address: {}", address);
		}

		return result;
	}

	private Address extractAddress(List<String> addressLines)
	{
		Address address = of.createAddress();

		List<String> left = new ArrayList<String>();
		Collections.reverse(addressLines);
		for (int x = 0; x < addressLines.size(); x++)
		{
			String s = addressLines.get(x);
			if ("australia".equals(s.toLowerCase()))
			{
				Country c = new Country();
				c.setValue("AUS");
				c.setDesc("Australia");
				address.setCountry(c);
			}
			else if (s.matches("\\d{4}"))
			{
				address.setPostalCode(s);
			}
			else if (states.contains(s.toLowerCase()))
			{
				address.setStateProvince(s);
				if (addressLines.size() >= x + 1 && address.getPostalCode() != null &&
				    !"".equals(address.getPostalCode()))
					address.setCity(addressLines.get(++x));
			}
			else
			{
				left.add(s);
			}
		}

		if (left.size() == 0)
			return address;

		Collections.reverse(left);

		address.setLine1(left.remove(0));
		if (left.size() == 0)
			return address;

		address.setLine2(left.remove(0));
		if (left.size() == 0)
			return address;

		address.setLine3(left.remove(0));
		if (left.size() == 0)
			return address;

		address.setLine4(left.remove(0));
		if (left.size() == 0)
			return address;

		address.setLine5(left.remove(0));

		return address;
	}
}
