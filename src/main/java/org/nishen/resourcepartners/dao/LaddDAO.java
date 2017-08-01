package org.nishen.resourcepartners.dao;

import java.util.Map;

import org.nishen.resourcepartners.entity.ElasticSearchPartner;

public interface LaddDAO
{
	public String getPage();

	public Map<String, ElasticSearchPartner> getDataFromPage(String page) throws Exception;
}