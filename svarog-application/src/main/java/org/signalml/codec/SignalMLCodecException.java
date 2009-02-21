/* SignalMLCodecException.java created 2007-09-18
 * 
 */

package org.signalml.codec;

import org.signalml.exception.SignalMLException;

/** SignalMLCodecException
 *
 * 
 * @author Michal Dobaczewski &copy; 2007-2008 CC Otwarte Systemy Komputerowe Sp. z o.o.
 */
public class SignalMLCodecException extends SignalMLException {

	private static final long serialVersionUID = 1L;

	public SignalMLCodecException() {
		super();
	}

	public SignalMLCodecException(String message, Throwable cause) {
		super(message, cause);
	}

	public SignalMLCodecException(String message) {
		super(message);
	}

	public SignalMLCodecException(Throwable cause) {
		super(cause);
	}

	
}
