/* SignalSourceSelectionPanel.java created 2011-03-06
 *
 */
package org.signalml.app.view.opensignal;

import static org.signalml.app.SvarogApplication._;
import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import javax.swing.ComboBoxModel;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import org.signalml.app.view.element.TitledPanelWithABorder;

/**
 * A panel for selecting a signal source type (file/openBCI/amplifier).
 *
 * @author Piotr Szachewicz
 */
public class SignalSourceSelectionPanel extends TitledPanelWithABorder implements ActionListener {

	/**
	 * A property representing the selected signal source.
	 */
	public static String SIGNAL_SOURCE_SELECTION_CHANGED_PROPERTY = "signalSourceSelectionChangedProperty";

	/**
	 * PropertyChangeSupport for notifying about property changes.
	 */
	private PropertyChangeSupport propertyChangeSupport = new PropertyChangeSupport(this);

	/**
	 * A combo box for selecting the signal source.
	 */
	private JComboBox selectionComboBox;

	/**
	 * Constructor.
	 * @param messageSource message source capable of resolving localized
	 * messages.
	 */
	public  SignalSourceSelectionPanel() {
		createInterface();
	}

	/**
	 * Returns the title for this panel.
	 * @return the title for this panel
	 */
	protected String getTitle() {
		return _("Select signal source");
	}

	/**
	 * Creates the GUI components for this panel.
	 */
	private void createInterface() {
		this.setLayout(new BorderLayout());
		JPanel selectionPanel = new JPanel(new BorderLayout());

		JLabel label = new JLabel(_("Signal source"));

		selectionPanel.add(label, BorderLayout.CENTER);
		selectionPanel.add(getSelectionComboBox(), BorderLayout.EAST);
		this.setTitledBorder(getTitle());

		this.add(selectionPanel, BorderLayout.CENTER);
	}

	/**
	 * Returns the signal source selection combo box.
	 * @return the signal source selection combo box
	 */
	protected JComboBox getSelectionComboBox() {
		if (selectionComboBox == null) {
			selectionComboBox = new JComboBox();

			selectionComboBox.addActionListener(this);

		}
		return selectionComboBox;
	}

	/**
	 * Sets the combo box model to be used for the signal source selection
	 * combo box.
	 * @param model the combo box model to be used
	 */
	public void setSelectionComboBoxModel(ComboBoxModel model) {
		getSelectionComboBox().setModel(model);
	}

	@Override
	public void addPropertyChangeListener(PropertyChangeListener listener) {
		propertyChangeSupport.addPropertyChangeListener(listener);
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		fireSignalSourceSelectionChanged();
	}

	@Override
	public void setEnabled(boolean enabled) {
		getSelectionComboBox().setEnabled(enabled);
	}

	/**
	 * Notifies all listeners that the signal source has changed.
	 */
	public void fireSignalSourceSelectionChanged() {
		SignalSource currentSignalSource = (SignalSource) getSelectionComboBox().getSelectedItem();
		propertyChangeSupport.firePropertyChange(SIGNAL_SOURCE_SELECTION_CHANGED_PROPERTY, null, currentSignalSource);
	}

}
