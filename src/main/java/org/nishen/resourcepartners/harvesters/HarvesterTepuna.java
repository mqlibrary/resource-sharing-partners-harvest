package org.nishen.resourcepartners.harvesters;

import java.util.List;
import java.util.Map;

import org.nishen.resourcepartners.dao.TepunaDAO;
import org.nishen.resourcepartners.entity.ElasticSearchChangeRecord;
import org.nishen.resourcepartners.entity.ElasticSearchPartner;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.Inject;

/**
 * @author nishen.naidoo
 *
 */
public class HarvesterTepuna implements Harvester
{
	private static final Logger log = LoggerFactory.getLogger(HarvesterTepuna.class);

	private TepunaDAO tepuna;

	@Inject
	public HarvesterTepuna(TepunaDAO tepuna)
	{
		this.tepuna = tepuna;

		log.debug("instantiated class: {}", this.getClass().getName());
	}

	@Override
	public Map<String, ElasticSearchPartner> harvest()
	{
		Map<String, ElasticSearchPartner> tepunaPartners = tepuna.getData();

		return tepunaPartners;
	}

	@Override
	public Map<String, ElasticSearchPartner> update(Map<String, ElasticSearchPartner> partners,
	                                                Map<String, ElasticSearchPartner> latest,
	                                                List<ElasticSearchChangeRecord> changes)
	{
		return null;
	}

}
