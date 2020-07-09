/* AntialiasedLabel.java created 2007-12-16
 *
 */

package org.signalml.app.view.common.components;

import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import javax.swing.JComponent;
import javax.swing.JLabel;

/**
 * Label with the anti-aliasing.
 *
 * @author Michal Dobaczewski &copy; 2007-2008 CC Otwarte Systemy Komputerowe Sp. z o.o.
 */
public class AntialiasedLabel extends JLabel {

	private static final long serialVersionUID = 1L;

	/**
	 * Sets that this component should be anti-aliased and
	 * {@link JComponent#paintComponent(Graphics) paints} it.
	 */
	@Override
	protected void paintComponent(Graphics g) {
		((Graphics2D) g).setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		super.paintComponent(g);
	}

}
