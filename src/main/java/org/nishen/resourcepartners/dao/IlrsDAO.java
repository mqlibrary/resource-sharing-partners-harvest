package org.nishen.resourcepartners.dao;

import java.util.Map;
import java.util.Optional;

import javax.ws.rs.ClientErrorException;

import org.nishen.resourcepartners.entity.ResourcePartnerAddress;

public interface IlrsDAO
{
	public String getPage(String nuc) throws ClientErrorException;

	public Map<String, ResourcePartnerAddress> getAddressesFromPage(String page);

	public Optional<String> getEmailFromPage(String page);

	public Optional<String> getPhoneIllFromPage(String page);

	public Optional<String> getPhoneFaxFromPage(String page);
}