/* SignalPlotRowHeader.java created 2007-10-15
 *
 */

package org.signalml.app.view.signal;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.geom.AffineTransform;
import java.awt.geom.Rectangle2D;

import javax.swing.JComponent;

import org.signalml.domain.signal.MultichannelSampleSource;

/** SignalPlotRowHeader
 *
 *
 * @author Michal Dobaczewski &copy; 2007-2008 CC Otwarte Systemy Komputerowe Sp. z o.o.
 */
public class SignalPlotRowHeader extends JComponent {

	private static final long serialVersionUID = 1L;

	private static final Dimension MINIMUM_SIZE = new Dimension(0,0);

	private boolean calculated = false;

	private Font normalFont;
	private Font verticalFont;

	private int channelCount;
	private double pixelPerValue;
	private int pixelPerChannel;
	private int[] channelLevel;

	private double pixelPerRowUnit;
	private String rowUnitLabel;

	private Rectangle2D unitLabelBounds;
	private Rectangle2D[] channelLabelBounds;
	private int maxChannelLabelWidth = 0;

	private SignalPlot plot;
	private MultichannelSampleSource labelSource;

	private boolean active = true;

	public SignalPlotRowHeader(SignalPlot plot) {
		super();
		this.plot = plot;
	}

	public void reset() {
		calculated = false;
	}

	private void calculate(Graphics2D g) {

		if (calculated) {
			return;
		}

		int i;

		channelCount = plot.getChannelCount();
		pixelPerValue = plot.getPixelPerValue();
		pixelPerChannel = plot.getPixelPerChannel();
		labelSource = plot.getSignalOutput();
		channelLevel = plot.getChannelLevel();

		pixelPerRowUnit = pixelPerValue;

		StringBuilder sb = new StringBuilder("1");
		while (pixelPerRowUnit <= 5) {
			pixelPerRowUnit *= 10;
			sb.append("0");
		}
		sb.append(" uV");
		rowUnitLabel = sb.toString();

		normalFont = g.getFont();
		verticalFont = normalFont.deriveFont(AffineTransform.getQuadrantRotateInstance(1));

		unitLabelBounds = verticalFont.getStringBounds(rowUnitLabel, g.getFontRenderContext());

		double max = 0;
		channelLabelBounds = new Rectangle2D[channelCount];
		for (i=0; i < channelCount; i++) {
			channelLabelBounds[i] = normalFont.getStringBounds(labelSource.getLabel(i), g.getFontRenderContext());
			if (max < channelLabelBounds[i].getWidth())  {
				max = channelLabelBounds[i].getWidth();
			}
		}
		maxChannelLabelWidth = (int) Math.ceil(max);

		calculated = true;

	}

	@Override
	protected void paintComponent(Graphics gOrig) {

		Graphics2D g = (Graphics2D)gOrig;
		calculate(g);

		Point viewportPoint = plot.getViewport().getViewPosition();
		Dimension viewportSize = plot.getViewport().getExtentSize();
		Dimension size = getSize();

		Rectangle clip = g.getClipBounds();

		g.setColor(getBackground());
		g.fillRect(clip.x,clip.y,clip.width,clip.height);

		int clipEndY = clip.y + clip.height - 1;

		size.width -= SignalPlot.SCALE_TO_SIGNAL_GAP;

		int i;
		int y;

		// this draws value ticks
		g.setColor(Color.GRAY);
		g.drawLine(size.width-4, viewportPoint.y, size.width-4, viewportPoint.y + viewportSize.height);
		int tickCnt = 1 + ((int)(((float)(viewportSize.height+1))  / pixelPerRowUnit));
		for (i=0; i<tickCnt; i++) {
			y = viewportPoint.y + ((int)(i*pixelPerRowUnit));
			g.drawLine(size.width-3, y, size.width-1, y);
		}

		// this draws channel labels
		int startChannel = (int) Math.max(0, Math.floor(clip.y / pixelPerChannel));
		int endChannel = (int) Math.min(channelCount-1, Math.ceil(clipEndY / pixelPerChannel));

		if (active) {
			g.setColor(Color.BLUE);
		} else {
			g.setColor(Color.GRAY);
		}
		for (i=startChannel; i <= endChannel; i++) {
			g.drawString(labelSource.getLabel(i), 3, channelLevel[i] + ((int) -channelLabelBounds[i].getY()/2));
		}

		g.setColor(Color.GRAY);
		g.setFont(verticalFont);
		g.drawString(rowUnitLabel, size.width+((float)unitLabelBounds.getY())-5, viewportPoint.y+3);

	}

	public int getPreferredWidth() {
		calculate((Graphics2D) getGraphics());
		return maxChannelLabelWidth + (int) Math.ceil(unitLabelBounds.getHeight() + SignalPlot.SCALE_TO_SIGNAL_GAP + 3 + 3 + 2);
	}

	@Override
	public Dimension getPreferredSize() {
		// preferred widths must be coordinated!
		calculate((Graphics2D) getGraphics());
		return new Dimension(plot.getView().getSynchronizedRowHeaderWidth(),channelCount*pixelPerChannel);
	}

	@Override
	public Dimension getMaximumSize() {
		return getPreferredSize();
	}

	@Override
	public Dimension getMinimumSize() {
		return MINIMUM_SIZE;
	}

	@Override
	public boolean isOpaque() {
		return true;
	}

	public SignalPlot getPlot() {
		return plot;
	}

	public boolean isActive() {
		return active;
	}

	public void setActive(boolean active) {
		if (this.active != active) {
			this.active = active;
			repaint();
		}
	}

}
