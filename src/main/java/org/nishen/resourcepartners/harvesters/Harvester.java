package org.nishen.resourcepartners.harvesters;

import java.util.Map;

import org.nishen.resourcepartners.entity.ElasticSearchPartner;

public interface Harvester
{
	public Map<String, ElasticSearchPartner> harvest();
}