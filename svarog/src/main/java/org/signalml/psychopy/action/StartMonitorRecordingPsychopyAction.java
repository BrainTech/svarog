
package org.signalml.psychopy.action;

import java.awt.event.ActionEvent;
import org.signalml.app.action.document.monitor.StartMonitorRecordingAction;
import org.signalml.app.action.selector.SignalDocumentFocusSelector;
import org.signalml.app.document.MonitorSignalDocument;
import org.signalml.app.document.signal.SignalDocument;
import org.signalml.app.model.document.opensignal.ExperimentDescriptor;
import static org.signalml.app.util.i18n.SvarogI18n._;
import org.signalml.app.worker.monitor.recording.RecordingState;
import org.signalml.exception.SanityCheckException;
import org.signalml.psychopy.PsychopyExperiment;

public class StartMonitorRecordingPsychopyAction extends StartMonitorRecordingAction{
	
	public StartMonitorRecordingPsychopyAction(SignalDocumentFocusSelector signalDocumentFocusSelector) {
		super(signalDocumentFocusSelector);
		setText(_("Start recording"));
		setIconPath("org/signalml/app/icon/record.png");
		setToolTip(_("Run Psychopy experiment and record signal and tags from this monitor to a file."));
		setIconPath("org/signalml/psychopy/icon/psychopy.png");
	}
	
	@Override
	protected void additionalWork()
	{
		SignalDocument signalDocument = getActionFocusSelector().getActiveSignalDocument();
		if ((signalDocument != null) && (signalDocument instanceof MonitorSignalDocument)) {
			MonitorSignalDocument monitorSignalDocument = (MonitorSignalDocument) signalDocument;
			PsychopyExperiment psychopyExperiment = monitorSignalDocument.getPsychopyExperiment();
			ExperimentDescriptor experimentDescriptor = monitorSignalDocument.getExperimentDescriptor();
			psychopyExperiment.experimentPath = experimentDescriptor.experimentPath;
			psychopyExperiment.outputPathPrefix = experimentDescriptor.outputPathPrefix;
			psychopyExperiment.run();
		}
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
		super.setEnabledAsNeeded();
		SignalDocument signalDocument = getActionFocusSelector().getActiveSignalDocument();

		boolean enabled = false;
		if (signalDocument != null && signalDocument instanceof MonitorSignalDocument) {
			MonitorSignalDocument monitor = (MonitorSignalDocument) signalDocument;
			enabled = (monitor.getRecordingState() == RecordingState.FINISHED) && !monitor.isPsychopyExperimentRunning();
		}
		setEnabled(enabled);
	}
	
}
