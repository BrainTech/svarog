package org.signalml.app.view.montage;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.signalml.app.SvarogApplication;
import org.signalml.app.config.ManagerOfPresetManagers;
import org.signalml.app.config.preset.managers.EegSystemsPresetManager;
import org.signalml.app.document.signal.SignalDocument;
import org.signalml.app.model.montage.MontageGeneratorListModel;
import org.signalml.app.view.common.components.ResolvableComboBox;
import org.signalml.app.view.common.dialogs.errors.Dialogs;
import org.signalml.app.view.common.dialogs.errors.ValidationErrorsDialog;
import org.signalml.domain.montage.Montage;
import org.signalml.domain.montage.MontageException;
import org.signalml.domain.montage.generators.IMontageGenerator;
import org.signalml.domain.montage.system.EegSystem;

public class MontageGeneratorComboBoxMainPanel extends ResolvableComboBox {

	private ValidationErrorsDialog errorsDialog;
	private int previouslySelectedIndex;
	private Montage userDefinedMontage;

	public MontageGeneratorComboBoxMainPanel(SignalDocument document) {
		super();
		ManagerOfPresetManagers managerOfPresetsManagers = SvarogApplication.getManagerOfPresetsManagers();
		EegSystemsPresetManager eegSystemsPresetManager = managerOfPresetsManagers.getEegSystemsPresetManager();

		Montage montage = document.getMontage();
		EegSystem system = (EegSystem) eegSystemsPresetManager.getPresetByName(montage.getEegSystemFullName());
		MontageGeneratorListModel model = new MontageGeneratorListModel();
		model.setEegSystem(system);
		this.setModel(model);
		previouslySelectedIndex = getSelectedIndex();

		addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				Montage originalMontage = document.getMontage();
				Montage modifiedMontage = new Montage(originalMontage);
				// when we come back to user defined montage position
				if (getSelectedIndex() == 0 & previouslySelectedIndex != 0) {
					document.setMontage(userDefinedMontage);
					previouslySelectedIndex = getSelectedIndex();
					return;
				}
				// when we leave user defined montage position
				if (previouslySelectedIndex == 0) {
					userDefinedMontage = new Montage(originalMontage);
				}
				Object item = getSelectedItem();

				if (!(item instanceof IMontageGenerator)) {
					montage.setMontageGenerator(null);
					return;
				}
				IMontageGenerator generator = (IMontageGenerator) item;
				try {
					generator.validateSourceMontage(originalMontage, null);
					generator.createMontage(modifiedMontage);
				} catch (MontageException ex) {
					Logger.getLogger(MontageGeneratorComboBoxMainPanel.class.getName()).log(Level.SEVERE, null, ex);
					Dialogs.showError(ex.getLocalizedMessage());
					setSelectedIndex(previouslySelectedIndex);
				}
				previouslySelectedIndex = getSelectedIndex();

				document.setMontage(modifiedMontage);
			}

		});
	}

}
