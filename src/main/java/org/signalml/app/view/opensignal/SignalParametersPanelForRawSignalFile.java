/* SignalParametersPanelForRawSignalFile.java created 2011-03-11
 *
 */

package org.signalml.app.view.opensignal;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
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
 *
 * @author Piotr Szachewicz
 */
public class SignalParametersPanelForRawSignalFile extends AbstractSignalParametersPanel {

	private ReadXMLManifestAction readManifestAction;

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

	protected ReadXMLManifestAction getReadManifestAction() {
		if (readManifestAction == null)
			readManifestAction = new ReadXMLManifestAction(messageSource, this);
		return readManifestAction;
	}

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

	public void setSignalFileChooserPanel(FileChooserPanel fileChooserPanel) {
		getReadManifestAction().setSignalFileChooserPanel(fileChooserPanel);
	}

}
