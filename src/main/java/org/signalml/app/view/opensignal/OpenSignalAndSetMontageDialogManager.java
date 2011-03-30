/* SignalSourceAndMontageGUIManager.java created 2011-03-11
 *
 */

package org.signalml.app.view.opensignal;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.signalml.domain.montage.Montage;
import org.signalml.domain.montage.MontageException;
import org.signalml.domain.signal.SignalType;
import org.signalml.plugin.export.SignalMLException;

/**
 * This manager controls the flow of information between the montage part
 * of the OpenSignalAndSetMontageDialog and the SignalSource part.
 * For example: if the sampling frequency changes in the SignalSource tab
 * this manager should be notified and change the sampling frequency for the
 * montage tabs (mainly Filters tab).
 *
 * @author Piotr Szachewicz
 */
public class OpenSignalAndSetMontageDialogManager implements PropertyChangeListener {

	/**
	 * The OpenSignalAndSetMontageDialog to which this manager is connected.
	 */
	private OpenSignalAndSetMontageDialog openSignalAndSetMontageDialog;

	/**
	 * The SignalSource panel shown in the Signal Source tab in the
	 * openSignalAndSetMontageDialog.
	 */
	private SignalSourcePanel signalSourcePanel;

	/**
	 * Constructor.
	 * @param openSignalAndSetMontageDialog the dialog to which this manager
	 * should be connected.
	 */
	public OpenSignalAndSetMontageDialogManager(OpenSignalAndSetMontageDialog openSignalAndSetMontageDialog) {
		this.openSignalAndSetMontageDialog = openSignalAndSetMontageDialog;
		this.signalSourcePanel = openSignalAndSetMontageDialog.getSignalSourcePanel();

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
			String[] channelLabels = (String[]) evt.getNewValue();
			channelLabelsChangedTo(channelLabels);
		}
		else if (propertyName.equals(SignalSourceSelectionPanel.SIGNAL_SOURCE_SELECTION_CHANGED_PROPERTY)) {
			int signalSourcePanelChannelCount = signalSourcePanel.getCurrentSignalSourcePanel().getChannelCount();

			numberOfChannelsChangedTo(signalSourcePanelChannelCount);
			samplingFrequencyChangedTo(signalSourcePanel.getCurrentSignalSourcePanel().getSamplingFrequency());
			enableTabsAndOKButtonAsNeeded();
		}
		else if (propertyName.equals(AbstractMonitorSourcePanel.OPENBCI_CONNECTED_PROPERTY)) {
			enableTabsAndOKButtonAsNeeded();
		}
	}

	/**
	 * Enables or disables the OK button depending on the status of the
	 * signal source panel.
	 */
	public void enableTabsAndOKButtonAsNeeded() {
		AbstractSignalSourcePanel currentSignalSourcePanel = signalSourcePanel.getCurrentSignalSourcePanel();
		boolean metadataFilled = currentSignalSourcePanel.isMetadataFilled();

		openSignalAndSetMontageDialog.setMontageTabsEnabled(metadataFilled);
		openSignalAndSetMontageDialog.setOKButtoneEnabled(metadataFilled);
	}

	/**
	 * Changes the number of channels in the montage tabs to the given
	 * number. (generates a new montage for that number of channels).
	 * @param newNumberOfChannels the current number of channels
	 */
	protected void numberOfChannelsChangedTo(int newNumberOfChannels) {
		Montage createdMontage = SignalType.EEG_10_20.getConfigurer().createMontage(newNumberOfChannels);

		try {
			openSignalAndSetMontageDialog.fillDialogFromModel(createdMontage);
		} catch (SignalMLException ex) {
			Logger.getLogger(OpenSignalAndSetMontageDialogManager.class.getName()).log(Level.SEVERE, null, ex);
		}
	}

	/**
	 * Changes the sampling frequency in the montage tabs.
	 * @param newSamplingFrequency the current sampling frequency
	 */
	protected void samplingFrequencyChangedTo(float newSamplingFrequency) {
		openSignalAndSetMontageDialog.setSamplingFrequency(newSamplingFrequency);
	}

	/**
	 * Changes the channel labels in the montage tabs.
	 * @param channelLabels the new values of the channel labels
	 */
	protected void channelLabelsChangedTo(String[] channelLabels) {
		Montage currentMontage = openSignalAndSetMontageDialog.getCurrentMontage();

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
