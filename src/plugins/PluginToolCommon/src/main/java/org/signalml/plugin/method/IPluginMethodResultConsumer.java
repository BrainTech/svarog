package org.signalml.plugin.method;

import org.signalml.app.method.MethodResultConsumer;

public interface IPluginMethodResultConsumer extends MethodResultConsumer {

	public void initialize(PluginMethodManager manager);

}
