/* MontageException.java created 2007-11-22
 *
 */

package org.signalml.domain.montage;

import org.signalml.exception.SignalMLException;

/**
 * This class represents an exception in a {@link Montage montage}.
 *
 * @author Michal Dobaczewski &copy; 2007-2008 CC Otwarte Systemy Komputerowe Sp. z o.o.
 */
public class MontageException extends SignalMLException {

	private static final long serialVersionUID = 1L;

        /**
         * Constructor. Creates an empty montage exception.
         */
	public MontageException() {
		super();
	}

        /**
         * Constructor.
         * @param message a String with the exception message
         * @param cause the cause of an exception or
         * null if the cause is nonexistent or unknown
         */
	public MontageException(String message, Throwable cause) {
		super(message, cause);
	}

        /**
         * Constructor. Creates an exception with the specified message.
         * @param message String with an exception message
         */
	public MontageException(String message) {
		super(message);
	}

        /**
         * Constructor. Creates an exception based on a given Throwable object.
         * Message of an exception is cause.toString()
         * @param cause a throwable object used as a cause for an event
         */
	public MontageException(Throwable cause) {
		super(cause);
	}

}
