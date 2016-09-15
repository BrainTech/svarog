package org.signalml.peer;

/**
 * Exception to be thrown to notify that communication between Svarog and OBCI
 * (using Peer class) failed for whatever reason.
 *
 * @author piotr.rozanski@braintech.pl
 */
public class CommunicationException extends Exception {

    /**
     * Constructs a new exception with the specified detail message.
     *
     * @param  message the detail message (which is saved for later retrieval
     * by the {@link #getMessage()} method)
     */
	public CommunicationException(String message) {
		super(message);
	}

    /**
     * Constructs a new exception with the specified detail message and
     * cause.
	 *
     * @param  message the detail message (which is saved for later retrieval
     * by the {@link #getMessage()} method)
     * @param  cause the cause (which is saved for later retrieval by the
     * {@link #getCause()} method)
     */
	public CommunicationException(String message, Throwable cause) {
		super(message, cause);
	}

}
