/* CompilationRefusedException.java created 2008-03-03
 *
 */

package org.signalml.compilation;

/** CompilationRefusedException
 *
 *
 * @author Michal Dobaczewski &copy; 2007-2008 CC Otwarte Systemy Komputerowe Sp. z o.o.
 */
public class CompilationRefusedException extends CompilationException {

	private static final long serialVersionUID = 1L;

	public CompilationRefusedException() {
		super();
	}

	public CompilationRefusedException(String message, Throwable cause) {
		super(message, cause);
	}

	public CompilationRefusedException(String message) {
		super(message);
	}

	public CompilationRefusedException(Throwable cause) {
		super(cause);
	}



}
