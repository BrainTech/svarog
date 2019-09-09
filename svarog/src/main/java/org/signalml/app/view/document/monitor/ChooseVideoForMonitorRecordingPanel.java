package org.signalml.app.view.document.monitor;

import static org.signalml.app.util.i18n.SvarogI18n._;

import java.awt.BorderLayout;
import java.awt.Window;
import java.beans.PropertyChangeEvent;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.logging.Level;
import javax.swing.BoxLayout;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingWorker;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
import org.apache.log4j.Logger;
import org.signalml.app.model.components.validation.ValidationErrors;
import org.signalml.app.model.document.opensignal.ExperimentDescriptor;
import org.signalml.app.model.monitor.MonitorRecordingDescriptor;
import org.signalml.app.video.PreviewVideoDialog;
import org.signalml.app.video.VideoSourceSpecification;
import org.signalml.app.video.VideoStreamManager;
import org.signalml.app.video.VideoStreamSpecification;
import org.signalml.app.video.components.VideoStreamSelectionCompactPanel;
import org.signalml.app.video.components.VideoStreamSelectionListener;
import org.signalml.app.worker.monitor.GetAvailableVideoWorker;
import org.signalml.app.worker.monitor.ObciServerCapabilities;
import org.signalml.app.worker.monitor.exceptions.OpenbciCommunicationException;

/**
 * Represents a panel for selecting video stream to be recorded.
 *
 * @author piotr.rozanski@braintech.pl
 */
public class ChooseVideoForMonitorRecordingPanel extends JPanel {

	private static final Logger logger = Logger.getLogger(ChooseVideoForMonitorRecordingPanel.class);

	private final Window parentWindow;

	private VideoStreamSelectionCompactPanel videoStreamPanel;
	private JPanel previewCheckPanel;
	private JCheckBox previewCheckBox;
	private JLabel previewCheckLabel;

	public ChooseVideoForMonitorRecordingPanel(Window parentWindow) {
		super(new BorderLayout());
		this.parentWindow = parentWindow;
		initialize();
		launchRefreshingWorker();
	}

	/**
	 * Initializes this panel.
	 */
	private void initialize() {
		setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
		CompoundBorder border = new CompoundBorder(
			new TitledBorder(_("Choose a video stream to record")),
			new EmptyBorder(3, 3, 3, 3));
		setBorder(border);
		add(getVideoStreamSelectionPanel());
		add(getPreviewCheckPanel());
	}

	protected JPanel getPreviewCheckPanel() {
		if (previewCheckPanel == null) {
			previewCheckBox = new JCheckBox();
			previewCheckLabel = new JLabel(_("display preview during recording"));
			previewCheckLabel.setLabelFor(previewCheckBox);

			previewCheckPanel = new JPanel(new BorderLayout());
			previewCheckPanel.add(previewCheckBox, BorderLayout.WEST);
			previewCheckPanel.add(previewCheckLabel, BorderLayout.CENTER);
		}
		return previewCheckPanel;
	}

	protected VideoStreamSelectionCompactPanel getVideoStreamSelectionPanel() {
		if (videoStreamPanel == null) {
			videoStreamPanel = new VideoStreamSelectionCompactPanel(new VideoStreamSelectionListener() {
				@Override
				public void previewRequested(VideoStreamSpecification stream) {
					try {
						PreviewVideoDialog dialog = new PreviewVideoDialog(parentWindow, stream);
						dialog.setVisible(true);
					} catch (OpenbciCommunicationException ex) {
						ex.showErrorDialog(_("Cannot display video preview"));
					}
				}
				@Override
				public void refreshRequested() {
					launchRefreshingWorker();
				}
				@Override
				public void videoStreamSelected(VideoStreamSpecification stream) {
					// nothing here; instead, result goes through fillModelFromPanel
				}
			});
		}
		return videoStreamPanel;
	}

	private void launchRefreshingWorker() {
		GetAvailableVideoWorker worker = new GetAvailableVideoWorker(getParent());
		worker.addPropertyChangeListener((PropertyChangeEvent evt) -> {
			if (SwingWorker.StateValue.DONE == evt.getNewValue()) {
				try {
					List<VideoSourceSpecification> cameras = worker.get();
					videoStreamPanel.setAvailableSources(cameras);
					List<VideoStreamSpecification> streams = VideoStreamManager.getAllActiveStreams();
					if (streams.size() == 1) {
						// if exactly one stream is active, it is automatically selected
						videoStreamPanel.setSelectedVideoStream(streams.get(0));
					}
				} catch (ExecutionException|InterruptedException e) {
					logger.error("Could not refresh list of cameras!", e);
				}
			}
		});
		worker.execute();
	}

	@Override
	public void setEnabled(boolean enabled) {
		super.setEnabled(enabled);
		videoStreamPanel.setEnabled(enabled);
		previewCheckBox.setEnabled(enabled);
		previewCheckLabel.setEnabled(enabled);
	}

	/**
	 * Fills the model with the data from this panel (user input).
	 * @param model the model to be filled.
	 */
	public void fillModelFromPanel(Object model) {
		MonitorRecordingDescriptor monitorRecordingDescriptor = ((ExperimentDescriptor) model).getMonitorRecordingDescriptor();
		if (getVideoStreamSelectionPanel().isEnabled()) {
			monitorRecordingDescriptor.setVideoStreamSpecification(getVideoStreamSelectionPanel().getSelectedVideoStream());
			monitorRecordingDescriptor.setDisplayVideoPreviewWhileSaving(previewCheckBox.isSelected());
		} else {
			monitorRecordingDescriptor.setVideoStreamSpecification(null);
			monitorRecordingDescriptor.setDisplayVideoPreviewWhileSaving(false);
		}
	}

	public void fillPanelFromModel(Object model) {
		ExperimentDescriptor experimentDescriptor = (ExperimentDescriptor) model;
		MonitorRecordingDescriptor monitorRecordingDescriptor = experimentDescriptor.getMonitorRecordingDescriptor();
		setEnabled(ObciServerCapabilities.getSharedInstance().hasVideoSaving() && monitorRecordingDescriptor.isVideoRecordingEnabled());
		previewCheckBox.setSelected(monitorRecordingDescriptor.getDisplayVideoPreviewWhileSaving());
	}

	/**
	 * Checks if this panel is properly filled.
	 * @param model the model for this panel
	 * @param errors the object in which errors are stored
	 */
	public void validatePanel(Object model, ValidationErrors errors) {
		if (getVideoStreamSelectionPanel().isEnabled()){
                    if (getVideoStreamSelectionPanel().getSelectedVideoStream() == null) {
                        errors.addError(_("Please select a video stream to be recorded"));
                    }
                    else{
                        VideoStreamSpecification stream = getVideoStreamSelectionPanel().getSelectedVideoStream();
                        VideoStreamManager vsm = new VideoStreamManager();
                        try {
                            vsm.replace(stream);
                        } catch (OpenbciCommunicationException ex) {
                            errors.addError(ex.getMessage()); 
                        }
                        vsm.free();
                    }
			
		}
	}

}
