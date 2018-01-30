package org.signalml.psychopy.view;


import org.signalml.app.model.components.validation.ValidationErrors;
import org.signalml.app.view.common.dialogs.AbstractDialog;
import org.signalml.plugin.export.SignalMLException;
import org.signalml.psychopy.PsychopyExperiment;
import org.signalml.psychopy.view.panel.SelectOutputDirectoryPanel;
import org.signalml.psychopy.view.panel.SelectPsychopyExperimentPanel;

import javax.swing.*;
import java.awt.*;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
import static org.signalml.app.util.i18n.SvarogI18n._;
import org.signalml.psychopy.view.panel.SelectFilePanel;

public class PsychopyExperimentDialog extends AbstractDialog {

	private SelectPsychopyExperimentPanel experimentPanel;
	private SelectOutputDirectoryPanel outputDirectoryPanel;

	public PsychopyExperimentDialog(Window dialogParent, boolean isModal) {
		super(dialogParent, isModal);
	}

	@Override
	public boolean supportsModelClass(Class<?> clazz) {
		return PsychopyExperiment.class.isAssignableFrom(clazz);
	}

	@Override
	protected void fillDialogFromModel(Object model) throws SignalMLException {
		experimentPanel.fillPanelFromModel(model);
		outputDirectoryPanel.fillPanelFromModel(model);
	}

	@Override
	public void fillModelFromDialog(Object model) throws SignalMLException {
		experimentPanel.fillModelFromPanel(model);
		outputDirectoryPanel.fillModelFromPanel(model);
	}
	
	@Override
	protected JComponent createInterface() {
		experimentPanel = new SelectPsychopyExperimentPanel();
		outputDirectoryPanel = new SelectOutputDirectoryPanel();
		JPanel panel = new JPanel();
		panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
		
		panel.add(experimentPanel);
		panel.add(outputDirectoryPanel);
		setTitle(_("Start Psychopy Experiment"));
		setContentPane(panel);
		return panel;
	}

	protected JButton getOkButton() {
		JButton okButton = super.getOkButton();
		okButton.setText(_("Ok"));
		return okButton;
	}

	public void validateDialog(Object model, ValidationErrors errors) throws SignalMLException {
		experimentPanel.validate(errors);
		outputDirectoryPanel.validate(errors);
	}

}
