/* ComputationException.java created 2007-09-12
 *
 */
package org.signalml.method;

import org.signalml.exception.ResolvableException;

/**
 * A generic exception during computation, not related to errors in the input data.
 *
 * @author Michal Dobaczewski &copy; 2007-2008 CC Otwarte Systemy Komputerowe Sp. z o.o.
 */
public class ComputationException extends ResolvableException {

	private static final long serialVersionUID = 1L;

	/**
	 * Constructs a new computation exception with "error.computationException"
	 * as its only one code, "" as detail message and empty array of arguments.
	 */
	public ComputationException() {
		super(new String[] { "error.computationException" }, new Object[0]);
	}

	/**
	 * Constructs a new computation exception with specified message and cause of it.
	* Array of codes get this message as its only one element and array of arguments is empty.
	 * @param message String representation of this exception detail message
	 * @param cause the cause (which is saved for later retrieval by the Throwable.getCause()
	 * method). (A null value is permitted, and indicates that the cause is nonexistent or
	 * unknown.)
	       */
	public ComputationException(String message, Throwable cause) {
		super(message, cause);
	}

	/**
	 * Constructs a new computation exception with specified message.
	 * Array of codes get this message as its only one element and array of arguments is empty.
	     * @param message String representation of this exception detail message
	 */
	public ComputationException(String message) {
		super(message);
	}

	/**
	 * Constructs a new computation exception with specified message and arguments.
	 * Array of codes get this message as its only one element.
	 * @param message String representation of this exception detail message
	 */
	public ComputationException(String message, Object[] arguments) {
		super(message, arguments);
	}

	/**
	 * Constructs a new exception with the specified cause.
	 * If cause is an instance of class MessageSourceResolvable then codes, arguments and
	 * message are copied from this message.  Otherwise, codes becomes array of names of
	 * following exceptions occured, arguments has two elements: name of class of exception
	 * occured and message from last exception or "" if this message is null. Detail message
	 * becomes message from last exception or "Exception occured" when this message is null.
	 * @param cause (which is saved for later retrieval by the Throwable.getCause() method).
	 * (A null value is permitted, and indicates that the cause is nonexistent or unknown.)
	 */
	public ComputationException(Throwable cause) {
		super(cause);
	}

}
