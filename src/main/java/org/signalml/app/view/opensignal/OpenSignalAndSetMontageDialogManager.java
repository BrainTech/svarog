/* SignalSourceAndMontageGUIManager.java created 2011-03-11
 *
 */

package org.signalml.app.view.opensignal;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.signalml.domain.montage.Montage;
import org.signalml.domain.signal.SignalType;
import org.signalml.plugin.export.SignalMLException;

/**
 *
 * @author Piotr Szachewicz
 */
public class OpenSignalAndSetMontageDialogManager implements PropertyChangeListener {

	private OpenSignalAndSetMontageDialog openSignalAndSetMontageDialog;
	private SignalSourcePanel signalSourcePanel;

	public OpenSignalAndSetMontageDialogManager(OpenSignalAndSetMontageDialog openSignalAndSetMontageDialog, SignalSourcePanel signalSourcePanel) {
		this.openSignalAndSetMontageDialog = openSignalAndSetMontageDialog;
		this.signalSourcePanel = signalSourcePanel;

		signalSourcePanel.addPropertyChangeListener(this);
	}

	@Override
	public void propertyChange(PropertyChangeEvent evt) {
		String propertyName = evt.getPropertyName();

		if (propertyName.equals(SignalParametersPanel.NUMBER_OF_CHANNELS_CHANGED_PROPERTY)) {
			int numberOfChannels = Integer.parseInt(evt.getNewValue().toString());
			numberOfChannelsChangedTo(numberOfChannels);
		}
		else if (propertyName.equals(SignalParametersPanel.SAMPLING_FREQUENCY_PROPERTY)){
			float samplingFrequency = Float.parseFloat(evt.getNewValue().toString());
			samplingFrequencyChangedTo(samplingFrequency);
		}
	}

	protected void numberOfChannelsChangedTo(int newNumberOfChannels) {
		Montage createdMontage = SignalType.EEG_10_20.getConfigurer().createMontage(newNumberOfChannels);
		try {
			openSignalAndSetMontageDialog.fillDialogFromModel(createdMontage);
		} catch (SignalMLException ex) {
			Logger.getLogger(OpenSignalAndSetMontageDialogManager.class.getName()).log(Level.SEVERE, null, ex);
		}
		System.out.println("Number of channels = " + newNumberOfChannels);
	}

	protected void samplingFrequencyChangedTo(float newSamplingFrequency) {
		openSignalAndSetMontageDialog.setSamplingFrequency(newSamplingFrequency);
	}

}
