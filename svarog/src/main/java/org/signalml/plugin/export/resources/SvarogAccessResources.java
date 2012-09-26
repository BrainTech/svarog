package org.signalml.plugin.export.resources;

import java.io.IOException;
import java.util.Properties;

import javax.swing.ImageIcon;

/**
 * resources support for plugins.
 *
 */
public interface SvarogAccessResources {

	/**
	 * Translates the message for the specified key using the current Svarog locale,
	 * and renders it using actual values.
	 * The message is fetched from local plugin resources store first, and if not found,
	 * global Svarog resources store is used.
	 *
	 * @param auth plugin authentication object
	 * @param classpath path to resources
	 */
	ImageIcon loadClassPathIcon(String classpath) throws IOException;

	Properties loadPluginConfigurationDefaults(String classpath) throws IOException;
}
