package org.nishen.resourcepartners;

import java.util.Map;

public interface ResourcePartnerHarvester
{
	public void process(Map<String, String> options) throws Exception;
}
