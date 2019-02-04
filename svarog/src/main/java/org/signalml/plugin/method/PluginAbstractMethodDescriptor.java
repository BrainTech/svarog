package org.signalml.plugin.method;

import org.signalml.app.method.ApplicationIterableMethodDescriptor;
import org.signalml.plugin.export.method.SvarogMethodDescriptor;

public abstract class PluginAbstractMethodDescriptor implements SvarogMethodDescriptor, ApplicationIterableMethodDescriptor {

	protected PluginMethodManager methodManager;

	public void setPluginMethodManager(PluginMethodManager methodManager) {
		this.methodManager = methodManager;
	}
}
