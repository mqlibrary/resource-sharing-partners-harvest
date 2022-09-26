package org.nishen.resourcepartners.harvesters;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.nishen.resourcepartners.SkipHarvestException;
import org.nishen.resourcepartners.dao.DatastoreDAO;
import org.nishen.resourcepartners.dao.OutlookDAO;
import org.nishen.resourcepartners.entity.ResourcePartner;
import org.nishen.resourcepartners.entity.ResourcePartnerChangeRecord;
import org.nishen.resourcepartners.entity.ResourcePartnerSuspension;
import org.nishen.resourcepartners.util.ObjectUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.inject.Inject;

/**
 * @author nishen
 *
 */
public class HarvesterTepunaStatus implements Harvester
{
	private static final Logger log = LoggerFactory.getLogger(HarvesterTepunaStatus.class);

	private static final String SOURCE_SYSTEM = "OUTLOOK";

	private static final SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ", Locale.ENGLISH);

	private static final SimpleDateFormat idf = new SimpleDateFormat("dd-MMM-yy", Locale.ENGLISH);

	private static ObjectMapper om = new ObjectMapper();

	private static Pattern pHeader;

	private static Pattern pBody;

	static
	{
		String regexHeader = "";
		regexHeader += "STATUS (\\w+?) NLNZ BEGIN SUSPENSION LIST\\s+";
		regexHeader += "(.+)";
		regexHeader += "STATUS \\w+ NLNZ END SUSPENSION LIST";

		String regexBody = "";
		regexBody += "STATUS \\w+ NLNZ (?:(NO SUSPENSIONS)|(\\d\\d-\\w\\w\\w-\\d\\d) " +
		             "(\\d\\d-\\w\\w\\w-\\d\\d) (\\w+) (\\w+))\\s+";

		pHeader = Pattern.compile(regexHeader, Pattern.CASE_INSENSITIVE | Pattern.DOTALL);
		pBody = Pattern.compile(regexBody, Pattern.CASE_INSENSITIVE | Pattern.DOTALL);
	}

	private OutlookDAO outlook;

	private DatastoreDAO datastoreDAO;

	@Inject
	public HarvesterTepunaStatus(OutlookDAO outlook, DatastoreDAO datastoreDAO)
	{
		this.outlook = outlook;

		this.datastoreDAO = datastoreDAO;

		log.debug("instantiated class: {}", this.getClass().getName());
	}

	@Override
	public String getSource()
	{
		return SOURCE_SYSTEM;
	}

	@Override
	public Map<String, ResourcePartner> harvest() throws IOException, SkipHarvestException
	{
		Map<String, ResourcePartner> partners = datastoreDAO.getPartners();
		Map<String, ResourcePartner> tepunaPartners = new TreeMap<String, ResourcePartner>();

		Map<String, JsonNode> messages = outlook.getMessages();

		Map<String, Set<ResourcePartnerSuspension>> suspensions = getSuspensions(messages);

		Date now = new Date();

		for (String nuc : suspensions.keySet())
		{
			Set<ResourcePartnerSuspension> s = suspensions.get(nuc);
			ResourcePartner p = partners.get(nuc);

			if (p == null)
			{
				log.warn("processing suspensions and partner does not exist: {}", nuc);
				continue;
			}

			ResourcePartner l = ObjectUtil.deepClone(p);

			l.getSuspensions().addAll(s);

			// initial state
			l.setStatus(ResourcePartnerSuspension.NOT_SUSPENDED);

			// process all suspensions in order - last one is the latest
			for (ResourcePartnerSuspension susp : l.getSuspensions())
			{
				if (ResourcePartnerSuspension.SUSPENDED.equals(susp.getSuspensionStatus()))
				{
					try
					{
						Date start = sdf.parse(susp.getSuspensionStart());
						Date end = sdf.parse(susp.getSuspensionEnd());

						if (now.after(start) && now.before(end))
							l.setStatus(ResourcePartnerSuspension.SUSPENDED);
					}
					catch (ParseException pe)
					{
						log.error("issue parsing date. This should not have happened: [{}] [{}]",
						          susp.getSuspensionStart(), susp.getSuspensionEnd());
					}
				}
				else
				{
					l.setStatus(ResourcePartnerSuspension.NOT_SUSPENDED);
				}
			}

			tepunaPartners.put(nuc, l);
		}

		outlook.markMessagesProcessed(messages);

		return tepunaPartners;
	}

	@Override
	public Map<String, ResourcePartner> update(Map<String, ResourcePartner> partners,
	                                           Map<String, ResourcePartner> latest,
	                                           List<ResourcePartnerChangeRecord> changes)
	{
		Map<String, ResourcePartner> updated = new HashMap<String, ResourcePartner>();

		for (String nuc : latest.keySet())
		{
			ResourcePartner p = partners.get(nuc);
			ResourcePartner l = latest.get(nuc);

			boolean requiresUpdate = false;

			for (ResourcePartnerSuspension s : l.getSuspensions())
			{
				try
				{
					if (!p.getSuspensions().contains(s))
					{
						changes.add(new ResourcePartnerChangeRecord(SOURCE_SYSTEM, nuc, "suspension", null,
						                                            om.writeValueAsString(s)));
						p.getSuspensions().add(s);
						p.setStatus(l.getStatus());
						requiresUpdate = true;
					}
				}
				catch (JsonProcessingException jpe)
				{
					log.error("{}", jpe.getMessage(), jpe);
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

	public Map<String, Set<ResourcePartnerSuspension>> getSuspensions(Map<String, JsonNode> messages)
	{
		log.debug("getting suspensions");

		Map<String, Set<ResourcePartnerSuspension>> suspensions = new TreeMap<String, Set<ResourcePartnerSuspension>>();

		for (String id : messages.keySet())
		{
			JsonNode entry = messages.get(id);

			String content = entry.get("body").get("content").asText().replace("\\r\\n", "\n");

			Matcher m = pHeader.matcher(content);

			String nuc = null;
			String body = null;
			if (m.find())
			{
				nuc = NZ_NUC_PREFIX + ":" + m.group(1);
				body = m.group(2);

				log.debug("regex [{}]: {}", nuc, body);
			}

			if (nuc == null || body == null)
			{
				log.debug("no applicable content: {}", content);
				continue;
			}

			if (suspensions.get(nuc) == null)
				suspensions.put(nuc, new LinkedHashSet<ResourcePartnerSuspension>());

			m = pBody.matcher(body);
			while (m.find())
			{
				if (log.isDebugEnabled())
					for (int x = 0; x <= m.groupCount(); x++)
						log.debug("found[{}]: {}", x, m.group(x));

				if ("NO SUSPENSIONS".equals(m.group(1)))
				{
					ResourcePartnerSuspension s = new ResourcePartnerSuspension();
					s.setSuspensionAdded(entry.get("receivedDateTime").asText());
					s.setSuspensionStatus(ResourcePartnerSuspension.NOT_SUSPENDED);

					suspensions.get(nuc).add(s);
				}
				else if (m.group(1) == null)
				{
					ResourcePartnerSuspension s = new ResourcePartnerSuspension();
					s.setSuspensionAdded(entry.get("receivedDateTime").asText());
					s.setSuspensionStatus(ResourcePartnerSuspension.SUSPENDED);
					s.setSuspensionStart(formatDate(nuc, m.group(2)));
					s.setSuspensionEnd(formatDate(nuc, m.group(3)));
					s.setSuspensionCode(m.group(4));
					s.setSuspensionReason(m.group(4));

					suspensions.get(nuc).add(s);
				}
			}
		}

		return suspensions;
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
