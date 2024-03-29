package org.signalml.app.action.document.monitor;

import java.awt.event.ActionEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import org.signalml.app.document.MonitorSignalDocument;
import org.signalml.app.document.signal.SignalDocument;
import static org.signalml.app.util.i18n.SvarogI18n._;
import org.signalml.app.video.VideoStreamSpecification;
import org.signalml.app.worker.monitor.recording.RecordingState;
import org.signalml.plugin.export.view.AbstractSignalMLAction;

/**
 * Action displaying a video preview for the video stream being recorded.
 *
 * @author piotr.rozanski@braintech.pl
 */
public class StartVideoPreviewAction extends AbstractSignalMLAction implements PropertyChangeListener {

	private final MonitorSignalDocument monitor;
	private boolean isVideoRecording = false;
	private String toolTipBase = null;

	/**
	 * @param document for which video preview should be displayed
	 */
	public StartVideoPreviewAction(SignalDocument document) {
		super();
		if (document instanceof MonitorSignalDocument) {
			this.monitor = (MonitorSignalDocument) document;
			this.monitor.addPropertyChangeListener(this);
		} else {
			this.monitor = null;
		}
		setEnabled(false);
		setIconPath("org/signalml/app/icon/camera.png");
		setText(_("Show video preview"));
	}

	/**
	 * Stops the recording for the currently open document (if it is a {@link MonitorSignalDocument}).
	 *
	 * @param ev represents the event that has happened
	 */
	@Override
	public void actionPerformed(ActionEvent ev) {
		if (monitor != null && isVideoRecording) {
			monitor.showVideoFrameForPreview();
		}
	}

	@Override
	public void propertyChange(PropertyChangeEvent evt) {
		if (MonitorSignalDocument.RECORDING_STATE_PROPERTY.equals(evt.getPropertyName())) {
			RecordingState state = (RecordingState) evt.getNewValue();
			boolean enabled = (state == RecordingState.SAVING) && monitor.isVideoRecordingEnabled();
			setEnabled(enabled);
			updateToolTip();
		}
	}

	/**
	 * Decides to enable/disable the menu item to which this action is connected to according
	 * to the state of the recorder.
	 */
	@Override
	public void setEnabledAsNeeded() {
		setEnabled(isVideoRecording);
	}

	/**
	 * Set this action's tooltip based on the given stream's specification.
	 * If given stream is null, clear the tooltip.
	 *
	 * @param stream  video stream's specification
	 */
	public void setToolTipFromVideoSpecs(VideoStreamSpecification stream) {
		this.toolTipBase = (stream == null) ? null :
			String.format("%s (%d x %d): ", stream.cameraName, stream.width, stream.height);
		updateToolTip();
	}

	private void updateToolTip() {
		String toolTip = toolTipBase;
		if (toolTip != null) {
			toolTip += isVideoRecording ? _("recording") : _("stopped");
		}
		setToolTip(toolTip);
	}

}
