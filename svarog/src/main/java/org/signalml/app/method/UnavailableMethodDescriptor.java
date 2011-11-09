/* UnavailableMethodDescriptor.java created 2008-03-03
 *
 */

package org.signalml.app.method;

/** UnavailableMethodDescriptor
 *
 *
 * @author Michal Dobaczewski &copy; 2007-2008 CC Otwarte Systemy Komputerowe Sp. z o.o.
 */
public class UnavailableMethodDescriptor {

	private String name;
	private Throwable exception;

	public UnavailableMethodDescriptor(String name, Throwable exception) {
		this.name = name;
		this.exception = exception;
	}

	public String getIconPath() {
		return "org/signalml/app/icon/unavailablemethod.png";
	}

	public String getName() {
		return name;
	}

	public Throwable getException() {
		return exception;
	}

}
