package org.signalml.psychopy.view.panel;

import org.signalml.app.model.components.validation.ValidationErrors;
import org.signalml.psychopy.PsychopyExperiment;

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;

import static org.signalml.app.util.i18n.SvarogI18n._;
import static org.signalml.psychopy.FilePathValidator.pathIsValid;

public class SelectPsychopyExperimentPanel extends SelectFilePanel{

	@Override
	JLabel createPathLabel() {
		return new JLabel(_("Procedure filename:"));
	}

	protected JLabel createInfoLabel()
	{
		return new JLabel(_("Choose Psychopy procedure that will run after you click \"Ok\""));
	}
	protected String createBorderTitle()
	{
		return _("Input: Psychopy procedure");
	}
	
	@Override
	JFileChooser createFileChooser() {
		JFileChooser fileChooser = new JFileChooser();
		fileChooser.setAcceptAllFileFilterUsed(false);
		fileChooser.addChoosableFileFilter(
			new FileNameExtensionFilter(
				_("Psychopy experiment files (*.psyexp, *.py)"),
				"psyexp","py"
			)
		);
		return fileChooser;
	}

	@Override
	public void validate(ValidationErrors errors) {
		if (!pathIsValid(this.selectedPath())) {
			errors.addError(_("Psychopy experiment path has not been selected."));
		}
	}

	@Override
	public void fillPanelFromModel(Object model) {
		PsychopyExperiment psychopyExperiment = (PsychopyExperiment) model;
		setPath(psychopyExperiment.experimentPath);
	}

	@Override
	public void fillModelFromPanel(Object model) {
		PsychopyExperiment psychopyExperiment = (PsychopyExperiment) model;
		psychopyExperiment.experimentPath = selectedPath();
	}

}
