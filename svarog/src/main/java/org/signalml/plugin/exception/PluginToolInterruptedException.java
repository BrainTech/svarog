package org.signalml.plugin.exception;

public class PluginToolInterruptedException extends Exception {

	private static final long serialVersionUID = 5430791206884764588L;

	public PluginToolInterruptedException() {
		super();
	}

	public PluginToolInterruptedException(String message) {
		super(message);
	}

	public PluginToolInterruptedException(String message, Throwable cause) {
		super(message, cause);
	}

	public PluginToolInterruptedException(Throwable cause) {
		super(cause);
	}
}
