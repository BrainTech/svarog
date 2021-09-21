package org.signalml.psychopy.action;

import java.awt.Window;
import javax.swing.JPanel;
import org.signalml.app.model.components.validation.ValidationErrors;
import org.signalml.app.model.document.opensignal.ExperimentDescriptor;
import org.signalml.app.model.monitor.MonitorRecordingDescriptor;
import org.signalml.app.view.document.monitor.StartMonitorRecordingDialog;
import org.signalml.plugin.export.SignalMLException;
import org.signalml.psychopy.view.panel.SelectPsychopyExperimentPanel;


public class StartMonitorRecordingPsychopyDialog  extends StartMonitorRecordingDialog{
	private SelectPsychopyExperimentPanel experimentPanel;
	public StartMonitorRecordingPsychopyDialog(Window w, boolean isModal) {
		super(w, isModal);
	}
	
	public SelectPsychopyExperimentPanel getSelectPsychopyExperimentPanel() {
	if (experimentPanel == null)
		experimentPanel = new SelectPsychopyExperimentPanel();
	return experimentPanel;
	}
	
	@Override
	protected void addAdditionalPanel(JPanel panel)
	{
		panel.add(getSelectPsychopyExperimentPanel());
	}
	
	@Override
	public void validateDialog(Object model, ValidationErrors errors) throws SignalMLException {
		super.validateDialog(model, errors);
		getSelectPsychopyExperimentPanel().validate(errors);
	}
	
	@Override
	public void fillDialogFromModel(Object model) throws SignalMLException {
		super.fillDialogFromModel(model);
		getSelectPsychopyExperimentPanel().fillPanelFromModel(model);
		
	}

	@Override
	public void fillModelFromDialog(Object model) throws SignalMLException {
		super.fillModelFromDialog(model);
		getSelectPsychopyExperimentPanel().fillModelFromPanel(model);
		ExperimentDescriptor experimentDescriptor = (ExperimentDescriptor) model;
		MonitorRecordingDescriptor monitorRecordingDescriptor = experimentDescriptor.getMonitorRecordingDescriptor();
		experimentDescriptor.outputPathPrefix = monitorRecordingDescriptor.getSignalRecordingFilePath();
	}
	
}
