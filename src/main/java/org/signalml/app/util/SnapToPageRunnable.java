/* SnapToPageRunnable.java created 2007-12-16
 * 
 */

package org.signalml.app.util;

import org.signalml.app.view.signal.SignalView;

/** SnapToPageRunnable
 *
 * 
 * @author Michal Dobaczewski &copy; 2007-2008 CC Otwarte Systemy Komputerowe Sp. z o.o.
 */
public class SnapToPageRunnable implements Runnable {

	private SignalView signalView;
	private boolean snapToPageMode;
	
	public SnapToPageRunnable(SignalView signalView, boolean snapToPageMode) {
		this.signalView = signalView;
		this.snapToPageMode = snapToPageMode;
	}
	
	@Override
	public void run() {
		if( !signalView.isClosed() ) {
			signalView.setSnapToPageMode(snapToPageMode);
		}
	}

}
