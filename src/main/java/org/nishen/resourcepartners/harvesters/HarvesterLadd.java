package org.nishen.resourcepartners.harvesters;

import java.util.Map;

import org.nishen.resourcepartners.dao.LaddDAO;
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
}
