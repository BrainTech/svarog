package org.signalml.psychopy.view;

import static org.signalml.app.util.i18n.SvarogI18n._;

import org.signalml.app.model.components.validation.ValidationErrors;
import org.signalml.app.model.document.opensignal.ExperimentDescriptor;
import org.signalml.app.util.IconUtils;
import org.signalml.app.view.common.dialogs.AbstractDialog;
import org.signalml.plugin.export.SignalMLException;
import org.signalml.psychopy.messages.RunPsychopyExperiment;
import org.signalml.psychopy.view.panel.SelectOutputDirectoryPanel;
import org.signalml.psychopy.view.panel.SelectPsychopyExperimentPanel;

import javax.swing.*;
import javax.swing.border.EtchedBorder;
import java.awt.*;


public class PsychopyExperimentDialog extends AbstractDialog {
	private SelectPsychopyExperimentPanel experimentPanel;
	private SelectOutputDirectoryPanel outputDirectoryPanel;
	private ExperimentDescriptor model;

	public PsychopyExperimentDialog(Window dialogParent, boolean isModal) {
		super(dialogParent, isModal);
	}

	@Override
	public boolean supportsModelClass(Class<?> clazz) {
		return true;
		// todo: return correct model class
		// return ExperimentDescriptor.class.isAssignableFrom(clazz);
	}

	@Override
	protected void fillDialogFromModel(Object model) throws SignalMLException {
		// todo: uncomment after added model
		// experimentPanel.fillPanelFromModel(model);
		// outputDirectoryPanel.fillPanelFromModel(model);
	}

	@Override
	public void fillModelFromDialog(Object model) throws SignalMLException {
		// todo: uncomment after added model
		// experimentPanel.fillModelFromPanel(model);
		// outputDirectoryPanel.fillModelFromPanel(model);
	}

	@Override
	protected JComponent createInterface() {
		initializeSelectFilePanels();

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

	private void initializeSelectFilePanels() {
		if (experimentPanel == null) {
			experimentPanel = new SelectPsychopyExperimentPanel();
		}
		if (outputDirectoryPanel == null) {
			outputDirectoryPanel = new SelectOutputDirectoryPanel();
		}
	}

	protected JButton getCancelButton(){
		JButton cancelButton = super.getCancelButton();
		Icon icon = IconUtils.loadClassPathIcon("org/signalml/app/icon/cancel.png");
		cancelButton.setText(_("Cancel"));
		cancelButton.setIcon(icon);
		cancelButton.setHorizontalAlignment(SwingConstants.CENTER);
		return cancelButton;
	}

	protected JButton getOkButton(){
		JButton okButton = super.getOkButton();
		Icon icon = IconUtils.loadClassPathIcon("org/signalml/app/icon/ok.png");
		okButton.setText(_("Run"));
		okButton.setIcon(icon);
		okButton.setHorizontalAlignment(SwingConstants.CENTER);
		return okButton;
	}

	protected void onOkPressed() {
		super.onOkPressed();
		if (isClosedWithOk()) {
			// todo: after model is added
//			model.getPeer().publish(
//				new RunPsychopyExperiment(
//					model.getPeerId(),
//					experimentPanel.selectedPath(),
//					outputDirectoryPanel.selectedPath()
//				)
//			);
			System.out.print("Ok without errors");
		} else {
			System.out.print("Ok with errors");
		}
	}

	public void validateDialog(Object model, ValidationErrors errors) throws SignalMLException {
		super.validateDialog(model, errors);
		experimentPanel.validate(errors);
		outputDirectoryPanel.validate(errors);
	}

}
