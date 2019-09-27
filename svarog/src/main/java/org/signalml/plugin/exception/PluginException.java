package org.signalml.plugin.exception;

import org.signalml.plugin.export.SignalMLException;

public class PluginException extends SignalMLException {

	private static final long serialVersionUID = -8204661975168062195L;

	public PluginException() {
		super();
	}

	public PluginException(String message) {
		super(message);
	}

	public PluginException(Throwable cause) {
		super(cause);
	}

	public PluginException(String message, Throwable cause) {
		super(message, cause);
	}
}
