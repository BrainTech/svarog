/* ZoomBookAreaPlot.java created 2008-03-06
 *
 */

package org.signalml.app.view.book;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.geom.Rectangle2D;
import javax.swing.JComponent;
import org.signalml.app.util.IconUtils;
import org.signalml.util.Util;

/** ZoomBookAreaPlot
 *
 *
 * @author Michal Dobaczewski &copy; 2007-2008 CC Otwarte Systemy Komputerowe Sp. z o.o.
 */
public class ZoomBookAreaPlot extends JComponent {

	private static final float MIN_LABEL_WIDTH = 60F;

	private static final long serialVersionUID = 1L;

	private static final Color boxColor = new Color(255,255,187);

	private Rectangle frame;

	/*
	private float xValue;
	private float yValue;
	*/

	public ZoomBookAreaPlot() {
		super();
		setCursor(IconUtils.getCrosshairCursor());
	}

	@Override
	protected void paintComponent(Graphics gOrig) {

		if (frame == null) {
			return;
		}

		Graphics2D g = (Graphics2D) gOrig;

		g.setColor(Color.RED);

		g.draw(frame);

	}

	// TODO maybe modify & use
	@SuppressWarnings("unused")
	private void drawValueBox(Graphics2D g, float value, int x, int y, boolean under) {

		String text = Float.toString(value);
		Rectangle2D labelRect2D = g.getFont().getStringBounds(text, g.getFontRenderContext());
		int offset = 0;
		double width = labelRect2D.getWidth();
		if (width < MIN_LABEL_WIDTH) {
			offset = (int) Math.round(((MIN_LABEL_WIDTH-width)/2));
			width = MIN_LABEL_WIDTH;
		}
		Rectangle labelRect = new Rectangle(
			x,
			y,
			(int) Math.ceil(width),
			(int) Math.ceil(labelRect2D.getHeight())
		);
		labelRect.grow(3, 3);

		int realX;
		int realY;

		if (under) {
			realX = x - (labelRect.width  / 2);
			realY = y + 10 + (labelRect.height / 2);
		} else {
			realX = x + 12;
			realY = y - (labelRect.height / 2);
		}

		g.setColor(boxColor);
		g.fillRect(realX, realY, labelRect.width, labelRect.height);
		g.setColor(Color.BLACK);
		g.drawRect(realX, realY, labelRect.width-1, labelRect.height-1);

		g.drawString(text, realX+3+offset, realY+3-((int) labelRect2D.getY()));

	}

	@Override
	public boolean isOpaque() {
		return false;
	}

	public Rectangle getFrame() {
		return frame;
	}

	public void setFrame(Rectangle frame) {
		if (!Util.equalsWithNulls(this.frame, frame)) {
			this.frame = frame;
			repaint();
		}
	}

	/*
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

	public void setStartParameters(Point start) {
		this.start = start;
		this.end = start;
		this.xValue = 0;
		this.yValue = 0;
		repaint();
	}

	public void setEndParameters(Point end, float xValue, float yValue) {
		this.end = end;
		this.xValue = xValue;
		this.yValue = yValue;
		repaint();
	}
	*/

}
