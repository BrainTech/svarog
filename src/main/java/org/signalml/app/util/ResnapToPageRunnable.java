/* ResnapToPageRunnable.java created 2007-12-17
 *
 */

package org.signalml.app.util;

import org.signalml.app.view.signal.SignalView;

/** ResnapToPageRunnable
 *
 *
 * @author Michal Dobaczewski &copy; 2007-2008 CC Otwarte Systemy Komputerowe Sp. z o.o.
 */
public class ResnapToPageRunnable implements Runnable {

	private SignalView signalView;

	public ResnapToPageRunnable(SignalView signalView) {
		this.signalView = signalView;
	}

	@Override
	public void run() {
		if (!signalView.isClosed()) {
			signalView.snapPageToView();
		}
	}

}
