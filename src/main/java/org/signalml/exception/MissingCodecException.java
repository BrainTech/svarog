/* MissingCodecException.java created 2007-09-21
 * 
 */

package org.signalml.exception;

/** MissingCodecException
 *
 * 
 * @author Michal Dobaczewski &copy; 2007-2008 CC Otwarte Systemy Komputerowe Sp. z o.o.
 */
public class MissingCodecException extends SignalMLException {

	private static final long serialVersionUID = 1L;

	public MissingCodecException() {
		super();
	}

	public MissingCodecException(String message, Throwable cause) {
		super(message, cause);
	}

	public MissingCodecException(String message) {
		super(message);
	}

	public MissingCodecException(Throwable cause) {
		super(cause);
	}
	
}
