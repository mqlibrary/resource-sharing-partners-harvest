package org.nishen.resourcepartners.harvesters;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import org.nishen.resourcepartners.entity.ElasticSearchChangeRecord;
import org.nishen.resourcepartners.entity.ElasticSearchPartner;

public interface Harvester
{
	public Map<String, ElasticSearchPartner> harvest() throws IOException;

	public Map<String, ElasticSearchPartner> update(Map<String, ElasticSearchPartner> partners,
	                                                Map<String, ElasticSearchPartner> latest,
	                                                List<ElasticSearchChangeRecord> changes);
}