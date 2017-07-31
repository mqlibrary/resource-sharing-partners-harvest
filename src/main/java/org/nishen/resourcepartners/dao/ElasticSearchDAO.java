package org.nishen.resourcepartners.dao;

import java.util.List;

import org.nishen.resourcepartners.entity.ElasticSearchEntity;
import org.nishen.resourcepartners.entity.ElasticSearchPartner;

public interface ElasticSearchDAO
{
	public ElasticSearchPartner getPartner(String id);

	public void saveEntity(ElasticSearchEntity esEntity) throws Exception;

	void saveEntities(List<ElasticSearchEntity> esEntities) throws Exception;
}