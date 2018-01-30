package org.signalml.psychopy.view.panel;

import java.io.File;
import org.signalml.app.model.components.validation.ValidationErrors;
import org.signalml.psychopy.PsychopyExperiment;

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;

import static org.signalml.app.util.i18n.SvarogI18n._;
import org.signalml.app.view.common.dialogs.errors.Dialogs;
import static org.signalml.psychopy.FilePathValidator.isEmptyDirectory;
import static org.signalml.psychopy.FilePathValidator.pathIsValid;

public class SelectOutputDirectoryPanel extends SelectFilePanel {

	@Override
	JLabel createPathLabel() {
		return new JLabel(_("Results file path:"));
	}

	@Override
	JFileChooser createFileChooser() {
		JFileChooser fileChooser = new JFileChooser();
		FileNameExtensionFilter xmlfilter = new FileNameExtensionFilter(
			"xml files (*.xml)", "xml");
		fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
		fileChooser.setFileFilter(xmlfilter);
		fileChooser.setAcceptAllFileFilterUsed(false);
		return fileChooser;
	}

	@Override
	public void validate(ValidationErrors errors) {
		File f = new File(this.selectedPath());
		if (f.isDirectory()) {
			errors.addError(_("Results path is a directory."));
		}
		File dir = f.getParentFile();
		for (File file : dir.listFiles()) {
			if (file.getName().startsWith(f.getName())) {
				if (Dialogs.showWarningYesNoDialog(_("Are you sure you want to overrite data in this location?")) == Dialogs.DIALOG_OPTIONS.NO) {
					errors.addError(_("Choose different file name prefix"));
				}
				break;
			}
		}
		String name = f.getName();
		if (name.endsWith(".xml")) {
			name = name.substring(0, name.lastIndexOf(".xml"));
		}
		this.setPath((new File(dir, name)).toString());
	}

	@Override
	public void fillPanelFromModel(Object model) {
		PsychopyExperiment psychopyExperiment = (PsychopyExperiment) model;
		setPath(psychopyExperiment.outputDirectoryPath);
	}

	@Override
	public void fillModelFromPanel(Object model) {
		PsychopyExperiment psychopyExperiment = (PsychopyExperiment) model;
		psychopyExperiment.outputDirectoryPath = selectedPath();
	}
}
