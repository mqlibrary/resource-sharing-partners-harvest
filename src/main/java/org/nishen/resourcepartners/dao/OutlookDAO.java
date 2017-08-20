package org.nishen.resourcepartners.dao;

import java.util.Map;

public interface OutlookDAO
{
	public Map<String, String> getMessages() throws Exception;

	public String getProcessedFolderId() throws Exception;

	public void markMessagesProcessed(Map<String, String> messages) throws Exception;
}
