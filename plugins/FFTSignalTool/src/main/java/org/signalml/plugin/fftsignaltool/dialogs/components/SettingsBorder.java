/* TitledCrossBorder.java created 2007-12-13
 *
 */

package org.signalml.plugin.fftsignaltool.dialogs.components;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.Stroke;

import javax.swing.border.TitledBorder;

/**
 * Titled border which can contain the closing cross at the top right corner
 * (if {@code hasCloseCross==true}).
 *
 * @author Michal Dobaczewski &copy; 2007-2008 CC Otwarte Systemy Komputerowe
 *         Sp. z o.o.
 */
public class SettingsBorder extends TitledBorder {

	/**
	 * the serialization constant
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * the size of the cross
	 */
	private static final Dimension CLOSE_CROSS_SIZE = new Dimension(10, 10);
	/**
	 * the distance between the cross and the border
	 */
	private static final int CLOSE_CROSS_OFFSET = 2;

	/**
	 * {@code true} if this border should contain the cross,
	 * {@code false} otherwise
	 */
	private boolean hasCloseCross;

	/**
	 * Constructor.
	 * Creates this border with the given title and, if {@code
	 * hasCloseCross==true}, the closing cross.
	 * @param title the title of this border
	 * @param hasCloseCross {@code true} if this border should contain the
	 * cross, {@code false} otherwise
	 */
	public SettingsBorder(String title, boolean hasCloseCross) {
		super(title);
		this.hasCloseCross = hasCloseCross;
	}

	/**
	 * {@link TitledBorder#paintBorder(Component, Graphics, int, int, int, int)
	 * Paints} the border of this component and if contains the close cross
	 * ({@code hasCloseCross==true}) paints two red lines, which form a cross
	 * at the top right corner.
	 */
	@Override
	public void paintBorder(Component c, Graphics gOrig, int x, int y,
							int width, int height) {
		super.paintBorder(c, gOrig, x, y, width, height);

		if (hasCloseCross) {
			Graphics2D g = (Graphics2D) gOrig;

			Object oldHint = g
							 .getRenderingHint(RenderingHints.KEY_ANTIALIASING);
			Color oldColor = g.getColor();
			Stroke oldStroke = g.getStroke();
			try {
				g.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
								   RenderingHints.VALUE_ANTIALIAS_ON);
				g.setColor(Color.RED);
				g.setStroke(new BasicStroke(3F, BasicStroke.CAP_ROUND,
											BasicStroke.JOIN_BEVEL));
				g.drawLine(width
						   - (CLOSE_CROSS_OFFSET + CLOSE_CROSS_SIZE.width + 1),
						   CLOSE_CROSS_OFFSET + CLOSE_CROSS_SIZE.height - 1, width
						   - (1 + CLOSE_CROSS_OFFSET), CLOSE_CROSS_OFFSET);
				g.drawLine(width
						   - (CLOSE_CROSS_OFFSET + CLOSE_CROSS_SIZE.width + 1),
						   CLOSE_CROSS_OFFSET, width - (1 + CLOSE_CROSS_OFFSET),
						   CLOSE_CROSS_OFFSET + CLOSE_CROSS_SIZE.height - 1);
			} finally {
				g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, oldHint);
				g.setColor(oldColor);
				g.setStroke(oldStroke);
			}
		}

	}

}
