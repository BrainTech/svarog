package org.signalml.plugin.bookreporter.exception;

/**
 * @author piotr@develancer.pl
 */
public class BookReporterBookReaderException extends BookReporterPluginException {

	public BookReporterBookReaderException() {
		super();
	}

	public BookReporterBookReaderException(String message) {
		super(message);
	}

	public BookReporterBookReaderException(Throwable cause) {
		super(cause);
	}

	public BookReporterBookReaderException(String message, Throwable cause) {
		super(message, cause);
	}
}
