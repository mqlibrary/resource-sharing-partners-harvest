package org.nishen.resourcepartners.dao;

import java.util.List;
import java.util.Map;

import org.nishen.resourcepartners.entity.ElasticSearchEntity;
import org.nishen.resourcepartners.entity.ElasticSearchPartner;

public interface LaddDAO
{
	public ElasticSearchPartner getPartner(String id);

	public Map<String, ElasticSearchPartner> getPartners();

	public void saveEntity(ElasticSearchEntity esEntity) throws Exception;

	void saveEntities(List<? extends ElasticSearchEntity> esEntities) throws Exception;
}