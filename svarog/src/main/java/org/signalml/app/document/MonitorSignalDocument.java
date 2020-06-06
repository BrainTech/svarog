package org.signalml.app.document;

import java.beans.IntrospectionException;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.io.IOException;
import java.util.Iterator;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import org.apache.log4j.Logger;
import org.signalml.app.action.document.monitor.StartVideoPreviewAction;
import org.signalml.app.document.signal.AbstractSignal;
import org.signalml.app.document.signal.SignalChecksumProgressMonitor;
import org.signalml.app.model.components.LabelledPropertyDescriptor;
import org.signalml.app.model.document.opensignal.ExperimentDescriptor;
import org.signalml.app.model.monitor.MonitorRecordingDescriptor;
import static org.signalml.app.util.i18n.SvarogI18n._;
import org.signalml.app.video.PreviewVideoFrame;
import org.signalml.app.video.VideoStreamManager;
import org.signalml.app.video.VideoStreamSpecification;
import org.signalml.app.view.signal.SignalPlot;
import org.signalml.app.view.signal.SignalView;
import org.signalml.app.worker.monitor.DisconnectFromExperimentWorker;
import org.signalml.app.worker.monitor.MonitorWorker;
import org.signalml.app.worker.monitor.exceptions.OpenbciCommunicationException;
import org.signalml.app.worker.monitor.messages.BaseMessage;
import org.signalml.app.worker.monitor.messages.StartSavingSignal;
import org.signalml.app.worker.monitor.recording.RecordingManager;
import org.signalml.app.worker.monitor.recording.RecordingState;
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
public class MonitorSignalDocument extends AbstractSignal implements PsychopyStatusListener {

	/**
	 * A property describing the state of the recording process.
	 */
	public static String RECORDING_STATE_PROPERTY = "getRecordingState";

	/**
	 * A property describing whether this document is waiting on psychopy
	 * experiment
	 */
	public static String IS_PSYCHOPY_EXPERIMENT_RUNNING_PROPERTY = "isPsychopyExperimentRunning";

	/**
	 * A logger to save history of execution at.
	 */
	protected static final Logger logger = Logger.getLogger(MonitorSignalDocument.class);

	/**
	 * The name of this document.
	 */
	private String name = _("Signal online");

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
	 * Tag set to which the {@link MonitorWorker} saves tags.
	 */
	private StyledMonitorTagSet tagSet;

	/**
	 * Video manager responsible for allocating and disposing of RTSP stream.
	 */
	private final VideoStreamManager videoStreamManager;

	/**
	 * Manager responsible for signal and video recording using OBCI.
	 */
	private final RecordingManager recordingManager;

	/**
	 * Currently active frame for video preview.
	 */
	private PreviewVideoFrame previewVideoFrame;

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
		this.videoStreamManager = new VideoStreamManager();
		this.recordingManager = new RecordingManager();
		this.recordingManager.addRecordingListener((RecordingState state) -> {
			pcSupport.firePropertyChange(RECORDING_STATE_PROPERTY, null, state);
			if (state == RecordingState.FINISHED) {
				videoStreamManager.free();
			}
		});
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
			for (SignalPlot signalPlot : ((SignalView) documentView).getPlots()) {
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
        
        private void disconnectFromExperiment()
        {
            DisconnectFromExperimentWorker worker = new DisconnectFromExperimentWorker(null, descriptor);
            worker.execute();
            while (!worker.isDone())
                {
                        try
                        {
                            Thread.sleep(10);
			}
                        catch (InterruptedException e)
                        {
                        }

                }
        }

	@Override
	public void closeDocument() throws SignalMLException {

		refreshPlotsTimer.cancel();

		//stop recording
		stopMonitorRecording();

		//stop monitor worker
		if (monitorWorker != null && !monitorWorker.isCancelled()) {
			monitorWorker.cancel(false);
			do {
				try {
					Thread.sleep(10);
				} catch (InterruptedException e) {
				}
			} while (!monitorWorker.isFinished());
			monitorWorker = null;
		}
		tagSet.stopTagsRemoving();

		//disconnect from Jmx
                disconnectFromExperiment();

		//close document
		super.closeDocument();
                logger.debug("Closed document");

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
	 * Returns current status of signal (and video) recording.
	 *
	 * @return one of the constants from RecordingState
	 */
	public RecordingState getRecordingState() {
		return recordingManager.getRecordingState();
	}

	/**
	 * Returns whether the PsychoPy experiment is currently running.
	 *
	 * @return true if PsychoPy experiment is running, false otherwise
	 */
	public boolean isPsychopyExperimentRunning() {
		PsychopyExperiment psychopy = psychopyExperiment;
		return (psychopy != null && psychopy.isRunning);
	}

	/**
	 * Returns whether the video recording is enabled.
	 *
	 * @return true if video recording is enabled, false otherwise
	 */
	public boolean isVideoRecordingEnabled() {
		MonitorRecordingDescriptor monitorRecordingDescriptor = descriptor.getMonitorRecordingDescriptor();
		return monitorRecordingDescriptor.isVideoRecordingEnabled();
	}

	@Override
	public String getName() {
		return name;
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
	 * @throws OpenbciCommunicationException thrown when video preview is
	 * not available
	 */
	public void startMonitorRecording() throws OpenbciCommunicationException {

		MonitorRecordingDescriptor monitorRecordingDescriptor = descriptor.getMonitorRecordingDescriptor();

		documentView.requestFocusInWindow();  // for user tags in monitor mode

		String targetSignalPath = monitorRecordingDescriptor.getSignalRecordingFilePath();

		StartSavingSignal request = new StartSavingSignal(descriptor.getId(), "amplifier", targetSignalPath);
		request.saveTags = monitorRecordingDescriptor.isTagsRecordingEnabled();
		request.saveImpedance = monitorRecordingDescriptor.isSaveImpedanceEnabled();
		request.appendTimestamps = monitorRecordingDescriptor.isAppendTimestampsEnabled();

		// requesting RTSP stream for video recording
		if (monitorRecordingDescriptor.isVideoRecordingEnabled()) {

			StartVideoPreviewAction startVideoPreviewAction = getSignalView().getStartVideoPreviewAction();
			VideoStreamSpecification videoSpecs = monitorRecordingDescriptor.getVideoStreamSpecification();

			request.videoStreamURL = videoStreamManager.replace(videoSpecs);
			String relativeVideoPath = monitorRecordingDescriptor.getVideoRecordingFilePathWithExtension();
			request.videoFileName = (new File(relativeVideoPath)).getAbsolutePath();

			// display camera name and status in button's tool tip
			startVideoPreviewAction.setToolTipFromVideoSpecs(videoSpecs);

			if (monitorRecordingDescriptor.getDisplayVideoPreviewWhileSaving()) {
				showVideoFrameForPreview();
			}
		}

		// starting signal (and video) recording
		if (!recordingManager.startRecording(request)) {
			// could not send recording request, so we should free the video stream
			videoStreamManager.free();
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
				ex.showErrorDialog(_("Failed to start video preview"));
				logger.error("Failed to start video preview", ex);
			}
		}
	}

	/**
	 * Stops to record data and writes the recorded data to the files set in
	 * the {@link ExperimentDescriptor}.
	 */
	public void stopMonitorRecording() {
		recordingManager.stopRecording();
	}

	public void stopPsychopyExperiment() {
		PsychopyExperiment psychopy = psychopyExperiment;
		if (psychopy != null && psychopy.isRunning) {
			psychopy.finish();
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
		list.add(new LabelledPropertyDescriptor(_("recording state"), RECORDING_STATE_PROPERTY, MonitorSignalDocument.class, RECORDING_STATE_PROPERTY, null));
		list.add(new LabelledPropertyDescriptor(_("is psychopy experiment running"), IS_PSYCHOPY_EXPERIMENT_RUNNING_PROPERTY, MonitorSignalDocument.class, IS_PSYCHOPY_EXPERIMENT_RUNNING_PROPERTY, null));
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
		if (psychopyExperiment.isRunning) //It is recording, so this action is enabled by default
		{
			((SignalView) documentView).getStopMonitorRecordingAction().setEnabled(false);
		}
	}

}
