/* StartMonitorRecordingAction.java created 2010-10-29
 *
 */

package org.signalml.app.action.document.monitor;

import static org.signalml.app.util.i18n.SvarogI18n._;

import java.io.FileNotFoundException;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;

import org.apache.log4j.Logger;

import org.signalml.app.action.selector.SignalDocumentFocusSelector;
import org.signalml.app.document.MonitorSignalDocument;
import org.signalml.app.document.SignalDocument;
import org.signalml.app.view.document.monitor.StartMonitorRecordingDialog;

/**
 * This class is responsible for actions regarding the menu item which starts
 * the recording of the open monitor.
 *
 * @author Piotr Szachewicz
 */
public class StartMonitorRecordingAction extends MonitorRecordingAction {

	/**
	 * Logger to save history of execution at.
	 */
	protected static final Logger logger = Logger.getLogger(StartMonitorRecordingAction.class);

	/**
	 * The dialog which is shown after evoking this action.
	 */
	protected StartMonitorRecordingDialog startMonitorRecordingDialog;

	/**
	 * Constructor.
	 *
	 * @param signalDocumentFocusSelector a {@link SignalDocumentFocusSelector} used to detect
	 * which document is active.
	 */
	public StartMonitorRecordingAction(SignalDocumentFocusSelector signalDocumentFocusSelector) {
		super(signalDocumentFocusSelector);
		setText(_("Start monitor recording"));
		setIconPath("org/signalml/app/icon/record.png");
		setToolTip(_("Record signal and tags from this monitor to a file"));
		setMnemonic(KeyEvent.VK_S);
	}

	/**
	 * Starts the recording for the currently open document (if it is a {@link MonitorSignalDocument}).
	 *
	 * @param ev an event describing a change
	 */
	@Override
	public void actionPerformed(ActionEvent ev) {

		SignalDocument signalDocument = getActionFocusSelector().getActiveSignalDocument();
		if ((signalDocument != null) && (signalDocument instanceof MonitorSignalDocument)) {

			MonitorSignalDocument monitorSignalDocument = (MonitorSignalDocument) signalDocument;

			boolean ok = startMonitorRecordingDialog.showDialog(monitorSignalDocument.getExperimentDescriptor(), true);
			if (!ok) {
				return;
			}

			try {
				monitorSignalDocument.startMonitorRecording();
			} catch (FileNotFoundException ex) {
				logger.error("The files to which you want to record signal/tags were not found", ex);
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

		if ((signalDocument != null) && (signalDocument instanceof MonitorSignalDocument)
				&& !((MonitorSignalDocument) signalDocument).isRecording())
			setEnabled(true);
		else
			setEnabled(false);

	}

	/**
	 * Returns the {@link StartMonitorRecordingDialog} which is shown
	 * when this action is performed.
	 *
	 * @return the {@link StartMonitorRecordingDialog} associated with this action
	 */
	public StartMonitorRecordingDialog getStartMonitorRecordingDialog() {
		return startMonitorRecordingDialog;
	}

	/**
	 * Sets a {@link StartMonitorRecordingDialog} which will be shown when
	 * this action is performed.
	 *
	 * @param startMonitorRecordingDialog the {@link StartMonitorRecordingDialog}
	 * to be associated with this action.
	 */
	public void setStartMonitorRecordingDialog(StartMonitorRecordingDialog startMonitorRecordingDialog) {
		this.startMonitorRecordingDialog = startMonitorRecordingDialog;
	}

}