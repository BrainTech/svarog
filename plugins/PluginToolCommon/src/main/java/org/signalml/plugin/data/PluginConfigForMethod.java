package org.signalml.plugin.data;

import org.signalml.plugin.export.Plugin;

public class PluginConfigForMethod extends PluginConfig  {

	private PluginConfigMethodData methodConfig;

	public PluginConfigForMethod(Class<? extends Plugin> pluginClass) {
		super(pluginClass);
	}

	public void setMethodConfig(PluginConfigMethodData methodConfig) {
		this.methodConfig = methodConfig;
	}

	public PluginConfigMethodData getMethodConfig() {
		return methodConfig;
	}

}
