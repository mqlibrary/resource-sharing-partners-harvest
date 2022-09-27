package org.nishen.resourcepartners.dao;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentMap;

import org.nishen.resourcepartners.ResourcePartnerSynchroniserException;
import org.nishen.resourcepartners.model.Partner;

public interface AlmaDAO
{
	public ConcurrentMap<String, Partner> getPartners() throws ResourcePartnerSynchroniserException;

	public Optional<Partner> getPartner(String nuc) throws ResourcePartnerSynchroniserException;

	public void savePartner(Partner p) throws ResourcePartnerSynchroniserException;

	public void savePartners(Map<String, Partner> partner) throws ResourcePartnerSynchroniserException;
}
