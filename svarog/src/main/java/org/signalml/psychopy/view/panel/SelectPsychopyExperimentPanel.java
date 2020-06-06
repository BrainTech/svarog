package org.signalml.psychopy.view.panel;

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import org.signalml.app.model.components.validation.ValidationErrors;
import static org.signalml.app.util.i18n.SvarogI18n._;
import static org.signalml.psychopy.FilePathValidator.isFile;
import static org.signalml.psychopy.FilePathValidator.pathIsValid;
import org.signalml.psychopy.PsychopyExperiment;

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

	protected int showDialog()
	{
		return this.fileChooser.showOpenDialog(this);
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
		makePathAbsolute();
		if (!validPsychopyFileIsSelected()) {
			errors.addError(_("Wrong procedure filename."));
		}
	}

	private boolean validPsychopyFileIsSelected() {
		return pathIsValid(this.selectedPath())
				&& isFile(this.selectedPath())
				&& (
					this.selectedPath().endsWith(".psyexp")
					|| this.selectedPath().endsWith(".py")
				);
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
