/* SignalSourcePanel.java created 2011-03-06
 *
 */
package org.signalml.app.view.opensignal;

import java.awt.CardLayout;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.ComboBoxModel;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JPanel;
import org.signalml.app.model.AmplifierConnectionDescriptor;
import org.signalml.app.model.OpenFileSignalDescriptor;
import org.signalml.app.model.OpenMonitorDescriptor;
import org.signalml.app.model.OpenSignalDescriptor;
import org.signalml.app.view.ViewerElementManager;
import org.signalml.plugin.export.SignalMLException;
import org.springframework.context.support.MessageSourceAccessor;

/**
 *
 * @author Piotr Szachewicz
 */
public class SignalSourcePanel extends JPanel implements PropertyChangeListener {

	private ViewerElementManager viewerElementManager;

	private MessageSourceAccessor messageSource;
	private ComboBoxModel signalSourceSelectionComboBoxModel;
	private FileSignalSourcePanel fileSignalSourcePanel;
	private OpenBCISignalSourcePanel openBCISignalSourcePanel;
	private AmplifierSignalSourcePanel amplifierSignalSourcePanel;

	public SignalSourcePanel(MessageSourceAccessor messageSource, ViewerElementManager viewerElementManager) {
		this.messageSource = messageSource;
		this.viewerElementManager = viewerElementManager;
		createInterface();
	}

	private void createInterface() {
		CardLayout cardLayout = new CardLayout();
		this.setLayout(cardLayout);

		fileSignalSourcePanel = new FileSignalSourcePanel(messageSource, viewerElementManager);
		openBCISignalSourcePanel = new OpenBCISignalSourcePanel(messageSource, viewerElementManager);
		amplifierSignalSourcePanel = new AmplifierSignalSourcePanel(messageSource, viewerElementManager);

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
			System.out.println("changed signal source type");
			firePropertyChange(SignalSourceSelectionPanel.SIGNAL_SOURCE_SELECTION_CHANGED_PROPERTY, null, newSignalSource);
		}
		else if (propertyName.equals(AbstractSignalParametersPanel.NUMBER_OF_CHANNELS_PROPERTY) ||
			propertyName.equals(AbstractSignalParametersPanel.SAMPLING_FREQUENCY_PROPERTY) ||
			propertyName.equals(AbstractSignalParametersPanel.CHANNEL_LABELS_PROPERTY)
			) {
			Object source = evt.getSource();
			SignalSource selectedSignalSource = getSelectedSignalSource();
			if ( (source == fileSignalSourcePanel && selectedSignalSource.isFile()) ||
				(source == openBCISignalSourcePanel && selectedSignalSource.isOpenBCI()) ||
				source == amplifierSignalSourcePanel && selectedSignalSource.isAmplifier())
				firePropertyChange(propertyName, 0, evt.getNewValue());
		}
		else if (propertyName.equals(AbstractMonitorSourcePanel.OPENBCI_CONNECTED_PROPERTY)) {
			firePropertyChange(propertyName, evt.getOldValue(), evt.getNewValue());
		}
	}

	protected void showPanelForSignalSource(SignalSource signalSource) {
		CardLayout cardLayout = (CardLayout) (this.getLayout());
		cardLayout.show(this, signalSource.toString());
	}

	public SignalSource getSelectedSignalSource() {
		return (SignalSource) getSignalSourceSelectionComboBoxModel().getSelectedItem();
	}

	public void fillPanelFromModel(OpenSignalDescriptor openSignalDescriptor) {
		SignalSource signalSource = openSignalDescriptor.getSignalSource();
		signalSourceSelectionComboBoxModel.setSelectedItem(signalSource);

		if (openSignalDescriptor.getOpenFileSignalDescriptor() != null)
			fileSignalSourcePanel.fillPanelFromModel(openSignalDescriptor.getOpenFileSignalDescriptor());

		openBCISignalSourcePanel.fillPanelFromModel(openSignalDescriptor.getOpenMonitorDescriptor());

                try {
                        amplifierSignalSourcePanel.fillPanelFromModel(openSignalDescriptor.getAmplifierConnectionDescriptor());
                } catch (SignalMLException ex) {
                        // TODO: when amp cannot be found
                }
	}

	public void fillModelFromPanel(OpenSignalDescriptor openSignalDescriptor) {
		SignalSource signalSource = (SignalSource) signalSourceSelectionComboBoxModel.getSelectedItem();
		openSignalDescriptor.setSignalSource(signalSource);
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

	public AbstractSignalSourcePanel getCurrentSignalSourcePanel() {
		SignalSource signalSource = getSelectedSignalSource();

		if (signalSource.isFile())
			return fileSignalSourcePanel;
		else if (signalSource.isOpenBCI())
			return openBCISignalSourcePanel;
		else
			return amplifierSignalSourcePanel;
	}

}
