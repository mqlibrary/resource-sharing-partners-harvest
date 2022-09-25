package org.nishen.resourcepartners.dao;

import java.util.Map;

import javax.ws.rs.ClientErrorException;

import org.nishen.resourcepartners.entity.ResourcePartner;

public interface TepunaDAO
{
	public Map<String, ResourcePartner> getData() throws ClientErrorException;
}