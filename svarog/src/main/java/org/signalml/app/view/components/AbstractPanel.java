package org.signalml.app.view.components;

import java.awt.Component;
import java.awt.Window;

import java.awt.Container;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import javax.swing.JPanel;

import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;

import org.apache.log4j.Logger;
import org.signalml.app.model.components.validation.ValidationErrors;

/**
 * All panels in Svarog should extend this panel. Contains an instance of
 * SvarogI18n and methods for enabling/disabling all components
 * within the panel.
 *
 * @author Piotr Szachewicz
 */
public class AbstractPanel extends JPanel implements PropertyChangeListener {

	protected transient final Logger logger = Logger.getLogger(getClass());

	/**
	 * PropertyChangeSupport to fire property changes when needed.
	 */
	private PropertyChangeSupport propertyChangeSupport;

	/**
	 * Constructor.
	 * @param messageSource message Source capable of returning localized
	 * messages
	 */
	public AbstractPanel() {
		super();
		propertyChangeSupport = new PropertyChangeSupport(this);
	}

	public Window getParentWindow() {

		Container container = this;

		while (!(container instanceof Window)) {
			if (container == null)
				return null;
			container = container.getParent();
		}

		return (Window) container;
	}

	/**
	 * Sets enabled to this panel and all it's children.
	 * Clears all fields if enabled == false.
	 *
	 * @param enabled true or false
	 */
	public void setEnabledAll(boolean enabled) {
		setEnabledToChildren(this, enabled);
	}

	/**
	 * Sets enabled to a component and all of it's children.
	 *
	 * @param component target component
	 * @param enabled true or false
	 * @param omit wheter to omit component
	 */
	private void setEnabledToChildren(Component component, boolean enabled) {

		component.setEnabled(enabled);
		if (component instanceof Container) {
			Component[] children = ((Container) component).getComponents();
			for (Component child : children) {
				setEnabledToChildren(child, enabled);
			}
		}
	}

	/**
	 * Puts a border around this panel with a given title.
	 * @param label the title to be shown on the border
	 */
	protected void setTitledBorder(String label) {
		setTitledBorder(this, label);
	}

	protected void setTitledBorder(JPanel panel, String label) {
		CompoundBorder cb = new CompoundBorder(
			new TitledBorder(label),
			new EmptyBorder(3,3,3,3)
		);

		panel.setBorder(cb);
	}

	@Override
	public void addPropertyChangeListener(PropertyChangeListener listener) {
		propertyChangeSupport.addPropertyChangeListener(listener);
	}

	@Override
	public void propertyChange(PropertyChangeEvent evt) {
	}

	@Override
	protected void firePropertyChange(String propertyName, Object oldValue, Object newValue) {
		if (propertyChangeSupport != null)
			propertyChangeSupport.firePropertyChange(propertyName, oldValue, newValue);
	}

}
