/* MP5ExecutorManagerEvent.java created 2008-02-08
 *
 */

package org.signalml.app.method.mp5;

import java.util.EventObject;

/** MP5ExecutorManagerEvent
 *
 *
 * @author Michal Dobaczewski &copy; 2007-2008 CC Otwarte Systemy Komputerowe Sp. z o.o.
 */
public class MP5ExecutorManagerEvent extends EventObject {

	private static final long serialVersionUID = 1L;

	private int index;

	public MP5ExecutorManagerEvent(Object source) {
		super(source);
	}

	public MP5ExecutorManagerEvent(Object source, int index) {
		super(source);
		this.index = index;
	}

	public int getIndex() {
		return index;
	}

}
