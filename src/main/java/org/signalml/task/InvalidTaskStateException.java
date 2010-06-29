/* InvalidTaskStateException.java created 2007-09-12
 *
 */
package org.signalml.task;

/** This exception indicates that the internal state of the task (usually it's status)
 *  does not permit the requested operation to be performed (i.e. aborting an already
 *  aborted task is not possible).
 *
 * @author Michal Dobaczewski &copy; 2007-2008 CC Otwarte Systemy Komputerowe Sp. z o.o.
 */
public class InvalidTaskStateException extends RuntimeException {

	private static final long serialVersionUID = 1L;

	public InvalidTaskStateException(String arg0) {
		super(arg0);
	}

}
