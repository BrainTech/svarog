/* AbstractSignalMLAction.java created 2007-09-10
 *
 */
package org.signalml.plugin.export.view;

import java.awt.Component;
import java.awt.event.KeyEvent;

import javax.swing.AbstractAction;
import javax.swing.ImageIcon;
import javax.swing.JPopupMenu;
import javax.swing.KeyStroke;

import org.signalml.app.action.selector.ViewFocusSelector;
import org.signalml.app.util.IconUtils;

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
	 * Constructor.
	 */
	protected AbstractSignalMLAction() {
		super();
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
	 * @param text the new name
	 */
	protected final void setText(String text) {
		putValue(AbstractAction.NAME, text);
	}

	/**
	 * Sets the short description (used in tooltip texts) of this action.
	 * @param toolTip the tooltip text
	 */
	protected void setToolTip(String toolTip) {
		putValue(AbstractAction.SHORT_DESCRIPTION, toolTip);
	}

	/**
	 * Sets a mnemonic for this action.
	 * @param mnemonic the value of the mnemonic
	 * (e.g. KeyEvent.VK_H).
	 */
	protected void setMnemonic(int mnemonic) {
		putValue(MNEMONIC_KEY, mnemonic);
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
