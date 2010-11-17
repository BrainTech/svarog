package org.signalml.app.document;

import java.beans.IntrospectionException;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.logging.Level;
import javax.swing.SwingWorker;

import org.apache.log4j.Logger;
import org.signalml.app.model.LabelledPropertyDescriptor;
import org.signalml.app.model.MonitorRecordingDescriptor;
import org.signalml.app.model.OpenMonitorDescriptor;
import org.signalml.domain.montage.MontageMismatchException;
import org.signalml.plugin.export.view.DocumentView;
import org.signalml.app.view.signal.SignalPlot;
import org.signalml.app.view.signal.SignalView;
import org.signalml.app.worker.MonitorWorker;
import org.signalml.app.worker.SignalRecorderWorker;
import org.signalml.app.worker.TagRecorder;
import org.signalml.domain.signal.RoundBufferMultichannelSampleSource;
import org.signalml.domain.signal.RoundBufferSampleSource;
import org.signalml.domain.signal.SignalChecksum;
import org.signalml.domain.signal.SignalProcessingChain;
import org.signalml.domain.signal.raw.RawSignalDescriptor;
import org.signalml.domain.signal.raw.RawSignalDescriptorWriter;
import org.signalml.domain.signal.raw.RawSignalDescriptor.SourceSignalType;
import org.signalml.domain.tag.StyledTagSet;
import org.signalml.domain.tag.StyledMonitorTagSet;
import org.signalml.plugin.export.SignalMLException;
import org.signalml.plugin.export.signal.TagStyle;
import org.signalml.util.FileUtils;

/**
 * @author Mariusz Podsiadło
 *
 */
public class MonitorSignalDocument extends AbstractSignal implements MutableDocument {

	/**
	 * A property describing whether this document is recording its signal.
	 */
	public static String IS_RECORDING_PROPERTY = "isRecording";

	/**
	 * A logger to save history of execution at.
	 */
	protected static final Logger logger = Logger.getLogger(MonitorSignalDocument.class);

	/**
	 * The name of this document.
	 */
	private String name;

	/**
	 * Describes the parameters of the monitor connected to this {@link MonitorSignalDocument}.
	 */
	private OpenMonitorDescriptor monitorOptions;

	/**
	 * This worker is responsible for receiving the monitor signal and tags.
	 */
	private MonitorWorker monitorWorker;

	/**
	 * This {@link RoundBufferMultichannelSampleSource} stores the timestamps
	 * of each sample in the sample sample source
	 */
	private RoundBufferSampleSource timestampsSource;

	/**
	 * This worker is responsible for recording the samples to a file.
	 */
	private SignalRecorderWorker signalRecorderWorker = null;

	/**
	 * This worker is responsible for recording the tags to a file.
	 */
	private TagRecorder tagRecorderWorker = null;

	/**
	 * The name of the file, to which the samples will be written by the signalRecorderWorker.
	 */
	private static final String signalRecorderBufferFilename = "signal.buf";

	/**
	 * The file to which the samples will be written by the signalRecorderWorker (its name is signalRecorderBufferFilename).
	 * It is used as a buffer before the samples are written to the signalRecorderOutputFile.
	 */
	private File signalRecorderBufferFile;

	/**
	 * This is the file, to which the signal recorder writes the data after it has recorded all samples.
	 */
	private File signalRecorderOutputFile;

	/**
	 * This is the file to which tags are written after all tags are recorded.
	 */
	private File tagRecorderOutputFile;

	StyledMonitorTagSet tagSet;

	private boolean saved = true;

	public MonitorSignalDocument(OpenMonitorDescriptor monitorOptions) {

		super(monitorOptions.getType());
		this.monitorOptions = monitorOptions;
		double freq = monitorOptions.getSamplingFrequency();
		double ps = monitorOptions.getPageSize();
		int sampleCount = (int) Math.ceil(ps * freq);
		sampleSource = new RoundBufferMultichannelSampleSource(monitorOptions.getSelectedChannelList().length, sampleCount);
		timestampsSource = new RoundBufferSampleSource(sampleCount);
		((RoundBufferMultichannelSampleSource) sampleSource).setLabels(monitorOptions.getSelectedChannelsLabels());
		((RoundBufferMultichannelSampleSource) sampleSource).setDocumentView(getDocumentView());

	}

	public RoundBufferSampleSource getTimestampSource() {
		return timestampsSource;
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

	/**
	 * Returns an integer value representing amplifier`s channel value for non-connected channel
	 * @return an integer value representing amplifier`s channel value for non-connected channel
	 */
	public double getAmplifierNull() {
		return monitorOptions.getAmplifierNull();
	}

	@Override
	public void setDocumentView(DocumentView documentView) {
		super.setDocumentView(documentView);
		if (documentView != null) {
			for (Iterator<SignalPlot> i = ((SignalView) documentView).getPlots().iterator(); i.hasNext();) {
				SignalPlot signalPlot = i.next();
				SignalProcessingChain signalChain = SignalProcessingChain.createNotBufferedFilteredChain(sampleSource, getType());
				try {
					signalChain.applyMontageDefinition(this.getMontage());
				} catch (MontageMismatchException ex) {
					logger.error("Failed to apply montage to the Signal Chain.", ex);
				}
				signalPlot.setSignalChain(signalChain);
			}
		}
		if (sampleSource != null && sampleSource instanceof RoundBufferMultichannelSampleSource) {
			((RoundBufferMultichannelSampleSource) sampleSource).setDocumentView(documentView);
		}
	}

	@Override
	public void openDocument() throws SignalMLException, IOException {

		setSaved(true);

		if (monitorOptions.getJmxClient() == null) {
			throw new IOException();
		}

		logger.info("Start initializing monitor data.");
		tagSet = new StyledMonitorTagSet((float) 20.0, 5);//TODO - pobierac rozmiar strony w init
		TagDocument tagDoc = new TagDocument(tagSet);
		tagDoc.setParent(this);
		monitorWorker = new MonitorWorker(monitorOptions.getJmxClient(), monitorOptions, (RoundBufferMultichannelSampleSource) sampleSource, timestampsSource, tagSet);
		monitorWorker.execute();
		logger.info("Monitor executed.");

	}

	@Override
	public void closeDocument() throws SignalMLException {

		//stop recording
		try {
			stopMonitorRecording();
		} catch (IOException ex) {
			logger.error("Cannot stop monitor recording for this monitor.", ex);
		}

		//stop monitor worker
		if (monitorWorker != null && !monitorWorker.isCancelled()) {
			monitorWorker.cancel(false);
			do {
				try {
					Thread.sleep(1);
				}
				catch (InterruptedException e) {}
			} while (!monitorWorker.isFinished());
			monitorWorker = null;
		}

		//close document
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

	/**
	 * Returns whether this monitor document is recording the signal (and optionally tags)
	 * to a file.
	 *
	 * @return true, if this {@link MonitorSignalDocument} is recording the signal (and optionally
	 * tags) it monitors, false otherwise
	 */
	public boolean isRecording() {
		if (signalRecorderWorker != null)
			return true;
		return false;
	}

	@Override
	public String getName() {
		return name;
	}

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
		throw new UnsupportedOperationException("Saving monitor document is not supported - use monitor recording instead.");
	}

	@Override
	public void newDocument() throws SignalMLException {

	}

	/**
	 * Starts to record this monitor document samples and tags according
	 * to the configuration (file names etc.) maintained in the
	 * {@link OpenMonitorDescriptor} related to this MonitorSignalDocument
	 * (this configuration can be changed by changing this object:
	 * {@link MonitorSignalDocument#getOpenMonitorDescriptor()}.
	 * After calling {@link MonitorSignalDocument#stopMonitorRecording()} the data
	 * will be written to the given files.
	 *
	 * @param monitorRecordingDescriptor describes the configuration of the recording (file names etc.)
	 * @throws FileNotFoundException thrown when the files for buffering, signal and tag recording are not found
	 */
	public void startMonitorRecording() throws FileNotFoundException {

		MonitorRecordingDescriptor monitorRecordingDescriptor = monitorOptions.getMonitorRecordingDescriptor();

		signalRecorderOutputFile = new File(monitorRecordingDescriptor.getSignalRecordingFilePath());
		tagRecorderOutputFile = null;

		if(!monitorRecordingDescriptor.isTagsRecordingDisabled())
			tagRecorderOutputFile = new File(monitorRecordingDescriptor.getTagsRecordingFilePath());

		signalRecorderBufferFile = new File(signalRecorderBufferFilename);
		FileOutputStream signalRecorderBufferOutput = new FileOutputStream(signalRecorderBufferFile);

		//starting signal recorder
		LinkedBlockingQueue< double[]> sampleQueue = null;
		if (signalRecorderBufferOutput != null) {
			sampleQueue = new LinkedBlockingQueue< double[]>();
			signalRecorderWorker = new SignalRecorderWorker(sampleQueue, signalRecorderBufferFile, monitorOptions, 50L);
			signalRecorderWorker.execute();
		}

		//starting tag recorder
		if (tagRecorderOutputFile != null)
			tagRecorderWorker = new TagRecorder();

		monitorWorker.setSampleQueue(sampleQueue);
		monitorWorker.connectTagRecorderWorker(tagRecorderWorker);

		pcSupport.firePropertyChange(IS_RECORDING_PROPERTY, false, true);

	}

	/**
	 * Stops to record data and writes the recorded data to the files set
	 * in the {@link OpenMonitorDescriptor}.
	 *
	 * @throws IOException thrown where an error while writing the recorded
	 * data to the given files occurs
	 */
	public void stopMonitorRecording() throws IOException {

		//stops the monitorWorker from sending more samples and tags to the recorders
		monitorWorker.connectTagRecorderWorker(null);
		monitorWorker.setSampleQueue(null);

		//stops the recorderWorker and saves the recorded samples
		if (signalRecorderWorker != null && !signalRecorderWorker.isCancelled()) {
			signalRecorderWorker.cancel(true);
			do {
				try {
					Thread.sleep(1);
				}
				catch (InterruptedException e) {}
			} while (!signalRecorderWorker.isFinished());

			saveRecordedSamples();
		}

		if (tagRecorderWorker != null) {
			saveRecordedTags();
		}

		tagRecorderWorker = null;
		signalRecorderWorker = null;

		pcSupport.firePropertyChange(IS_RECORDING_PROPERTY, true, false);

	}

	/**
	 * Saves the recorded samples to the file specified in the
	 * {@link MonitorSignalDocument#getOpenMonitorDescriptor()} describing
	 * this monitor and writes a metafile (with the same name) describing
	 * this signal file.
	 *
	 * @throws IOException thrown where an error while writing the recorded data to the given files occurs
	 */
	private void saveRecordedSamples() throws IOException {

		int sampleCount = signalRecorderWorker.getSavedSampleCount();
		signalRecorderWorker = null;

		String dataPath = null;
		String metadataPath = null;

		File dataFile = null;
		File metadataFile = null;

		dataPath = signalRecorderOutputFile.getCanonicalPath();

		if (dataPath.endsWith(".raw"))
			metadataPath = dataPath.substring(0, dataPath.length() - 4) + ".xml";
		else {
			metadataPath = dataPath + ".xml";
			dataPath = dataPath + ".raw";
		}

		metadataFile = new File(metadataPath);
		dataFile = new File(dataPath);

		//write signal metadata file
		RawSignalDescriptor rsd = new RawSignalDescriptor();
		rsd.setExportFileName(dataPath);
		rsd.setBlocksPerPage(1);
		rsd.setByteOrder(monitorOptions.getByteOrder());
		rsd.setCalibrationGain(monitorOptions.getSelectedChannelsCalibrationGain());
		rsd.setCalibrationOffset(monitorOptions.getSelectedChannelsCalibrationOffset());
		rsd.setChannelCount(monitorOptions.getSelectedChannelsCount());
		rsd.setChannelLabels(monitorOptions.getSelectedChannelsLabels());
		rsd.setPageSize(monitorOptions.getPageSize().floatValue());
		rsd.setSampleCount(sampleCount);
		rsd.setSampleType(monitorOptions.getSampleType());
		rsd.setSamplingFrequency(monitorOptions.getSamplingFrequency());
		rsd.setSourceSignalType(SourceSignalType.RAW);
		RawSignalDescriptorWriter descrWriter = new RawSignalDescriptorWriter();
		descrWriter.writeDocument(rsd, metadataFile);

		//write signal data to an appropriate file
		FileUtils.copyFile(signalRecorderBufferFile, dataFile);
		signalRecorderBufferFile.delete();

	}

	/**
	 * Saves the recorded tags to the file specified in the
	 * {@link OpenMonitorDescriptor}.
	 *
	 * @throws IOException thrown where an error while writing the recorded data to the given files occurs
	 */
	private void saveRecordedTags() {

		StyledTagSet toBeSavedTagSet = tagRecorderWorker.getRecordedTagSet();

		TagDocument tagDocument;
		try {
			for(TagStyle tagStyle: tagSet.getStyles())
				toBeSavedTagSet.addStyle(tagStyle);
			tagDocument = new TagDocument(toBeSavedTagSet);

			String tagsPath = tagRecorderOutputFile.getCanonicalPath();
			if (!tagsPath.endsWith(".tag"))
				tagsPath += ".tag";

			tagDocument.setBackingFile(new File(tagsPath));
			tagDocument.saveDocument();
		} catch (IOException ex) {
			java.util.logging.Logger.getLogger(MonitorSignalDocument.class.getName()).log(Level.SEVERE, null, ex);
		} catch (SignalMLException ex) {
			java.util.logging.Logger.getLogger(MonitorSignalDocument.class.getName()).log(Level.SEVERE, null, ex);
		}

	}

	/**
	 * Returns a list of properties that {@link PropertyChangeListener
	 * PropertyChangeListeners} can handle.
	 * 
	 * @return a list of properties
	 * @throws IntrospectionException if an exception occurs during introspection
	 */
	@Override
	public List<LabelledPropertyDescriptor> getPropertyList() throws IntrospectionException {
		List<LabelledPropertyDescriptor> list = super.getPropertyList();
		list.add(new LabelledPropertyDescriptor("property.monitorsignaldocument."+IS_RECORDING_PROPERTY, IS_RECORDING_PROPERTY, MonitorSignalDocument.class, "isRecording", null));
		return list;
	}

	/**
	 * Returns an {@link OpenMonitorDescriptor} which describes the open monitor
	 * associated with this {@link MonitorSignalDocument}.
	 *
	 * @return an {@link OpenMonitorDescriptor} associated with this {@link
	 * MonitorSignalDocument}.
	 */
	public OpenMonitorDescriptor getOpenMonitorDescriptor() {
		return monitorOptions;
	}

}
