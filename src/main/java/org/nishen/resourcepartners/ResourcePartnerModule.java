package org.nishen.resourcepartners;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Properties;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.WebTarget;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import com.google.inject.name.Named;
import com.google.inject.name.Names;

public class ResourcePartnerModule extends AbstractModule
{
	private static final Logger log = LoggerFactory.getLogger(ResourcePartnerModule.class);

	private static final String CONFIG_FILE = "app.properties";

	private static final Properties config = new Properties();

	private WebTarget elasticTarget = null;

	private WebTarget ilrsTarget = null;

	@Override
	protected void configure()
	{
		String configFilename = CONFIG_FILE;
		if (System.getProperty("config") != null)
			configFilename = System.getProperty("config");

		File configFile = new File(configFilename);
		try
		{
			if (!configFile.exists() || !configFile.canRead())
				throw new IOException("cannot read config file: " + configFile.getAbsolutePath());

			config.load(new FileReader(configFile));

			if (log.isDebugEnabled())
				for (String k : config.stringPropertyNames())
					log.debug("{}: {}={}", new Object[] { CONFIG_FILE, k, config.getProperty(k) });
		}
		catch (IOException e)
		{
			log.error("unable to load configuration: {}", configFile.getAbsoluteFile(), e);
			return;
		}

		// bind instances
		bind(Properties.class).annotatedWith(Names.named("app.config")).toInstance(config);
		// bind(ILRSScraperUtil.class).to(ILRSScraperUtil.class);
	}

	@Provides
	@Named("ws.url.elastic.index")
	protected WebTarget provideWebTargetAlma()
	{
		if (elasticTarget == null)
		{
			Client client = ClientBuilder.newClient();
			elasticTarget = client.target(config.getProperty("ws.url.elastic.index"));
		}

		return elasticTarget;
	}

	@Provides
	@Named("ws.url.ilrs")
	protected WebTarget provideWebTargetIlrs()
	{
		if (ilrsTarget == null)
		{
			Client client = ClientBuilder.newClient();
			ilrsTarget = client.target(config.getProperty("ws.url.ilrs"));
		}

		return ilrsTarget;
	}
}
