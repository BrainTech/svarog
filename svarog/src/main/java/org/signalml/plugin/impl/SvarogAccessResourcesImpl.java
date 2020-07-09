package org.signalml.plugin.impl;

import java.io.IOException;
import java.util.Properties;
import org.apache.log4j.Logger;
import org.signalml.app.config.ConfigurationDefaultsLoader;
import org.signalml.plugin.export.Plugin;
import org.signalml.plugin.export.resources.SvarogAccessResources;

/**
 * {@link SvarogAccessResources} implementation using org.springframework.context.
 *
 */
public class SvarogAccessResourcesImpl implements SvarogAccessResources {
	protected static final Logger log = Logger.getLogger(SvarogAccessResources.class);

	private final Class<? extends Plugin> klass;

	public SvarogAccessResourcesImpl(Class<? extends Plugin> klass) {
		this.klass = klass;
	}

	@Override
	public Properties loadPluginConfigurationDefaults(String classpath)
	throws IOException {
		return ConfigurationDefaultsLoader.Load(this.klass, classpath);
	}
}
