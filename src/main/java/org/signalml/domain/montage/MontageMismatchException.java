/* MontageMismatchException.java created 2007-10-25
 *
 */

package org.signalml.domain.montage;

import org.signalml.exception.SignalMLException;

/** MontageMismatchException
 *
 *
 * @author Michal Dobaczewski &copy; 2007-2008 CC Otwarte Systemy Komputerowe Sp. z o.o.
 */
public class MontageMismatchException extends SignalMLException {

	private static final long serialVersionUID = 1L;

	public MontageMismatchException() {
		super();
	}

	public MontageMismatchException(String message, Throwable cause) {
		super(message, cause);
	}

	public MontageMismatchException(String message) {
		super(message);
	}

	public MontageMismatchException(Throwable cause) {
		super(cause);
	}

}
