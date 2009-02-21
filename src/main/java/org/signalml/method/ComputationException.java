/* ComputationException.java created 2007-09-12
 * 
 */
package org.signalml.method;

import org.signalml.exception.ResolvableException;

/** A generic exception during computation, not related to errors in the input data.
 * 
 * @author Michal Dobaczewski &copy; 2007-2008 CC Otwarte Systemy Komputerowe Sp. z o.o.
 */
public class ComputationException extends ResolvableException {

	private static final long serialVersionUID = 1L;
	
	public ComputationException() {
		super(new String[] { "error.computationException" }, new Object[0] );
	}

	public ComputationException(String message, Throwable cause) {
		super(message, cause);
	}

	public ComputationException(String message) {
		super(message);
	}

	public ComputationException(String message, Object[] arguments ) {
		super(message, arguments);
	}
	
	public ComputationException(Throwable cause) {
		super(cause);
	}
	
}
