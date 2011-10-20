/* SignalSourceAndMontageGUIManager.java created 2011-03-11
 *
 */

package org.signalml.app.view.opensignal;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JOptionPane;
import org.signalml.app.config.preset.EegSystemsPresetManager;
import org.signalml.domain.montage.Montage;
import org.signalml.domain.montage.MontageException;
import org.signalml.domain.montage.system.EegSystem;
import org.signalml.domain.signal.SignalType;
import org.signalml.plugin.export.SignalMLException;
import org.springframework.context.support.MessageSourceAccessor;

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
	 * Message source to resolve localized messages.
	 */
	private MessageSourceAccessor messageSource;

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
	 * This boolean variables tells whether the next sampling frequency
	 * property change should be ignored (it is needed to distinguish
	 * the changes made by this manager from the changes made by the other
	 * parts of the program).
	 */
	private boolean ignoreNextSamplingFrequencyChange = false;

	/**
	 * Constructor.
	 * @param openSignalAndSetMontageDialog the dialog to which this manager
	 * should be connected.
	 */
	public OpenSignalAndSetMontageDialogManager(OpenSignalAndSetMontageDialog openSignalAndSetMontageDialog, MessageSourceAccessor messageSource) {

		this.openSignalAndSetMontageDialog = openSignalAndSetMontageDialog;
		this.signalSourcePanel = openSignalAndSetMontageDialog.getSignalSourcePanel();
		this.messageSource = messageSource;

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
			float newSamplingFrequency = Float.parseFloat(evt.getNewValue().toString());
			samplingFrequencyChangedTo(newSamplingFrequency);
		}
		else if (propertyName.equals(AbstractSignalParametersPanel.CHANNEL_LABELS_PROPERTY)) {
			String[] channelLabels = (String[]) evt.getNewValue();
			channelLabelsChangedTo(channelLabels);
		}
		else if (propertyName.equals(SignalSourceSelectionPanel.SIGNAL_SOURCE_SELECTION_CHANGED_PROPERTY)) {
			int signalSourcePanelChannelCount = signalSourcePanel.getCurrentSignalSourcePanel().getChannelCount();

			numberOfChannelsChangedTo(signalSourcePanelChannelCount);
			float samplingFrequency = signalSourcePanel.getCurrentSignalSourcePanel().getSamplingFrequency();
			samplingFrequencyChangedTo(samplingFrequency);
			enableTabsAndOKButtonAsNeeded();
		}
		else if (propertyName.equals(AbstractMonitorSourcePanel.OPENBCI_CONNECTED_PROPERTY)) {
			enableTabsAndOKButtonAsNeeded();
		}
		else if (propertyName.equals(AbstractSignalParametersPanel.EEG_SYSTEM_PROPERTY)) {

			String newEegSystemName = evt.getNewValue().toString();

			EegSystemsPresetManager eegSystemsPresetManager = openSignalAndSetMontageDialog.getEegSystemsPresetManager();

			EegSystem eegSystem = (EegSystem) eegSystemsPresetManager.getPresetByName(newEegSystemName);
			Montage currentMontage = getCurrentMontage();
			currentMontage.setEegSystem(eegSystem);
			try {
				openSignalAndSetMontageDialog.fillDialogFromModel(currentMontage);
				//getCurrentMontage().setEe
				/*currentMontage.setSourceChannelLabelAt(i, channelLabels[i]);
				currentMontage.setMontageChannelLabelAt(i, channelLabels[i]);
				}
				openSignalAndSetMontageDialog.fillDialogFromModel(currentMontage);*/
			} catch (SignalMLException ex) {
				Logger.getLogger(OpenSignalAndSetMontageDialogManager.class.getName()).log(Level.SEVERE, null, ex);
			}

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

		if (ignoreNextSamplingFrequencyChange) {
			ignoreNextSamplingFrequencyChange = false;
			return;
		}

		Montage currentMontage = getCurrentMontage();
		int numberOfFilters = (currentMontage != null) ? currentMontage.getSampleFilterCount() : 0;

		if (numberOfFilters > 0) {
			showSamplingFrequencyChangedFiltersWillBeDeletedDialog(newSamplingFrequency);
		}
		else {
			openSignalAndSetMontageDialog.setSamplingFrequency(newSamplingFrequency);
		}

	}

	/**
	 * Returns the current montage set in the montage tabs.
	 * @return the montage set in the montage tabs
	 */
	private Montage getCurrentMontage() {
		return openSignalAndSetMontageDialog.getCurrentMontage();
	}

	/**
	 * Shows a dialog that warns that there are filters added and changing
	 * the sampling frequency will result in deleting all the filters.
	 * Performs appropriate actions depending on the user response.
	 * @param newSamplingFrequency the value to which the sampling frequency
	 * has been changed
	 */
	private void showSamplingFrequencyChangedFiltersWillBeDeletedDialog(float newSamplingFrequency) {
		String dialogText = messageSource.getMessage("opensignal.warning.samplingFrequencyChangedFiltersWillBeDeleted");
		String dialogTitle = messageSource.getMessage("warning");
		int response = JOptionPane.showConfirmDialog(null, dialogText, dialogTitle, JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);

		switch (response) {
			case JOptionPane.YES_OPTION:
				openSignalAndSetMontageDialog.setSamplingFrequency(newSamplingFrequency);
				getCurrentMontage().clearFilters();
				break;
			case JOptionPane.NO_OPTION:
				float oldSamplingFrequency = openSignalAndSetMontageDialog.getSamplingFrequency();
				signalSourcePanel.getCurrentSignalSourcePanel().setSamplingFrequency(oldSamplingFrequency);
				ignoreNextSamplingFrequencyChange = true;
				break;
		}
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
