package org.nishen.resourcepartners.dao;

import java.io.IOException;
import java.util.Collection;
import java.util.Map;
import java.util.Optional;

import org.nishen.resourcepartners.entity.BaseEntity;
import org.nishen.resourcepartners.entity.ResourcePartner;

public interface DatastoreDAO
{
	public Optional<ResourcePartner> getPartner(String id) throws IOException;

	public Map<String, ResourcePartner> getPartners() throws IOException;

	public void addEntity(BaseEntity esEntity) throws IOException;

	public void addEntities(Collection<? extends BaseEntity> esEntities) throws IOException;

	public void delEntity(BaseEntity esEntity) throws IOException;

	public void delEntities(Collection<? extends BaseEntity> esEntities) throws IOException;
}