package org.signalml.psychopy.view;

import static org.signalml.app.util.i18n.SvarogI18n._;

import org.signalml.app.model.components.validation.ValidationErrors;
import org.signalml.app.view.common.dialogs.AbstractDialog;
import org.signalml.plugin.export.SignalMLException;
import org.signalml.psychopy.PsychopyExperiment;
import org.signalml.psychopy.view.panel.SelectOutputDirectoryPanel;
import org.signalml.psychopy.view.panel.SelectPsychopyExperimentPanel;

import javax.swing.*;
import javax.swing.border.EtchedBorder;
import java.awt.*;

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
		panel.setBorder(new EtchedBorder());

		JLabel experimentLabel = new JLabel(_("Choose Psychopy procedure that will run"));
		experimentLabel.setHorizontalAlignment(SwingConstants.LEFT);
		panel.add(experimentLabel);
		panel.add(experimentPanel);

		JLabel directoryLabel = new JLabel(_("Choose directory where results will be saved to"));
		directoryLabel.setHorizontalAlignment(SwingConstants.LEFT);
		panel.add(directoryLabel);
		panel.add(outputDirectoryPanel);

		setTitle(_("Start Psychopy Experiment"));
		setContentPane(panel);
		return panel;
	}

	protected JButton getOkButton() {
		JButton okButton = super.getOkButton();
		okButton.setText(_("Run"));
		return okButton;
	}

	public void validateDialog(Object model, ValidationErrors errors) throws SignalMLException {
		experimentPanel.validate(errors);
		outputDirectoryPanel.validate(errors);
	}

}
