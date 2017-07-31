package org.nishen.resourcepartners;

import org.nishen.resourcepartners.dao.ILRSScraperDAO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.Inject;
import com.google.inject.name.Named;

public class ResourcePartnerApp
{
	private static final Logger log = LoggerFactory.getLogger(ResourcePartnerApp.class);

	@Inject
	private ResourcePartnerApp(@Named("app.cmdline") final String[] args, ILRSScraperDAO scraper)
	{}

	public void run()
	{
		log.debug("application execution starting");

		log.debug("application execution complete");
	}
}
