package org.nishen.resourcepartners.dao;

import java.util.Map;

import org.nishen.resourcepartners.model.Address;

public interface IlrsDAO
{
	public String getPage(String nuc);

	public Map<String, Address> getAddressFromPage(String page) throws Exception;
}