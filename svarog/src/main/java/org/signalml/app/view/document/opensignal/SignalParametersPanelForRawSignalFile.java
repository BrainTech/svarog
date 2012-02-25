/* SignalParametersPanelForRawSignalFile.java created 2011-03-11
 *
 */

package org.signalml.app.view.document.opensignal;

import javax.swing.JButton;
import javax.swing.JPanel;
import java.awt.GridBagConstraints;

import org.signalml.app.view.components.EmbeddedFileChooser;
import org.signalml.app.config.ApplicationConfiguration;
import org.signalml.domain.montage.system.EegSystemName;
import org.signalml.domain.signal.raw.RawSignalByteOrder;
import org.signalml.domain.signal.raw.RawSignalDescriptor;
import org.signalml.domain.signal.raw.RawSignalSampleType;
import org.signalml.plugin.export.SignalMLException;

import org.springframework.validation.Errors;

/**
 * Signal parameters panel adapted to the needs of the open file signal source panel.
 *
 * @author Piotr Szachewicz
 */
public class SignalParametersPanelForRawSignalFile extends AbstractSignalParametersPanel {

	/**
	 * The action called when the user chooses an option to read signal parameters
	 * from an XML file.
	 */
	private ReadXMLManifestAction readManifestAction;

	public void setApplicationConfiguration(ApplicationConfiguration applicationConfig){	
		readManifestAction.setApplicationConfiguration(applicationConfig);
	}

	/**
	 * Constructor.
	 * @param applicationConfiguration the current application configuration
	 */
	public SignalParametersPanelForRawSignalFile() {
		super();

		setEnabledAll(true);
		getSamplingFrequencyComboBox().setEditable(true);
	}


	@Override
	protected int createFieldsPanelElements(JPanel fieldsPanel, GridBagConstraints constraints, int startingRow) {
		int row = startingRow;
                fillConstraints(constraints, 0, row, 0, 0, 1);
                fieldsPanel.add(new JButton(getReadManifestAction()), constraints);
		row++;
		return super.createFieldsPanelElements(fieldsPanel, constraints, row);
	}

	/**
	 * Returns the action called when the user chooses an option to
	 * read signal parameters from an XML manifest file.
	 * @return the action performed to read an XML manifest
	 */
	protected ReadXMLManifestAction getReadManifestAction() {
		if (readManifestAction == null)
			readManifestAction = new ReadXMLManifestAction(this);
		return readManifestAction;
	}

	/**
	 * Fills this panel with the data contained in the descriptor.
	 * @param descriptor the descriptor containing data
	 */
	public void fillPanelFromModel(RawSignalDescriptor descriptor) {
		getSamplingFrequencyComboBox().setSelectedItem(descriptor.getSamplingFrequency());
		getChannelCountSpinner().setValue(descriptor.getChannelCount());
		getByteOrderComboBox().setSelectedItem(descriptor.getByteOrder());
		getSampleTypeComboBox().setSelectedItem(descriptor.getSampleType());
		getPageSizeSpinner().setValue(descriptor.getPageSize());
		getBlocksPerPageSpinner().setValue(descriptor.getBlocksPerPage());

		String[] channelLabels = descriptor.getChannelLabels();
		if (channelLabels != null)
			firePropertyChange(AbstractSignalParametersPanel.CHANNEL_LABELS_PROPERTY, null, channelLabels);

                getEditGainAndOffsetDialog().fillDialogFromModel(descriptor);
                currentModel = descriptor;
	}

	/**
	 * Fills the descriptor with the data contained in this panel.
	 * @param descriptor the descriptor to be filled
	 */
	public void fillModelFromPanel(RawSignalDescriptor descriptor) {
		descriptor.setSamplingFrequency(getSamplingFrequency());
		descriptor.setChannelCount(getChannelCountSpinner().getValue());
		descriptor.setByteOrder((RawSignalByteOrder) getByteOrderComboBox().getSelectedItem());
		descriptor.setSampleType((RawSignalSampleType) getSampleTypeComboBox().getSelectedItem());
		descriptor.setPageSize(getPageSizeSpinner().getValue());
		descriptor.setBlocksPerPage(getBlocksPerPageSpinner().getValue());
		descriptor.setSourceSignalType(RawSignalDescriptor.SourceSignalType.RAW);

                getEditGainAndOffsetDialog().fillModelFromDialog(descriptor);
	}

        @Override
        protected void fillCurrentModelFromPanel() throws SignalMLException {
                fillModelFromPanel((RawSignalDescriptor) currentModel);
        }

	/**
	 * Sets the fileChooser responsible for choosing a signal file.
	 * Used for opening the same directory in both signal file fileChooser
	 * and XML manifest fileChooser.
	 * @param fileChooserPanel the file chooser for choosing a signal file
	 */
	public void setSignalFileChooser(EmbeddedFileChooser signalFileChooser) {
		getReadManifestAction().setSignalFileChooser(signalFileChooser);
	}

	@Override
	protected void fireNumberOfChannelsChanged(int numberOfChannels) {
		RawSignalDescriptor rawSignalDescriptor = new RawSignalDescriptor();
		rawSignalDescriptor.setChannelCount(numberOfChannels);

		float[] gain = new float[numberOfChannels];
		float[] offset = new float[numberOfChannels];
		String[] labels = new String[numberOfChannels];
		for (int i = 0; i < numberOfChannels; i++) {
			gain[i] = 1.0F;
			offset[i] = 0.0F;
			labels[i] = "";
		}

		rawSignalDescriptor.setCalibrationGain(gain);
		rawSignalDescriptor.setCalibrationOffset(offset);
		rawSignalDescriptor.setChannelLabels(labels);

		getEditGainAndOffsetDialog().fillDialogFromModel(rawSignalDescriptor);
		super.fireNumberOfChannelsChanged(numberOfChannels);
	}

	/**
	 * Fires an event telling all listeners that the EEG system has changed.
	 * It is used when reading an XML signal descriptor from file to change
	 * the currently selected EEG system..
	 * @param newEegSystemName the name of the EEG system to be selected
	 */
	public void fireEegSystemChanged(EegSystemName newEegSystemName) {
		firePropertyChange(AbstractSignalParametersPanel.EEG_SYSTEM_PROPERTY, null, newEegSystemName);
	}

}
