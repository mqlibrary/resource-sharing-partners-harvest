package org.nishen.resourcepartners.harvesters;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.nishen.resourcepartners.dao.LaddDAO;
import org.nishen.resourcepartners.entity.ElasticSearchChangeRecord;
import org.nishen.resourcepartners.entity.ElasticSearchPartner;
import org.nishen.resourcepartners.util.JaxbUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.Inject;

/**
 * @author nishen.naidoo
 *
 */
public class HarvesterLadd implements Harvester
{
	private static final Logger log = LoggerFactory.getLogger(HarvesterLadd.class);

	private static final SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ");

	private static final String SOURCE_SYSTEM = "LADD";

	private LaddDAO ladd;

	@Inject
	public HarvesterLadd(LaddDAO ladd)
	{
		this.ladd = ladd;

		log.debug("instantiated class: {}", this.getClass().getName());
	}

	@Override
	public Map<String, ElasticSearchPartner> harvest()
	{
		Map<String, ElasticSearchPartner> laddPartners = ladd.getData();

		return laddPartners;
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

			if (p == null)
			{
				l.setUpdated(sdf.format(new Date()));
				updated.put(nuc, l);
				changes.add(new ElasticSearchChangeRecord(SOURCE_SYSTEM, nuc, "partner", null, JaxbUtil.format(l)));

				continue;
			}

			if (p.isEnabled() != l.isEnabled())
			{
				changes.add(new ElasticSearchChangeRecord(SOURCE_SYSTEM, nuc, "enabled",
				                                          Boolean.toString(p.isEnabled()),
				                                          Boolean.toString(l.isEnabled())));
				p.setEnabled(l.isEnabled());
				requiresUpdate = true;
			}

			if (!compareStrings(p.getName(), l.getName()))
			{
				changes.add(new ElasticSearchChangeRecord(SOURCE_SYSTEM, nuc, "name", p.getName(), l.getName()));
				p.setName(l.getName());
				requiresUpdate = true;
			}

			if (!compareStrings(p.getStatus(), l.getStatus()))
			{
				changes.add(new ElasticSearchChangeRecord(SOURCE_SYSTEM, nuc, "status", p.getStatus(), l.getStatus()));
				p.setStatus(l.getStatus());
				requiresUpdate = true;
			}

			if (!compareStrings(p.getEmailMain(), l.getEmailMain()))
			{
				changes.add(new ElasticSearchChangeRecord(SOURCE_SYSTEM, nuc, "email_main", p.getEmailMain(),
				                                          l.getEmailMain()));
				p.setEmailMain(l.getEmailMain());
				requiresUpdate = true;
			}

			if (!compareStrings(p.getEmailIll(), l.getEmailIll()))
			{
				changes.add(new ElasticSearchChangeRecord(SOURCE_SYSTEM, nuc, "email_ill", p.getEmailIll(),
				                                          l.getEmailIll()));
				p.setEmailIll(l.getEmailIll());
				requiresUpdate = true;
			}

			if (!compareStrings(p.getPhoneMain(), l.getPhoneMain()))
			{
				changes.add(new ElasticSearchChangeRecord(SOURCE_SYSTEM, nuc, "phone_main", p.getPhoneMain(),
				                                          l.getPhoneMain()));
				p.setPhoneMain(l.getPhoneMain());
				requiresUpdate = true;
			}

			if (!compareStrings(p.getPhoneIll(), l.getPhoneIll()))
			{
				changes.add(new ElasticSearchChangeRecord(SOURCE_SYSTEM, nuc, "phone_ill", p.getPhoneIll(),
				                                          l.getPhoneIll()));
				p.setPhoneIll(l.getPhoneIll());
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

	private boolean compareStrings(String a, String b)
	{
		if (a == null && b == null)
			return true;

		if (a == null || b == null)
			return false;

		return a.equals(b);
	}
}
