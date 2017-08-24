package org.nishen.resourcepartners.dao;

import java.util.Map;

import com.fasterxml.jackson.databind.JsonNode;

public interface OutlookDAO
{
	public Map<String, JsonNode> getMessages();

	public String getProcessedFolderId() throws Exception;

	public void markMessagesProcessed(Map<String, JsonNode> messages);
}
