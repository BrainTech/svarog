package org.signalml.app.document;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.LinkedBlockingQueue;

import javax.swing.JFileChooser;

import org.apache.log4j.Logger;
import org.signalml.app.model.OpenMonitorDescriptor;
import org.signalml.app.view.DocumentView;
import org.signalml.app.view.signal.SignalPlot;
import org.signalml.app.view.signal.SignalView;
import org.signalml.app.worker.MonitorWorker;
import org.signalml.app.worker.SignalRecorderWorker;
import org.signalml.domain.signal.RoundBufferMultichannelSampleSource;
import org.signalml.domain.signal.SignalChecksum;
import org.signalml.domain.signal.SignalProcessingChain;
import org.signalml.domain.signal.raw.RawSignalDescriptor;
import org.signalml.domain.signal.raw.RawSignalDescriptorWriter;
import org.signalml.domain.signal.raw.RawSignalDescriptor.SourceSignalType;
import org.signalml.domain.tag.StyledTagSet;
import org.signalml.exception.SignalMLException;
import org.signalml.util.FileUtils;

/**
 * @author Mariusz Podsiadło
 *
 */
public class MonitorSignalDocument extends AbstractSignal implements MutableDocument, FileBackedDocument {

	public static final String BACKING_FILE_PROPERTY = "backingFile";

	protected static final Logger logger = Logger.getLogger(MonitorSignalDocument.class);

	private OpenMonitorDescriptor monitorOptions;
	private File recorderOutputFile;
	private OutputStream recorderOutput;
	private MonitorWorker monitorWorker;
	private SignalRecorderWorker recorderWorker;
	private String name;

	private File backingFile = null;

	private boolean saved = true;

	public MonitorSignalDocument(OpenMonitorDescriptor monitorOptions) {
		super(monitorOptions.getType());
		this.monitorOptions = monitorOptions;
		double freq = monitorOptions.getSamplingFrequency();
		double ps = monitorOptions.getPageSize();
		int sampleCount = (int) Math.ceil(ps * freq);
		sampleSource = new RoundBufferMultichannelSampleSource(monitorOptions.getSelectedChannelList().length, sampleCount);
		((RoundBufferMultichannelSampleSource) sampleSource).setLabels(monitorOptions.getSelectedChannelList());
		((RoundBufferMultichannelSampleSource) sampleSource).setDocumentView(getDocumentView());

		recorderOutputFile = new File("signal.buf");
		try {
			recorderOutput = new FileOutputStream(recorderOutputFile);
		}
		catch (FileNotFoundException e) {
		}
	}

	public void setName(String name) {
		this.name = name;
	}

	public float getMinValue() {
		return monitorOptions.getMinimumValue();
	}

	public float getMaxValue() {
		return monitorOptions.getMaximumValue();
	}

	public double[] getGain() {
		double[] result = new double[monitorOptions.getChannelCount()];
		float[] fg = monitorOptions.getCalibrationGain();
		for (int i = 0; i < monitorOptions.getChannelCount(); i++)
			result[i] = fg[i];
		return result;
	}

	public double[] getOffset() {
		double[] result = new double[monitorOptions.getChannelCount()];
		float[] fg = monitorOptions.getCalibrationOffset();
		for (int i = 0; i < monitorOptions.getChannelCount(); i++)
			result[i] = fg[i];
		return result;
	}

	@Override
	public void setDocumentView(DocumentView documentView) {
		super.setDocumentView(documentView);
		if (documentView != null) {
			for (Iterator<SignalPlot> i = ((SignalView) documentView).getPlots().iterator(); i.hasNext();) {
				SignalPlot signalPlot = i.next();
				SignalProcessingChain signalChain = SignalProcessingChain.createNotBufferedFilteredChain(sampleSource, getType());
				signalPlot.setSignalChain(signalChain);
			}
		}
		if (sampleSource != null && sampleSource instanceof RoundBufferMultichannelSampleSource) {
			((RoundBufferMultichannelSampleSource) sampleSource).setDocumentView(documentView);
		}
	}

	public OutputStream getRecorderOutput() {
		return recorderOutput;
	}

	public void setRecorderOutput(OutputStream recorderOutput) {
		this.recorderOutput = recorderOutput;
	}

	@Override
	public void openDocument() throws SignalMLException, IOException {

		setSaved(true);

		if (monitorOptions.getJmxClient() == null) {
			throw new IOException();
		}

		LinkedBlockingQueue< double[]> sampleQueue = null;
		if (recorderOutput != null) {
			sampleQueue = new LinkedBlockingQueue< double[]>();
			recorderWorker = new SignalRecorderWorker(sampleQueue, recorderOutputFile, monitorOptions, 50L);
			recorderWorker.execute();
		}

		monitorWorker = new MonitorWorker(monitorOptions.getJmxClient(), monitorOptions, (RoundBufferMultichannelSampleSource) sampleSource);
		if (sampleQueue != null)
			monitorWorker.setSampleQueue(sampleQueue);
		monitorWorker.execute();

	}

	@Override
	public void closeDocument() throws SignalMLException {

		int sampleCount = 0;
		StyledTagSet tagSet = null;
		if (monitorWorker != null && !monitorWorker.isCancelled()) {
			monitorWorker.cancel(false);
			do {
				try {
					Thread.sleep(1);
				}
				catch (InterruptedException e) {}
			} while (!monitorWorker.isFinished());
			tagSet = monitorWorker.getTagSet();
			monitorWorker = null;
		}

		if (recorderWorker != null && !recorderWorker.isCancelled()) {
			recorderWorker.cancel(false);
			do {
				try {
					Thread.sleep(1);
				}
				catch (InterruptedException e) {}
			} while (!recorderWorker.isFinished());
			sampleCount = recorderWorker.getSavedSampleCount();
			recorderWorker = null;
		}

		String path = null;
		if (backingFile != null) {
			try {
				path = backingFile.getCanonicalPath();
			}
			catch (IOException e) {}
		}
		if (path == null && monitorOptions.getFileName() != null) {
			File f = null;
			f = new File(monitorOptions.getFileName());
			try {
				path = f.getCanonicalPath();
			}
			catch (IOException e) {}
		}

		if (path != null) {

			String metadataPath = null;
			File metadataFile   = null;
			String dataPath	 = null;
			File dataFile	   = null;
			String tagPath	  = null;
			File tagFile		= null;

			if (path.endsWith(".xml")) {
				metadataPath = path;
				dataPath = path.substring(0, path.length() - 4) + ".raw";
				tagPath = path.substring(0, path.length() - 4) + ".tag";
			}
			else if (path.endsWith("raw")) {
				dataPath = path;
				metadataPath = path.substring(0, path.length() - 4) + ".xml";
				tagPath = path.substring(0, path.length() - 4) + ".tag";
			}
			else {
				metadataPath = path + ".xml";
				dataPath = path + ".raw";
				tagPath = path + ".tag";
			}
			metadataFile = new File(metadataPath);
			dataFile = new File(dataPath);
			tagFile = new File(tagPath);

			RawSignalDescriptor rsd = new RawSignalDescriptor();
			rsd.setExportFileName(dataPath);
			rsd.setBlocksPerPage(1);
			rsd.setByteOrder(monitorOptions.getByteOrder());
			rsd.setCalibrationGain(monitorOptions.getCalibrationGain());
			rsd.setCalibrationOffset(monitorOptions.getCalibrationOffset());
			rsd.setChannelCount(monitorOptions.getChannelCount());
			rsd.setChannelLabels(monitorOptions.getChannelLabels());
			rsd.setPageSize(monitorOptions.getPageSize().floatValue());
			rsd.setSampleCount(sampleCount);
			rsd.setSampleType(monitorOptions.getSampleType());
			rsd.setSamplingFrequency(monitorOptions.getSamplingFrequency());
			rsd.setSourceSignalType(SourceSignalType.RAW);
			RawSignalDescriptorWriter descrWriter = new RawSignalDescriptorWriter();
			try {
				descrWriter.writeDocument(rsd, metadataFile);
			}
			catch (IOException e) {
				throw new SignalMLException(e);
			}

			try {
				FileUtils.copyFile(recorderOutputFile, dataFile);
			}
			catch (IOException e) {
				throw new SignalMLException(e);
			}

			if (tagSet != null && tagSet.getTagCount() > 0) {
				TagDocument tagDoc = new TagDocument(tagSet);
				tagDoc.setBackingFile(tagFile);
				try {
					tagDoc.saveDocument();
				}
				catch (IOException e) {
					throw new SignalMLException(e);
				}
			}

		}

		super.closeDocument();
	}

	@Override
	public int getBlockCount() {
		return 1;
	}

	@Override
	public float getBlockSize() {
		return getPageSize();
	}

	@Override
	public int getChannelCount() {
		return sampleSource.getChannelCount();
	}

	@Override
	public SignalChecksum[] getChecksums(String[] types,
	                                     SignalChecksumProgressMonitor monitor) throws SignalMLException {
		return null;
	}

	@Override
	public String getFormatName() {
		return null;
	}

	@Override
	public float getMaxSignalLength() {
		return sampleSource.getSampleCount(0);
	}

	@Override
	public float getMinSignalLength() {
		return sampleSource.getSampleCount(0);
	}

	@Override
	public int getPageCount() {
		return 1;
	}

	@Override
	public float getPageSize() {
		return pageSize;
	}

	@Override
	public void setPageSize(float pageSize) {
		if (this.pageSize != pageSize) {
			float last = this.pageSize;
			this.pageSize = pageSize;
			this.blockSize = pageSize;
			pcSupport.firePropertyChange(PAGE_SIZE_PROPERTY, last, pageSize);
		}
	}

	@Override
	public int getBlocksPerPage() {
		return 1;
	}

	@Override
	public void setBlocksPerPage(int blocksPerPage) {
		if (blocksPerPage != 1)
			throw new IllegalArgumentException();
		this.blocksPerPage = 1;
	}

	@Override
	public void addDependentDocument(Document document) {
		throw new UnsupportedOperationException();
	}

	@Override
	public void removeDependentDocument(Document document) {
		throw new UnsupportedOperationException();
	}

	@Override
	public List<Document> getDependentDocuments() {
		return new ArrayList<Document>();
	}

	@Override
	public boolean hasDependentDocuments() {
		return false;
	}

//	@Override
	public String getName() {
		return name;
	}

	@Override
	public void addTagDocument(TagDocument document) {
		throw new UnsupportedOperationException();
	}

	@Override
	public TagDocument getActiveTag() {
		// dla monitora tagi nie są obsługiwane - na razie
		return null;
	}

////////////////////////////////////////////////////////////////////////////////////////////////////////////////

	@Override
	public boolean isSaved() {
		return saved;
	}

	@Override
	public void setSaved(boolean saved) {
		if (this.saved != saved) {
			this.saved = saved;
			pcSupport.firePropertyChange(AbstractMutableFileDocument.SAVED_PROPERTY, !saved, saved);
		}
	}

	public void invalidate() {
		setSaved(false);
	}

	@Override
	public final void saveDocument() throws SignalMLException, IOException {


		if (backingFile == null) {

			String fileName = monitorOptions.getFileName();
			if (fileName != null && !"".equals(fileName)) {
				backingFile = new File(monitorOptions.getFileName());
			}
			else {
				JFileChooser fileChooser = new JFileChooser();
				int res = fileChooser.showSaveDialog(null);
				if (res == JFileChooser.APPROVE_OPTION) {
					backingFile = fileChooser.getSelectedFile();
				}

				if (backingFile != null) {
					setSaved(false);
				}
			}

		}

		setSaved(true);

	}

	@Override
	public void newDocument() throws SignalMLException {

	}


	@Override
	public File getBackingFile() {
		return backingFile;
	}


	@Override
	public void setBackingFile(File file) {
		this.backingFile = file;
	}


}
