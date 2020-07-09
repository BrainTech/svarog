/* MonitorRecordingAction.java created 2010-11-01
 *
 */

package org.signalml.app.action.document.monitor;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import org.signalml.app.action.AbstractFocusableSignalMLAction;
import org.signalml.app.action.selector.ActionFocusEvent;
import org.signalml.app.action.selector.SignalDocumentFocusSelector;
import org.signalml.app.document.MonitorSignalDocument;
import org.signalml.app.document.signal.SignalDocument;

/**
 * This class represents an action toggling the recording on/off. It is the upper class
 * for {@link StartMonitorRecordingAction} and {@link StopMonitorRecordingAction} and contains
 * code responsible for enabling and disabling apropriate menu items according to the state of
 * the recording (i. e. whether the recording is on or off).
 *
 * @author Piotr Szachewicz
 */
public abstract class MonitorRecordingAction extends AbstractFocusableSignalMLAction<SignalDocumentFocusSelector> implements PropertyChangeListener {

	public MonitorRecordingAction(SignalDocumentFocusSelector signalDocumentFocusSelector) {
		super(signalDocumentFocusSelector);
	}

	@Override
	public void actionFocusChanged(ActionFocusEvent e) {
		super.actionFocusChanged(e);
		if (this.getActionFocusSelector().getActiveDocument() instanceof MonitorSignalDocument) {
			SignalDocument sd = getActionFocusSelector().getActiveSignalDocument();
			sd.addPropertyChangeListener(this);
		}
	}

	@Override
	public void propertyChange(PropertyChangeEvent evt) {
		String propertyName = evt.getPropertyName();
		if (propertyName.equals(MonitorSignalDocument.RECORDING_STATE_PROPERTY) ||
			propertyName.equals(MonitorSignalDocument.IS_PSYCHOPY_EXPERIMENT_RUNNING_PROPERTY))
		{
			setEnabledAsNeeded();
		}
	}

}
