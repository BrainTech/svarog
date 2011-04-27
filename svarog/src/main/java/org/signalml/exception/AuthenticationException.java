/* AuthenticationException.java created 2008-02-18
 *
 */

package org.signalml.exception;

/**
 * TODO this exception is not referenced anywhere, delete it?
 * 
 * Thrown to indicate authentication attempt failure.
 *
 * @author Michal Dobaczewski &copy; 2007-2008 CC Otwarte Systemy Komputerowe Sp. z o.o.
 */
public class AuthenticationException extends SignalMLWSException {

	private static final long serialVersionUID = 1L;

	public AuthenticationException(String code) {
		super(new String[] { code }, new Object[0]);
	}

	public AuthenticationException(String[] codes, Object[] arguments, String defaultMessage) {
		super(codes, arguments, defaultMessage);
	}

	public AuthenticationException(String[] codes, Object[] arguments) {
		super(codes, arguments);
	}

	public AuthenticationException(Throwable cause) {
		super(cause);
	}

}
