/* BookFormatException.java created 2008-02-16
 *
 */

package org.signalml.domain.book;

/** BookFormatException
 *
 *
 * @author Michal Dobaczewski &copy; 2007-2008 CC Otwarte Systemy Komputerowe Sp. z o.o.
 */
public class BookFormatException extends Exception {

	// TODO make this extend SignalMLException

	private static final long serialVersionUID = 1L;

	public BookFormatException() {
		super();
	}

	public BookFormatException(String message, Throwable cause) {
		super(message, cause);
	}

	public BookFormatException(String message) {
		super(message);
	}

	public BookFormatException(Throwable cause) {
		super(cause);
	}

}
