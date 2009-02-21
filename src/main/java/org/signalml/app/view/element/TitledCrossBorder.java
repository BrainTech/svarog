/* TitledCrossBorder.java created 2007-12-13
 * 
 */

package org.signalml.app.view.element;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.Stroke;

import javax.swing.border.TitledBorder;

/** TitledCrossBorder
 *
 * 
 * @author Michal Dobaczewski &copy; 2007-2008 CC Otwarte Systemy Komputerowe Sp. z o.o.
 */
public class TitledCrossBorder extends TitledBorder {

	private static final long serialVersionUID = 1L;

	private static final Dimension CLOSE_CROSS_SIZE = new Dimension(10,10);
	private static final int CLOSE_CROSS_OFFSET = 2;
	
	private boolean hasCloseCross;
	
	public TitledCrossBorder(String title, boolean hasCloseCross) {
		super(title);
		this.hasCloseCross = hasCloseCross;
	}

	@Override
	public void paintBorder(Component c, Graphics gOrig, int x, int y, int width, int height) {
		super.paintBorder(c, gOrig, x, y, width, height);

		if( hasCloseCross ) {
			Graphics2D g = (Graphics2D) gOrig;
			
	
			Object oldHint = g.getRenderingHint( RenderingHints.KEY_ANTIALIASING );
			Color oldColor = g.getColor();
			Stroke oldStroke = g.getStroke();
			try {
				g.setRenderingHint( RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON );
				g.setColor(Color.RED);
				g.setStroke( new BasicStroke(3F, BasicStroke.CAP_ROUND, BasicStroke.JOIN_BEVEL ) );				
				g.drawLine(width-(CLOSE_CROSS_OFFSET+CLOSE_CROSS_SIZE.width+1), CLOSE_CROSS_OFFSET+CLOSE_CROSS_SIZE.height-1, width-(1+CLOSE_CROSS_OFFSET), CLOSE_CROSS_OFFSET);
				g.drawLine(width-(CLOSE_CROSS_OFFSET+CLOSE_CROSS_SIZE.width+1), CLOSE_CROSS_OFFSET, width-(1+CLOSE_CROSS_OFFSET), CLOSE_CROSS_OFFSET+CLOSE_CROSS_SIZE.height-1);
			} finally {
				g.setRenderingHint( RenderingHints.KEY_ANTIALIASING, oldHint );
				g.setColor(oldColor);
				g.setStroke(oldStroke);
			}
		}
		
	}

}
