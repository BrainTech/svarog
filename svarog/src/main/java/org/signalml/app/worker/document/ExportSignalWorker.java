/* ExportSignalWorker.java created 2008-01-27
 *
 */

package org.signalml.app.worker.document;

import static org.signalml.app.util.i18n.SvarogI18n._;

import java.io.File;
import java.io.IOException;
import java.util.List;

import javax.swing.SwingWorker;

import org.apache.log4j.Logger;
import org.signalml.app.document.signal.SignalDocument;
import org.signalml.app.model.signal.SignalExportDescriptor;
import org.signalml.app.view.common.dialogs.PleaseWaitDialog;
import org.signalml.domain.signal.ExportFormatType;
import org.signalml.domain.signal.MultichannelSampleProcessor;
import org.signalml.domain.signal.SignalProcessingChain;
import org.signalml.domain.signal.SignalWriterMonitor;
import org.signalml.domain.signal.export.ISignalWriter;
import org.signalml.domain.signal.export.eeglab.EEGLabSignalWriter;
import org.signalml.domain.signal.filter.export.MultichannelSampleFilterForExport;
import org.signalml.domain.signal.samplesource.MultichannelSampleSource;
import org.signalml.math.iirdesigner.BadFilterParametersException;


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

		ExportFormatType formatType = descriptor.getFormatType();
		ISignalWriter signalWriter = formatType.getSignalWriter();

		if (formatType == ExportFormatType.EEGLab) {
			((EEGLabSignalWriter) signalWriter).setSignalDocument(signalDocument);
		}

		prepareFilteredData();

		pleaseWaitDialog.setActivity(_("exporting signal"));
		signalWriter.writeSignal(signalFile, sampleSource, descriptor, this);

		return null;
	}

	/**
	 * Filters the data before exporting it.
	 * @throws BadFilterParametersException
	 * @throws IOException
	 */
	protected void prepareFilteredData() throws BadFilterParametersException, IOException {
		MultichannelSampleProcessor channelSubsetSampleSource = ((MultichannelSampleProcessor)sampleSource);
		SignalProcessingChain signalProcessingChain = ((SignalProcessingChain)channelSubsetSampleSource.getSource());
		if (signalProcessingChain.getOutput() instanceof MultichannelSampleFilterForExport) {
			MultichannelSampleFilterForExport multichannelSampleFilterForExport = (MultichannelSampleFilterForExport) signalProcessingChain.getOutput();
			multichannelSampleFilterForExport.setSignalWriterMonitor(this);
			pleaseWaitDialog.setActivity(_("filtering data"));

			int maximumSampleCount = signalProcessingChain.getSampleCount(0);
			pleaseWaitDialog.configureForDeterminate(0, maximumSampleCount, 0);

			multichannelSampleFilterForExport.prepareFilteredData();
		}
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
