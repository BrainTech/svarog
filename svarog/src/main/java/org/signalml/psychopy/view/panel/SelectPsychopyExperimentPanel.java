package org.signalml.psychopy.view.panel;

import org.signalml.app.model.components.validation.ValidationErrors;

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;

import static org.signalml.app.util.i18n.SvarogI18n._;
import static org.signalml.psychopy.FilePathValidator.pathIsValid;

public class SelectPsychopyExperimentPanel extends SelectFilePanel{

	@Override
	JLabel createPathLabel() {
		return new JLabel(_("Procedure filename:"));
	}

	@Override
	JFileChooser createFileChooser() {
		JFileChooser fileChooser = new JFileChooser();
		fileChooser.setAcceptAllFileFilterUsed(false);
		fileChooser.addChoosableFileFilter(
			new FileNameExtensionFilter(
				_("Psychopy experiment files (*.psyexp)"),
				"psyexp"
			)
		);
		return fileChooser;
	}

	@Override
	public void validate(ValidationErrors errors) {
		if (!pathIsValid(this.selectedPath())) {
			errors.addError(_("Psychopy experiment path has not been selected."));
			clearPath();
		}
	}

}
