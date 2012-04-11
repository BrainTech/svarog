package org.signalml.app.view.montage;

import static org.signalml.app.util.i18n.SvarogI18n._;

import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeListener;

import javax.swing.JComboBox;

import org.signalml.app.config.preset.EegSystemsPresetManager;
import org.signalml.app.config.preset.PresetComboBoxModel;
import org.signalml.app.config.preset.PresetManager;
import org.signalml.app.model.document.opensignal.AbstractOpenSignalDescriptor;
import org.signalml.app.view.components.AbstractPanel;
import org.signalml.domain.montage.Montage;
import org.signalml.domain.montage.system.EegSystem;
import org.signalml.domain.montage.system.EegSystemName;
import org.signalml.domain.signal.raw.RawSignalDescriptor;

/**
 * A panel containing a {@link JComboBox} for selecting which {@link EegSystem}
 * should be used for the current montage.
 *
 * @author Piotr Szachewicz
 */
public class EegSystemSelectionPanel extends AbstractPanel {

	public static String EEG_SYSTEM_PROPERTY = "eegSystemProperty";
	
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
	 * @param eegSystemsPresetManager {@link PresetManager} for managing
	 * available EEG systems
	 */
	public EegSystemSelectionPanel(EegSystemsPresetManager eegSystemsPresetManager, PropertyChangeListener listener) {
		this.eegSystemsPresetManager = eegSystemsPresetManager;
		addPropertyChangeListener(listener);
		createInterface();
	}
	
	public EegSystemSelectionPanel(EegSystemsPresetManager eegSystemsPresetManager) {
		this.eegSystemsPresetManager = eegSystemsPresetManager;
		createInterface();
	}

	protected void createInterface() {
		
		//setTitledBorder(_("Select EEG system"));
		add(getPresetComboBox());

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
					fireEegSystemChangedProperty();
				}
			});
		}
		return presetComboBox;
	}
	
	public void fireEegSystemChangedProperty() {
		firePropertyChange(EEG_SYSTEM_PROPERTY, null, getSelectedEegSystem());
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
	public EegSystem getSelectedEegSystem() {
		return (EegSystem) presetComboBoxModel.getSelectedItem();
	}

	/**
	 * Sets the EEG system which should be selected in this panel.
	 * @param name the name of the EEG system to be selected
	 */
	public void setEegSystemByName(EegSystemName name) {
		EegSystem eegSystem = (EegSystem) eegSystemsPresetManager.getPresetByName(name.getFullName());

		if (eegSystem != null)
			setEegSystem(eegSystem);
		else
			setEegSystem(getSelectedEegSystem());
	}

	/**
	 * Sets the EEG system which should be selected in this panel.
	 * @param name the EEG system to be selected
	 */
	public void setEegSystem(EegSystem eegSystem) {
		presetComboBoxModel.setSelectedItem(eegSystem);
		fireEegSystemChangedProperty();
	}

	public void fillPanelFromModel(AbstractOpenSignalDescriptor openSignalDescriptor) {
		if (openSignalDescriptor instanceof RawSignalDescriptor) {
			RawSignalDescriptor rawSignalDescriptor = (RawSignalDescriptor) openSignalDescriptor;
			EegSystemName eegSystemName = rawSignalDescriptor.getEegSystemName();
			
			if (eegSystemName != null)
				setEegSystemByName(eegSystemName);
		}
	}
}
