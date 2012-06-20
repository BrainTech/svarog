/* ExportSignalWorker.java created 2008-01-27
 *
 */

package org.signalml.app.worker.document;

import java.io.File;
import java.util.List;

import javax.swing.SwingWorker;

import org.apache.log4j.Logger;
import org.signalml.app.document.SignalDocument;
import org.signalml.app.model.signal.SignalExportDescriptor;
import org.signalml.app.view.components.dialogs.PleaseWaitDialog;
import org.signalml.domain.signal.ExportFormatType;
import org.signalml.domain.signal.SignalWriterMonitor;
import org.signalml.domain.signal.ascii.ASCIISignalWriter;
import org.signalml.domain.signal.eeglab.EEGLabSignalWriter;
import org.signalml.domain.signal.raw.RawSignalWriter;
import org.signalml.domain.signal.samplesource.MultichannelSampleSource;


/** ExportSignalWorker
 *
 *
 * @author Michal Dobaczewski &copy; 2007-2008 CC Otwarte Systemy Komputerowe Sp. z o.o.
 */
public class ExportSignalWorker extends SwingWorker<Void,Integer> implements SignalWriterMonitor {

	protected static final Logger logger = Logger.getLogger(ExportSignalWorker.class);

	private MultichannelSampleSource sampleSource;
	private File signalFile;
	private SignalExportDescriptor descriptor;

	private SignalDocument signalDocument;

	private PleaseWaitDialog pleaseWaitDialog;

	private volatile boolean requestingAbort;

	private volatile int processedSampleCount;

	public ExportSignalWorker(MultichannelSampleSource sampleSource, File signalFile, SignalExportDescriptor descriptor, PleaseWaitDialog pleaseWaitDialog) {
		this.sampleSource = sampleSource;
		this.signalFile = signalFile;
		this.descriptor = descriptor;
		this.pleaseWaitDialog = pleaseWaitDialog;
	}

	public ExportSignalWorker(MultichannelSampleSource sampleSource, File signalFile, SignalExportDescriptor descriptor, PleaseWaitDialog pleaseWaitDialog, SignalDocument signalDocument) {
		this.sampleSource = sampleSource;
		this.signalFile = signalFile;
		this.descriptor = descriptor;
		this.pleaseWaitDialog = pleaseWaitDialog;
		this.signalDocument = signalDocument;
	}

	@Override
	protected Void doInBackground() throws Exception {
		if (descriptor == null || descriptor.getFormatType() == ExportFormatType.RAW){
			RawSignalWriter rawSignalWriter = new RawSignalWriter();
 			rawSignalWriter.writeSignal(signalFile, sampleSource, descriptor, this);
		} else if (descriptor.getFormatType() == ExportFormatType.ASCII){
			ASCIISignalWriter writer = new ASCIISignalWriter(signalFile, sampleSource, descriptor, this);
			writer.writeSignal();
		} else {
			new EEGLabSignalWriter().writeSignal(signalFile, sampleSource, descriptor, signalDocument, this);
		}

		return null;

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
