/* SignalMLException.java created 2007-09-20
 * 
 */

package org.signalml.exception;

/** SignalMLException
 *
 * 
 * @author Michal Dobaczewski &copy; 2007-2008 CC Otwarte Systemy Komputerowe Sp. z o.o.
 */
public class SignalMLException extends Exception {

	private static final long serialVersionUID = 1L;

	public SignalMLException() {
	}

	public SignalMLException(String message) {
		super(message);
	}

	public SignalMLException(Throwable cause) {
		super(cause);
	}

	public SignalMLException(String message, Throwable cause) {
		super(message, cause);
	}

}
