/* CompilationException.java created 2007-11-07
 *
 */

package org.signalml.compilation;

import org.signalml.plugin.export.SignalMLException;

/** CompilationException
 *
 *
 * @author Michal Dobaczewski &copy; 2007-2008 CC Otwarte Systemy Komputerowe Sp. z o.o.
 */
public class CompilationException extends SignalMLException {

	private static final long serialVersionUID = 1L;

	public CompilationException() {
		super();
	}

	public CompilationException(String message, Throwable cause) {
		super(message, cause);
	}

	public CompilationException(String message) {
		super(message);
	}

	public CompilationException(Throwable cause) {
		super(cause);
	}

}
