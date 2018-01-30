package org.signalml.app.document;

import static org.signalml.app.util.i18n.SvarogI18n._;

import java.beans.IntrospectionException;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Iterator;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import javax.swing.JOptionPane;

import org.apache.log4j.Logger;
import org.signalml.app.action.document.monitor.StartVideoPreviewAction;
import org.signalml.app.document.signal.AbstractSignal;
import org.signalml.app.document.signal.SignalChecksumProgressMonitor;
import org.signalml.app.model.components.LabelledPropertyDescriptor;
import org.signalml.app.model.document.opensignal.ExperimentDescriptor;
import org.signalml.app.model.monitor.MonitorRecordingDescriptor;
import org.signalml.app.video.PreviewVideoFrame;
import org.signalml.app.video.TimecodeData;
import org.signalml.app.video.VideoStreamManager;
import org.signalml.app.video.VideoStreamSpecification;
import org.signalml.app.view.signal.SignalPlot;
import org.signalml.app.view.signal.SignalView;
import org.signalml.app.worker.monitor.DisconnectFromExperimentWorker;
import org.signalml.app.worker.monitor.MonitorWorker;
import org.signalml.app.worker.monitor.SignalRecorderWorker;
import org.signalml.app.worker.monitor.TagRecorder;
import org.signalml.app.worker.monitor.VideoRecordingStatusListener;
import org.signalml.app.worker.monitor.exceptions.OpenbciCommunicationException;
import org.signalml.app.worker.monitor.messages.BaseMessage;
import org.signalml.domain.montage.MontageMismatchException;
import org.signalml.domain.signal.SignalChecksum;
import org.signalml.domain.signal.SignalProcessingChain;
import org.signalml.domain.signal.samplesource.RoundBufferMultichannelSampleSource;
import org.signalml.domain.tag.StyledMonitorTagSet;
import org.signalml.plugin.export.SignalMLException;
import org.signalml.plugin.export.view.DocumentView;
import org.signalml.psychopy.PsychopyExperiment;
import org.signalml.psychopy.PsychopyStatusListener;

/**
 * @author Mariusz Podsiadło
 *
 */
public class MonitorSignalDocument extends AbstractSignal implements MutableDocument, PsychopyStatusListener {

	/**
	 * A property describing whether this document is recording its signal.
	 */
	public static String IS_RECORDING_PROPERTY = "isRecording";

	/**
	 * A property describing whether this document is waiting on video
	 * saving finalization.
	 */
	public static String IS_VIDEO_SAVING_PROPERTY = "isVideoSaving";
	/**
	 * A property describing whether this document is waiting on psychopy
	 * experiment
	 */
	public static String IS_PSYCHOPY_EXPERIMENT_RUNNING_PROPERTY = "isPsychopyRunning";

	/**
	 * A logger to save history of execution at.
	 */
	protected static final Logger logger = Logger.getLogger(MonitorSignalDocument.class);

	/**
	 * The name of this document.
	 */
	private String name = "Signal online";

	/**
	 * Describes the parameters of the monitor connected to this
	 * {@link MonitorSignalDocument}.
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
	 * Video manager responsible for the RTSP stream being recorded.
	 */
	private final VideoStreamManager videoStreamRecordingManager;

	/**
	 * Currently active frame for video preview.
	 */
	private PreviewVideoFrame previewVideoFrame;

	/**
	 * Whether the signal was saved.
	 */
	private boolean saved = true;

	/**
	 * How often (in milliseconds) should {@link SignalPlot signal plots} be
	 * refreshed by the {@link RefreshPlotsTimerTask}.
	 */
	private static long refreshPlotsInterval = 50;

	/**
	 * A timer used to refresh {@link SignalPlot signal plots} after new
	 * samples have been added.
	 */
	private Timer refreshPlotsTimer;

	private PsychopyExperiment psychopyExperiment;

	public MonitorSignalDocument(ExperimentDescriptor descriptor) {

		super();
		this.descriptor = descriptor;
		this.videoStreamRecordingManager = new VideoStreamManager();
		float freq = descriptor.getSignalParameters().getSamplingFrequency();
		double ps = descriptor.getSignalParameters().getPageSize();
		int sampleCount = (int) Math.ceil(ps * freq);
		sampleSource = new RoundBufferMultichannelSampleSource(descriptor.getSignalParameters().getChannelCount(), sampleCount);

		sampleSource.setCalibrationGain(descriptor.getSignalParameters().getCalibrationGain());
		sampleSource.setCalibrationOffset(descriptor.getSignalParameters().getCalibrationOffset());

		((RoundBufferMultichannelSampleSource) sampleSource).setLabels(descriptor.getAmplifier().getSelectedChannelsLabels());
		((RoundBufferMultichannelSampleSource) sampleSource).setDocumentView(getDocumentView());
		((RoundBufferMultichannelSampleSource) sampleSource).setSamplingFrequency(freq);

		refreshPlotsTimer = new Timer();
		refreshPlotsTimer.schedule(new RefreshPlotsTimerTask(), 0, refreshPlotsInterval);
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

		if (descriptor.getPeer() == null) {
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
		monitorWorker.connectPsychopyStatusListener(this);
		logger.info("Monitor executed.");

	}

	@Override
	public void closeDocument() throws SignalMLException {

		refreshPlotsTimer.cancel();

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
				} catch (InterruptedException e) {
				}
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
		if (blocksPerPage != 1) {
			throw new IllegalArgumentException();
		}
		this.blocksPerPage = 1;
	}

	/**
	 * Returns whether this monitor document is recording the signal (and
	 * optionally tags) to a file.
	 *
	 * @return true, if this {@link MonitorSignalDocument} is recording the
	 * signal (and optionally tags) it monitors, false otherwise
	 */
	public boolean isRecording() {
		if (signalRecorderWorker != null || getPsychopyExperiment().isRunning) {
			return true;
		}
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
	 * Starts to record this monitor document samples and tags according to
	 * the configuration (file names etc.) maintained in the
	 * {@link ExperimentDescriptor} related to this MonitorSignalDocument
	 * (this configuration can be changed by changing this object:
	 * {@link MonitorSignalDocument#getExperimentDescriptor()}. After
	 * calling {@link MonitorSignalDocument#stopMonitorRecording()} the data
	 * will be written to the given files.
	 *
	 * @throws FileNotFoundException thrown when the files for buffering,
	 * signal and tag recording are not found
	 * @throws OpenbciCommunicationException thrown when video preview is
	 * not available
	 */
	public void startMonitorRecording() throws FileNotFoundException, OpenbciCommunicationException {

		MonitorRecordingDescriptor monitorRecordingDescriptor = descriptor.getMonitorRecordingDescriptor();

		// starting signal recorder
		String dataPath = monitorRecordingDescriptor.getSignalRecordingFilePath();
		signalRecorderWorker = new SignalRecorderWorker(dataPath, descriptor);

		// if tags recording is enabled: starting tag recorder and connecting it
		// to signal recorder
		if (monitorRecordingDescriptor.isTagsRecordingEnabled()) {
			String tagsPath = monitorRecordingDescriptor.getTagsRecordingFilePath();
			tagRecorderWorker = new TagRecorder(tagsPath);
			signalRecorderWorker.setTagRecorder(tagRecorderWorker);
		}

		// connecting recorders to the monitor worker
		monitorWorker.connectSignalRecorderWorker(signalRecorderWorker);
		monitorWorker.connectTagRecorderWorker(tagRecorderWorker);
		documentView.requestFocusInWindow();  // for user tags in monitor mode

		pcSupport.firePropertyChange(IS_RECORDING_PROPERTY, false, true);

		// requesting RTSP stream and starting video recording
		if (monitorRecordingDescriptor.isVideoRecordingEnabled()) {

			StartVideoPreviewAction startVideoPreviewAction = getSignalView().getStartVideoPreviewAction();
			monitorWorker.connectVideoRecordingStatusListener(startVideoPreviewAction);

			VideoStreamSpecification videoSpecs = monitorRecordingDescriptor.getVideoStreamSpecification();

			String rtcpURL = videoStreamRecordingManager.replace(videoSpecs);
			String relativeFilePath = monitorRecordingDescriptor.getVideoRecordingFilePathWithExtension();
			String targetFilePath = (new File(relativeFilePath)).getAbsolutePath();
			monitorWorker.startVideoSaving(rtcpURL, targetFilePath);

			// display camera name and status in button's tool tip
			startVideoPreviewAction.setToolTipFromVideoSpecs(videoSpecs);

			if (monitorRecordingDescriptor.getDisplayVideoPreviewWhileSaving()) {
				showVideoFrameForPreview();
			}
		}

	}

	/**
	 * Requests that a window displaying a preview of the video being
	 * recorded will be displayed for the user.
	 *
	 * If the window does not exists, it is created. If it already exists,
	 * it is brought to front.
	 */
	public void showVideoFrameForPreview() {
		MonitorRecordingDescriptor monitorRecordingDescriptor = descriptor.getMonitorRecordingDescriptor();
		if (previewVideoFrame != null && previewVideoFrame.isVisible()) {
			previewVideoFrame.toFront();
		} else if (monitorRecordingDescriptor.isVideoRecordingEnabled()) {
			VideoStreamSpecification videoSpecs = monitorRecordingDescriptor.getVideoStreamSpecification();
			try {
				previewVideoFrame = new PreviewVideoFrame(videoSpecs);
				previewVideoFrame.setVisible(true);
			} catch (OpenbciCommunicationException ex) {
				ex.showErrorDialog("Failed to start video preview");
				logger.error("Failed to start video preview", ex);
			}
		}
	}

	/**
	 * Stops to record data and writes the recorded data to the files set in
	 * the {@link ExperimentDescriptor}.
	 *
	 * @throws IOException thrown where an error while writing the recorded
	 * data to the given files occurs
	 */
	public void stopMonitorRecording() throws IOException {
		if (descriptor.getMonitorRecordingDescriptor().isVideoRecordingEnabled()
			&& signalRecorderWorker != null) {
			monitorWorker.connectVideoRecordingStatusListener(new VideoRecordingStatusListener() {
				@Override
				public void videoRecordingStatusChanged(boolean recording) {
					if (!recording) {
						monitorWorker.disconnectVideoRecordingStatusListener(this);
						videoStreamRecordingManager.free();

						if (!tryToFinalizeVideoRecordingMetadata()) {
							JOptionPane.showMessageDialog(null,
								_("Video saving has not been finalized! Video data may be missing from XML file."),
								_("Video saving issue"),
								JOptionPane.WARNING_MESSAGE
							);
						}
						pcSupport.firePropertyChange(IS_VIDEO_SAVING_PROPERTY, true, false);
						stopMonitorRecordingFinalizeMetadata();

					}
				}
			});
			stopMonitorRecordingSignal();
			pcSupport.firePropertyChange(IS_VIDEO_SAVING_PROPERTY, false, true);
			monitorWorker.stopVideoSaving();
		} else if (getPsychopyExperiment().isRunning) {
			psychopyExperiment.finish();
		} else {
			stopMonitorRecordingInternally();
		}
	}

	private void stopMonitorRecordingSignal() {
		monitorWorker.disconnectTagRecorderWorker();
		monitorWorker.disconnectSignalRecorderWorker();

		if (signalRecorderWorker != null) {
			signalRecorderWorker.stopSaving();
		}

		if (tagRecorderWorker != null) {
			tagRecorderWorker.save();
		}
		tagRecorderWorker = null;

	}

	private void stopMonitorRecordingFinalizeMetadata() {
		if (signalRecorderWorker != null) {
			try {
				signalRecorderWorker.saveMetadata(false);
			} catch (IOException ex) {
				logger.error("Failed to write metadata XML", ex);
			}
		}
		signalRecorderWorker = null;
		if (documentView != null) {
			// for user tags in monitor mode
			documentView.requestFocusInWindow();
		}
		pcSupport.firePropertyChange(IS_RECORDING_PROPERTY, true, false);

	}

	private void stopMonitorRecordingInternally() {
		stopMonitorRecordingSignal();
		stopMonitorRecordingFinalizeMetadata();
	}

	/**
	 * @return true if success, false if failure
	 */
	private boolean tryToFinalizeVideoRecordingMetadata() {
		String relativeFilePath = descriptor.getMonitorRecordingDescriptor().getVideoRecordingFilePath();
		File timestampFile = new File(relativeFilePath + ".ts.txt");

		try {
			TimecodeData timestamps = TimecodeData.readFromFile(timestampFile);
			if (timestamps.size() < 2) {
				// we need at least two entries,
				// since if there is only one, it may be partial
				return false;
			}
			double firstFrameTimestamp = timestamps.get(0) / 1000;
			signalRecorderWorker.setFirstVideoFrameTimestamp(firstFrameTimestamp);
			return true;

		} catch (IOException ex) {
			// timestamp file is not ready yet
			return false;
		}
	}

	/**
	 * Returns a list of properties that {@link PropertyChangeListener
	 * PropertyChangeListeners} can handle.
	 *
	 * @return a list of properties
	 * @throws IntrospectionException if an exception occurs during
	 * introspection
	 */
	@Override
	public List<LabelledPropertyDescriptor> getPropertyList() throws IntrospectionException {
		List<LabelledPropertyDescriptor> list = super.getPropertyList();
		list.add(new LabelledPropertyDescriptor(_("is video saving"), IS_VIDEO_SAVING_PROPERTY, MonitorSignalDocument.class, null, null));
		list.add(new LabelledPropertyDescriptor(_("is recording"), IS_RECORDING_PROPERTY, MonitorSignalDocument.class, "isRecording", null));
		list.add(new LabelledPropertyDescriptor(_("is psychopy experiment running"), IS_PSYCHOPY_EXPERIMENT_RUNNING_PROPERTY, MonitorSignalDocument.class, null, null));
		return list;
	}

	/**
	 * Returns an {@link ExperimentDescriptor} which describes the open
	 * monitor associated with this {@link MonitorSignalDocument}.
	 *
	 * @return an {@link ExperimentDescriptor} associated with this {@link
	 * MonitorSignalDocument}.
	 */
	public ExperimentDescriptor getExperimentDescriptor() {
		return descriptor;
	}

	class RefreshPlotsTimerTask extends TimerTask {

		@Override
		public void run() {

			//logger.debug("refreshig plot - curent timestamp: " + System.currentTimeMillis());
			if (documentView != null && ((SignalView) documentView).getPlots() != null) {
				for (Iterator<SignalPlot> i = ((SignalView) documentView).getPlots().iterator(); i.hasNext();) {
					i.next().repaint();
				}
			}

		}

	}

	public MonitorWorker getMonitorWorker() {
		return monitorWorker;
	}

	public PsychopyExperiment getPsychopyExperiment() {
		if (psychopyExperiment == null) {
			psychopyExperiment = new PsychopyExperiment(descriptor, this);
		}
		return psychopyExperiment;
	}

	@Override
	public void psychopyStatusChanged(BaseMessage msg) {
		PsychopyExperiment psychopyExperiment = getPsychopyExperiment();
		boolean previous = psychopyExperiment.isRunning;
		psychopyExperiment.updateStatus(msg);
		pcSupport.firePropertyChange(IS_PSYCHOPY_EXPERIMENT_RUNNING_PROPERTY, previous, psychopyExperiment.isRunning);
		pcSupport.firePropertyChange(IS_RECORDING_PROPERTY, previous, psychopyExperiment.isRunning);
		if (psychopyExperiment.isRunning) //It is recording, so this action is enabled by default
		{
			((SignalView) documentView).getStopMonitorRecordingAction().setEnabled(false);
		}

	}

}
