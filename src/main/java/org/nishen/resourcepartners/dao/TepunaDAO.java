package org.nishen.resourcepartners.dao;

import java.util.Map;

import javax.ws.rs.ClientErrorException;

import org.nishen.resourcepartners.entity.ElasticSearchPartner;

public interface TepunaDAO
{
	public Map<String, ElasticSearchPartner> getData() throws ClientErrorException;
}