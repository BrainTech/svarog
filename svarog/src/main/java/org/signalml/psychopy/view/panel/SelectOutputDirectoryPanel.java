package org.signalml.psychopy.view.panel;

import org.signalml.app.model.components.validation.ValidationErrors;
import org.signalml.psychopy.PsychopyExperiment;

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;

import static org.signalml.app.util.i18n.SvarogI18n._;
import org.signalml.app.view.common.dialogs.errors.Dialogs;

import java.io.File;

import static org.signalml.psychopy.FilePathValidator.isDirectory;
import static org.signalml.psychopy.FilePathValidator.fileWithPrefixExists;

public class SelectOutputDirectoryPanel extends SelectFilePanel {

	@Override
	JLabel createPathLabel() {
		return new JLabel(_("Signal filename:"));
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

	
	protected JLabel createInfoLabel()
	{
		return new JLabel(_("<html>Choose files to which signal, tags and Psychopy results will be saved.<br>File Extensions will be added automatically</html>"));
	}
	protected String createBorderTitle()
	{
		return _("Output: Experiment results");
	}
	
	@Override
	public void validate(ValidationErrors errors) {
		if (this.selectedPath() == null || this.selectedPath().isEmpty()) {
			errors.addError(_("Results path should not be empty"));
		}

		makePathAbsolute();

		if (isDirectory(this.selectedPath())) {
			errors.addError(_("Results path is a directory."));
		}
		if (this.selectedPath().endsWith(File.separator)) {
			errors.addError(_("Results path should not end with path separator"));
		}
		if (fileWithPrefixExists(this.selectedPath())) {
			Dialogs.DIALOG_OPTIONS overwrite = Dialogs.showWarningYesNoDialog(
				_("Are you sure you want to overrite data in this location?")
			);
			if (overwrite == Dialogs.DIALOG_OPTIONS.NO) {
				errors.addError(_("Choose different file name prefix"));
			}
		}

		cutXmlFileExtension();
	}

	private void cutXmlFileExtension() {
		if (this.selectedPath().endsWith(".xml")) {
			String path = this.selectedPath();
			String pathWithoutExtension = path.substring(0, path.lastIndexOf(".xml"));
			this.setPath(pathWithoutExtension);
		}
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
