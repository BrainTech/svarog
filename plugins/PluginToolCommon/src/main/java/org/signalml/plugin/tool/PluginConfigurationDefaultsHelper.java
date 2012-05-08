package org.signalml.plugin.tool;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import org.apache.log4j.Logger;
import org.signalml.app.config.ConfigurationDefaultsLoader;
import org.signalml.plugin.exception.PluginException;
import org.signalml.plugin.export.Plugin;
import org.signalml.plugin.export.SvarogAccess;
import org.signalml.plugin.export.resources.SvarogAccessResources;

public class PluginConfigurationDefaultsHelper {

	protected static final Logger logger = Logger
			.getLogger(ConfigurationDefaultsLoader.class);

	protected class ConfigurationDefaultsException extends PluginException {

		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;

	}


	private Map<Class<? extends Plugin>, Properties> propertiesMap;

	public PluginConfigurationDefaultsHelper() {
		this.propertiesMap = new HashMap<Class<? extends Plugin>, Properties>();
	}
	
	public Properties getProperties() throws ConfigurationDefaultsException {
		Class<? extends Plugin> pluginClass = PluginContextHelper.FindContextPluginClass();
		
		Properties properties = this.propertiesMap.get(pluginClass);
		if (properties == null) {
			properties = this.loadProperties(pluginClass);
			if (properties == null) {
				throw new ConfigurationDefaultsException();
			}
			this.propertiesMap.put(pluginClass, properties);
		}
		
		return properties;
	}

	private Properties loadProperties(Class<? extends Plugin> pluginClass) throws ConfigurationDefaultsException {
		SvarogAccess access = PluginAccessHelper.GetSvarogAccess();
		if (access == null) {
			throw new ConfigurationDefaultsException();
		}

		SvarogAccessResources resourceAccess = access.getResourcesAccess();
		if (resourceAccess == null) {
			throw new ConfigurationDefaultsException();
		}

		String path = this.getConfigurationDefaultsPath(pluginClass);
		if (path == null) {
			throw new ConfigurationDefaultsException();
		}

		try {
			return resourceAccess.loadPluginConfigurationDefaults(path);
		} catch (IOException e) {
			logger.error("Can't access plugin configuration defaults", e);
			throw new ConfigurationDefaultsException();
		}
	}

	protected String getConfigurationDefaultsPath(Class<? extends Plugin> pluginClass) {
		return null;
	}
}
