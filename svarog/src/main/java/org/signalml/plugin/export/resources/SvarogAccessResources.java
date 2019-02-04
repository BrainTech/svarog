package org.signalml.plugin.export.resources;

import java.io.IOException;
import java.util.Properties;

/**
 * resources support for plugins.
 *
 */
public interface SvarogAccessResources {

	Properties loadPluginConfigurationDefaults(String classpath) throws IOException;
}
