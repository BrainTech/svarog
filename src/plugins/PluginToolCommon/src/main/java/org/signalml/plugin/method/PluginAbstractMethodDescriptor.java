package org.signalml.plugin.method;

import org.signalml.app.method.ApplicationIterableMethodDescriptor;

public abstract class PluginAbstractMethodDescriptor implements ApplicationIterableMethodDescriptor {

	protected PluginMethodManager methodManager;

	public void setPluginMethodManager(PluginMethodManager methodManager) {
		this.methodManager = methodManager;
	}
}
