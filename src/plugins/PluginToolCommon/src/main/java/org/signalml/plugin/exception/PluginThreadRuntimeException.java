package org.signalml.plugin.exception;

public class PluginThreadRuntimeException extends RuntimeException {

	/**
	 *
	 */
	private static final long serialVersionUID = -2817503819169351864L;

	public PluginThreadRuntimeException() {
		super();
	}

	public PluginThreadRuntimeException(String message) {
		super(message);
	}

	public PluginThreadRuntimeException(String message, Throwable cause) {
		super(message, cause);
	}

	public PluginThreadRuntimeException(Throwable cause) {
		super(cause);
	}

}
