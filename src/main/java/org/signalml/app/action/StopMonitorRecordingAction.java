/* StopMonitorRecordingAction.java created 2010-10-29
 *
 */

package org.signalml.app.action;

import java.io.IOException;
import java.util.logging.Level;
import java.awt.event.ActionEvent;
import org.apache.log4j.Logger;
import org.signalml.app.action.selector.SignalDocumentFocusSelector;
import org.signalml.app.document.MonitorSignalDocument;
import org.signalml.app.document.SignalDocument;
import org.springframework.context.support.MessageSourceAccessor;

/** 
 * This class is responsible for actions regarding the menu item which stops the recording of the monitor.
 *
 * @author Piotr Szachewicz
 */
public class StopMonitorRecordingAction extends MonitorRecordingAction {

	/**
	 * Logger to save history of execution at.
	 */
        protected static final Logger logger = Logger.getLogger(StopMonitorRecordingAction.class);
	
	public StopMonitorRecordingAction(MessageSourceAccessor messageSource, SignalDocumentFocusSelector signalDocumentFocusSelector) {

                super(messageSource, signalDocumentFocusSelector);
		setIconPath("org/signalml/app/icon/stop-recording.png");
		setText("action.stopMonitorRecordingLabel");
		setToolTip("action.stopMonitorRecordingToolTip");

	}

	/**
	 * Stops the recording for the currently open document. (If it is a {@link MonitorSignalDocument}).
	 *
	 * @param ev represents the event that has happened
	 */
	@Override
	public void actionPerformed(ActionEvent ev) {
		System.out.println("stopped recording");
		SignalDocument signalDocument = getActionFocusSelector().getActiveSignalDocument();
		if ((signalDocument != null) && (signalDocument instanceof MonitorSignalDocument)) {
			MonitorSignalDocument monitorSignalDocument = (MonitorSignalDocument) signalDocument;
			try {
				monitorSignalDocument.stopMonitorRecording();
			} catch (IOException ex) {
				java.util.logging.Logger.getLogger(StopMonitorRecordingAction.class.getName()).log(Level.SEVERE, null, ex);
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

		if ((signalDocument != null) && (signalDocument instanceof MonitorSignalDocument)) {
			if(((MonitorSignalDocument) signalDocument).isRecording())
				setEnabled(true);
			else
				setEnabled(false);
		}
		else
			setEnabled(false);

	}

}