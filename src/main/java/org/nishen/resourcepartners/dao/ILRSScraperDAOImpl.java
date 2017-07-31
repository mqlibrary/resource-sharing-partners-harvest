package org.nishen.resourcepartners.dao;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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
import org.nishen.resourcepartners.entity.ObjectFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.Inject;
import com.google.inject.Provider;
import com.google.inject.name.Named;

public class ILRSScraperDAOImpl implements ILRSScraperDAO
{
	private static final Logger log = LoggerFactory.getLogger(ILRSScraperDAOImpl.class);

	private static final String REGEX = "<P><B>(\\w+) address:</B>\\s*<BR>(.*?)</P>";

	private static final Pattern p = Pattern.compile(REGEX, Pattern.CASE_INSENSITIVE | Pattern.DOTALL);

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
	public ILRSScraperDAOImpl(@Named("ws.ilrs") Provider<WebTarget> webTargetProvider)
	{
		this.webTargetProvider = webTargetProvider;

		this.of = new ObjectFactory();

		log.debug("initialised ilrsscraperutil");
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.nishen.resourcepartners.dao.ILRSScraperDAO#getPage(java.lang.String)
	 */
	@Override
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
			log.trace("\n{}", cleanPage);
		}
		catch (Exception e)
		{
			log.error("unable to acquire page: {}", t.getUri().toString());
			return result;
		}

		return result;
	}

	@Override
	public Map<String, Address> getAddressFromPage(String page) throws Exception
	{
		Map<String, Address> addresses = new HashMap<String, Address>();

		Matcher m = p.matcher(page);
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
