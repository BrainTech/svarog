/* AbstractSignalWriterMonitor.java created 2008-03-18
 *
 */

package org.signalml.domain.signal;

/**
 * This abstract class represents the monitor that monitors the processing
 * of samples. Can request abortion of that process.
 * 
 * @see SignalWriterMonitor
 * @author Michal Dobaczewski &copy; 2007-2008 CC Otwarte Systemy Komputerowe Sp. z o.o.
 */
public abstract class AbstractSignalWriterMonitor implements SignalWriterMonitor {

	private volatile boolean requestingAbort;

	@Override
	public void abort() {
		synchronized (this) {
			requestingAbort = true;
		}

	}

	@Override
	public boolean isRequestingAbort() {
		return requestingAbort;
	}

}
