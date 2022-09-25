package org.nishen.resourcepartners.dao;

import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import org.nishen.resourcepartners.entity.BaseEntity;
import org.nishen.resourcepartners.entity.ResourcePartner;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.google.inject.Inject;
import com.google.inject.name.Named;

public class DatastoreDAOImpl implements DatastoreDAO
{
	private static final Logger log = LoggerFactory.getLogger(DatastoreDAOImpl.class);

	private ObjectMapper om;

	private String dataFolder;

	@Inject
	public DatastoreDAOImpl(@Named("location.partners") String dataFolder)
	{
		this.dataFolder = dataFolder;
		this.om = new ObjectMapper().enable(SerializationFeature.INDENT_OUTPUT);
	}

	@Override
	public Optional<ResourcePartner> getPartner(String id) throws IOException
	{
		String filename = dataFolder + File.separatorChar + makeFilenameSafe(id).toUpperCase() + ".json";
		log.debug("getting partner: {}", filename);
		ResourcePartner p = om.readValue(new File(filename), ResourcePartner.class);

		return Optional.ofNullable(p);
	}

	@Override
	public Map<String, ResourcePartner> getPartners() throws IOException
	{
		File folder = new File(this.dataFolder);

		Map<String, ResourcePartner> partners = new HashMap<>();
		for (File f : folder.listFiles())
		{
			if (!f.getName().toLowerCase().endsWith(".json"))
				continue;

			String id = makeNucFromFilename(f.getName()).replace(".json", "");
			ResourcePartner partner = getPartner(id).get();
			partners.put(id, partner);
		}

		return partners;
	}

	@Override
	public void addEntity(BaseEntity esEntity) throws IOException
	{
		String filename = this.dataFolder + File.separatorChar + makeFilenameSafe(esEntity.getEntityId()) + ".json";
		om.writeValue(new File(filename), esEntity);
	}

	@Override
	public void addEntities(Collection<? extends BaseEntity> esEntities) throws IOException
	{
		for (BaseEntity e : esEntities)
			addEntity(e);
	}

	@Override
	public void delEntity(BaseEntity esEntity) throws IOException
	{
		String filename = this.dataFolder + File.separatorChar + makeFilenameSafe(esEntity.getEntityId()) + ".json";
		File f = new File(filename);
		f.delete();
	}

	@Override
	public void delEntities(Collection<? extends BaseEntity> esEntities) throws IOException
	{
		for (BaseEntity e : esEntities)
			delEntity(e);
	}

	private static String makeFilenameSafe(String s)
	{
		return s.replace(':', '_');
	}

	private static String makeNucFromFilename(String s)
	{
		return s.replace('_', ':');
	}
}
