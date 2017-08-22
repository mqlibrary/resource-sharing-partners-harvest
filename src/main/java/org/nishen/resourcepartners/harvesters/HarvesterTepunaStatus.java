package org.nishen.resourcepartners.harvesters;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.nishen.resourcepartners.SkipHarvestException;
import org.nishen.resourcepartners.dao.ElasticSearchDAO;
import org.nishen.resourcepartners.dao.OutlookDAO;
import org.nishen.resourcepartners.entity.ElasticSearchChangeRecord;
import org.nishen.resourcepartners.entity.ElasticSearchPartner;
import org.nishen.resourcepartners.entity.ElasticSearchSuspension;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.Inject;

/**
 * @author nishen
 *
 */
public class HarvesterTepunaStatus implements Harvester
{
	private static final Logger log = LoggerFactory.getLogger(HarvesterTepunaStatus.class);

	private static final String SOURCE_SYSTEM = "OUTLOOK";

	private static final String NZ_NUC_PREFIX = "NLNZ";

	private static final SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ");

	private static final SimpleDateFormat idf = new SimpleDateFormat("dd-MMM-yy");

	private static Pattern p;

	static
	{
		String regex = "";
		regex += "STATUS (\\w+?) NLNZ BEGIN SUSPENSION LIST\\s+";
		regex += "STATUS \\w+ NLNZ (?:(NO SUSPENSIONS)|(\\d\\d-\\w\\w\\w-\\d\\d) " +
		         "(\\d\\d-\\w\\w\\w-\\d\\d) (\\w+) (\\w+))\\s+";
		regex += "STATUS \\w+ NLNZ END SUSPENSION LIST";

		p = Pattern.compile(regex, Pattern.CASE_INSENSITIVE | Pattern.DOTALL);
	}

	private OutlookDAO outlook;

	private ElasticSearchDAO elastic;

	@Inject
	public HarvesterTepunaStatus(OutlookDAO outlook, ElasticSearchDAO elastic)
	{
		this.outlook = outlook;

		this.elastic = elastic;

		log.debug("instantiated class: {}", this.getClass().getName());
	}

	@Override
	public String getSource()
	{
		return SOURCE_SYSTEM;
	}

	@Override
	public Map<String, ElasticSearchPartner> harvest() throws IOException, SkipHarvestException
	{
		Map<String, ElasticSearchPartner> partners = elastic.getPartners();
		Map<String, ElasticSearchPartner> tepunaPartners = new TreeMap<String, ElasticSearchPartner>();

		Map<String, String> messages = outlook.getMessages();

		Map<String, ElasticSearchSuspension> suspensions = getSuspensions(messages);

		for (String nuc : suspensions.keySet())
		{
			ElasticSearchSuspension s = suspensions.get(nuc);
			ElasticSearchPartner p = partners.get(nuc);
			p.setStatus(s.getSuspensionStatus());

			Date start = null;
			Date end = null;

			try
			{
				if (s.getSuspensionStart() != null)
				{
					start = idf.parse(s.getSuspensionStart());
					p.setSuspensionStart(sdf.format(start));
				}
			}
			catch (ParseException pe)
			{
				log.warn("can't parse tepuna date[{}]: {}", nuc, s.getSuspensionStart());
			}

			try
			{
				if (s.getSuspensionEnd() != null)
				{
					end = idf.parse(s.getSuspensionEnd());
					p.setSuspensionEnd(sdf.format(end));
				}
			}
			catch (ParseException pe)
			{
				log.warn("can't parse tepuna date[{}]: {}", nuc, s.getSuspensionEnd());
			}

			Date now = new Date();
			if (start != null && end != null)
				if (now.after(start) && now.before(end))
					p.setStatus("suspended");

			tepunaPartners.put(nuc, p);
		}

		return tepunaPartners;
	}

	public Map<String, ElasticSearchSuspension> getSuspensions(Map<String, String> messages)
	{
		log.debug("getting suspensions");

		Map<String, ElasticSearchSuspension> suspensions = new TreeMap<String, ElasticSearchSuspension>();

		for (String id : messages.keySet())
		{
			Matcher m = p.matcher(messages.get(id));
			if (m.find())
			{
				String nuc = NZ_NUC_PREFIX + ":" + m.group(1);

				if ("NO SUSPENSIONS".equals(m.group(2)))
				{
					ElasticSearchSuspension s = new ElasticSearchSuspension();
					s.setStatus("not suspended");
					suspensions.put(nuc, s);
				}
				else if (m.group(2) == null)
				{
					ElasticSearchSuspension s = new ElasticSearchSuspension();
					s.setStatus("suspended");
					s.setSuspensionStart(m.group(3));
					s.setSuspensionEnd(m.group(4));
					s.setSuspensionReason(m.group(6));
					suspensions.put(nuc, s);
				}
			}
		}

		return suspensions;
	}

	@Override
	public Map<String, ElasticSearchPartner> update(Map<String, ElasticSearchPartner> partners,
	                                                Map<String, ElasticSearchPartner> latest,
	                                                List<ElasticSearchChangeRecord> changes)
	{
		Map<String, ElasticSearchPartner> updated = new HashMap<String, ElasticSearchPartner>();

		for (String nuc : latest.keySet())
		{
			ElasticSearchPartner l = latest.get(nuc);
			ElasticSearchPartner p = partners.get(nuc);

			boolean requiresUpdate = false;

			if (!compareStrings(p.getStatus(), l.getStatus()))
			{
				changes.add(new ElasticSearchChangeRecord(SOURCE_SYSTEM, nuc, "status", p.getStatus(), l.getStatus()));
				p.setStatus(l.getStatus());
				requiresUpdate = true;
			}

			if (!compareStrings(p.getSuspensionStart(), l.getSuspensionStart()))
			{
				changes.add(new ElasticSearchChangeRecord(SOURCE_SYSTEM, nuc, "suspension_start",
				                                          p.getSuspensionStart(), l.getSuspensionStart()));
				p.setSuspensionStart(l.getSuspensionStart());
				requiresUpdate = true;
			}

			if (!compareStrings(p.getSuspensionEnd(), l.getSuspensionEnd()))
			{
				changes.add(new ElasticSearchChangeRecord(SOURCE_SYSTEM, nuc, "suspension_end", p.getSuspensionEnd(),
				                                          l.getSuspensionEnd()));
				p.setSuspensionEnd(l.getSuspensionEnd());
				requiresUpdate = true;
			}

			if (requiresUpdate)
			{
				p.setUpdated(sdf.format(new Date()));
				updated.put(nuc, p);
			}
		}

		return updated;
	}
}
