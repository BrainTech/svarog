/* AbstractSignalMLAction.java created 2007-09-10
 *
 */
package org.signalml.plugin.export.view;

import java.awt.Component;

import javax.swing.AbstractAction;
import javax.swing.ImageIcon;
import javax.swing.JPopupMenu;
import javax.swing.KeyStroke;

import org.signalml.app.action.selector.TableFocusSelector;
import org.signalml.app.action.selector.ViewFocusSelector;
import org.signalml.app.util.IconUtils;
import org.springframework.context.support.MessageSourceAccessor;

/**
 * This is a super-class for all actions in Svarog.
 * Allows to:
 * <ul>
 * <li>set parameters of this action, such as accelerator key, icon, name
 * and tooltip,</li>
 * <li>finds the focus selector (see {@link ViewFocusSelector},
 * {@link TableFocusSelector}).</li>
 * </ul>
 *
 * @author Michal Dobaczewski &copy; 2007-2008 CC Otwarte Systemy Komputerowe Sp. z o.o.
 */
public abstract class AbstractSignalMLAction extends AbstractAction {

	static final long serialVersionUID = 1L;

	/**
	 * the source of messages (labels, names, etc.)
	 */
	protected MessageSourceAccessor messageSource;

	/**
	 * Constructor. Only calls the {@link AbstractAction#AbstractAction()
	 * constructor} of the superclass.
	 */
	protected AbstractSignalMLAction() {
		super();
	}

	/**
	 * Constructor. Calls the {@link AbstractAction#AbstractAction()
	 * constructor} of the superclass and sets the source of messages.
	 * @param messageSource the source of messages (labels, names, etc.)
	 */
	public AbstractSignalMLAction(MessageSourceAccessor messageSource) {
		super();
		if (messageSource == null) {
			throw new NullPointerException("No message source");
		}
		this.messageSource = messageSource;
		setEnabledAsNeeded();
	}

	/**
	 * Sets the KeyStroke to be used as the accelerator for the action.
	 * @param accelerator the String that will be converted to {@link KeyStroke}.
	 * @see KeyStroke#getKeyStroke(String)
	 */
	public void setAccelerator(String accelerator) {
		if (accelerator != null) {
			putValue(AbstractAction.ACCELERATOR_KEY, KeyStroke.getKeyStroke(accelerator));
		} else {
			putValue(AbstractAction.ACCELERATOR_KEY, null);
		}
	}

	/**
	 * Sets the {@link ImageIcon icon} for this action.
	 * @param iconPath the path to the icon
	 */
	public void setIconPath(String iconPath) {
		if (iconPath != null) {
			putValue(AbstractAction.SMALL_ICON, IconUtils.loadClassPathIcon(iconPath));
		} else {
			putValue(AbstractAction.SMALL_ICON, null);
		}
	}

	/**
	 * Sets the name of this action.
	 * @param text the code for the message that is to be used as the name
	 */
	public void setText(String text) {
		if (text != null) {
			putValue(AbstractAction.NAME, messageSource.getMessage(text));
		} else {
			putValue(AbstractAction.NAME, null);
		}
	}

	/**
	 * Sets the name of this action.
	 * @param text the code for the message that is to be used as the name
	 * @param arguments arguments for the message, or {@code null} if none
	 */
	public void setText(String text, Object[] arguments) {
		if (text != null) {
			putValue(AbstractAction.NAME, messageSource.getMessage(text, arguments));
		} else {
			putValue(AbstractAction.NAME, null);
		}
	}

	/**
	 * Sets the short description (used in tooltip texts) of this action.
	 * @param toolTip the code for the message that is to be used as short description
	 */
	public void setToolTip(String toolTip) {
		if (toolTip != null) {
			putValue(AbstractAction.SHORT_DESCRIPTION, messageSource.getMessage(toolTip));
		} else {
			putValue(AbstractAction.SHORT_DESCRIPTION, null);
		}
	}

	/**
	 * Enables or disables the action as it is currently needed.
	 */
	public void setEnabledAsNeeded() {
		setEnabled(true);
	}

	/**
	 * Finds the focus selector for a given source and type.
	 * @param source the source where the event occurred
	 * @param clazz the type of requested selector
	 * @return the found selector
	 * @see ViewFocusSelector
	 * @see TableFocusSelector
	 */
	public Object findFocusSelector(Object source, Class<?> clazz) {

		if (source == null || clazz == null) {
			return null;
		}

		if (clazz.isAssignableFrom(source.getClass())) {

			return source;

		} else if (source instanceof Component) {


			Component current = (Component) source;
			do {

				if (current instanceof JPopupMenu) {
					current = ((JPopupMenu) current).getInvoker();
					continue;
				}

				current = current.getParent();

				if (current != null) {

					if (clazz.isAssignableFrom(current.getClass())) {
						return current;
					}

				}

			} while (current != null);

		}


		return null;
	}

}
