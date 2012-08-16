package org.signalml.app.document;

import java.beans.IntrospectionException;
import java.beans.PropertyChangeListener;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Iterator;
import java.util.List;

import org.apache.log4j.Logger;
import org.signalml.app.document.signal.AbstractSignal;
import org.signalml.app.document.signal.SignalChecksumProgressMonitor;
import org.signalml.app.model.components.LabelledPropertyDescriptor;
import org.signalml.app.model.document.opensignal.ExperimentDescriptor;
import org.signalml.app.model.monitor.MonitorRecordingDescriptor;
import org.signalml.app.view.signal.SignalPlot;
import org.signalml.app.view.signal.SignalView;
import org.signalml.app.worker.monitor.DisconnectFromExperimentWorker;
import org.signalml.app.worker.monitor.MonitorWorker;
import org.signalml.app.worker.monitor.SignalRecorderWorker;
import org.signalml.app.worker.monitor.TagRecorder;
import org.signalml.domain.montage.MontageMismatchException;
import org.signalml.domain.signal.SignalChecksum;
import org.signalml.domain.signal.SignalProcessingChain;
import org.signalml.domain.signal.samplesource.RoundBufferMultichannelSampleSource;
import org.signalml.domain.tag.StyledMonitorTagSet;
import org.signalml.plugin.export.SignalMLException;
import org.signalml.plugin.export.view.DocumentView;

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
	private String name="Signal online";

	/**
	 * Describes the parameters of the monitor connected to this {@link MonitorSignalDocument}.
	 */
	private ExperimentDescriptor descriptor;

	/**
	 * This worker is responsible for receiving the monitor signal and tags.
	 */
	private MonitorWorker monitorWorker;

	/**
	 * This worker is responsible for recording the samples to a file.
	 */
	private SignalRecorderWorker signalRecorderWorker = null;

	/**
	 * This worker is responsible for recording the tags to a file.
	 */
	private TagRecorder tagRecorderWorker = null;

	/**
	 * Tag set to which the {@link MonitorWorker} saves tags.
	 */
	private StyledMonitorTagSet tagSet;

	/**
	 * Whether the signal was saved.
	 */
	private boolean saved = true;

	public MonitorSignalDocument(ExperimentDescriptor descriptor) {

		super();
		this.descriptor = descriptor;
		float freq = descriptor.getSignalParameters().getSamplingFrequency();
		double ps = descriptor.getSignalParameters().getPageSize();
		int sampleCount = (int) Math.ceil(ps * freq);
		sampleSource = new RoundBufferMultichannelSampleSource(descriptor.getSignalParameters().getChannelCount(), sampleCount);

		sampleSource.setCalibrationGain(descriptor.getSignalParameters().getCalibrationGain());
		sampleSource.setCalibrationOffset(descriptor.getSignalParameters().getCalibrationOffset());

		((RoundBufferMultichannelSampleSource) sampleSource).setLabels(descriptor.getAmplifier().getSelectedChannelsLabels());
		((RoundBufferMultichannelSampleSource) sampleSource).setDocumentView(getDocumentView());
		((RoundBufferMultichannelSampleSource) sampleSource).setSamplingFrequency(freq);
	}

	public void setName(String name) {
		this.name = name;
	}

	public float[] getGain() {
		return descriptor.getSignalParameters().getCalibrationGain();
	}

	public float[] getOffset() {
		return descriptor.getSignalParameters().getCalibrationOffset();
	}

	@Override
	public void setDocumentView(DocumentView documentView) {
		super.setDocumentView(documentView);
		if (documentView != null) {
			for (Iterator<SignalPlot> i = ((SignalView) documentView).getPlots().iterator(); i.hasNext();) {
				SignalPlot signalPlot = i.next();
				SignalProcessingChain signalChain = SignalProcessingChain.createNotBufferedFilteredChain(sampleSource);
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

		if (descriptor.getJmxClient() == null) {
			throw new IOException();
		}

		logger.info("Start initializing monitor data.");
		tagSet = new StyledMonitorTagSet(descriptor.getSignalParameters().getPageSize(), 5,
										 descriptor.getSignalParameters().getSamplingFrequency());
		if (descriptor.getTagStyles() != null) {
			tagSet.copyStylesFrom(descriptor.getTagStyles());
		}

		TagDocument tagDoc = new TagDocument(tagSet);
		tagDoc.setParent(this);
		monitorWorker = new MonitorWorker(descriptor, (RoundBufferMultichannelSampleSource) sampleSource, tagSet);

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
		tagSet.stopTagsRemoving();

		//disconnect from Jmx
		DisconnectFromExperimentWorker worker = new DisconnectFromExperimentWorker(descriptor);
		worker.execute();

		while (!worker.isDone())
			;

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
	 * {@link ExperimentDescriptor} related to this MonitorSignalDocument
	 * (this configuration can be changed by changing this object:
	 * {@link MonitorSignalDocument#getExperimentDescriptor()}.
	 * After calling {@link MonitorSignalDocument#stopMonitorRecording()} the data
	 * will be written to the given files.
	 *
	 * @throws FileNotFoundException thrown when the files for buffering, signal and tag recording are not found
	 */
	public void startMonitorRecording() throws FileNotFoundException {

		MonitorRecordingDescriptor monitorRecordingDescriptor = descriptor.getMonitorRecordingDescriptor();
		boolean tagsRecordingEnabled = monitorRecordingDescriptor.isTagsRecordingEnabled();

		// starting signal recorder
		String dataPath = monitorRecordingDescriptor.getSignalRecordingFilePath();
		signalRecorderWorker = new SignalRecorderWorker(dataPath, descriptor);

		// if tags recording is enabled: starting tag recorder and connecting it
		// to signal recorder
		if (tagsRecordingEnabled) {

			String tagsPath = monitorRecordingDescriptor.getTagsRecordingFilePath();
			tagRecorderWorker = new TagRecorder(tagsPath);
			signalRecorderWorker.setTagRecorder(tagRecorderWorker);
		}

		// connecting recorders to the monitor worker
		monitorWorker.connectSignalRecorderWorker(signalRecorderWorker);
		monitorWorker.connectTagRecorderWorker(tagRecorderWorker);

		pcSupport.firePropertyChange(IS_RECORDING_PROPERTY, false, true);

	}

	/**
	 * Stops to record data and writes the recorded data to the files set
	 * in the {@link ExperimentDescriptor}.
	 *
	 * @throws IOException thrown where an error while writing the recorded
	 * data to the given files occurs
	 */
	public void stopMonitorRecording() throws IOException {

		//stops the monitorWorker from sending more samples and tags to the recorders
		monitorWorker.disconnectTagRecorderWorker();
		monitorWorker.disconnectSignalRecorderWorker();

		if (signalRecorderWorker != null) {
			signalRecorderWorker.save();
		}

		if (tagRecorderWorker != null) {
			tagRecorderWorker.save();
		}

		tagRecorderWorker = null;
		signalRecorderWorker = null;

		pcSupport.firePropertyChange(IS_RECORDING_PROPERTY, true, false);

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
	 * Returns an {@link ExperimentDescriptor} which describes the open monitor
	 * associated with this {@link MonitorSignalDocument}.
	 *
	 * @return an {@link ExperimentDescriptor} associated with this {@link
	 * MonitorSignalDocument}.
	 */
	public ExperimentDescriptor getExperimentDescriptor() {
		return descriptor;
	}

}
