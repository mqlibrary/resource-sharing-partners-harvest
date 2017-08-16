package org.nishen.resourcepartners;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.nishen.resourcepartners.dao.ElasticSearchDAO;
import org.nishen.resourcepartners.entity.ElasticSearchChangeRecord;
import org.nishen.resourcepartners.entity.ElasticSearchPartner;
import org.nishen.resourcepartners.harvesters.Harvester;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.Inject;
import com.google.inject.name.Named;

/**
 * @author nishen.naidoo
 *
 */

public class ResourcePartnerProcessorImpl implements ResourcePartnerProcessor
{
	private static final Logger log = LoggerFactory.getLogger(ResourcePartnerProcessorImpl.class);

	private ElasticSearchDAO elastic;

	private Harvester laddHarvester;
	private Harvester ilrsHarvester;
	private Harvester tepunaHarvester;

	@Inject
	public ResourcePartnerProcessorImpl(ElasticSearchDAO elastic, @Named("harvester.ladd") Harvester laddHarvester,
	                                    @Named("harvester.ilrs") Harvester ilrsHarvester,
	                                    @Named("harvester.tepuna") Harvester tepunaHarvester)
	{
		this.elastic = elastic;

		this.laddHarvester = laddHarvester;
		this.ilrsHarvester = ilrsHarvester;
		this.tepunaHarvester = tepunaHarvester;

		log.debug("instantiated ResourcePartnerProcessor");
	}

	@Override
	public void process() throws Exception
	{
		List<ElasticSearchChangeRecord> changes = new ArrayList<ElasticSearchChangeRecord>();

		Map<String, ElasticSearchPartner> partners = elastic.getPartners();

		Map<String, ElasticSearchPartner> changedPartners = null;
		Map<String, ElasticSearchPartner> harvestedPartners = null;

		harvestedPartners = laddHarvester.harvest();
		changedPartners = laddHarvester.update(partners, harvestedPartners, changes);
		partners.putAll(changedPartners);

		elastic.addEntities(new ArrayList<ElasticSearchPartner>(changedPartners.values()));
		elastic.addEntities(changes);
	}
}
