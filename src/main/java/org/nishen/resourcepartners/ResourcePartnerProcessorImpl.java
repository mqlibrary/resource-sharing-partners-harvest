package org.nishen.resourcepartners;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.nishen.resourcepartners.dao.ElasticSearchDAO;
import org.nishen.resourcepartners.entity.ElasticSearchChangeRecord;
import org.nishen.resourcepartners.entity.ElasticSearchPartner;
import org.nishen.resourcepartners.harvesters.Harvester;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.Inject;

/**
 * @author nishen.naidoo
 *
 */

public class ResourcePartnerProcessorImpl implements ResourcePartnerProcessor
{
	private static final Logger log = LoggerFactory.getLogger(ResourcePartnerProcessorImpl.class);

	private ElasticSearchDAO elastic;

	private Set<Harvester> harvesters;

	@Inject
	public ResourcePartnerProcessorImpl(ElasticSearchDAO elastic, Set<Harvester> harvesters)
	{
		this.elastic = elastic;
		this.harvesters = harvesters;

		log.debug("instantiated ResourcePartnerProcessor");
	}

	@Override
	public void process() throws Exception
	{
		Map<String, ElasticSearchPartner> partners = elastic.getPartners();

		for (Harvester harvester : harvesters)
		{
			try
			{
				log.info("harvesting from: {}", harvester.getSource());
				Map<String, ElasticSearchPartner> harvestedPartners = harvester.harvest();

				List<ElasticSearchChangeRecord> changes = new ArrayList<ElasticSearchChangeRecord>();

				log.info("updating partners: {}", harvestedPartners.size());
				Map<String, ElasticSearchPartner> changed = harvester.update(partners, harvestedPartners, changes);
				partners.putAll(changed);
				log.info("partners updated: {}", changed.size());

				log.info("saving elasticsearch entities: {}", changed.size());
				if (changed.size() > 0)
					elastic.addEntities(new ArrayList<ElasticSearchPartner>(changed.values()));
				if (changes.size() > 0)
					elastic.addEntities(changes);
			}
			catch (SkipHarvestException sre)
			{
				log.info("skipping harvesting: {}", harvester.getSource());
			}
		}
	}
}
