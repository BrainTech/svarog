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
import static org.signalml.app.util.i18n.SvarogI18n._;
import org.signalml.app.view.common.components.ResolvableComboBox;
import org.signalml.app.view.common.dialogs.errors.Dialogs;
import org.signalml.domain.montage.Montage;
import org.signalml.domain.montage.MontageException;
import org.signalml.domain.montage.generators.IMontageGenerator;
import org.signalml.domain.montage.system.EegSystem;

public class MontageGeneratorComboBoxMainPanel extends ResolvableComboBox {

	private int previouslySelectedIndex;
	private Montage userDefinedMontage;
	private final MontageGeneratorListModel model;

	public MontageGeneratorComboBoxMainPanel(SignalDocument document) {
		super();
		ManagerOfPresetManagers managerOfPresetsManagers = SvarogApplication.getManagerOfPresetsManagers();
		EegSystemsPresetManager eegSystemsPresetManager = managerOfPresetsManagers.getEegSystemsPresetManager();

		Montage montage = document.getMontage();
		EegSystem system = (EegSystem) eegSystemsPresetManager.getPresetByName(montage.getEegSystemFullName());
		model = new MontageGeneratorListModel(_("Common user defined"), system);
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

	/**
	 * Replace currently selected montage generator to match the one given.
	 * If generator == null, "User-defined montage" will be selected.
	 *
	 * @param generator  generator to select or null for "user-defined montage"
	 */
	public void setMatchingMontageGenerator(IMontageGenerator generator) {
		Object matching = null;
		if (generator == null) {
			matching = model.getElementAt(0);
		} else {
			for (int i=1; i<model.getSize(); ++i) {
				IMontageGenerator item = (IMontageGenerator) model.getElementAt(i);
				if (item.getClass() == generator.getClass()) {
					matching = item;
					break;
				}
			}
		}
		if (matching != null) {
			model.setSelectedItem(matching);
			revalidate();
			repaint();
		}
	}
}
