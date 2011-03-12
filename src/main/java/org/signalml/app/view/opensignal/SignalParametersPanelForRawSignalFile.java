/* SignalParametersPanelForRawSignalFile.java created 2011-03-11
 *
 */

package org.signalml.app.view.opensignal;

import javax.swing.JButton;
import javax.swing.JPanel;
import org.signalml.app.config.ApplicationConfiguration;
import org.signalml.domain.signal.raw.RawSignalDescriptor;
import org.springframework.context.support.MessageSourceAccessor;

/**
 *
 * @author Piotr Szachewicz
 */
public class SignalParametersPanelForRawSignalFile extends SignalParametersPanel {

	public SignalParametersPanelForRawSignalFile(MessageSourceAccessor messageSource, ApplicationConfiguration applicationConfiguration) {
		super(messageSource, applicationConfiguration);
		setEnabledAll(true);
		getSamplingFrequencyComboBox().setEditable(true);
	}

	@Override
	protected JPanel createButtonPanel() {
		JPanel buttonPanel = super.createButtonPanel();
		buttonPanel.add(new JButton(new ReadXMLManifestAction(messageSource, this)));
		return buttonPanel;
	}

	public void fillPanelFromModel(RawSignalDescriptor descriptor) {
		getSamplingFrequencyComboBox().setSelectedItem(descriptor.getSamplingFrequency());
		getChannelCountSpinner().setValue(descriptor.getChannelCount());
		getByteOrderComboBox().setSelectedItem(descriptor.getByteOrder());
		getSampleTypeComboBox().setSelectedItem(descriptor.getSampleType());
		getPageSizeSpinner().setValue(descriptor.getPageSize());
		getBlocksPerPageSpinner().setValue(descriptor.getBlocksPerPage());
	}

}
