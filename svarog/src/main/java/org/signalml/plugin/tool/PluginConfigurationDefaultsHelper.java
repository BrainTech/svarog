package org.signalml.plugin.tool;

import java.io.IOException;
import java.util.Properties;
import org.apache.log4j.Logger;
import org.signalml.app.config.ConfigurationDefaultsLoader;
import org.signalml.plugin.exception.PluginException;
import org.signalml.plugin.export.Plugin;
import org.signalml.plugin.export.SvarogAccess;
import org.signalml.plugin.export.resources.SvarogAccessResources;
import org.signalml.util.MinMaxRange;

public class PluginConfigurationDefaultsHelper {

	protected static final Logger logger = Logger
										   .getLogger(ConfigurationDefaultsLoader.class);

	protected class ConfigurationDefaultsException extends PluginException {

		/**
		 *
		 */
		private static final long serialVersionUID = 1L;

	}

	private Properties properties;

	public PluginConfigurationDefaultsHelper() {
		this.properties = null;
	}

	public Properties getProperties() throws ConfigurationDefaultsException {
		if (this.properties == null) {
			Class<? extends Plugin> pluginClass = PluginContextHelper
												  .FindContextPluginClass();
			if (pluginClass == null) {
				throw new ConfigurationDefaultsException();
			}

			this.properties = this.loadProperties(pluginClass);
			if (this.properties == null) {
				throw new ConfigurationDefaultsException();
			}
		}

		return this.properties;
	}

	private Properties loadProperties(Class<? extends Plugin> pluginClass)
	throws ConfigurationDefaultsException {
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

	protected String getConfigurationDefaultsPath(
		Class<? extends Plugin> pluginClass) {
		return null;
	}

	protected boolean hasProperties() {
		try {
			this.getProperties();
		} catch (ConfigurationDefaultsException e) {
			return false;
		}
		return true;
	}

	protected String get(String key) throws ConfigurationDefaultsException {
		return this.getProperties().getProperty(key);
	}

	protected boolean bool_(String key) throws ConfigurationDefaultsException {
		return Boolean.parseBoolean(this.get(key));
	}

	protected double double_(String key) throws NumberFormatException, ConfigurationDefaultsException {
		return Double.parseDouble(this.get(key));
	}

	protected void setRange(MinMaxRange range, String key) throws NumberFormatException, ConfigurationDefaultsException {
		range.setMin(double_(key + "Min"));
		range.setMinUnlimited(bool_(key + "MinUnlimited"));
		range.setMax(double_(key + "Max"));
		range.setMaxUnlimited(bool_(key + "MaxUnlimited"));
	}

}
