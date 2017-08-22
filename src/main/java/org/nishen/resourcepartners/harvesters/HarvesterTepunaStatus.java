package org.nishen.resourcepartners.harvesters;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.nishen.resourcepartners.SkipHarvestException;
import org.nishen.resourcepartners.dao.ElasticSearchDAO;
import org.nishen.resourcepartners.dao.OutlookDAO;
import org.nishen.resourcepartners.entity.ElasticSearchChangeRecord;
import org.nishen.resourcepartners.entity.ElasticSearchPartner;
import org.nishen.resourcepartners.entity.ElasticSearchSuspension;
import org.nishen.resourcepartners.util.JaxbUtil;
import org.nishen.resourcepartners.util.ObjectUtil;
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

		Map<String, Set<ElasticSearchSuspension>> suspensions = getSuspensions(messages);

		for (String nuc : suspensions.keySet())
		{
			Set<ElasticSearchSuspension> s = suspensions.get(nuc);
			ElasticSearchPartner p = partners.get(nuc);
			ElasticSearchPartner l = ObjectUtil.deepClone(p);

			for (ElasticSearchSuspension suspension : s)
				l.getSuspensions().add(suspension);

			Date now = new Date();

			for (ElasticSearchSuspension susp : l.getSuspensions())
			{
				if (ElasticSearchSuspension.SUSPENDED.equals(susp.getSuspensionStatus()))
				{
					try
					{
						Date start = sdf.parse(susp.getSuspensionStart());
						Date end = sdf.parse(susp.getSuspensionEnd());

						if (now.after(start) && now.before(end))
							l.setStatus(ElasticSearchSuspension.SUSPENDED);
					}
					catch (ParseException pe)
					{
						log.error("issue parsing date. This should not have happened: [{}] [{}]",
						          susp.getSuspensionStart(), susp.getSuspensionEnd());
					}
				}
				else if (ElasticSearchSuspension.NOT_SUSPENDED.equals(susp.getSuspensionStatus()))
				{
					l.setStatus(ElasticSearchSuspension.NOT_SUSPENDED);
				}
			}

			tepunaPartners.put(nuc, l);
		}

		return tepunaPartners;
	}

	public Map<String, Set<ElasticSearchSuspension>> getSuspensions(Map<String, String> messages)
	{
		log.debug("getting suspensions");

		Map<String, Set<ElasticSearchSuspension>> suspensions = new TreeMap<String, Set<ElasticSearchSuspension>>();

		for (String id : messages.keySet())
		{
			Matcher m = p.matcher(messages.get(id));
			if (m.find())
			{
				String nuc = NZ_NUC_PREFIX + ":" + m.group(1);

				if (suspensions.get(nuc) == null)
					suspensions.put(nuc, new LinkedHashSet<ElasticSearchSuspension>());

				if ("NO SUSPENSIONS".equals(m.group(2)))
				{
					ElasticSearchSuspension s = new ElasticSearchSuspension();
					s.setSuspensionStatus(ElasticSearchSuspension.NOT_SUSPENDED);

					suspensions.get(nuc).add(s);
				}
				else if (m.group(2) == null)
				{
					ElasticSearchSuspension s = new ElasticSearchSuspension();
					s.setSuspensionStatus(ElasticSearchSuspension.SUSPENDED);
					s.setSuspensionStart(formatDate(nuc, m.group(3)));
					s.setSuspensionEnd(formatDate(nuc, m.group(4)));
					s.setSuspensionReason(m.group(6));

					suspensions.get(nuc).add(s);
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

			for (ElasticSearchSuspension s : l.getSuspensions())
			{
				if (!p.getSuspensions().contains(s))
				{
					changes.add(new ElasticSearchChangeRecord(SOURCE_SYSTEM, nuc, "suspension", null,
					                                          JaxbUtil.format(s)));
					p.getSuspensions().add(s);
					requiresUpdate = true;
				}
			}

			if (requiresUpdate)
			{
				p.setUpdated(sdf.format(new Date()));
				updated.put(nuc, p);
			}
		}

		return updated;
	}

	private String formatDate(String nuc, String date)
	{
		String result = null;

		if (date != null)
		{
			try
			{
				Date d = idf.parse(date);
				result = sdf.format(d);
			}
			catch (ParseException pe)
			{
				log.warn("can't parse tepuna date[{}]: {}", nuc, date);
			}
		}

		return result;
	}
}
