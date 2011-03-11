/* SignalSourceSelectionPanel.java created 2011-03-06
 *
 */
package org.signalml.app.view.opensignal;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import javax.swing.ComboBoxModel;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import org.signalml.app.view.element.TitledPanelWithABorder;
import org.springframework.context.support.MessageSourceAccessor;

/**
 *
 * @author Piotr Szachewicz
 */
public class SignalSourceSelectionPanel extends TitledPanelWithABorder implements ActionListener {

	public static String SIGNAL_SOURCE_SELECTION_CHANGED_PROPERTY = "signalSourceSelectionChangedProperty";
	private PropertyChangeSupport propertyChangeSupport = new PropertyChangeSupport(this);
	private MessageSourceAccessor messageSource;
	private JComboBox selectionComboBox;

	public SignalSourceSelectionPanel(MessageSourceAccessor messageSource) {
		this.messageSource = messageSource;
		createInterface();
	}

	protected String getTitle() {
		return "select signal source";
	}

	private void createInterface() {

		this.setLayout(new BorderLayout());
		JPanel selectionPanel = new JPanel(new BorderLayout());
		//this.setLayout(new BorderLayout());

		JLabel label = new JLabel("Signal source");

		selectionPanel.add(label, BorderLayout.CENTER);
		selectionPanel.add(getSelectionComboBox(), BorderLayout.EAST);
		this.setTitledBorder(getTitle());

		this.add(selectionPanel, BorderLayout.CENTER);

//		this.setMaximumSize(new Dimension(300, 100));
	}

	protected JComboBox getSelectionComboBox() {
		if (selectionComboBox == null) {
			selectionComboBox = new JComboBox();

			selectionComboBox.addActionListener(this);

		}
		return selectionComboBox;
	}

	public void setSelectionComboBoxModel(ComboBoxModel model) {
		getSelectionComboBox().setModel(model);
	}

	@Override
	public void addPropertyChangeListener(PropertyChangeListener listener) {
		propertyChangeSupport.addPropertyChangeListener(listener);
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		SignalSource currentSignalSource = (SignalSource) getSelectionComboBox().getSelectedItem();
		propertyChangeSupport.firePropertyChange(SIGNAL_SOURCE_SELECTION_CHANGED_PROPERTY, null, currentSignalSource);
	}

}
