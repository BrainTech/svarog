/* ScanSignalWorker.java created 2008-01-30
 *
 */

package org.signalml.app.worker;

import java.util.List;

import javax.swing.SwingWorker;

import org.apache.log4j.Logger;
import org.signalml.app.view.dialog.PleaseWaitDialog;
import org.signalml.app.view.signal.SignalScanResult;
import org.signalml.domain.signal.MultichannelSampleSource;
import org.signalml.domain.signal.SignalScanner;
import org.signalml.domain.signal.SignalWriterMonitor;

/** ScanSignalWorker
 *
 *
 * @author Michal Dobaczewski &copy; 2007-2008 CC Otwarte Systemy Komputerowe Sp. z o.o.
 */
public class ScanSignalWorker extends SwingWorker<SignalScanResult,Integer> implements SignalWriterMonitor {

	protected static final Logger logger = Logger.getLogger(ScanSignalWorker.class);

	private MultichannelSampleSource sampleSource;

	private PleaseWaitDialog pleaseWaitDialog;

	private volatile boolean requestingAbort;

	private volatile int processedSampleCount;

	public ScanSignalWorker(MultichannelSampleSource sampleSource, PleaseWaitDialog pleaseWaitDialog) {
		this.sampleSource = sampleSource;
		this.pleaseWaitDialog = pleaseWaitDialog;
	}

	@Override
	protected SignalScanResult doInBackground() throws Exception {

		SignalScanner signalScanner = new SignalScanner();

		return signalScanner.scanSignal(sampleSource,  this);

	}


	public int getProcessedSampleCount() {
		return processedSampleCount;
	}

	@Override
	public void setProcessedSampleCount(int processedSampleCount) {
		if (this.processedSampleCount != processedSampleCount) {
			this.processedSampleCount = processedSampleCount;
			publish(processedSampleCount);
		}
	}

	public PleaseWaitDialog getPleaseWaitDialog() {
		synchronized (pleaseWaitDialog) {
			return pleaseWaitDialog;
		}
	}

	@Override
	protected void done() {
		if (pleaseWaitDialog != null) {
			pleaseWaitDialog.releaseIfOwnedBy(this);
		}
	}

	@Override
	protected void process(List<Integer> chunks) {
		if (pleaseWaitDialog != null && !chunks.isEmpty()) {
			synchronized (pleaseWaitDialog) {
				pleaseWaitDialog.setProgress((int) chunks.get(0));
			}
		}
	}

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
