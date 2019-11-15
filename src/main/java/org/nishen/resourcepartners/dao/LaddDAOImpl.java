package org.nishen.resourcepartners.dao;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Map;
import java.util.TreeMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.ws.rs.ClientErrorException;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;

import org.nishen.resourcepartners.entity.ElasticSearchPartner;
import org.nishen.resourcepartners.entity.ElasticSearchSuspension;
import org.nishen.resourcepartners.util.JaxbUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.Inject;
import com.google.inject.Provider;
import com.google.inject.name.Named;

public class LaddDAOImpl implements LaddDAO
{
	private static final Logger log = LoggerFactory.getLogger(LaddDAOImpl.class);

	private static final SimpleDateFormat idf = new SimpleDateFormat("dd MMM, yyyy", Locale.ENGLISH);

	private static final SimpleDateFormat odf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ", Locale.ENGLISH);

	private static Pattern p;

	private static Pattern pDate;

	static
	{
		String regex = "";
		regex += "<tr class=\"(?:odd|even).*?\">\\s*";
		regex += "<td .*?>\\s*(.*?)\\s*</td>\\s*";
		regex += "<td .*?>\\s*(.*?)\\s*</td>\\s*";
		regex += "<td .*?>\\s*(.*?)\\s*</td>\\s*";
		regex += "<td .*?>\\s*(.*?)\\s*</td>\\s*";
		regex += "<td .*?>\\s*(.*?)\\s*</td>\\s*";
		regex += "<td .*?>\\s*(.*?)\\s*</td>\\s*";
		regex += "</tr>";

		p = Pattern.compile(regex, Pattern.CASE_INSENSITIVE | Pattern.DOTALL);

		regex = "<span .*?>\\s*(.*)\\s*</span>";
		pDate = Pattern.compile(regex, Pattern.CASE_INSENSITIVE);
	}

	private WebTarget laddTarget;

	@Inject
	public LaddDAOImpl(@Named("ws.ladd") Provider<WebTarget> laddTargetProvider)
	{
		this.laddTarget = laddTargetProvider.get();

		log.debug("instantiated class: {}", this.getClass().getName());
	}

	@Override
	public Map<String, ElasticSearchPartner> getData() throws ClientErrorException
	{
		Map<String, ElasticSearchPartner> data = new TreeMap<String, ElasticSearchPartner>();

		String page = laddTarget.request(MediaType.TEXT_HTML).get(String.class);
		log.debug("fetched page: {}", laddTarget.getUri());

		Matcher m = p.matcher(page);
		while (m.find())
		{
			log.debug("found item:");
			if (log.isDebugEnabled())
				for (int x = 1; x <= m.groupCount(); x++)
					log.debug("group[{}]: {}", x, m.group(x));

			String nuc1 = m.group(1);
			String name = m.group(2);
			String isoi = m.group(3);
			String susp = m.group(4);
			String begs = m.group(5);
			String ends = m.group(6);

			log.debug("nuc1: {}", nuc1);
			log.debug("name: {}", name);
			log.debug("isoi: {}", isoi);
			log.debug("susp: {}", susp);
			log.debug("begs: {}", begs);
			log.debug("ends: {}", ends);

			ElasticSearchPartner e = new ElasticSearchPartner();
			e.setNuc(nuc1);
			e.setEnabled(true);

			if (name != null && !"".equals(name))
				e.setName(name);

			if (isoi != null && "ISO ILL".equals(isoi))
				e.setIsoIll(true);

			if (susp != null)
				susp = susp.trim().toLowerCase();

			if ("not suspended".equals(susp))
			{
				e.setStatus(ElasticSearchSuspension.NOT_SUSPENDED);
			}
			else if ("suspended".equals(susp))
			{
				e.setStatus(ElasticSearchSuspension.SUSPENDED);

				String begSusp = formatDate(nuc1, begs);
				String endSusp = formatDate(nuc1, ends);
				if (begSusp != null && endSusp != null)
				{
					ElasticSearchSuspension suspension = new ElasticSearchSuspension();
					suspension.setSuspensionStatus(e.getStatus());
					suspension.setSuspensionStart(begSusp);
					suspension.setSuspensionEnd(endSusp);

					if (!e.getSuspensions().contains(suspension))
						e.getSuspensions().add(suspension);
				}
			}
			else
			{
				e.setStatus(ElasticSearchSuspension.UNKNOWN);
				log.warn("unknown status [{}]: {}", nuc1, susp);
			}

			if (log.isDebugEnabled())
				log.debug("elasticSearchPartner: {}", JaxbUtil.format(e));

			data.put(nuc1, e);
		}

		log.debug("elasticSearchPartners found: {}", data.size());

		return data;
	}

	private String formatDate(String nuc, String date)
	{
		String result = null;

		if (date == null || "".equals(date.trim()))
			return result;

		try
		{
			Matcher m = pDate.matcher(date);
			if (m.find())
			{
				Date d = idf.parse(m.group(1));
				result = odf.format(d);
			}
		}
		catch (ParseException pe)
		{
			log.warn("can't parse tepuna date[{}]: {}", nuc, date);
		}

		return result;
	}
}
