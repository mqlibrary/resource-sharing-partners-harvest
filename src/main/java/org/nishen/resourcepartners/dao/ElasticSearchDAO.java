package org.nishen.resourcepartners.dao;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.nishen.resourcepartners.entity.ElasticSearchEntity;
import org.nishen.resourcepartners.entity.ElasticSearchPartner;

public interface ElasticSearchDAO
{
	public Optional<ElasticSearchPartner> getPartner(String id);

	public Map<String, ElasticSearchPartner> getPartners();

	public void saveEntity(ElasticSearchEntity esEntity) throws Exception;

	void saveEntities(List<? extends ElasticSearchEntity> esEntities) throws Exception;
}