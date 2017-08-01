package org.nishen.resourcepartners.dao;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.safety.Whitelist;
import org.nishen.resourcepartners.entity.ElasticSearchPartner;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.Inject;
import com.google.inject.Provider;
import com.google.inject.name.Named;

public class LaddDAOImpl implements LaddDAO
{
	private static final Logger log = LoggerFactory.getLogger(LaddDAOImpl.class);

	private static final SimpleDateFormat idf = new SimpleDateFormat("dd MMM yyyy");

	private static final SimpleDateFormat odf = new SimpleDateFormat("yyyyMMdd");

	private Pattern p;

	private WebTarget laddTarget;

	@Inject
	public LaddDAOImpl(@Named("ws.ladd") Provider<WebTarget> laddTargetProvider)
	{
		this.laddTarget = laddTargetProvider.get();

		String regex = "";
		regex += "<tr id=\"(\\w*?)\">\\s*<td>(\\w*?)</td>\\s*";
		regex += "<td>(.*?)</td>\\s*<td>(.*?)</td>\\s*<td>(.*?)</td>\\s*";
		regex += "<td>(.*?)</td>\\s*<td>(.*?)</td>\\s*</tr>";

		p = Pattern.compile(regex, Pattern.CASE_INSENSITIVE | Pattern.DOTALL);
	}

	@Override
	public String getPage()
	{
		String result = null;

		try
		{
			result = laddTarget.request(MediaType.TEXT_HTML).get(String.class);

			Document doc = Jsoup.parse(result);
			String cleanPage = Jsoup.clean(doc.toString(), Whitelist.basic());
			log.trace("\n{}", cleanPage);
		}
		catch (Exception e)
		{
			log.error("unable to acquire page: {}", laddTarget.getUri().toString());
			return result;
		}

		return result;
	}

	@Override
	public Map<String, ElasticSearchPartner> getDataFromPage(String page) throws Exception
	{
		Map<String, ElasticSearchPartner> data = new HashMap<String, ElasticSearchPartner>();

		Matcher m = p.matcher(page);
		while (m.find())
		{
			String nuc1 = m.group(1);
			String name = m.group(3);
			String susp = m.group(5);
			String srts = m.group(6);
			String ends = m.group(7);

			ElasticSearchPartner e = new ElasticSearchPartner();
			e.setNuc(nuc1);

			if (name != null && !"".equals(name))
				e.setName(name);

			if (susp != null && !"".equals(susp))
				e.setStatus(susp.toLowerCase());

			if (srts != null && !"".equals(srts))
			{
				try
				{
					String date = odf.format(idf.parse(srts));
					e.setSuspensionStart(date);
				}
				catch (ParseException pe)
				{
					log.warn("[{}] unable to parse date: {}", nuc1, srts);
				}
			}

			if (ends != null && !"".equals(ends))
			{
				try
				{
					String date = odf.format(idf.parse(ends));
					e.setSuspensionEnd(date);
				}
				catch (ParseException pe)
				{
					log.warn("[{}] unable to parse date: {}", nuc1, ends);
				}
			}
		}

		return data;
	}
}
