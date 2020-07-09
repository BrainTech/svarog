/* StopMonitorRecordingAction.java created 2010-10-29
 *
 */

package org.signalml.app.action.document.monitor;

import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.io.IOException;
import org.apache.log4j.Logger;
import org.signalml.app.action.selector.SignalDocumentFocusSelector;
import org.signalml.app.document.MonitorSignalDocument;
import org.signalml.app.document.signal.SignalDocument;
import static org.signalml.app.util.i18n.SvarogI18n._;
import org.signalml.app.worker.monitor.recording.RecordingState;

/**
 * This class is responsible for actions regarding the menu item which stops the recording of the monitor.
 *
 * @author Piotr Szachewicz
 */
public class StopMonitorRecordingAction extends MonitorRecordingAction {

	/**
	 * Interface for this action's variants.
	 */
	private static interface Action {
		public void perform(MonitorSignalDocument document) throws IOException;
	}

	/**
	 * Logger to save history of execution at.
	 */
	protected static final Logger logger = Logger.getLogger(StopMonitorRecordingAction.class);

	private Action action;

	/**
	 * Constructor.
	 *
	 * @param signalDocumentFocusSelector a {@link SignalDocumentFocusSelector} used to detect
	 * which document is active.
	 */
	public StopMonitorRecordingAction(SignalDocumentFocusSelector signalDocumentFocusSelector) {

		super(signalDocumentFocusSelector);
		setIconPath("org/signalml/app/icon/stop-recording.png");
		setText(_("Stop recording"));
		setToolTip(_("Stop to record signal and tags to a file"));
		setMnemonic(KeyEvent.VK_P);

	}

	/**
	 * Stops the recording for the currently open document (if it is a {@link MonitorSignalDocument}).
	 *
	 * @param ev represents the event that has happened
	 */
	@Override
	public void actionPerformed(ActionEvent ev) {

		SignalDocument signalDocument = getActionFocusSelector().getActiveSignalDocument();
		if ((signalDocument != null) && (signalDocument instanceof MonitorSignalDocument)) {
			MonitorSignalDocument monitorSignalDocument = (MonitorSignalDocument) signalDocument;
			if (action != null) {
				try {
					action.perform(monitorSignalDocument);
				} catch (IOException ex) {
					logger.error(ex, ex);
				}
			}
		}
	}

	/**
	 * Decides to enable/disable the menu item to which this action is connected to according
	 * to the state of the recorder.
	 */
	@Override
	public void setEnabledAsNeeded() {

		SignalDocument signalDocument = getActionFocusSelector().getActiveSignalDocument();

		boolean enabled = false;
		if (signalDocument != null && signalDocument instanceof MonitorSignalDocument) {
			MonitorSignalDocument monitor = (MonitorSignalDocument) signalDocument;
			if (monitor.getRecordingState() == RecordingState.SAVING) {
				enabled = true;
				action = (MonitorSignalDocument document) -> {
					document.stopMonitorRecording();
				};
			} else if (monitor.isPsychopyExperimentRunning()) {
				enabled = true;
				action = (MonitorSignalDocument document) -> {
					document.stopPsychopyExperiment();
				};
			} else {
				action = null;
			}
		}
		setEnabled(enabled);
	}

}
