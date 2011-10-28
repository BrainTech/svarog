package org.signalml.app.view.montage;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import org.signalml.app.config.preset.EegSystemsPresetManager;
import org.signalml.app.config.preset.PresetComboBoxModel;
import org.signalml.app.view.element.AbstractSignalMLPanel;
import org.signalml.domain.montage.Montage;
import org.signalml.domain.montage.system.EegSystem;
import org.springframework.context.support.MessageSourceAccessor;

/**
 * A panel containing a {@link JComboBox} for selecting which {@link EegSystem}
 * should be used for the current montage.
 *
 * @author Piotr Szachewicz
 */
public class EegSystemSelectionPanel extends AbstractSignalMLPanel {

	/**
	 * The current montage.
	 */
	private Montage montage;
	/**
	 * The {@link PresetManager} that manages available {@link EegSystem EEG Systems}.
	 */
	private EegSystemsPresetManager eegSystemsPresetManager;
	/**
	 * The {@link JComboBox} for EEG system selection.
	 */
	private JComboBox presetComboBox;
	/**
	 * The model for the {@link EegSystemSelectionPanel#presetComboBox}.
	 */
	private PresetComboBoxModel presetComboBoxModel;

	/**
	 * Constructor.
	 * @param messageSource MessageSourceAccessor for resolving localized messages
	 * @param eegSystemsPresetManager {@link PresetManager} for managing
	 * available EEG systems
	 */
	public EegSystemSelectionPanel(MessageSourceAccessor messageSource, EegSystemsPresetManager eegSystemsPresetManager) {
		super(messageSource);
		this.eegSystemsPresetManager = eegSystemsPresetManager;
		initialize();
	}

	@Override
	protected void initialize() {
		setLayout(new BorderLayout());
		setTitledBorder(messageSource.getMessage("signalMontage.selectEegSystem"));

		JLabel comboBoxLabel = new JLabel(messageSource.getMessage("signalMontage.currentEEGSystem"));
		add(comboBoxLabel, BorderLayout.WEST);
		add(getPresetComboBox(), BorderLayout.EAST);

	}

	/**
	 * Returns (and if necessary - creates) the combo box for EEG system
	 * selection.
	 * @return the combo box for EEG system selection
	 */
	protected JComboBox getPresetComboBox() {
		if (presetComboBox == null) {
			presetComboBox = new JComboBox(getPresetComboBoxModel());
			presetComboBox.setPreferredSize(new Dimension(300, 20));
			presetComboBox.addActionListener(new ActionListener() {

				@Override
				public void actionPerformed(ActionEvent e) {
					if (montage != null) {
						montage.setEegSystem(getSelectedEegSystem());
					}
				}
			});
		}
		return presetComboBox;
	}

	/**
	 * Returns (and if necessary - creates) a ComboBoxModel for EEG system
	 * selection.
	 * @return the ComboBoxModel for EEG system selection
	 */
	protected PresetComboBoxModel getPresetComboBoxModel() {
		if (presetComboBoxModel == null) {
			presetComboBoxModel = new PresetComboBoxModel(null, eegSystemsPresetManager);
			Object firstElement = presetComboBoxModel.getElementAt(0);
			if (firstElement != null) {
				presetComboBoxModel.setSelectedItem(firstElement);
			}
		}
		return presetComboBoxModel;
	}

	/**
	 * Returns the EEG system selected using this panel.
	 * @return the selected EEG system
	 */
	protected EegSystem getSelectedEegSystem() {
		return (EegSystem) presetComboBoxModel.getSelectedItem();
	}

	/**
	 * Sets the current {@link Montage}.
	 * @param montage the current Montage
	 */
	public void setMontage(Montage montage) {
		this.montage = montage;
		if (montage != null && montage.getEegSystem() != null) {
			presetComboBoxModel.setSelectedItem(montage.getEegSystem());
		} else if (montage != null) {
			EegSystem eegSystem = (EegSystem) eegSystemsPresetManager.getPresetAt(0);
			presetComboBoxModel.setSelectedItem(eegSystem);
			montage.setEegSystem(eegSystem);
		}
	}
}
