package org.nishen.resourcepartners.dao;

import java.util.List;

import org.nishen.resourcepartners.entity.ElasticSearchEntity;

public interface ElasticSearchDAO
{
	void saveData(List<? extends ElasticSearchEntity> esEntities) throws Exception;
}