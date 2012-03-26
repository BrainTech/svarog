package org.signalml.app.view.document.opensignal.elements;

import static org.signalml.app.util.i18n.SvarogI18n._;

import java.awt.Dimension;
import java.awt.event.ActionEvent;

import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;

import org.signalml.app.config.preset.EegSystemsPresetManager;
import org.signalml.app.config.preset.PresetComboBoxModel;
import org.signalml.app.config.preset.PresetManager;
import org.signalml.app.model.document.opensignal.AbstractOpenSignalDescriptor;
import org.signalml.app.model.document.opensignal.ExperimentDescriptor;
import org.signalml.app.model.document.opensignal.elements.SignalSource;
import org.signalml.app.model.document.opensignal.elements.TagPresetComboBoxModel;
import org.signalml.app.view.components.AbstractPanel;
import org.signalml.app.view.montage.EegSystemSelectionPanel;
import org.signalml.app.view.workspace.ViewerElementManager;
import org.signalml.domain.montage.system.EegSystem;
import org.signalml.domain.montage.system.EegSystemName;
import org.signalml.domain.signal.raw.RawSignalDescriptor;
import org.signalml.domain.tag.StyledTagSet;
import org.signalml.plugin.export.view.AbstractSignalMLAction;

public class OtherSettingsPanel extends AbstractPanel {

	public static String EEG_SYSTEM_PROPERTY = "eegSystemProperty";
	
	private ViewerElementManager viewerElementManager;

	private JButton manageCodecsButton;
	private ManageSignalMLCodecsDialog manageSignalMLCodecsDialog;
	
	private JLabel tagStylesLabel = new JLabel(_("Tag styles preset"));
	/**
	 * {@link JComboBox} that displays the list of available presets.
	 */
	private JComboBox tagPresetComboBox;
	/**
	 * The {@link PresetManager} that manages available {@link EegSystem EEG
	 * Systems}.
	 */
	private EegSystemsPresetManager eegSystemsPresetManager;
	/**
	 * The {@link JComboBox} for EEG system selection.
	 */
	private JComboBox presetComboBox;
	/**
	 * The model for the {@link EegSystemSelectionPanel#presetComboBox}.
	 */
	private PresetComboBoxModel eegSystemsPresetComboBoxModel;

	public OtherSettingsPanel(ViewerElementManager viewerElementManager) {
		this.viewerElementManager = viewerElementManager;
		eegSystemsPresetManager = viewerElementManager.getEegSystemsPresetManager();
		createInterface();
	}

	protected void createInterface() {
		setTitledBorder(_("Other settings"));
		
		GroupLayout layout = new GroupLayout(this);
		setLayout(layout);
		layout.setAutoCreateContainerGaps(false);
		layout.setAutoCreateGaps(true);

		JLabel eegSystemsLabel = new JLabel(_("EEG system"));
		JLabel signalMLCodecsLabel = new JLabel(_("SignalML codecs"));

		GroupLayout.SequentialGroup hGroup = layout.createSequentialGroup();

		hGroup.addGroup(
		        layout.createParallelGroup()
		        .addComponent(tagStylesLabel)
		        .addComponent(eegSystemsLabel)
		        .addComponent(signalMLCodecsLabel)
		);

		hGroup.addGroup(
		        layout.createParallelGroup()
		        .addComponent(getTagPresetComboBox())
		        .addComponent(getEegSystemsPresetComboBox())
		        .addComponent(getManageCodecsButton())
		);

		layout.setHorizontalGroup(hGroup);

		GroupLayout.SequentialGroup vGroup = layout.createSequentialGroup();

		vGroup.addGroup(
				layout.createParallelGroup(Alignment.BASELINE)
				.addComponent(tagStylesLabel)
				.addComponent(getTagPresetComboBox())
			);

		vGroup.addGroup(
				layout.createParallelGroup(Alignment.BASELINE)
				.addComponent(eegSystemsLabel)
				.addComponent(getEegSystemsPresetComboBox())
			);
		
		vGroup.addGroup(
				layout.createParallelGroup(Alignment.BASELINE)
				.addComponent(signalMLCodecsLabel)
				.addComponent(getManageCodecsButton())
			);

		layout.setVerticalGroup(vGroup);
		
	}

	public JButton getManageCodecsButton() {
		if (manageCodecsButton == null) {
			manageCodecsButton = new JButton(new AbstractSignalMLAction() {
				@Override
				public void actionPerformed(ActionEvent e) {
					getManageSignalMLCodecsDialog().showDialog(
							viewerElementManager, true);
				}
			});
			manageCodecsButton.setText(_("Manage SignalML codecs"));
		}
		return manageCodecsButton;
	}

	protected ManageSignalMLCodecsDialog getManageSignalMLCodecsDialog() {
		if (manageSignalMLCodecsDialog == null) {
			manageSignalMLCodecsDialog = new ManageSignalMLCodecsDialog(
					viewerElementManager);
		}
		return manageSignalMLCodecsDialog;
	}

	/**
	 * Returns the {@link JComboBox} that lists the available tag style presets.
	 * 
	 * @return the ComboBox with tag style presets.
	 */
	public JComboBox getTagPresetComboBox() {
		if (tagPresetComboBox == null) {
			TagPresetComboBoxModel model = new TagPresetComboBoxModel(
					viewerElementManager.getStyledTagSetPresetManager());
			tagPresetComboBox = new JComboBox(model);
		}
		return tagPresetComboBox;
	}

	/**
	 * Returns (and if necessary - creates) the combo box for EEG system
	 * selection.
	 * 
	 * @return the combo box for EEG system selection
	 */
	protected JComboBox getEegSystemsPresetComboBox() {
		if (presetComboBox == null) {
			presetComboBox = new JComboBox(getPresetComboBoxModel());
			presetComboBox.setPreferredSize(new Dimension(300, 20));
		}
		return presetComboBox;
	}

	/**
	 * Returns (and if necessary - creates) a ComboBoxModel for EEG system
	 * selection.
	 * 
	 * @return the ComboBoxModel for EEG system selection
	 */
	protected PresetComboBoxModel getPresetComboBoxModel() {
		if (eegSystemsPresetComboBoxModel == null) {
			eegSystemsPresetComboBoxModel = new PresetComboBoxModel(null,eegSystemsPresetManager);
			Object firstElement = eegSystemsPresetComboBoxModel.getElementAt(0);
			if (firstElement != null) {
				eegSystemsPresetComboBoxModel.setSelectedItem(firstElement);
			}
		}
		return eegSystemsPresetComboBoxModel;
	}

	/**
	 * Returns the EEG system selected using this panel.
	 * 
	 * @return the selected EEG system
	 */
	public EegSystem getSelectedEegSystem() {
		return (EegSystem) eegSystemsPresetComboBoxModel.getSelectedItem();
	}

	/**
	 * Sets the EEG system which should be selected in this panel.
	 * 
	 * @param name
	 *            the name of the EEG system to be selected
	 */
	public void setEegSystemByName(EegSystemName name) {
		EegSystem eegSystem = (EegSystem) eegSystemsPresetManager
				.getPresetByName(name.getFullName());

		if (eegSystem != null)
			setEegSystem(eegSystem);
		else
			setEegSystem(getSelectedEegSystem());
	}

	/**
	 * Sets the EEG system which should be selected in this panel.
	 * 
	 * @param name
	 *            the EEG system to be selected
	 */
	public void setEegSystem(EegSystem eegSystem) {
		eegSystemsPresetComboBoxModel.setSelectedItem(eegSystem);
	}

	public void fillPanelFromModel(AbstractOpenSignalDescriptor openSignalDescriptor) {
		if (openSignalDescriptor instanceof RawSignalDescriptor) {
			RawSignalDescriptor rawSignalDescriptor = (RawSignalDescriptor) openSignalDescriptor;
			EegSystemName eegSystemName = rawSignalDescriptor.getEegSystemName();

			if (eegSystemName != null)
				setEegSystemByName(eegSystemName);
		}
	}

	public void fillModelFromPanel(AbstractOpenSignalDescriptor descriptor) {
		if (descriptor instanceof ExperimentDescriptor) {
			ExperimentDescriptor experimentDescriptor = (ExperimentDescriptor) descriptor;
			StyledTagSet selectedStylesPreset = (StyledTagSet) getTagPresetComboBox().getSelectedItem();
			experimentDescriptor.setTagStyles(selectedStylesPreset);
		}
		descriptor.setEegSystem(getSelectedEegSystem());
	}

	public void preparePanelForSignalSource(SignalSource selectedSignalSource) {
		boolean isMonitor = selectedSignalSource.isOpenBCI();
		getTagPresetComboBox().setVisible(isMonitor);
		tagStylesLabel.setVisible(isMonitor);
	}

}
