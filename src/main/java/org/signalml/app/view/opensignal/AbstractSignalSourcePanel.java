/* AbstractSignalSourcePanel.java created 2011-03-06
 *
 */
package org.signalml.app.view.opensignal;

import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import javax.swing.BoxLayout;
import javax.swing.ComboBoxModel;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.BevelBorder;
import javax.swing.border.Border;
import org.signalml.app.view.element.TitledPanelWithABorder;
import org.springframework.context.support.MessageSourceAccessor;

/**
 *
 * @author Piotr Szachewicz
 */
abstract public class AbstractSignalSourcePanel extends JPanel implements PropertyChangeListener {

	private PropertyChangeSupport propertyChangeSupport = new PropertyChangeSupport(this);
	private MessageSourceAccessor messageSource;
	private SignalSourceSelectionPanel signalSourceSelectionPanel;

	public AbstractSignalSourcePanel(MessageSourceAccessor messageSource) {
		this.messageSource = messageSource;
		createInterface();
	}

	private void createInterface() {
		this.setLayout(new GridLayout(1, 2));

		this.add(createLeftColumnPanel());
		this.add(createRightColumnPanel());
	}

	abstract protected JPanel createLeftColumnPanel();

	abstract protected JPanel createRightColumnPanel();

	protected SignalSourceSelectionPanel getSignalSourceSelectionPanel() {
		if (signalSourceSelectionPanel == null) {
			signalSourceSelectionPanel = new SignalSourceSelectionPanel(messageSource);
			signalSourceSelectionPanel.addPropertyChangeListener(this);
		}
		return signalSourceSelectionPanel;
	}

	public void setSignalSourceSelectionComboBoxModel(ComboBoxModel model) {
		getSignalSourceSelectionPanel().setSelectionComboBoxModel(model);
	}

	@Override
	public void addPropertyChangeListener(PropertyChangeListener listener) {
		propertyChangeSupport.addPropertyChangeListener(listener);
	}

	@Override
	public void propertyChange(PropertyChangeEvent evt) {
		propertyChangeSupport.firePropertyChange(evt);
	}
}
