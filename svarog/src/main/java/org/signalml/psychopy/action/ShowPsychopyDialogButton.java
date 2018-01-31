package org.signalml.psychopy.action;

import static org.signalml.app.util.i18n.SvarogI18n._;

import org.signalml.app.action.AbstractFocusableSignalMLAction;
import org.signalml.app.document.signal.SignalDocument;
import org.signalml.exception.SanityCheckException;
import org.signalml.psychopy.PsychopyExperiment;
import org.signalml.psychopy.view.PsychopyExperimentDialog;

import java.awt.event.ActionEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import org.signalml.app.action.selector.ActionFocusEvent;
import org.signalml.app.action.selector.SignalDocumentFocusSelector;
import org.signalml.app.document.MonitorSignalDocument;

public class ShowPsychopyDialogButton extends AbstractFocusableSignalMLAction<SignalDocumentFocusSelector> implements PropertyChangeListener {

	private PsychopyExperimentDialog dialog;

	public ShowPsychopyDialogButton(SignalDocumentFocusSelector actionFocusSelector) {
		super(actionFocusSelector);
		setIconPath("org/signalml/psychopy/icon/psychopy.png");
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
	public void actionPerformed(ActionEvent event) {
		SignalDocument signalDocument = getActionFocusSelector().getActiveSignalDocument();
		if ((signalDocument != null) && (signalDocument instanceof MonitorSignalDocument)) {
			MonitorSignalDocument monitorSignalDocument = (MonitorSignalDocument) signalDocument;
			PsychopyExperiment psychopyExperiment = monitorSignalDocument.getPsychopyExperiment();
			boolean closedWithOk = dialog.showDialog(psychopyExperiment, true);
			if (closedWithOk) {
				psychopyExperiment.run();
			}
		}
	}

	public void setSelectPsychopyExperimentDialog(PsychopyExperimentDialog psychopyExperimentDialog) {
		this.dialog = psychopyExperimentDialog;
	}

	private void updateToolTip(SignalDocument signalDocument) {
		boolean experimentIsOnline = !isSignalDocumentOfflineSignalDocument(signalDocument);
		boolean experimentIsRemote = false; // todo + update on hover

		if (experimentIsOnline && experimentIsRemote) {
			setToolTip(_("Select psychopy experiment to run"));
		} else if (experimentIsOnline) {
			setToolTip(_("Psychopy experiment can not be run on remote host."));
		} else if (experimentIsRemote) {
			setToolTip(_("Psychopy experiment can not be run in offline mode."));
		} else {
			throw new SanityCheckException(
				_("Svarog do not support opening offline signals from remote experiments.")
			);
		}
	}

	@Override
	public void setEnabledAsNeeded() {
		SignalDocument signalDocument = getActionFocusSelector().getActiveSignalDocument();

		if ((signalDocument != null) && (signalDocument instanceof MonitorSignalDocument)
			&& !((MonitorSignalDocument) signalDocument).isRecording()) {
			setEnabled(true);
		} else {
			setEnabled(false);
		}
	}
	
	@Override
	public void propertyChange(PropertyChangeEvent evt) {
		if (evt.getPropertyName().equals(MonitorSignalDocument.IS_RECORDING_PROPERTY)) {
			setEnabledAsNeeded();
		}
	}

}
