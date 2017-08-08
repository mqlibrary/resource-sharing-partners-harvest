package org.nishen.resourcepartners.harvesters;

import java.util.List;

import org.nishen.resourcepartners.entity.ElasticSearchPartner;

public interface Harvester
{
	public List<ElasticSearchPartner> harvest();
}