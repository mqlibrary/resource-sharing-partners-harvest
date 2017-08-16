package org.nishen.resourcepartners;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.Inject;

public class ResourcePartnerApp
{
	private static final Logger log = LoggerFactory.getLogger(ResourcePartnerApp.class);

	@Inject
	private ResourcePartnerApp()
	{
		log.debug("instantiated class: {}", this.getClass().getName());
	}

	public void run()
	{
		log.debug("application execution starting");

		log.debug("application execution complete");
	}
}
