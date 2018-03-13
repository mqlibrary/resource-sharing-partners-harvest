package org.nishen.resourcepartners;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
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
	public void process(Map<String, String> options) throws Exception
	{
		Map<String, ElasticSearchPartner> partners = elastic.getPartners();

		Set<String> harvestersToProcess = new HashSet<String>();

		Set<String> harvestersPresent = new HashSet<String>();
		for (Harvester harvester : harvesters)
			harvestersPresent.add(harvester.getSource());

		Set<String> harvesterSet = null;
		String harvesterOption = options.get("harvesters");
		if (harvesterOption != null)
			harvesterSet = new HashSet<String>(Arrays.asList(harvesterOption.split(",")));

		if (harvesterSet != null && harvesterSet.size() > 0)
		{
			for (String h : harvesterSet)
			{
				if (harvestersPresent.contains(h))
					harvestersToProcess.add(h);
				else
					throw new Exception("invalid harvester specified: " + h);
			}
		}
		else
		{
			harvestersToProcess = new HashSet<String>(harvestersPresent);
		}

		for (Harvester harvester : harvesters)
		{
			if (!harvestersToProcess.contains(harvester.getSource()))
			{
				log.debug("skipping harvester [{}]: harvester filtered out on command line", harvester.getSource());
				continue;
			}

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
