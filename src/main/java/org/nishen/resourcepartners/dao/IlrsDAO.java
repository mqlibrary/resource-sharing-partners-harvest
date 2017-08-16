package org.nishen.resourcepartners.dao;

import java.util.Map;

import javax.ws.rs.ClientErrorException;

import org.nishen.resourcepartners.model.Address;

public interface IlrsDAO
{
	public String getPage(String nuc) throws ClientErrorException;

	public Map<String, Address> getAddressesFromPage(String page);
}