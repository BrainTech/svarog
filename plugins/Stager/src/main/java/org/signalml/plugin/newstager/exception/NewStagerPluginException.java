package org.signalml.plugin.newstager.exception;

import org.signalml.plugin.exception.PluginException;

public class NewStagerPluginException extends PluginException {

	private static final long serialVersionUID = -3166567054726007569L;

	public NewStagerPluginException() {
		super();
	}

	public NewStagerPluginException(String message) {
		super(message);
	}

	public NewStagerPluginException(Throwable cause) {
		super(cause);
	}

	public NewStagerPluginException(String message, Throwable cause) {
		super(message, cause);
	}

}
