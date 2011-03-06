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
import org.springframework.context.support.MessageSourceAccessor;

/**
 *
 * @author Piotr Szachewicz
 */
public class SignalSourcePanel extends JPanel implements PropertyChangeListener {

	private MessageSourceAccessor messageSource;
	private ComboBoxModel signalSourceSelectionComboBoxModel;
	private FileSignalSourcePanel fileSignalSourcePanel;
	private OpenBCISignalSourcePanel openBCISignalSourcePanel;
	private AmplifierSignalSourcePanel amplifierSignalSourcePanel;

	public SignalSourcePanel(MessageSourceAccessor messageSource) {
		this.messageSource = messageSource;
		createInterface();
	}

	private void createInterface() {
		CardLayout cardLayout = new CardLayout();
		this.setLayout(cardLayout);

		fileSignalSourcePanel = new FileSignalSourcePanel(messageSource);
		openBCISignalSourcePanel = new OpenBCISignalSourcePanel(messageSource);
		amplifierSignalSourcePanel = new AmplifierSignalSourcePanel(messageSource);

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
		SignalSource newSignalSource = (SignalSource) evt.getNewValue();
		showPanelForSignalSource(newSignalSource);
		System.out.println("changed;");
	}

	protected void showPanelForSignalSource(SignalSource signalSource) {
		CardLayout cardLayout = (CardLayout) (this.getLayout());
		cardLayout.show(this, signalSource.toString());
	}

	public SignalSource getSelectedSignalSource() {
		return (SignalSource) getSignalSourceSelectionComboBoxModel().getSelectedItem();
	}
}
