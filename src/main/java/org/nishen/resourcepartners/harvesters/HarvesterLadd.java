package org.nishen.resourcepartners.harvesters;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.nishen.resourcepartners.dao.LaddDAO;
import org.nishen.resourcepartners.entity.ElasticSearchChangeRecord;
import org.nishen.resourcepartners.entity.ElasticSearchPartner;
import org.nishen.resourcepartners.entity.ElasticSearchSuspension;
import org.nishen.resourcepartners.util.JaxbUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.Inject;

/**
 * @author nishen
 *
 */
public class HarvesterLadd implements Harvester
{
	private static final Logger log = LoggerFactory.getLogger(HarvesterLadd.class);

	private static final String SOURCE_SYSTEM = "LADD";

	private static final SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ");

	private LaddDAO ladd;

	@Inject
	public HarvesterLadd(LaddDAO ladd)
	{
		this.ladd = ladd;

		log.debug("instantiated class: {}", this.getClass().getName());
	}

	@Override
	public String getSource()
	{
		return SOURCE_SYSTEM;
	}

	@Override
	public Map<String, ElasticSearchPartner> harvest()
	{
		return ladd.getData();
	}

	@Override
	public Map<String, ElasticSearchPartner> update(Map<String, ElasticSearchPartner> partners,
	                                                Map<String, ElasticSearchPartner> latest,
	                                                List<ElasticSearchChangeRecord> changes)
	{
		Map<String, ElasticSearchPartner> updated = new HashMap<String, ElasticSearchPartner>();

		List<String> removeList = new ArrayList<String>();
		for (String s : partners.keySet())
			if (!s.startsWith(NZ_NUC_PREFIX))
				removeList.add(s);

		for (String nuc : latest.keySet())
		{
			removeList.remove(nuc);

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

			Set<ElasticSearchSuspension> lSuspensions = new LinkedHashSet<ElasticSearchSuspension>(l.getSuspensions());
			for (ElasticSearchSuspension s : lSuspensions)
				if (!p.getSuspensions().contains(s))
				{
					changes.add(new ElasticSearchChangeRecord(SOURCE_SYSTEM, nuc, "suspension", null,
					                                          JaxbUtil.format(s)));
					p.getSuspensions().add(s);
					requiresUpdate = true;
				}

			if (requiresUpdate)
			{
				if (log.isDebugEnabled())
				{
					log.debug("latest:\n{}\npartner:\n{}\n", JaxbUtil.format(l), JaxbUtil.format(p));
				}

				p.setUpdated(sdf.format(new Date()));
				updated.put(nuc, p);
			}
		}

		for (String nuc : removeList)
		{
			ElasticSearchPartner p = partners.get(nuc);
			if (p != null && p.isEnabled())
			{
				p.setUpdated(sdf.format(new Date()));
				p.setEnabled(false);
				updated.put(nuc, p);
			}
		}

		return updated;
	}
}
