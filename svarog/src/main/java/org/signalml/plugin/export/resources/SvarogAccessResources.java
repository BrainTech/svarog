package org.signalml.plugin.export.resources;

import org.signalml.plugin.export.PluginAuth;

import java.io.IOException;
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
	ImageIcon loadClassPathIcon(PluginAuth auth, String classpath) throws IOException;
}
