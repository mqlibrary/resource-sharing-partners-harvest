package org.nishen.resourcepartners;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.Inject;

public class ResourcePartnerApp
{
	private static final Logger log = LoggerFactory.getLogger(ResourcePartnerApp.class);

	private ResourcePartnerProcessor processor;

	@Inject
	private ResourcePartnerApp(ResourcePartnerProcessor processor)
	{
		this.processor = processor;

		log.debug("instantiated class: {}", this.getClass().getName());
	}

	public void run()
	{
		log.debug("application execution starting");

		try
		{
			processor.process();
		}
		catch (Exception e)
		{
			log.error("{}", e.getMessage(), e);
		}

		log.debug("application execution complete");
	}
}
