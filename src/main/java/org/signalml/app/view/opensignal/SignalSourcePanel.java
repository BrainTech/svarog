/* SignalSourcePanel.java created 2011-03-06
 *
 */
package org.signalml.app.view.opensignal;

import java.awt.CardLayout;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import javax.swing.ComboBoxModel;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JPanel;
import org.signalml.app.view.ViewerElementManager;
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
		}
		else if (propertyName.equals(SignalParametersPanel.NUMBER_OF_CHANNELS_PROPERTY) ||
			propertyName.equals(SignalParametersPanel.SAMPLING_FREQUENCY_PROPERTY)) {
			firePropertyChange(propertyName, 0, evt.getNewValue());
		}
	}

	protected void showPanelForSignalSource(SignalSource signalSource) {
		CardLayout cardLayout = (CardLayout) (this.getLayout());
		cardLayout.show(this, signalSource.toString());
	}

	public SignalSource getSelectedSignalSource() {
		return (SignalSource) getSignalSourceSelectionComboBoxModel().getSelectedItem();
	}
}
