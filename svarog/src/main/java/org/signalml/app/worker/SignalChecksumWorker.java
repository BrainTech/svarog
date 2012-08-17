/* SignalChecksumWorker.java created 2007-10-17
 *
 */

package org.signalml.app.worker;

import java.util.List;

import javax.swing.SwingWorker;

import org.apache.log4j.Logger;
import org.signalml.app.document.signal.SignalChecksumProgressMonitor;
import org.signalml.app.document.signal.SignalDocument;
import org.signalml.app.view.common.dialogs.PleaseWaitDialog;
import org.signalml.domain.signal.SignalChecksum;

/** SignalChecksumWorker
 *
 *
 * @author Michal Dobaczewski &copy; 2007-2008 CC Otwarte Systemy Komputerowe Sp. z o.o.
 */
public class SignalChecksumWorker extends SwingWorker<SignalChecksum[],Long> implements SignalChecksumProgressMonitor {

	protected static final Logger logger = Logger.getLogger(SignalChecksumWorker.class);

	private static final int LOWER_PRIORITY = Thread.MIN_PRIORITY;

	private SignalDocument signalDocument;
	private PleaseWaitDialog pleaseWaitDialog;
	private String[] methods;
	private boolean lowerPriority = false;
	private Thread backgroundThread = null;

	private volatile long bytesProcessed = 0;

	public SignalChecksumWorker(SignalDocument signalDocument, PleaseWaitDialog pleaseWaitDialog, String[] methods) {
		this.signalDocument = signalDocument;
		this.pleaseWaitDialog = pleaseWaitDialog;
		this.methods = methods;
	}

	@Override
	protected SignalChecksum[] doInBackground() throws Exception {

		logger.debug("Checksumming document [" + signalDocument.toString() + "]");

		synchronized (this) {
			backgroundThread = Thread.currentThread();
			if (lowerPriority) {
				backgroundThread.setPriority(LOWER_PRIORITY);
				logger.debug("Priority lowered for (dIB) [" + signalDocument.toString() + "]");
			}
		}

		SignalChecksum[] checksums = signalDocument.getChecksums(methods, this);
		logger.debug("Checksumming document [" + signalDocument.toString() + "] done");

		return checksums;

	}

	public void lowerPriority() {
		synchronized (this) {
			if (backgroundThread != null) {
				backgroundThread.setPriority(LOWER_PRIORITY);
				logger.debug("Priority lowered for [" + signalDocument.toString() + "]");
			}
			lowerPriority = true;
		}
	}

	public void normalPriority() {
		synchronized (this) {
			if (backgroundThread != null) {
				backgroundThread.setPriority(Thread.NORM_PRIORITY);
				logger.debug("Priority restored for [" + signalDocument.toString() + "]");
			}
			lowerPriority = false;
		}
	}

	public boolean isLowerPriority() {
		synchronized (this) {
			return lowerPriority;
		}
	}

	public long getBytesProcessed() {
		return bytesProcessed;
	}

	public void setBytesProcessed(long bytesProceeded) {
		if (this.bytesProcessed != bytesProceeded) {
			this.bytesProcessed = bytesProceeded;
			publish(bytesProceeded);
		}
	}

	public PleaseWaitDialog getPleaseWaitDialog() {
		synchronized (pleaseWaitDialog) {
			return pleaseWaitDialog;
		}
	}

	public void setPleaseWaitDialog(PleaseWaitDialog pleaseWaitDialog) {
		if (this.pleaseWaitDialog != null) {
			synchronized (this.pleaseWaitDialog) {
				this.pleaseWaitDialog = pleaseWaitDialog;
			}
		} else {
			this.pleaseWaitDialog = pleaseWaitDialog;
		}
		logger.debug("Please wait dialog set for [" + signalDocument.toString() + "]");
	}

	@Override
	protected void done() {
		if (pleaseWaitDialog != null) {
			synchronized (pleaseWaitDialog) {
				logger.debug("Releasing please wait dialog set for [" + signalDocument.toString() + "]");
				pleaseWaitDialog.releaseIfOwnedBy(this);
			}
		}
	}

	@Override
	protected void process(List<Long> chunks) {
		if (pleaseWaitDialog != null && !chunks.isEmpty()) {
			synchronized (pleaseWaitDialog) {
				pleaseWaitDialog.setProgress((int)((long) chunks.get(0)));
			}
		}
	}

}
