package org.nishen.resourcepartners.harvesters;

import java.util.ArrayList;
import java.util.HashMap;
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

		List<ElasticSearchPartner> entities = new ArrayList<ElasticSearchPartner>();

		Map<String, List<ElasticSearchChangeRecord>> changeRecords = new HashMap<String, List<ElasticSearchChangeRecord>>();
		for (String nuc : laddPartners.keySet())
		{
			List<ElasticSearchChangeRecord> changes = new ArrayList<ElasticSearchChangeRecord>();

			ElasticSearchPartner lp = laddPartners.get(nuc);
			ElasticSearchPartner ep = esPartners.remove(nuc);

			if (ep == null)
			{
				entities.add(lp);
				ElasticSearchChangeRecord cr = new ElasticSearchChangeRecord(nuc, "record created", null, null);
				changes.add(cr);
			}
			else
			{
				changes.addAll(update(ep, lp));
				if (!changes.isEmpty())
				{
					entities.add(ep);
					log.debug("updating ep: {}", ep);
				}
			}

			changeRecords.put(nuc, changes);
		}

		try
		{
			elastic.addEntities(entities);
			elastic.delEntities(esPartners.values());
		}
		catch (Exception e)
		{
			log.warn("could not save entities: ladd");
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

		if (!compareStrings(current.getName(), latest.getName()))
		{
			ElasticSearchChangeRecord cr = new ElasticSearchChangeRecord(latest.getNuc(), "name", current.getName(), latest.getName());
			changes.add(cr);
			current.setName(latest.getName());
		}

		if (!compareStrings(current.getStatus(), latest.getStatus()))
		{
			ElasticSearchChangeRecord cr = new ElasticSearchChangeRecord(latest.getNuc(), "status", current.getStatus(), latest.getStatus());
			changes.add(cr);
			current.setStatus(latest.getStatus());
		}

		if (!compareStrings(current.getSuspensionStart(), latest.getSuspensionStart()))
		{
			ElasticSearchChangeRecord cr = new ElasticSearchChangeRecord(latest.getNuc(), "suspension_start", current.getSuspensionStart(),
			                                   latest.getSuspensionStart());
			changes.add(cr);
			current.setSuspensionStart(latest.getSuspensionStart());
		}

		if (!compareStrings(current.getSuspensionEnd(), latest.getSuspensionEnd()))
		{
			ElasticSearchChangeRecord cr = new ElasticSearchChangeRecord(latest.getNuc(), "suspension_end", current.getSuspensionEnd(),
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
