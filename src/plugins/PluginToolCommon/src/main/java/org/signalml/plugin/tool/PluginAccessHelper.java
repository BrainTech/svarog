package org.signalml.plugin.tool;

import org.signalml.plugin.export.Plugin;


public class PluginAccessHelper {

	public static void SetupConfig(Plugin plugin, String configResourceName) {
		PluginResourceRepository.RegisterPlugin(plugin.getClass(), configResourceName);
	}

}
