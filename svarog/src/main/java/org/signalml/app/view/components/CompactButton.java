/* CompactButton.java created 2007-11-21
 *
 */

package org.signalml.app.view.components;

import java.awt.Insets;

import javax.swing.Action;
import javax.swing.Icon;
import javax.swing.JButton;

/**
 * The icon-only button, which main purpose is to be compact.
 * Has parameters set in order to provide that functionality:
 * <ul>
 * <li>the text is not displayed,</li>
 * <li>there is no margin between border of the button and the label,</li>
 * <li>the button has no border,</li>
 * <li>the state of the button (focused, unfocused) is not painted,</li>
 * <li>the button doesn't paint the content area.</li></ul>
 * 
 * @author Michal Dobaczewski &copy; 2007-2008 CC Otwarte Systemy Komputerowe Sp. z o.o.
 */
public class CompactButton extends JButton {

	private static final long serialVersionUID = 1L;

	/**
     * Creates a button with no set text or icon and configures it.
	 */
	public CompactButton() {
		super();
		configure();
	}

	/**
     * Creates a button where properties are taken from the 
     * <code>Action</code> supplied and configures this button.
     * @param a the <code>Action</code> used to specify the new button
     */
	public CompactButton(Action a) {
		super(a);
		configure();
	}

	/**
     * Creates a button with an icon and configures it.
     * @param icon  the Icon image to display on the button
     */
	public CompactButton(Icon icon) {
		super(icon);
		configure();
	}

	/**
     * Creates a button with initial text and an icon and configures it.
     * @param text  the text of the button
     * @param icon  the Icon image to display on the button
     */
	public CompactButton(String text, Icon icon) {
		super(text, icon);
		configure();
	}

	/**
     * Creates a button with text and configures it.
     * @param text  the text of the button
     */
	public CompactButton(String text) {
		super(text);
		configure();
	}

	/**
	 * Sets the parameters of this button to make this icon-only button:
	 * <ul>
	 * <li>that the text shouldn't be displayed,</li>
	 * <li>that there should be no margin between border of the button and the
	 * label,</li>
	 * <li>that the button has no border,</li>
	 * <li>that the state of the button (focused, unfocused) shouldn't be
	 * painted,</li>
	 * <li>that the button shouldn't paint content area.</li></ul>
	 */
	private void configure() {

		setHideActionText(true);
		setMargin(new Insets(0,0,0,0));
		setContentAreaFilled(false);
		setBorder(null);
		setFocusPainted(false);

	}

}
