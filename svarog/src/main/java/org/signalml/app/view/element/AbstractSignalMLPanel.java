/* AbstractSignalMLPanel.java created 2011-04-20
 *
 */
package org.signalml.app.view.element;

import java.awt.Component;
import java.awt.Container;
import javax.swing.JPanel;
import org.springframework.context.support.MessageSourceAccessor;

/**
 * All panels in Svarog should extend this panel. Contains an instance of
 * MessageSourceAccessor and methods for enabling/disabling all components
 * within the panel.
 * 
 * @author Piotr Szachewicz
 */
public class AbstractSignalMLPanel extends JPanel {

	/**
	 * the {@link MessageSourceAccessor source} of messages (labels)
	 */
	protected MessageSourceAccessor messageSource;

	/**
	 * Constructor.
	 * @param messageSource message Source capable of returning localized
	 * messages
	 */
	public AbstractSignalMLPanel(MessageSourceAccessor messageSource) {
		this.messageSource = messageSource;
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
}