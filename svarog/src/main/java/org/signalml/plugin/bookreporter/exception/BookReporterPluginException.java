package org.signalml.plugin.bookreporter.exception;

import org.signalml.plugin.exception.PluginException;

/**
 * @author piotr@develancer.pl
 */
public class BookReporterPluginException extends PluginException {

	public BookReporterPluginException() {
		super();
	}

	public BookReporterPluginException(String message) {
		super(message);
	}

	public BookReporterPluginException(Throwable cause) {
		super(cause);
	}

	public BookReporterPluginException(String message, Throwable cause) {
		super(message, cause);
	}

}
