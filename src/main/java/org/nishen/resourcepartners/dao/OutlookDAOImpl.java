package org.nishen.resourcepartners.dao;

import java.util.LinkedHashMap;
import java.util.Map;

import javax.ws.rs.client.Entity;
import javax.ws.rs.client.Invocation.Builder;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.Form;
import javax.ws.rs.core.MediaType;

import org.nishen.resourcepartners.entity.AuthRefreshResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.inject.Inject;
import com.google.inject.Provider;
import com.google.inject.name.Named;

public class OutlookDAOImpl implements OutlookDAO
{
	private static final Logger log = LoggerFactory.getLogger(OutlookDAOImpl.class);

	private static final String SOURCE_SYSTEM = "OUTLOOK";

	private static final String PROCESSED_FOLDER_NAME = "Processed";

	private ObjectMapper mapper;

	private Config config;

	private String clientId;

	private String clientSecret;

	private WebTarget outlook;

	private WebTarget outlookToken;

	private String accessToken;

	private String processedFolderId;

	@Inject
	public OutlookDAOImpl(ConfigFactory configFactory, @Named("outlook.client.id") String clientId,
	                      @Named("outlook.client.secret") String clientSecret,
	                      @Named("ws.outlook") Provider<WebTarget> outlookWebTarget,
	                      @Named("ws.outlook.token") Provider<WebTarget> outlookTokenWebTarget) throws Exception
	{
		this.mapper = new ObjectMapper();
		this.config = configFactory.create(SOURCE_SYSTEM);
		this.clientId = clientId;
		this.clientSecret = clientSecret;
		this.outlook = outlookWebTarget.get();
		this.outlookToken = outlookTokenWebTarget.get();
		this.accessToken = getAccessToken();
		this.processedFolderId = getProcessedFolderId();

		log.debug("instantiated class: {}", this.getClass().getName());
	}

	public String getProcessedFolderId() throws Exception
	{
		String response = outlook.path("users").path("lib.rspartners@mq.edu.au").path("MailFolders")
		                         .queryParam("$top", 50).request().header("Authorization", "Bearer " + accessToken)
		                         .header("Prefer", "outlook.body-content-type=\"text\"")
		                         .accept(MediaType.APPLICATION_JSON).get(String.class);

		log.debug("folders:\n{}", response);

		JsonNode root = mapper.readTree(response);
		for (JsonNode entry : root.get("value"))
		{
			String id = entry.get("Id").asText();
			String name = entry.get("DisplayName").asText();
			if (PROCESSED_FOLDER_NAME.equals(name))
				return id;
		}

		return null;
	}

	public void markMessagesProcessed(Map<String, String> messages) throws Exception
	{
		String body = "{ \"DestinationId\": \"" + processedFolderId + "\" }";
		for (String id : messages.keySet())
		{
			WebTarget t = outlook.path("users").path("lib.rspartners@mq.edu.au").path("MailFolders").path("Inbox")
			                     .path("messages").path(id).path("move");

			Builder request = t.request();
			request = request.header("Authorization", "Bearer " + accessToken);
			request = request.accept(MediaType.APPLICATION_JSON);
			String response = request.post(Entity.json(body), String.class);
			log.debug("move response: {}", response);
		}
	}

	public Map<String, String> getMessages() throws Exception
	{
		Map<String, String> messages = new LinkedHashMap<String, String>();

		int batchSize = 10;
		int skip = 0;
		boolean hasMore = true;
		while (hasMore)
		{
			WebTarget t = outlook.path("users").path("lib.rspartners@mq.edu.au").path("MailFolders").path("Inbox")
			                     .path("messages");
			t = t.queryParam("$select", "ReceivedDateTime,Body");
			t = t.queryParam("$filter",
			                 "ReceivedDateTime ge 1900-01-01T00:00:00Z and Subject eq 'ISO-ILL Location Updates'");
			t = t.queryParam("$orderby", "ReceivedDateTime");
			t = t.queryParam("$top", batchSize);
			t = t.queryParam("$skip", skip);

			Builder request = t.request();
			request = request.header("Authorization", "Bearer " + accessToken);
			request = request.header("Prefer", "outlook.body-content-type=\"text\"");
			request = request.accept(MediaType.APPLICATION_JSON);

			log.debug("request: {}", request.toString());

			String response = request.get(String.class);
			log.debug("messages:\n{}", response);

			JsonNode root = mapper.readTree(response);
			log.debug("next: {}", root.get("@odata.nextLink"));

			hasMore = root.has("@odata.nextLink");

			for (JsonNode entry : root.get("value"))
			{
				String id = entry.get("Id").asText();
				String message = entry.get("Body").get("Content").asText().replace("\\r\\n", "\n");
				messages.put(id, message);
			}

			skip += batchSize;
		}

		return messages;
	}

	public String getAccessToken() throws Exception
	{
		String authRefreshResponseData =
		        config.get("refresh_token").orElseThrow(() -> new Exception("could not get refresh_token"));

		AuthRefreshResponse authRefreshResponse = mapper.readValue(authRefreshResponseData, AuthRefreshResponse.class);

		String refreshToken = authRefreshResponse.getRefreshToken();

		Form tokenForm = new Form().param("client_id", clientId).param("refresh_token", refreshToken)
		                           .param("grant_type", "refresh_token").param("resource", "https://outlook.office.com")
		                           .param("client_secret", clientSecret);

		authRefreshResponseData = outlookToken.request(MediaType.APPLICATION_FORM_URLENCODED)
		                                      .accept(MediaType.TEXT_HTML).post(Entity.form(tokenForm), String.class);

		log.debug("response: {}", authRefreshResponseData);

		config.set("refresh_token", authRefreshResponseData);

		authRefreshResponse = mapper.readValue(authRefreshResponseData, AuthRefreshResponse.class);

		return authRefreshResponse.getAccessToken();
	}
}
