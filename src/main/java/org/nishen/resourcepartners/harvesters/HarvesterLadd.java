package org.nishen.resourcepartners.harvesters;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.nishen.resourcepartners.dao.ElasticSearchDAO;
import org.nishen.resourcepartners.dao.LaddDAO;
import org.nishen.resourcepartners.entity.ElasticSearchPartner;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.Inject;

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
	public void harvest()
	{
		Map<String, ElasticSearchPartner> laddPartners = ladd.getData();

		Map<String, ElasticSearchPartner> esPartners = elastic.getPartners();

		List<ElasticSearchPartner> entities = new ArrayList<ElasticSearchPartner>();

		for (String nuc : laddPartners.keySet())
		{
			ElasticSearchPartner lp = laddPartners.get(nuc);
			ElasticSearchPartner ep = esPartners.remove(nuc);

			if (!compare(lp, ep))
				entities.add(lp);
		}

		for (ElasticSearchPartner e : entities)
			log.debug("e: {}", e);

		try
		{
			elastic.saveEntities(entities);
		}
		catch (Exception e)
		{
			log.warn("could not save entities: ladd");
		}
	}

	private boolean compare(ElasticSearchPartner a, ElasticSearchPartner b)
	{
		if (a == null && b == null)
			return true;

		if (a == null && b != null)
			return false;

		if (a != null && b == null)
			return false;

		if (!compareStrings(a.getNuc(), b.getNuc()))
			return false;

		if (!compareStrings(a.getStatus(), b.getStatus()))
			return false;

		if (!compareStrings(a.getStatus(), b.getStatus()))
			return false;

		if (!compareStrings(a.getSuspensionStart(), b.getSuspensionStart()))
			return false;

		if (!compareStrings(a.getSuspensionEnd(), b.getSuspensionEnd()))
			return false;

		return true;
	}

	private boolean compareStrings(String a, String p)
	{
		if (a == null && p == null)
			return true;

		if (a != null && p != null)
		{
			if (!a.equals(p))
				return false;
		}
		else if (a == null)
		{
			return false;
		}
		else if (p == null)
		{
			return false;
		}

		return true;
	}
}
