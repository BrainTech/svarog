package org.signalml.psychopy.view.panel;

import org.signalml.app.model.components.validation.ValidationErrors;

import javax.swing.*;

import static org.signalml.app.util.i18n.SvarogI18n._;
import static org.signalml.psychopy.FilePathValidator.isEmptyDirectory;
import static org.signalml.psychopy.FilePathValidator.pathIsValid;

public class SelectOutputDirectoryPanel extends SelectFilePanel {

	@Override
	JLabel createPathLabel() {
		return new JLabel(_("Results directory path:"));
	}

	@Override
	JFileChooser createFileChooser() {
		JFileChooser fileChooser = new JFileChooser();
		fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
		fileChooser.setAcceptAllFileFilterUsed(false);
		return fileChooser;
	}

	@Override
	public void validate(ValidationErrors errors) {
		if (!pathIsValid(this.selectedPath())) {
			errors.addError(_("Results directory path has not been selected."));
			clearPath();
		}
		if (!isEmptyDirectory(this.selectedPath())) {
			errors.addError(_("Selected directory is not empty. Please select an empty directory."));
			clearPath();
		}
	}
}
