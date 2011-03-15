/* NewClass.java created 2011-03-14
 *
 */

package org.signalml.app.view.opensignal;

import org.signalml.app.config.ApplicationConfiguration;
import org.signalml.app.model.OpenMonitorDescriptor;
import org.springframework.context.support.MessageSourceAccessor;

/**
 *
 * @author Piotr Szachewicz
 */
public class SignalParametersPanelForOpenMonitor extends SignalParametersPanel {

	public SignalParametersPanelForOpenMonitor(MessageSourceAccessor messageSource, ApplicationConfiguration applicationConfiguration) {
		super(messageSource, applicationConfiguration);
		setEnabledAll(false);
		getSamplingFrequencyComboBox().setEditable(true);
	}

	public void fillPanelFromModel(OpenMonitorDescriptor descriptor) {
		getSamplingFrequencyComboBox().setSelectedItem(descriptor.getSamplingFrequency());
		getChannelCountSpinner().setValue(descriptor.getChannelCount());
		getByteOrderComboBox().setSelectedItem(descriptor.getByteOrder());
		getSampleTypeComboBox().setSelectedItem(descriptor.getSampleType());
		getPageSizeSpinner().setValue(descriptor.getPageSize());
		//getBlocksPerPageSpinner().setValue(4);
	}

	public void fillModelFromPanel(OpenMonitorDescriptor descriptor) {
		
	}
}
