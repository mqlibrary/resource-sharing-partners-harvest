package org.nishen.resourcepartners.dao;

import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.Set;

import javax.ws.rs.client.Entity;
import javax.ws.rs.client.Invocation.Builder;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.PropertyException;

import org.eclipse.persistence.jaxb.MarshallerProperties;
import org.nishen.resourcepartners.entity.ElasticSearchEntity;
import org.nishen.resourcepartners.entity.ElasticSearchPartner;
import org.nishen.resourcepartners.util.DataUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.google.inject.Inject;
import com.google.inject.Provider;
import com.google.inject.name.Named;

public class ElasticSearchDAOImpl implements ElasticSearchDAO
{
	private static final Logger log = LoggerFactory.getLogger(ElasticSearchDAOImpl.class);
	
	private Map<String, Map<String, Marshaller>> marshallers;

	private Set<String> indices;

	private WebTarget elasticTarget;

	private ObjectMapper om;

	@Inject
	public ElasticSearchDAOImpl(@Named("ws.elastic") Provider<WebTarget> elasticTargetProvider)
	{
		this.marshallers = new HashMap<String, Map<String, Marshaller>>();

		this.elasticTarget = elasticTargetProvider.get();

		this.indices = new HashSet<String>();

		this.om = new ObjectMapper();
	}

	public ElasticSearchPartner getPartner(String id)
	{
		WebTarget t = elasticTarget.path("partners").path("partner").path(id).path("_source");

		ElasticSearchPartner partner = t.request().accept(MediaType.APPLICATION_JSON).get(ElasticSearchPartner.class);

		return partner;
	}

	public Map<String, ElasticSearchPartner> getPartners()
	{
		Map<String, ElasticSearchPartner> partners = new HashMap<String, ElasticSearchPartner>();

		WebTarget t = elasticTarget.path("partners").path("partner").path("_search");

		String result = t.request().accept(MediaType.APPLICATION_JSON).get(String.class);

		try
		{
			JsonNode root = om.readTree(result);
			log.debug("search result: {}", result);
			ArrayNode hitlist = (ArrayNode) root.get("hits").get("hits");
			log.debug("hitlist -> {}", hitlist.get(0).getNodeType());
		}
		catch (IOException ioe)
		{
			log.error("failed to parse json search results: {}", ioe.getMessage(), ioe);
		}

		return partners;
	}

	@Override
	public void saveEntity(ElasticSearchEntity esEntity) throws Exception
	{
		List<ElasticSearchEntity> esEntities = new ArrayList<>();
		esEntities.add(esEntity);
		saveEntities(esEntities);
	}

	@Override
	public void saveEntities(List<? extends ElasticSearchEntity> esEntities) throws Exception
	{
		if (esEntities == null || esEntities.size() == 0)
		{
			log.info("ESDAO saveData: no data to save");

			return;
		}

		Class<? extends ElasticSearchEntity> jaxbClass = esEntities.get(0).getClass();
		Marshaller marshaller = getMarshaller(Thread.currentThread().getName(), jaxbClass);

		indices = getElasticSearchIndices();

		int count = 0;
		StringWriter out = new StringWriter();
		for (ElasticSearchEntity e : esEntities)
		{
			if (!indices.contains(e.getElasticSearchIndex()))
				createElasticSearchIndex(e.getElasticSearchType(), e.getElasticSearchIndex());

			String pattern = "{\"create\": { \"_index\": \"%s\", \"_type\": \"%s\", \"_id\": \"%s\"}}\n";
			Object[] args = new String[3];
			args[0] = e.getElasticSearchIndex();
			args[1] = e.getElasticSearchType();
			args[2] = e.getElasticSearchId();

			out.append(String.format(pattern, args));
			marshaller.marshal(e, out);
			out.append("\n");

			count++;
		}

		if (!out.toString().isEmpty())
		{
			log.info("posting data: {}", count);
			postBulkData(out.toString());
		}
	}

	private Set<String> getElasticSearchIndices() throws IOException
	{
		Set<String> indices = new HashSet<String>();

		WebTarget t = elasticTarget.path("_cat/indices").queryParam("h", "index");
		Builder req = t.request(MediaType.TEXT_PLAIN);
		String result = req.get(String.class);

		Scanner scanner = new Scanner(result);
		while (scanner.hasNextLine())
			indices.add(scanner.nextLine().trim());
		scanner.close();

		if (log.isDebugEnabled())
			for (String i : indices)
				log.debug("index: {}", i);

		return indices;
	}

	private synchronized void createElasticSearchIndex(String type, String index) throws IOException
	{
		if (indices.contains(index))
			return;

		WebTarget t = elasticTarget.path(index);
		Builder req = t.request(MediaType.APPLICATION_JSON);

		String mapping = "/mapping-" + type + ".json";
		InputStream in = ClassLoader.class.getResourceAsStream(mapping);

		if (in == null)
		{
			log.error("unable to load input stream: {}", mapping);
			throw new IOException("unable to load input stream: " + mapping);
		}

		String data = DataUtils.extract(in);

		String result = req.put(Entity.entity(data, MediaType.APPLICATION_JSON), String.class);

		indices.add(index);

		log.debug("posted data:\n{}", data.toString());
		log.debug("result: {}", result);
	}

	private String postBulkData(String data)
	{
		WebTarget t = elasticTarget.path("_bulk");
		Builder req = t.request(MediaType.APPLICATION_JSON);
		String result = req.post(Entity.entity(data, MediaType.APPLICATION_JSON), String.class);

		log.debug("posted data:\n{}", data.toString());
		log.debug("result: {}", result);

		return result;
	}

	private Marshaller getMarshaller(String threadName,
	                                 Class<? extends ElasticSearchEntity> c) throws PropertyException, JAXBException
	{
		Map<String, Marshaller> ms = marshallers.get(threadName);
		if (ms == null)
		{
			ms = new HashMap<String, Marshaller>();
			marshallers.put(threadName, ms);
		}

		Marshaller marshaller = ms.get(c.getName());
		if (marshaller == null)
		{
			JAXBContext context = JAXBContext.newInstance(c);
			marshaller = context.createMarshaller();
			marshaller.setProperty("eclipselink.media-type", "application/json");
			marshaller.setProperty(MarshallerProperties.JSON_INCLUDE_ROOT, false);
			marshaller.setProperty(Marshaller.JAXB_ENCODING, "UTF-8");
			ms.put(c.getName(), marshaller);
		}

		return marshaller;
	}
}
