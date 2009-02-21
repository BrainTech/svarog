/* SignalMLWSException.java created 2008-02-17
 * 
 */

package org.signalml.exception;

/** SignalMLWSException
 *
 * 
 * @author Michal Dobaczewski &copy; 2007-2008 CC Otwarte Systemy Komputerowe Sp. z o.o.
 */
public class SignalMLWSException extends ResolvableException {

	private static final long serialVersionUID = 1L;

	public SignalMLWSException(String[] codes, Object[] arguments, String defaultMessage) {
		super(codes, arguments, defaultMessage);
	}

	public SignalMLWSException(String[] codes, Object[] arguments) {
		super(codes, arguments);
	}

	public SignalMLWSException(Throwable cause) {
		super(cause);
	}
	
}
