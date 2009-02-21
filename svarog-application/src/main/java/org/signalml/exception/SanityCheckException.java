/* SanityCheckException.java created 2007-09-21
 * 
 */

package org.signalml.exception;

/** SanityCheckException
 *
 * 
 * @author Michal Dobaczewski &copy; 2007-2008 CC Otwarte Systemy Komputerowe Sp. z o.o.
 */
public class SanityCheckException extends RuntimeException {

	private static final long serialVersionUID = 1L;

	public SanityCheckException() {
		super();
	}

	public SanityCheckException(String message, Throwable cause) {
		super(message, cause);
	}

	public SanityCheckException(String message) {
		super(message);
	}

	public SanityCheckException(Throwable cause) {
		super(cause);
	}
	
}
