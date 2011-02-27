package org.signalml.codec;

public class CodecException extends Exception {

	private static final long serialVersionUID = 1L;

	/**
	 *
	 */
	public CodecException() {
	}

	public CodecException(String message) {
		super(message);
	}

	public CodecException(Throwable cause) {
		super(cause);
	}

	public CodecException(String message, Throwable cause) {
		super(message, cause);
	}

}
