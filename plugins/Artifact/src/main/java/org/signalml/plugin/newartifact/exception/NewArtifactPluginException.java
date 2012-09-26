package org.signalml.plugin.newartifact.exception;

import org.signalml.plugin.exception.PluginException;

public class NewArtifactPluginException extends PluginException {

	/**
	 *
	 */
	private static final long serialVersionUID = -7907800548785251685L;

	public NewArtifactPluginException() {
		super();
	}

	public NewArtifactPluginException(String message) {
		super(message);
	}

	public NewArtifactPluginException(Throwable cause) {
		super(cause);
	}

	public NewArtifactPluginException(String message, Throwable cause) {
		super(message, cause);
	}

}
