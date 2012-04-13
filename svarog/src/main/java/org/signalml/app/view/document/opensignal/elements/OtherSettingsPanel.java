package org.signalml.app.view.document.opensignal.elements;

import static org.signalml.app.util.i18n.SvarogI18n._;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.util.List;

import javax.swing.AbstractAction;
import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;

import org.signalml.app.action.document.RegisterCodecAction;
import org.signalml.app.config.preset.EegSystemsPresetManager;
import org.signalml.app.config.preset.PresetComboBoxModel;
import org.signalml.app.config.preset.PresetManager;
import org.signalml.app.model.document.opensignal.AbstractOpenSignalDescriptor;
import org.signalml.app.model.document.opensignal.ExperimentDescriptor;
import org.signalml.app.model.document.opensignal.SignalMLDescriptor;
import org.signalml.app.model.document.opensignal.elements.AmplifierChannel;
import org.signalml.app.model.document.opensignal.elements.FileOpenSignalMethod;
import org.signalml.app.model.document.opensignal.elements.FileTypeComboBoxModel;
import org.signalml.app.model.document.opensignal.elements.SignalSource;
import org.signalml.app.model.document.opensignal.elements.TagPresetComboBoxModel;
import org.signalml.app.view.components.AbstractPanel;
import org.signalml.app.view.montage.EegSystemSelectionPanel;
import org.signalml.app.view.workspace.ViewerElementManager;
import org.signalml.domain.montage.system.EegSystem;
import org.signalml.domain.montage.system.EegSystemName;
import org.signalml.domain.signal.raw.RawSignalDescriptor;
import org.signalml.domain.tag.StyledTagSet;

public class OtherSettingsPanel extends AbstractPanel {

	public static String EEG_SYSTEM_PROPERTY = "eegSystemProperty";

	private ViewerElementManager viewerElementManager;
	protected AbstractOpenSignalDescriptor openSignalDescriptor;

	private JButton editGainAndOffsetButton;
	private EditGainAndOffsetDialog editGainAndOffsetDialog;

	private JButton registerSignalMLCodecButton;

	private JLabel tagStylesLabel = new JLabel(_("Tag styles preset"));
	private JLabel fileTypeLabel = new JLabel(_("File type"));
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

	private JComboBox fileTypeComboBox;

	public OtherSettingsPanel(ViewerElementManager viewerElementManager) {
		this.viewerElementManager = viewerElementManager;
		eegSystemsPresetManager = viewerElementManager.getEegSystemsPresetManager();
		createInterface();
	}

	protected void createInterface() {
		setTitledBorder(_("Other settings"));
		setLayout(new BorderLayout());

		add(createComboBoxesPanel(), BorderLayout.CENTER);
		add(createButtonsPanel(), BorderLayout.SOUTH);
	}

	protected JPanel createComboBoxesPanel() {
		JPanel comboBoxesPanel = new JPanel();

		GroupLayout layout = new GroupLayout(comboBoxesPanel);
		comboBoxesPanel.setLayout(layout);
		layout.setAutoCreateContainerGaps(false);
		layout.setAutoCreateGaps(true);

		JLabel eegSystemsLabel = new JLabel(_("EEG system"));

		GroupLayout.SequentialGroup hGroup = layout.createSequentialGroup();

		hGroup.addGroup(
			layout.createParallelGroup()
			.addComponent(tagStylesLabel)
			.addComponent(fileTypeLabel)
			.addComponent(eegSystemsLabel)
		);

		hGroup.addGroup(
			layout.createParallelGroup()
			.addComponent(getTagPresetComboBox())
			.addComponent(getFileTypeComboBox())
			.addComponent(getEegSystemsPresetComboBox())
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
			.addComponent(fileTypeLabel)
			.addComponent(getFileTypeComboBox())
		);

		vGroup.addGroup(
			layout.createParallelGroup(Alignment.BASELINE)
			.addComponent(eegSystemsLabel)
			.addComponent(getEegSystemsPresetComboBox())
		);

		layout.setVerticalGroup(vGroup);

		return comboBoxesPanel;
	}

	protected JPanel createButtonsPanel() {
		JPanel buttonsPanel = new JPanel(new FlowLayout());

		buttonsPanel.add(getEditGainAndOffsetButton());
		buttonsPanel.add(getRegisterSignalMLCodecButton());

		return buttonsPanel;
	}

	public JButton getRegisterSignalMLCodecButton() {
		if (registerSignalMLCodecButton == null) {
			RegisterCodecAction registerCodecAction = new RegisterCodecAction();
			registerCodecAction.setRegisterCodecDialog(viewerElementManager.getRegisterCodecDialog());
			registerCodecAction.setPleaseWaitDialog(viewerElementManager.getPleaseWaitDialog());
			registerCodecAction.initializeAll();

			registerSignalMLCodecButton = new JButton(registerCodecAction);
			registerSignalMLCodecButton.setText(_("Register SignalML codec"));
		}
		return registerSignalMLCodecButton;
	}

	public JComboBox getFileTypeComboBox() {
		if (fileTypeComboBox == null) {
			FileTypeComboBoxModel model = new FileTypeComboBoxModel();
			fileTypeComboBox = new JComboBox(model);
			fileTypeComboBox.setSelectedItem(FileOpenSignalMethod.AUTODETECT);
		}
		return fileTypeComboBox;
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
	 * Returns the edit gain and offset button.
	 *
	 * @return the edit gain and offset button
	 */
	protected JButton getEditGainAndOffsetButton() {

		if (editGainAndOffsetButton == null) {
			editGainAndOffsetButton = new JButton(new AbstractAction() {

				@Override
				public void actionPerformed(ActionEvent arg0) {
					fillSignalParametersGainAndOffset(openSignalDescriptor);
					getEditGainAndOffsetDialog().showDialog(openSignalDescriptor, true);
				}
			});

			editGainAndOffsetButton.setText(_("Edit gain and offset"));
			editGainAndOffsetButton.setEnabled(false);
		}
		return editGainAndOffsetButton;
	}

	/**
	 * Returns the edit gain and offset dialog
	 *
	 * @return the edit gain and offset dialog
	 */
	protected EditGainAndOffsetDialog getEditGainAndOffsetDialog() {

		if (editGainAndOffsetDialog == null) {
			editGainAndOffsetDialog = new EditGainAndOffsetDialog(null, true);
		}
		return editGainAndOffsetDialog;
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
	 *
	 * Sets the EEG system which should be selected in this panel.
	 * @param name
	 *            the EEG system to be selected
	 */
	public void setEegSystem(EegSystem eegSystem) {
		eegSystemsPresetComboBoxModel.setSelectedItem(eegSystem);
	}

	public void fillPanelFromModel(AbstractOpenSignalDescriptor openSignalDescriptor) {
		this.openSignalDescriptor = openSignalDescriptor;
		setEnabledAsNeeded(openSignalDescriptor);
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
		fillSignalParametersGainAndOffset(openSignalDescriptor);
	}

	protected void fillSignalParametersGainAndOffset(AbstractOpenSignalDescriptor openSignalDescriptor) {
		if (openSignalDescriptor instanceof ExperimentDescriptor) {
			ExperimentDescriptor experimentDescriptor = (ExperimentDescriptor) openSignalDescriptor;
			List<AmplifierChannel> channels = experimentDescriptor
											  .getAmplifier().getSelectedChannels();

			float[] gain = new float[channels.size()];
			float[] offset = new float[channels.size()];

			int i = 0;
			for (AmplifierChannel channel : channels) {
				gain[i] = channel.getCalibrationGain();
				offset[i] = channel.getCalibrationOffset();
				i++;
			}
			experimentDescriptor.getSignalParameters().setCalibrationGain(gain);
			experimentDescriptor.getSignalParameters().setCalibrationOffset(offset);
		}
	}

	public void preparePanelForSignalSource(SignalSource selectedSignalSource) {
		boolean isMonitor = selectedSignalSource.isOpenBCI();
		getTagPresetComboBox().setVisible(isMonitor);
		tagStylesLabel.setVisible(isMonitor);

		getRegisterSignalMLCodecButton().setVisible(!isMonitor);

		fileTypeLabel.setVisible(!isMonitor);
		fileTypeComboBox.setVisible(!isMonitor);
	}

	protected void setEnabledAsNeeded(AbstractOpenSignalDescriptor openSignalDescriptor) {

		if (openSignalDescriptor == null) {
			getEditGainAndOffsetButton().setEnabled(false);
			return;
		}

		if (openSignalDescriptor instanceof RawSignalDescriptor) {
			getEditGainAndOffsetButton().setEnabled(true);
		}
		else {
			if (openSignalDescriptor instanceof SignalMLDescriptor)
				getEditGainAndOffsetButton().setEnabled(false);
			else
				getEditGainAndOffsetButton().setEnabled(true);
		}

	}

}
