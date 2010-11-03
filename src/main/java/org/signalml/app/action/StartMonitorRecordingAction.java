/* StartMonitorRecordingAction.java created 2010-10-29
 *
 */

package org.signalml.app.action;

import java.io.FileNotFoundException;
import java.awt.event.ActionEvent;
import java.io.File;
import org.apache.log4j.Logger;
import org.signalml.app.action.selector.SignalDocumentFocusSelector;
import org.signalml.app.document.MonitorSignalDocument;
import org.signalml.app.document.SignalDocument;
import org.signalml.app.model.MonitorRecordingDescriptor;
import org.signalml.app.view.monitor.StartMonitorRecordingDialog;
import org.springframework.context.support.MessageSourceAccessor;

/**
 * This class is responsible for actions regarding the menu item which starts the recording of the monitor.
 *
 * @author Piotr Szachewicz
 */
public class StartMonitorRecordingAction extends MonitorRecordingAction {

	/**
	 * Logger to save history of execution at.
	 */
	protected static final Logger logger = Logger.getLogger(StartMonitorRecordingAction.class);

	protected StartMonitorRecordingDialog startMonitorRecordingDialog;
	
	public StartMonitorRecordingAction(MessageSourceAccessor messageSource, SignalDocumentFocusSelector signalDocumentFocusSelector) {
                super(messageSource, signalDocumentFocusSelector);
		setText("action.startMonitorRecordingLabel");
		setToolTip("action.startMonitorRecordingToolTip");		
	}

	/**
	 * Starts the recording for the currently open document. (If it is a {@link MonitorSignalDocument}).
	 *
	 * @param ev represents the event that has happened
	 */
	@Override
	public void actionPerformed(ActionEvent ev) {

		MonitorRecordingDescriptor monitorRecordingDescriptor;
		File signalRecordingFile;
		File tagsRecordingFile;

		SignalDocument signalDocument = getActionFocusSelector().getActiveSignalDocument();
		if ((signalDocument != null) && (signalDocument instanceof MonitorSignalDocument)) {

			MonitorSignalDocument monitorSignalDocument = (MonitorSignalDocument) signalDocument;
			monitorRecordingDescriptor = new MonitorRecordingDescriptor("monitor_signal_rec", "monitor_tags_rec", false);

			boolean ok = startMonitorRecordingDialog.showDialog(monitorRecordingDescriptor, true);
				if (!ok) {
				return;
			}

			signalRecordingFile = new File(monitorRecordingDescriptor.getSignalRecordingFilePath());
			if (monitorRecordingDescriptor.isTagsRecordingDisabled())
				tagsRecordingFile = null;
			else
				tagsRecordingFile = new File(monitorRecordingDescriptor.getTagsRecordingFilePath());

			try {
				monitorSignalDocument.startMonitorRecording(signalRecordingFile, tagsRecordingFile);
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

	public StartMonitorRecordingDialog getStartMonitorRecordingDialog() {
		return startMonitorRecordingDialog;
	}

	public void setStartMonitorRecordingDialog(StartMonitorRecordingDialog startMonitorRecordingDialog) {
		this.startMonitorRecordingDialog = startMonitorRecordingDialog;
	}

}