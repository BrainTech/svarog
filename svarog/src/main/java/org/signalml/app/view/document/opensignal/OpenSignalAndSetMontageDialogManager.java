/* SignalSourceAndMontageGUIManager.java created 2011-03-11
 *
 */

package org.signalml.app.view.document.opensignal;

import static org.signalml.app.util.i18n.SvarogI18n._;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JOptionPane;

import org.signalml.app.model.document.opensignal.ExperimentDescriptor;
import org.signalml.app.view.document.opensignal.elements.AmplifierChannel;
import org.signalml.app.view.document.opensignal.monitor.AbstractMonitorSourcePanel;
import org.signalml.app.view.document.opensignal.monitor.ChooseExperimentPanel;
import org.signalml.app.view.document.opensignal.monitor.OpenBCISignalSourcePanel;
import org.signalml.domain.montage.Montage;
import org.signalml.domain.montage.MontageException;
import org.signalml.domain.montage.system.EegSystem;
import org.signalml.domain.montage.SignalConfigurer;
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
			float newSamplingFrequency = Float.parseFloat(evt.getNewValue().toString());
			samplingFrequencyChangedTo(newSamplingFrequency);
		}
		else if (propertyName.equals(AbstractSignalParametersPanel.CHANNEL_LABELS_PROPERTY)) {
			String[] channelLabels = (String[]) evt.getNewValue();
			channelLabelsChangedTo(channelLabels);
		}
		else if (propertyName.equals(SignalSourcePanel.SIGNAL_SOURCE_SELECTION_CHANGED_PROPERTY)) {
			SignalSource signalSource = ((SignalSource)evt.getNewValue());
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
			EegSystem newEegSystem = (EegSystem) (evt.getNewValue() != null ? evt.getNewValue() : null);
			eegSystemChangedTo(newEegSystem);
		}
		else if (propertyName.equals(ChooseExperimentPanel.EXPERIMENT_SELECTED_PROPERTY)) {
			enableTabsAndOKButtonAsNeeded();

			OpenBCISignalSourcePanel panel = (OpenBCISignalSourcePanel) signalSourcePanel.getCurrentSignalSourcePanel();
			ExperimentDescriptor experiment = (ExperimentDescriptor) evt.getNewValue();

			if (experiment != null) {
				String[] channelLabels = experiment.getAmplifier().getSelectedChannelsLabels();
				numberOfChannelsChangedTo(channelLabels.length);
				channelLabelsChangedTo(channelLabels);
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
		Montage createdMontage = SignalConfigurer.createMontage(newNumberOfChannels);

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
		String dialogText = _("Do you really want to change the sampling frequency? All filters will be deleted.");
		String dialogTitle = _("Warning!");
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
			}
			currentMontage.getMontageGenerator().createMontage(currentMontage);
			openSignalAndSetMontageDialog.fillDialogFromModel(currentMontage);
		} catch (MontageException ex) {
			Logger.getLogger(OpenSignalAndSetMontageDialogManager.class.getName()).log(Level.SEVERE, null, ex);
		}
		catch (SignalMLException ex) {
			Logger.getLogger(OpenSignalAndSetMontageDialogManager.class.getName()).log(Level.SEVERE, null, ex);
		}
	}

	/**
	 * Changes the EEG system in the current montage.
	 * @param newEegSystemName the EEG system name that was selected
	 */
	protected void eegSystemChangedTo(EegSystem newEegSystem) {
		Montage currentMontage = getCurrentMontage();

		if (newEegSystem == null) {
			return;
		}

		currentMontage.setEegSystem(newEegSystem);
		try {
			openSignalAndSetMontageDialog.fillDialogFromModel(currentMontage);
		} catch (SignalMLException ex) {
			Logger.getLogger(OpenSignalAndSetMontageDialogManager.class.getName()).log(Level.SEVERE, null, ex);
		}
	}

}
