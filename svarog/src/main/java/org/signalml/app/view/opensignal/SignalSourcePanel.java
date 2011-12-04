/* SignalSourcePanel.java created 2011-03-06
 *
 */
package org.signalml.app.view.opensignal;

import java.awt.CardLayout;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;
import javax.swing.ComboBoxModel;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JPanel;
import org.signalml.app.config.preset.StyledTagSetPresetManager;
import org.signalml.app.document.AbstractSignal;
import org.signalml.app.document.ManagedDocumentType;
import org.signalml.app.model.AmplifierConnectionDescriptor;
import org.signalml.app.model.OpenDocumentDescriptor;
import org.signalml.app.model.OpenFileSignalDescriptor;
import org.signalml.app.model.OpenMonitorDescriptor;
import org.signalml.app.model.OpenSignalDescriptor;
import org.signalml.app.view.ViewerElementManager;
import org.signalml.domain.montage.system.EegSystem;
import org.signalml.plugin.export.SignalMLException;

import org.springframework.validation.Errors;

/**
 * This panel is composed of an signal source combo box which can be used to
 * select an appropriate signal source. If the signal source is choosen this
 * panel shows the available parameters and option for the chosen signal source.
 *
 * @author Piotr Szachewicz
 */
public class SignalSourcePanel extends JPanel implements PropertyChangeListener {

	/**
	 * The ViewerElementManager to be used by this panel.
	 */
	private ViewerElementManager viewerElementManager;

	/**
	 * The model of the signal source combo box. It holds the currently
	 * selected signal source option and changing it results in changing
	 * an option selected by the signal source combo box.
	 */
	private ComboBoxModel signalSourceSelectionComboBoxModel;

	/**
	 * A panel containing options for a file signal source.
	 */
	private FileSignalSourcePanel fileSignalSourcePanel;

	/**
	 * A panel containing options for receiving signal from a running openBCI
	 * system.
	 */
	private OpenBCISignalSourcePanel openBCISignalSourcePanel;

	/**
	 * A panel for setting options for an amplifier signal source.
	 */
	private AmplifierSignalSourcePanel amplifierSignalSourcePanel;

	/**
	 * Constructor.
	 * @param viewerElementManager ViewerElementManager to be used by this
	 * panel
	 */
	public SignalSourcePanel(ViewerElementManager viewerElementManager) {
		this.viewerElementManager = viewerElementManager;
		createInterface();
	}

	/**
	 * Creates the GUI components for this panel.
	 */
	private void createInterface() {
		CardLayout cardLayout = new CardLayout();
		this.setLayout(cardLayout);

		fileSignalSourcePanel = new FileSignalSourcePanel(viewerElementManager);
		openBCISignalSourcePanel = new OpenBCISignalSourcePanel(viewerElementManager);
		amplifierSignalSourcePanel = new AmplifierSignalSourcePanel(viewerElementManager);

		fileSignalSourcePanel.setSignalSourceSelectionComboBoxModel(getSignalSourceSelectionComboBoxModel());
		openBCISignalSourcePanel.setSignalSourceSelectionComboBoxModel(getSignalSourceSelectionComboBoxModel());
		amplifierSignalSourcePanel.setSignalSourceSelectionComboBoxModel(getSignalSourceSelectionComboBoxModel());

		fileSignalSourcePanel.addPropertyChangeListener(this);
		openBCISignalSourcePanel.addPropertyChangeListener(this);
		amplifierSignalSourcePanel.addPropertyChangeListener(this);

		this.add(fileSignalSourcePanel, SignalSource.FILE.toString());
		this.add(openBCISignalSourcePanel, SignalSource.OPENBCI.toString());
		this.add(amplifierSignalSourcePanel, SignalSource.AMPLIFIER.toString());
	}

	/**
	 * Returns the model of the combo box for signal source selection.
	 * @return the model of the combo box for signal source selection
	 */
	protected ComboBoxModel getSignalSourceSelectionComboBoxModel() {
		if (signalSourceSelectionComboBoxModel == null) {
			SignalSource[] signalSources = new SignalSource[3];
			signalSources[0] = SignalSource.FILE;
			signalSources[1] = SignalSource.OPENBCI;
			signalSources[2] = SignalSource.AMPLIFIER;
			signalSourceSelectionComboBoxModel = new DefaultComboBoxModel(signalSources);
		}
		return signalSourceSelectionComboBoxModel;
	}

	@Override
	public void propertyChange(PropertyChangeEvent evt) {
		String propertyName = evt.getPropertyName();

		if (propertyName.equals(SignalSourceSelectionPanel.SIGNAL_SOURCE_SELECTION_CHANGED_PROPERTY)) {
			SignalSource newSignalSource = (SignalSource) evt.getNewValue();
			showPanelForSignalSource(newSignalSource);
			firePropertyChange(SignalSourceSelectionPanel.SIGNAL_SOURCE_SELECTION_CHANGED_PROPERTY, null, newSignalSource);

			EegSystem eegSystem = getCurrentSignalSourcePanel().getEegSystemSelectionPanel().getSelectedEegSystem();
			firePropertyChange(AbstractSignalParametersPanel.EEG_SYSTEM_PROPERTY, null, eegSystem);
		}
		else if (propertyName.equals(AbstractSignalParametersPanel.NUMBER_OF_CHANNELS_PROPERTY) ||
			propertyName.equals(AbstractSignalParametersPanel.SAMPLING_FREQUENCY_PROPERTY) ||
			propertyName.equals(AbstractSignalParametersPanel.CHANNEL_LABELS_PROPERTY) ||
			propertyName.equals(AbstractSignalParametersPanel.EEG_SYSTEM_PROPERTY)
			) {
			Object source = evt.getSource();
			SignalSource selectedSignalSource = getSelectedSignalSource();

			if ((source == fileSignalSourcePanel && selectedSignalSource.isFile()) ||
				(source == openBCISignalSourcePanel && selectedSignalSource.isOpenBCI()) ||
				source == amplifierSignalSourcePanel && selectedSignalSource.isAmplifier())
				firePropertyChange(propertyName, 0, evt.getNewValue());
		}
		else if (propertyName.equals(AbstractMonitorSourcePanel.OPENBCI_CONNECTED_PROPERTY)) {
			firePropertyChange(propertyName, evt.getOldValue(), evt.getNewValue());
		}
	}

	/**
	 * Changes a panel visible to a panel for setting options for the given
	 * signal source.
	 * @param signalSource the current signal source
	 */
	protected void showPanelForSignalSource(SignalSource signalSource) {
		CardLayout cardLayout = (CardLayout) (this.getLayout());
		cardLayout.show(this, signalSource.toString());
	}

	/**
	 * Returns the signal source selected by this panel.
	 * @return the selected signal source
	 */
	public SignalSource getSelectedSignalSource() {
		return (SignalSource) getSignalSourceSelectionComboBoxModel().getSelectedItem();
	}

	/**
	 * Fills this panel using the data contained in the descriptor.
	 * @param openDocumentDescriptor descriptor to be used
	 */
	public void fillPanelFromModel(OpenDocumentDescriptor openDocumentDescriptor) {
		OpenSignalDescriptor openSignalDescriptor = openDocumentDescriptor.getOpenSignalDescriptor();

		SignalSource signalSource = openSignalDescriptor.getSignalSource();
		signalSourceSelectionComboBoxModel.setSelectedItem(signalSource);

		if (openSignalDescriptor.getOpenFileSignalDescriptor() != null) {
			fileSignalSourcePanel.fillPanelFromModel(openSignalDescriptor.getOpenFileSignalDescriptor());
			fileSignalSourcePanel.getSignalSourceSelectionPanel().fireSignalSourceSelectionChanged();
		}

		openBCISignalSourcePanel.fillPanelFromModel(openSignalDescriptor.getOpenMonitorDescriptor());
                try {
                        amplifierSignalSourcePanel.fillPanelFromModel(openSignalDescriptor.getAmplifierConnectionDescriptor());
                } catch (SignalMLException ex) {
                        // TODO: when amp cannot be found
                }

		clearPreviousConnections();
	}

	/**
	 * Sets all components in an unconnected state so that new connections
	 * can be made.
	 */
	protected void clearPreviousConnections() {
		openBCISignalSourcePanel.setConnected(false);
		openBCISignalSourcePanel.getMultiplexerConnectionPanel().setInterfaceInUnconnectedState();

		amplifierSignalSourcePanel.setConnected(false);
	}

	/**
	 * Fills the given descriptor with the data set using this panel.
	 * @param openDocumentDescriptor the descriptor to be filled
	 */
	public void fillModelFromPanel(OpenDocumentDescriptor openDocumentDescriptor) {
		OpenSignalDescriptor openSignalDescriptor = openDocumentDescriptor.getOpenSignalDescriptor();

		SignalSource signalSource = (SignalSource) signalSourceSelectionComboBoxModel.getSelectedItem();
		openSignalDescriptor.setSignalSource(signalSource);

		if (signalSource.isFile())
			openDocumentDescriptor.setType(ManagedDocumentType.SIGNAL);
		else
			openDocumentDescriptor.setType(ManagedDocumentType.MONITOR);

		if (signalSource.isFile()) {
			OpenFileSignalDescriptor openFileSignalDescriptor = openSignalDescriptor.getOpenFileSignalDescriptor();
			fileSignalSourcePanel.fillModelFromPanel(openFileSignalDescriptor);
		}
		else if (signalSource.isOpenBCI()) {
			OpenMonitorDescriptor openMonitorDescriptor = openSignalDescriptor.getOpenMonitorDescriptor();
			openBCISignalSourcePanel.fillModelFromPanel(openMonitorDescriptor);
		} else if (signalSource.isAmplifier()) {
                        AmplifierConnectionDescriptor connectionDescriptor = openSignalDescriptor.getAmplifierConnectionDescriptor();
                        try {
                                amplifierSignalSourcePanel.fillModelFromPanel(connectionDescriptor);
                        } catch (SignalMLException ex) {
                        }
                }
	}

	/**
	 * Returns the currently visible signal source panel.
	 * @return the currently visible signal source panel
	 */
	public AbstractSignalSourcePanel getCurrentSignalSourcePanel() {
		SignalSource signalSource = getSelectedSignalSource();

		if (signalSource.isFile())
			return fileSignalSourcePanel;
		else if (signalSource.isOpenBCI())
			return openBCISignalSourcePanel;
		else
			return amplifierSignalSourcePanel;
	}

	/**
	 * Validates if the data set using this panel is correct.
	 * @param model the model for this dialog
	 * @param errors the object in which errors are stored
	 */
	public void validatePanel(Object model, Errors errors) {
		if (getSelectedSignalSource().isFile()) {
			File selectedFile = fileSignalSourcePanel.getFileChooserPanel().getSelectedFile();
			if (selectedFile == null)
					errors.reject("opensignal.error.noFileSelected");
		}
	}

}
