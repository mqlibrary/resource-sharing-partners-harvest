package org.nishen.resourcepartners.harvesters;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import org.nishen.resourcepartners.SkipHarvestException;
import org.nishen.resourcepartners.entity.ElasticSearchChangeRecord;
import org.nishen.resourcepartners.entity.ElasticSearchPartner;

public interface Harvester
{
	public static final String NZ_NUC_PREFIX = "NLNZ";

	public String getSource();

	public Map<String, ElasticSearchPartner> harvest() throws IOException, SkipHarvestException;

	public Map<String, ElasticSearchPartner> update(Map<String, ElasticSearchPartner> partners,
	                                                Map<String, ElasticSearchPartner> latest,
	                                                List<ElasticSearchChangeRecord> changes);

	default boolean compareStrings(String a, String b)
	{
		if (a == null && b == null)
			return true;

		if (a == null || b == null)
			return false;

		return a.equals(b);
	}
}