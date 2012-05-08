package org.signalml.plugin.impl;

import java.io.IOException;
import java.util.Properties;

import javax.swing.ImageIcon;

import org.apache.log4j.Logger;
import org.signalml.app.config.ConfigurationDefaultsLoader;
import org.signalml.plugin.export.Plugin;
import org.signalml.plugin.export.resources.SvarogAccessResources;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;

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

	public ImageIcon loadClassPathIcon(String classpath) throws IOException {
		Resource icon = new ClassPathResource(classpath, this.klass);
		log.debug("trying to load " + icon.getURL());
		try {
			return new ImageIcon(icon.getURL());
		} catch (IOException ex) {
			log.error("WARNING: failed to open icon recource [" + icon + "]", ex);
			throw ex;
		}
	}

	@Override
	public Properties loadPluginConfigurationDefaults(String classpath)
			throws IOException {
		return ConfigurationDefaultsLoader.Load(this.klass, classpath);
	}
}
