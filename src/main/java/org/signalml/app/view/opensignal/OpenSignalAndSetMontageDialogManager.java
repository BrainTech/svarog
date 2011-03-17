/* SignalSourceAndMontageGUIManager.java created 2011-03-11
 *
 */

package org.signalml.app.view.opensignal;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.nio.channels.Channel;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.signalml.domain.montage.Montage;
import org.signalml.domain.montage.MontageException;
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

		if (propertyName.equals(AbstractSignalParametersPanel.NUMBER_OF_CHANNELS_PROPERTY)) {
			int numberOfChannels = Integer.parseInt(evt.getNewValue().toString());
			numberOfChannelsChangedTo(numberOfChannels);
		}
		else if (propertyName.equals(AbstractSignalParametersPanel.SAMPLING_FREQUENCY_PROPERTY)){
			float samplingFrequency = Float.parseFloat(evt.getNewValue().toString());
			samplingFrequencyChangedTo(samplingFrequency);
		}
		else if (propertyName.equals(AbstractSignalParametersPanel.CHANNEL_LABELS_PROPERTY)) {

			Montage currentMontage = openSignalAndSetMontageDialog.getCurrentMontage();
			String[] channelLabels = (String[]) evt.getNewValue();

			try {
				for (int i = 0; i < channelLabels.length; i++) {
					currentMontage.setSourceChannelLabelAt(i, channelLabels[i]);
					currentMontage.setMontageChannelLabelAt(i, channelLabels[i]);
				}
				openSignalAndSetMontageDialog.fillDialogFromModel(currentMontage);
			} catch (MontageException ex) {
				Logger.getLogger(OpenSignalAndSetMontageDialogManager.class.getName()).log(Level.SEVERE, null, ex);
			}
			catch (SignalMLException ex) {
				Logger.getLogger(OpenSignalAndSetMontageDialogManager.class.getName()).log(Level.SEVERE, null, ex);
			}


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
		//Montage currentMontage = openSignalAndSetMontageDialog.getCurrentMontage();
		
	}

}
