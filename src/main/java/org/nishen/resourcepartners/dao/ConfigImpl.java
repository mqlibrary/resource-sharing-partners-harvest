package org.nishen.resourcepartners.dao;

import java.io.File;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import org.nishen.resourcepartners.util.DataUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.inject.Inject;
import com.google.inject.assistedinject.Assisted;
import com.google.inject.name.Named;

public class ConfigImpl implements Config
{
	private static final Logger log = LoggerFactory.getLogger(ConfigImpl.class);

	private String configId;

	private String configFolder;

	private String configFolderBackup;

	private String configFilename;

	private String configFilenameBackup;

	private ConcurrentMap<String, String> config;

	private ObjectMapper om = new ObjectMapper();

	@Inject
	public ConfigImpl(@Named("location.config") String configFolder, @Assisted String configId)
	{
		this.configId = configId;
		this.configFolder = configFolder;
		this.configFolderBackup = configFolder + File.separatorChar + "backups";
		this.configFilename = configFolder + File.separatorChar + configId + ".json";
		this.configFilenameBackup = this.configFolderBackup + File.separatorChar + configId + ".json";

		File cf = new File(this.configFolder);
		if (!cf.isDirectory())
			cf.mkdirs();

		File cfb = new File(configFolderBackup);
		if (!cfb.isDirectory())
			cfb.mkdirs();

		this.config = new ConcurrentHashMap<String, String>();
		fetchConfig().ifPresent(c -> this.config.putAll(c));
		try
		{
			backupConfig();
		}
		catch (Exception e)
		{
			log.error("failed to backup config to: {}", this.configFilenameBackup);
		}
	}

	@Override
	public Optional<String> get(String key)
	{
		return Optional.ofNullable(config.get(key));
	}

	@Override
	public Map<String, String> getAll()
	{
		return Collections.unmodifiableMap(config);
	}

	@Override
	public void set(String key, String value)
	{
		config.put(key, value);
		try
		{
			saveConfig();
		}
		catch (Exception e)
		{
			log.error("unable to save config[{}]: {}", configId + "." + key, e.getMessage(), e);
		}
	}

	@Override
	public void setAll(Map<String, String> config)
	{
		this.config.putAll(config);
		try
		{
			saveConfig();
		}
		catch (Exception e)
		{
			log.error("unable to save config[{}]: {}", configId, e.getMessage(), e);
		}
	}

	@Override
	public Optional<String> remove(String key)
	{
		String value = config.remove(key);
		try
		{
			saveConfig();
		}
		catch (Exception e)
		{
			log.error("unable to save config[{}]: {}", configId + "." + key, e.getMessage(), e);
		}

		return Optional.ofNullable(value);
	}

	@Override
	public Map<String, String> removeAll()
	{
		Map<String, String> result = new HashMap<String, String>(config);
		config.clear();
		try
		{
			saveConfig();
		}
		catch (Exception e)
		{
			log.error("unable to save config[{}]: {}", configId + ".*", e.getMessage(), e);
		}

		return Collections.unmodifiableMap(result);
	}

	private void saveConfig() throws Exception
	{
		String esConfig = om.writeValueAsString(config);
		DataUtil.saveFile(this.configFilename, esConfig.getBytes());
	}

	private void backupConfig() throws Exception
	{
		File backup = new File(this.configFilenameBackup);
		if (backup.canWrite())
			backup.delete();

		String esConfig = om.writeValueAsString(config);
		DataUtil.saveFile(this.configFilenameBackup, esConfig.getBytes());
	}

	private Optional<Map<String, String>> fetchConfig()
	{
		File configFile = new File(this.configFilename);
		if (!configFile.canRead())
			return Optional.empty();

		Map<String, String> config = new ConcurrentHashMap<>();
		try
		{
			JsonNode root = om.readTree(configFile);
			root.fields().forEachRemaining(e -> {
				log.debug("{} = {}", e.getKey(), e.getValue().asText());
				config.put(e.getKey(), e.getValue().asText());
			});

		}
		catch (Exception e)
		{
			log.error("unable to retrieve config[{}]: {}", this.configId, e.getMessage(), e);
		}

		return Optional.ofNullable(config);
	}
}
