package org.signalml.plugin.data;

import org.signalml.plugin.export.Plugin;

public class PluginConfigWithMessageSourceReference extends PluginConfig {

	public PluginConfigWithMessageSourceReference(
		Class<? extends Plugin> pluginClass) {
		super(pluginClass);
	}

	private String messageSourceName;

	public String getMessageSourceName() {
		return messageSourceName;
	}

	public void setMessageSourceName(String messageSourceName) {
		this.messageSourceName = messageSourceName;
	}

}
