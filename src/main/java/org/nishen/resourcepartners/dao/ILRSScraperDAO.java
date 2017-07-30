package org.nishen.resourcepartners.dao;

import java.util.Map;

import org.nishen.resourcepartners.entity.Address;

public interface ILRSScraperDAO
{
	String getPage(String nuc);

	Map<String, Address> getAddressFromPage(String page) throws Exception;
}