/* MontageMismatchException.java created 2007-10-25
 *
 */

package org.signalml.domain.montage;

import org.signalml.plugin.export.SignalMLException;

/**
 * This class represents an exception of mismatch between
 * {@link Montage montages}.
 * Occurs if the number of channels in montages is different.
 *
 * @author Michal Dobaczewski &copy; 2007-2008 CC Otwarte Systemy Komputerowe Sp. z o.o.
 */
public class MontageMismatchException extends SignalMLException {

	private static final long serialVersionUID = 1L;

        /**
         * Constructor. Creates an empty montage mismatch exception.
         */
	public MontageMismatchException() {
		super();
	}

        /**
         * Constructor.
         * @param message a String with an exception message
         * @param cause the cause of an exception or null if the cause is
         * nonexistent or unknown
         */
	public MontageMismatchException(String message, Throwable cause) {
		super(message, cause);
	}

        /**
         * Constructor. Creates an exception with the specified message.
         * @param message String with an exception message
         */
	public MontageMismatchException(String message) {
		super(message);
	}

        /**
         * Constructor. Creates an exception based on a given Throwable object.
         * Message of an exception is cause.toString().
         * @param cause a throwable object used as a cause for an event
         */
	public MontageMismatchException(Throwable cause) {
		super(cause);
	}

}
