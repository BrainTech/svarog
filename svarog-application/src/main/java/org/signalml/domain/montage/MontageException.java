/* MontageException.java created 2007-11-22
 * 
 */

package org.signalml.domain.montage;

import org.signalml.exception.SignalMLException;

/** MontageException
 *
 * 
 * @author Michal Dobaczewski &copy; 2007-2008 CC Otwarte Systemy Komputerowe Sp. z o.o.
 */
public class MontageException extends SignalMLException {

	private static final long serialVersionUID = 1L;

	public MontageException() {
		super();
	}

	public MontageException(String message, Throwable cause) {
		super(message, cause);
	}

	public MontageException(String message) {
		super(message);
	}

	public MontageException(Throwable cause) {
		super(cause);
	}
	
}
