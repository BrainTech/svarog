/* RulerMeasurmentPlot.java created 2007-10-05
 * 
 */

package org.signalml.app.view.signal;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Stroke;
import java.awt.geom.Rectangle2D;

import javax.swing.JComponent;

import org.signalml.app.util.GeometryUtils;
import org.signalml.app.util.IconUtils;
import org.signalml.util.Util;

/** RulerMeasurmentPlot
 *
 * 
 * @author Michal Dobaczewski &copy; 2007-2008 CC Otwarte Systemy Komputerowe Sp. z o.o.
 */
public class RulerMeasurmentPlot extends JComponent {

	private static final float MIN_LABEL_WIDTH = 60F;
	private static final int OVAL_RADIUS = 6;
	private static final int OVAL_DIAMETER = 2*OVAL_RADIUS;
	
	private static final long serialVersionUID = 1L;

	private static final Stroke lineStroke = new BasicStroke(3F);
	private static final Color boxColor = new Color(255,255,187);
	
	private Point start;
	private Point end;

	private Point origin;
	
	private float xValue;
	private float yValue;
		
	public RulerMeasurmentPlot() {
		super();
		setCursor(IconUtils.getEmptyCursor());		
	}	
	
	@Override
	protected void paintComponent(Graphics gOrig) {

		if( start == null || end == null || origin == null ) {
			return;
		}
		
		Graphics2D g = (Graphics2D) gOrig;
		Stroke origStroke = g.getStroke();
		
		g.setColor(Color.RED);
		
		Point relStart = new Point( start.x - origin.x, start.y - origin.y );
		Point relEnd = new Point( end.x - origin.x, end.y - origin.y );
		
		g.fillOval(relStart.x-OVAL_RADIUS, relStart.y-OVAL_RADIUS, OVAL_DIAMETER, OVAL_DIAMETER);
		
		if( !relStart.equals(relEnd) ) {
			g.setStroke(lineStroke);
			g.drawOval(relEnd.x-OVAL_RADIUS, relEnd.y-OVAL_RADIUS, OVAL_DIAMETER, OVAL_DIAMETER);
			if( relStart.distance(relEnd) > OVAL_RADIUS ) {
				GeometryUtils.translatePointToCircleBorder(relEnd, relStart, OVAL_RADIUS);
				g.drawLine(relStart.x, relStart.y, relEnd.x, relEnd.y);
			}
			g.setStroke(origStroke);
			g.drawLine(relEnd.x, relEnd.y, relEnd.x, relEnd.y);
		}
		
		drawValueBox( g, xValue, relEnd.x, relEnd.y, true);
		drawValueBox( g, yValue, relEnd.x, relEnd.y, false);
		
	}
	
	private void drawValueBox(Graphics2D g, float value, int x, int y, boolean under) {
		
		String text = Float.toString(value);
		Rectangle2D labelRect2D = g.getFont().getStringBounds(text, g.getFontRenderContext());
		int offset = 0;
		double width = labelRect2D.getWidth(); 
		if( width < MIN_LABEL_WIDTH ) {
			offset = (int) Math.round( ( ( MIN_LABEL_WIDTH-width )/2 ) );
			width = MIN_LABEL_WIDTH;
		}
		Rectangle labelRect = new Rectangle(
				x, 
				y, 
				(int) Math.ceil( width ), 
				(int) Math.ceil( labelRect2D.getHeight() ) 
		);
		labelRect.grow(3, 3);

		int realX;
		int realY;
		
		if( under ) {
			realX = x - ( labelRect.width  / 2 );
			realY = y + 10 + ( labelRect.height / 2 );  
		} else {
			realX = x + 12;
			realY = y - ( labelRect.height / 2 );
		}
		
		g.setColor(boxColor);
		g.fillRect(realX, realY, labelRect.width, labelRect.height);
		g.setColor(Color.BLACK);
		g.drawRect(realX, realY, labelRect.width-1, labelRect.height-1);
				
		g.drawString(text, realX+3+offset, realY+3-((int) labelRect2D.getY()) );
				
	}

	@Override
	public boolean isOpaque() {
		return false;
	}
	
	public Point getStart() {
		return start;
	}

	public void setStart(Point start) {
		if( !Util.equalsWithNulls(this.start, start) ) {
			this.start = start;
			repaint();
		}
	}

	public Point getEnd() {
		return end;
	}

	public void setEnd(Point end) {
		if( !Util.equalsWithNulls(this.end, end) ) {
			this.end = end;
			repaint();
		}
	}

	public Point getOrigin() {
		return origin;
	}

	public void setOrigin(Point origin) {
		if( !Util.equalsWithNulls(this.origin, origin) ) {
			this.origin = origin;
			repaint();
		}
	}

	public float getXValue() {
		return xValue;
	}

	public void setXValue(float value) {
		if( xValue != value ) {
			xValue = value;
			repaint();
		}
	}

	public float getYValue() {		
		return yValue;
	}

	public void setYValue(float value) {
		if( yValue != value ) {
			yValue = value;
			repaint();
		}
	}

	public void setStartParameters(Point start, Point origin) {
		this.start = start;
		this.end = start;
		this.xValue = 0;
		this.yValue = 0;
		this.origin = origin;
		repaint();
	}
	
	public void setEndParameters(Point end, float xValue, float yValue, Point origin) {
		this.end = end;
		this.xValue = xValue;
		this.yValue = yValue;
		this.origin = origin;
		repaint();
	}
	
}
