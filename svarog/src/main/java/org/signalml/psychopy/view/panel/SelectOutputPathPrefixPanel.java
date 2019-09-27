package org.signalml.psychopy.view.panel;

import org.signalml.app.model.components.validation.ValidationErrors;
import org.signalml.psychopy.PsychopyExperiment;

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;

import static org.signalml.app.util.i18n.SvarogI18n._;

import java.io.File;

import static org.signalml.psychopy.FilePathValidator.cutXmlFileExtension;
import static org.signalml.psychopy.FilePathValidator.isDirectory;

public class SelectOutputPathPrefixPanel extends SelectFilePanel {

	@Override
	JLabel createPathLabel() {
		return new JLabel(_("Signal filename:"));
	}

	@Override
	JFileChooser createFileChooser() {
		JFileChooser fileChooser = new JFileChooser();
		FileNameExtensionFilter xmlfilter = new FileNameExtensionFilter(
			_("XML files (*.xml)"), "xml");
		fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
		fileChooser.setFileFilter(xmlfilter);
		fileChooser.setAcceptAllFileFilterUsed(false);
		return fileChooser;
	}

	protected JLabel createInfoLabel() {
		return new JLabel(_("<html>Choose files to which signal, tags and Psychopy results will be saved.<br>File Extensions will be added automatically</html>"));
	}

	protected String createBorderTitle() {
		return _("Output: Experiment results");
	}

	protected int showDialog()
	{
		return this.fileChooser.showSaveDialog(this);
	}

	@Override
	public void validate(ValidationErrors errors) {
		if (this.selectedPath() == null || this.selectedPath().isEmpty()) {
			errors.addError(_("Results path should not be empty"));
			return;
		}

		makePathAbsolute();

		if (isDirectory(this.selectedPath())) {
			errors.addError(_("Results path is a directory."));
			return;
		}
		if (this.selectedPath().endsWith(File.separator)) {
			errors.addError(_("Results path should not end with path separator"));
			return;
		}
		File existingDir = new File(this.selectedPath()).getParentFile();
		while (!existingDir.exists()) existingDir = existingDir.getParentFile();
		if (!existingDir.canWrite()) {
			errors.addError(_("Results path is not writable"));
			return;
		}
		this.setPath(cutXmlFileExtension(this.selectedPath()));
	}

	@Override
	public void fillPanelFromModel(Object model) {
		PsychopyExperiment psychopyExperiment = (PsychopyExperiment) model;
		setPath(psychopyExperiment.outputPathPrefix);
	}

	@Override
	public void fillModelFromPanel(Object model) {
		PsychopyExperiment psychopyExperiment = (PsychopyExperiment) model;
		psychopyExperiment.outputPathPrefix = selectedPath();
	}
}
