/* WSNoSuchUidException.java created 2008-02-18
 *
 */

package org.signalml.exception;

/** WSNoSuchUidException
 *
 *
 * @author Michal Dobaczewski &copy; 2007-2008 CC Otwarte Systemy Komputerowe Sp. z o.o.
 */
public class WSNoSuchUidException extends SignalMLWSException {

	private static final long serialVersionUID = 1L;

	private String uid;

	public WSNoSuchUidException(String uid) {
		super(new String[] { "error.ws.noSuchUid" }, new Object[] { uid }, "No such uid [" + uid + "]");
		this.uid = uid;
	}

	public String getUid() {
		return uid;
	}

}
