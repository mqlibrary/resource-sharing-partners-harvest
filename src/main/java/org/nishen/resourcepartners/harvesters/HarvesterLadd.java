package org.nishen.resourcepartners.harvesters;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.nishen.resourcepartners.dao.ElasticSearchDAO;
import org.nishen.resourcepartners.dao.LaddDAO;
import org.nishen.resourcepartners.entity.ElasticSearchChangeRecord;
import org.nishen.resourcepartners.entity.ElasticSearchPartner;
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

	private LaddDAO ladd;

	private ElasticSearchDAO elastic;

	@Inject
	public HarvesterLadd(LaddDAO ladd, ElasticSearchDAO elastic)
	{
		this.ladd = ladd;
		this.elastic = elastic;

		log.debug("instantiated class: {}", this.getClass().getName());
	}

	@Override
	public List<ElasticSearchPartner> harvest()
	{
		Map<String, ElasticSearchPartner> laddPartners = ladd.getData();

		Map<String, ElasticSearchPartner> esPartners = elastic.getPartners();

		List<ElasticSearchChangeRecord> changeRecords = new ArrayList<ElasticSearchChangeRecord>();

		List<ElasticSearchPartner> entities = new ArrayList<ElasticSearchPartner>();

		for (String nuc : laddPartners.keySet())
		{
			ElasticSearchPartner lp = laddPartners.get(nuc);
			ElasticSearchPartner ep = esPartners.remove(nuc);

			if (ep == null)
			{
				changeRecords.add(new ElasticSearchChangeRecord(nuc, "record created", null, null));
				entities.add(lp);
				log.debug("creating ep: {}", nuc);
			}
			else
			{
				List<ElasticSearchChangeRecord> changes = update(ep, lp);
				if (!changes.isEmpty())
				{
					changeRecords.addAll(changes);
					entities.add(ep);
					log.debug("updating ep: {}", ep);
				}
			}
		}

		for (String nuc : esPartners.keySet())
		{
			ElasticSearchPartner ep = esPartners.get(nuc);
			if (ep.isEnabled())
			{
				ep.setEnabled(false);
				entities.add(ep);
				changeRecords.add(new ElasticSearchChangeRecord(nuc, "record disabled", null, null));
			}
		}

		try
		{
			elastic.addEntities(entities);
			elastic.addEntities(changeRecords);
		}
		catch (Exception e)
		{
			log.warn("could not save entities [ladd]: {}", e.getMessage(), e);
		}

		return entities;
	}

	/*
	 * Update the 'current' object with the 'latest' data.
	 * 
	 */
	public List<ElasticSearchChangeRecord> update(ElasticSearchPartner current, ElasticSearchPartner latest)
	{
		List<ElasticSearchChangeRecord> changes = new ArrayList<ElasticSearchChangeRecord>();

		if (current.isEnabled() != latest.isEnabled())
		{
			ElasticSearchChangeRecord cr =
			        new ElasticSearchChangeRecord(latest.getNuc(), "enabled", Boolean.toString(current.isEnabled()),
			                                      Boolean.toString(latest.isEnabled()));
			changes.add(cr);

			current.setEnabled(latest.isEnabled());
		}

		if (!compareStrings(current.getName(), latest.getName()))
		{
			ElasticSearchChangeRecord cr =
			        new ElasticSearchChangeRecord(latest.getNuc(), "name", current.getName(), latest.getName());
			changes.add(cr);
			current.setName(latest.getName());
		}

		if (!compareStrings(current.getStatus(), latest.getStatus()))
		{
			ElasticSearchChangeRecord cr =
			        new ElasticSearchChangeRecord(latest.getNuc(), "status", current.getStatus(), latest.getStatus());
			changes.add(cr);
			current.setStatus(latest.getStatus());
		}

		if (!compareStrings(current.getSuspensionStart(), latest.getSuspensionStart()))
		{
			ElasticSearchChangeRecord cr =
			        new ElasticSearchChangeRecord(latest.getNuc(), "suspension_start", current.getSuspensionStart(),
			                                      latest.getSuspensionStart());
			changes.add(cr);
			current.setSuspensionStart(latest.getSuspensionStart());
		}

		if (!compareStrings(current.getSuspensionEnd(), latest.getSuspensionEnd()))
		{
			ElasticSearchChangeRecord cr =
			        new ElasticSearchChangeRecord(latest.getNuc(), "suspension_end", current.getSuspensionEnd(),
			                                      latest.getSuspensionEnd());
			changes.add(cr);
			current.setSuspensionEnd(latest.getSuspensionEnd());
		}

		return changes;
	}

	private boolean compareStrings(String a, String b)
	{
		if (a == null && b == null)
			return true;
		else if (a == null)
			return false;
		else if (b == null)
			return false;

		return a.equals(b);
	}
}
