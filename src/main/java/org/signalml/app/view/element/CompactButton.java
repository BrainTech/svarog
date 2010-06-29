/* CompactButton.java created 2007-11-21
 *
 */

package org.signalml.app.view.element;

import java.awt.Insets;

import javax.swing.Action;
import javax.swing.Icon;
import javax.swing.JButton;

/** CompactButton
 *
 *
 * @author Michal Dobaczewski &copy; 2007-2008 CC Otwarte Systemy Komputerowe Sp. z o.o.
 */
public class CompactButton extends JButton {

	private static final long serialVersionUID = 1L;

	public CompactButton() {
		super();
		configure();
	}

	public CompactButton(Action a) {
		super(a);
		configure();
	}

	public CompactButton(Icon icon) {
		super(icon);
		configure();
	}

	public CompactButton(String text, Icon icon) {
		super(text, icon);
		configure();
	}

	public CompactButton(String text) {
		super(text);
		configure();
	}

	private void configure() {

		setHideActionText(true);
		setMargin(new Insets(0,0,0,0));
		setContentAreaFilled(false);
		setBorder(null);
		setFocusPainted(false);

	}

}
