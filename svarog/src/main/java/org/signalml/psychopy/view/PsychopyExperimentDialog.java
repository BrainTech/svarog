package org.signalml.psychopy.view;

import org.signalml.app.model.components.validation.ValidationErrors;
import org.signalml.app.view.common.dialogs.AbstractDialog;
import org.signalml.plugin.export.SignalMLException;
import org.signalml.psychopy.PsychopyExperiment;
import org.signalml.psychopy.view.panel.SelectOutputPathPrefixPanel;
import org.signalml.psychopy.view.panel.SelectPsychopyExperimentPanel;

import javax.swing.*;
import java.awt.*;
import static org.signalml.app.util.i18n.SvarogI18n._;

public class PsychopyExperimentDialog extends AbstractDialog {

	private SelectPsychopyExperimentPanel experimentPanel;
	private SelectOutputPathPrefixPanel outputPathPrefixPanel;

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
		outputPathPrefixPanel.fillPanelFromModel(model);
	}

	@Override
	public void fillModelFromDialog(Object model) throws SignalMLException {
		experimentPanel.fillModelFromPanel(model);
		outputPathPrefixPanel.fillModelFromPanel(model);
	}
	
	@Override
	protected JComponent createInterface() {
		experimentPanel = new SelectPsychopyExperimentPanel();
		outputPathPrefixPanel = new SelectOutputPathPrefixPanel();
		JPanel panel = new JPanel();
		panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
		
		panel.add(experimentPanel);
		panel.add(outputPathPrefixPanel);
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
		outputPathPrefixPanel.validate(errors);
	}

}
