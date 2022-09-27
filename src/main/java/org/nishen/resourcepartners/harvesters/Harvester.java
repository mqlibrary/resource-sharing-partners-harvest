package org.nishen.resourcepartners.harvesters;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import org.nishen.resourcepartners.ResourcePartnerHarvesterSkipException;
import org.nishen.resourcepartners.entity.ResourcePartner;
import org.nishen.resourcepartners.entity.ResourcePartnerChangeRecord;

public interface Harvester
{
	public static final String NZ_NUC_PREFIX = "NLNZ";

	public String getSource();

	public Map<String, ResourcePartner> harvest() throws IOException, ResourcePartnerHarvesterSkipException;

	public Map<String, ResourcePartner> update(Map<String, ResourcePartner> partners,
	                                           Map<String, ResourcePartner> latest,
	                                           List<ResourcePartnerChangeRecord> changes);
}