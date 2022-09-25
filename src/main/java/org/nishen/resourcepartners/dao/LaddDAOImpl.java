package org.nishen.resourcepartners.dao;

import java.util.Map;
import java.util.TreeMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.ws.rs.ClientErrorException;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;

import org.nishen.resourcepartners.entity.ResourcePartner;
import org.nishen.resourcepartners.entity.ResourcePartnerSuspension;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.Inject;
import com.google.inject.Provider;
import com.google.inject.name.Named;

public class LaddDAOImpl implements LaddDAO
{
	private static final Logger log = LoggerFactory.getLogger(LaddDAOImpl.class);

	private static Pattern p;

	private static Pattern pDate;

	static
	{
		String regex = "";
		regex += "<tr>\\s*";
		regex += "<td .*?>\\s*<p>\\s*(.*?)\\s*</p>\\s*</td>\\s*";
		regex += "<td .*?>\\s*(.*?)\\s*</td>\\s*";
		regex += "<td .*?>\\s*(.*?)\\s*</td>\\s*";
		regex += "<td .*?>\\s*(.*?)\\s*</td>\\s*";
		regex += "<td .*?>\\s*(.*?)\\s*</td>\\s*";
		regex += "<td .*?>\\s*(.*?)\\s*</td>\\s*";
		regex += "</tr>";

		p = Pattern.compile(regex, Pattern.CASE_INSENSITIVE | Pattern.DOTALL);

		regex = "<time datetime=\"(.*?)\".*</time>";
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
	public Map<String, ResourcePartner> getData() throws ClientErrorException
	{
		Map<String, ResourcePartner> data = new TreeMap<String, ResourcePartner>();

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
			String begs = getDate(nuc1, m.group(5));
			String ends = getDate(nuc1, m.group(6));

			log.debug("nuc1: {}", nuc1);
			log.debug("name: {}", name);
			log.debug("isoi: {}", isoi);
			log.debug("susp: {}", susp);
			log.debug("begs: {}", begs);
			log.debug("ends: {}", ends);

			ResourcePartner e = new ResourcePartner();
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
				e.setStatus(ResourcePartnerSuspension.NOT_SUSPENDED);
			}
			else if ("suspended".equals(susp))
			{
				e.setStatus(ResourcePartnerSuspension.SUSPENDED);

				String begSusp = begs;
				String endSusp = ends;
				if (begSusp != null && endSusp != null)
				{
					ResourcePartnerSuspension suspension = new ResourcePartnerSuspension();
					suspension.setSuspensionStatus(e.getStatus());
					suspension.setSuspensionStart(begSusp);
					suspension.setSuspensionEnd(endSusp);

					if (!e.getSuspensions().contains(suspension))
						e.getSuspensions().add(suspension);
				}
			}
			else
			{
				e.setStatus(ResourcePartnerSuspension.UNKNOWN);
				log.warn("unknown status [{}]: {}", nuc1, susp);
			}

			data.put(nuc1, e);
		}

		log.debug("ResourceSharingPartners found: {}", data.size());

		return data;
	}

	private String getDate(String nuc, String date)
	{
		String result = null;

		if (date == null || "".equals(date.trim()))
			return result;

		Matcher m = pDate.matcher(date);
		if (m.find())
		{
			result = m.group(1);
			log.debug("found date [{}]: {}", nuc, result);
		}

		return result;
	}
}
