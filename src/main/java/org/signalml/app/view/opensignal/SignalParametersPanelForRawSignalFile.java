/* SignalParametersPanelForRawSignalFile.java created 2011-03-11
 *
 */

package org.signalml.app.view.opensignal;

import javax.swing.JButton;
import javax.swing.JPanel;
import org.signalml.app.config.ApplicationConfiguration;
import org.signalml.domain.signal.raw.RawSignalByteOrder;
import org.signalml.domain.signal.raw.RawSignalDescriptor;
import org.signalml.domain.signal.raw.RawSignalSampleType;
import org.signalml.plugin.export.SignalMLException;
import org.signalml.app.view.element.FileChooserPanel;
import org.springframework.context.support.MessageSourceAccessor;

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

	/**
	 * Constructor.
	 * @param messageSource message source capable of resolving localized
	 * messages
	 * @param applicationConfiguration the current application configuration
	 */
	public SignalParametersPanelForRawSignalFile(MessageSourceAccessor messageSource, ApplicationConfiguration applicationConfiguration) {
		super(messageSource, applicationConfiguration);

		setEnabledAll(true);
		getSamplingFrequencyComboBox().setEditable(true);
	}

	@Override
	protected JPanel createButtonPanel() {
		JPanel buttonPanel = super.createButtonPanel();
		buttonPanel.add(new JButton(getReadManifestAction()));
		return buttonPanel;
	}

	/**
	 * Returns the action called when the user chooses an option to
	 * read signal parameters from an XML manifest file.
	 * @return the action performed to read an XML manifest
	 */
	protected ReadXMLManifestAction getReadManifestAction() {
		if (readManifestAction == null)
			readManifestAction = new ReadXMLManifestAction(messageSource, this);
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
	public void setSignalFileChooserPanel(FileChooserPanel fileChooserPanel) {
		getReadManifestAction().setSignalFileChooserPanel(fileChooserPanel);
	}

}
