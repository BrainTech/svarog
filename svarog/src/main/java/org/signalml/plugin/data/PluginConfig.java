package org.signalml.plugin.data;

import org.signalml.plugin.export.Plugin;

public class PluginConfig {

	protected Class<? extends Plugin> pluginClass;

	public PluginConfig(Class<? extends Plugin> pluginClass) {
		this.pluginClass = pluginClass;
	}

	public void setPluginClass(Class<? extends Plugin> pluginClass) {
		this.pluginClass = pluginClass;
	}

	public Class<? extends Plugin> getPluginClass() {
		return pluginClass;
	}
}
