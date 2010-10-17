/* SignalMLException.java created 2007-09-20
 *
 */

package org.signalml.plugin.export;

/**
 * This class represents some exception in Svarog
 * @author Michal Dobaczewski &copy; 2007-2008 CC Otwarte Systemy Komputerowe Sp. z o.o.
 */
public class SignalMLException extends Exception {

	private static final long serialVersionUID = 1L;

	/**
	 * Empty constructor.
	 */
	public SignalMLException() {
	}

	/**
	 * Constructor.
     * @see Exception#Exception(String)
     */
	public SignalMLException(String message) {
		super(message);
	}

	/**
	 * Constructor.
	 * @see Exception#Exception(Throwable)
	 */
	public SignalMLException(Throwable cause) {
		super(cause);
	}

	/**
	 * Constructor.
	 * @see Exception#Exception(String, Throwable)
	 */
	public SignalMLException(String message, Throwable cause) {
		super(message, cause);
	}

}
