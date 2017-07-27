package org.nishen.resourcepartners;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Properties;

import org.nishen.resourcepartners.util.ILRSScraperUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.gargoylesoftware.htmlunit.ProxyConfig;
import com.gargoylesoftware.htmlunit.WebClient;
import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import com.google.inject.TypeLiteral;
import com.google.inject.name.Names;

public class ResourcePartnerModule extends AbstractModule
{
	private static final Logger log = LoggerFactory.getLogger(ResourcePartnerModule.class);

	private static final String CONFIG_FILE = "app.properties";

	private static final Properties config = new Properties();

	private static String[] args;

	public ResourcePartnerModule(final String[] args)
	{
		ResourcePartnerModule.args = args;
	}

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
		TypeLiteral<String[]> argsType = new TypeLiteral<String[]>() {};
		bind(argsType).annotatedWith(Names.named("app.cmdline")).toInstance(args);
		bind(Properties.class).annotatedWith(Names.named("app.config")).toInstance(config);
		bind(ILRSScraperUtil.class).to(ILRSScraperUtil.class);
	}

	@Provides
	protected WebClient provideWebClient()
	{
		WebClient webClient = new WebClient();

		String proxyHost = config.getProperty("ws.proxy.host");
		String proxyPort = config.getProperty("ws.proxy.port");
		if (proxyHost != null && !"".equals(proxyHost) && proxyPort != null && !"".equals(proxyPort))
		{
			ProxyConfig proxyConfig = new ProxyConfig(proxyHost, Integer.parseInt(proxyPort));
			webClient.getOptions().setProxyConfig(proxyConfig);
		}

		webClient.getOptions().setActiveXNative(false);
		webClient.getOptions().setJavaScriptEnabled(false);
		webClient.getOptions().setCssEnabled(false);
		webClient.getOptions().setUseInsecureSSL(true);

		return webClient;
	}
}
