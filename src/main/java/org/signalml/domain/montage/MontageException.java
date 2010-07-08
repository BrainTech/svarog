/* MontageException.java created 2007-11-22
 *
 */

package org.signalml.domain.montage;

import org.signalml.exception.SignalMLException;

/** MontageException
 * Class representing exception in a montage
 *
 * @author Michal Dobaczewski &copy; 2007-2008 CC Otwarte Systemy Komputerowe Sp. z o.o.
 */
public class MontageException extends SignalMLException {

	private static final long serialVersionUID = 1L;

        /**
         * Constructor. Creates an empty montage exception
         */
	public MontageException() {
		super();
	}

        /**
         * Constructor.
         * @param message String with a exception message
         * @param cause the cause of exception. null indicates that the cause is nonexistent or unknown
         */
	public MontageException(String message, Throwable cause) {
		super(message, cause);
	}

        /**
         * Constructor. Creates an exception with the specified message.
         * @param message String with a exception message
         */
	public MontageException(String message) {
		super(message);
	}

        /**
         * Constructor. Creates an exception based on given Throwable object.
         * Message of an exception is cause.toString()
         * @param cause throwable object used as a cause for event
         */
	public MontageException(Throwable cause) {
		super(cause);
	}

}
