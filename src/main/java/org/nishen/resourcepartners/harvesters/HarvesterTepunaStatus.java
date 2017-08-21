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
import org.nishen.resourcepartners.entity.ElasticSearchPartnerAddress;
import org.nishen.resourcepartners.util.JaxbUtil;
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

		Map<String, Suspension> suspensions = getSuspensions(messages);

		for (String nuc : suspensions.keySet())
		{
			Suspension s = suspensions.get(nuc);
			ElasticSearchPartner p = partners.get(nuc);
			p.setStatus(s.getStatus());

			Date start = null;
			Date end = null;

			try
			{
				if (s.getSuspension_start() != null)
				{
					start = idf.parse(s.getSuspension_start());
					p.setSuspensionStart(sdf.format(start));
				}
			}
			catch (ParseException pe)
			{
				log.warn("can't parse tepuna date[{}]: {}", nuc, s.getSuspension_start());
			}

			try
			{
				if (s.getSuspension_end() != null)
				{
					end = idf.parse(s.getSuspension_end());
					p.setSuspensionEnd(sdf.format(end));
				}
			}
			catch (ParseException pe)
			{
				log.warn("can't parse tepuna date[{}]: {}", nuc, s.getSuspension_end());
			}

			Date now = new Date();
			if (start != null && end != null)
				if (now.after(start) && now.before(end))
					p.setStatus("suspended");

			tepunaPartners.put(nuc, p);
		}

		return tepunaPartners;
	}

	public Map<String, Suspension> getSuspensions(Map<String, String> messages)
	{
		log.debug("getting suspensions");

		Map<String, Suspension> suspensions = new TreeMap<String, Suspension>();

		for (String id : messages.keySet())
		{
			Matcher m = p.matcher(messages.get(id));
			if (m.find())
			{
				String nuc = NZ_NUC_PREFIX + ":" + m.group(1);

				if ("NO SUSPENSIONS".equals(m.group(2)))
				{
					Suspension s = new Suspension();
					s.setStatus("not suspended");
					suspensions.put(nuc, s);
				}
				else if (m.group(2) == null)
				{
					Suspension s = new Suspension();
					s.setStatus("suspended");
					s.setSuspension_start(m.group(3));
					s.setSuspension_end(m.group(4));
					s.setSuspension_reason(m.group(6));
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

	private class Suspension
	{
		private String status;
		private String suspension_start;
		private String suspension_end;
		private String suspension_reason;

		public String getStatus()
		{
			return status;
		}

		public void setStatus(String status)
		{
			this.status = status;
		}

		public String getSuspension_start()
		{
			return suspension_start;
		}

		public void setSuspension_start(String suspension_start)
		{
			this.suspension_start = suspension_start;
		}

		public String getSuspension_end()
		{
			return suspension_end;
		}

		public void setSuspension_end(String suspension_end)
		{
			this.suspension_end = suspension_end;
		}

		public String getSuspension_reason()
		{
			return suspension_reason;
		}

		public void setSuspension_reason(String suspension_reason)
		{
			this.suspension_reason = suspension_reason;
		}

		@Override
		public int hashCode()
		{
			final int prime = 31;
			int result = 1;
			result = prime * result + getOuterType().hashCode();
			result = prime * result + ((status == null) ? 0 : status.hashCode());
			result = prime * result + ((suspension_end == null) ? 0 : suspension_end.hashCode());
			result = prime * result + ((suspension_reason == null) ? 0 : suspension_reason.hashCode());
			result = prime * result + ((suspension_start == null) ? 0 : suspension_start.hashCode());
			return result;
		}

		@Override
		public boolean equals(Object obj)
		{
			if (this == obj)
				return true;
			if (obj == null)
				return false;
			if (getClass() != obj.getClass())
				return false;
			Suspension other = (Suspension) obj;
			if (!getOuterType().equals(other.getOuterType()))
				return false;
			if (status == null)
			{
				if (other.status != null)
					return false;
			}
			else if (!status.equals(other.status))
				return false;
			if (suspension_end == null)
			{
				if (other.suspension_end != null)
					return false;
			}
			else if (!suspension_end.equals(other.suspension_end))
				return false;
			if (suspension_reason == null)
			{
				if (other.suspension_reason != null)
					return false;
			}
			else if (!suspension_reason.equals(other.suspension_reason))
				return false;
			if (suspension_start == null)
			{
				if (other.suspension_start != null)
					return false;
			}
			else if (!suspension_start.equals(other.suspension_start))
				return false;
			return true;
		}

		private HarvesterTepunaStatus getOuterType()
		{
			return HarvesterTepunaStatus.this;
		}

		@Override
		public String toString()
		{
			StringBuilder builder = new StringBuilder();
			builder.append("Suspension [status=");
			builder.append(status);
			builder.append(", suspension_start=");
			builder.append(suspension_start);
			builder.append(", suspension_end=");
			builder.append(suspension_end);
			builder.append(", suspension_reason=");
			builder.append(suspension_reason);
			builder.append("]");
			return builder.toString();
		}
	}
}
