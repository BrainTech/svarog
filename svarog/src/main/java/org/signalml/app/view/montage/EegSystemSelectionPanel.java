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
 *
 * @author Piotr Szachewicz
 */
public class EegSystemSelectionPanel extends AbstractSignalMLPanel {

	private Montage montage;
	private EegSystemsPresetManager eegSystemsPresetManager;
	private JComboBox presetComboBox;
	private PresetComboBoxModel presetComboBoxModel;

	public EegSystemSelectionPanel(MessageSourceAccessor messageSource, EegSystemsPresetManager eegSystemsPresetManager) {
		super(messageSource);
		this.eegSystemsPresetManager = eegSystemsPresetManager;
		initialize();
	}

	@Override
	protected void initialize() {
		setLayout(new BorderLayout());
		setTitledBorder("Select EEG system");
		
		JLabel comboBoxLabel = new JLabel("Current EEG system:");
		add(comboBoxLabel, BorderLayout.WEST);
		add(getPresetComboBox(), BorderLayout.EAST);

	}

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

	protected PresetComboBoxModel getPresetComboBoxModel() {
		if (presetComboBoxModel == null) {
			presetComboBoxModel = new PresetComboBoxModel(null, eegSystemsPresetManager);
			Object firstElement = presetComboBoxModel.getElementAt(0);
			if (firstElement != null)
				presetComboBoxModel.setSelectedItem(firstElement);
		}
		return presetComboBoxModel;
	}

	protected EegSystem getSelectedEegSystem() {
		return (EegSystem) presetComboBoxModel.getSelectedItem();
	}

	public void setMontage(Montage montage) {
		this.montage = montage;
		if (montage != null) {
			//montage.setEegSystem(getSelectedEegSystem());
			if (montage.getEegSystem() != null)
				presetComboBoxModel.setSelectedItem(montage.getEegSystem());
		}
	}

}
