/* ZoomSignalPlot.java created 2007-10-15
 *
 */

package org.signalml.app.view.signal;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Insets;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.font.TextLayout;
import java.awt.geom.GeneralPath;
import java.awt.geom.Point2D;
import javax.swing.JComponent;
import javax.swing.border.LineBorder;
import org.signalml.app.util.IconUtils;
import org.signalml.domain.signal.samplesource.MultichannelSampleSource;
import org.signalml.util.Util;

/** ZoomSignalPlot
 *
 *
 * @author Michal Dobaczewski &copy; 2007-2008 CC Otwarte Systemy Komputerowe Sp. z o.o.
 */
public class ZoomSignalPlot extends JComponent {

	private static final long serialVersionUID = 1L;

	private SignalPlot plot;

	private Point focusPoint;
	private int channel;

	private float factor = 2F;
	private double[] samples = null;

	private GeneralPath generalPath = new GeneralPath(GeneralPath.WIND_EVEN_ODD,50000);

	private final static Image zoomImage = IconUtils.loadClassPathImage("org/signalml/app/icon/zoom.png");

	public ZoomSignalPlot() {
		setBorder(new LineBorder(Color.LIGHT_GRAY,3,false));
		setCursor(IconUtils.getEmptyCursor());
	}

	@Override
	protected void paintComponent(Graphics gOrig) {

		if (plot == null || focusPoint == null) {
			return;
		}

		Graphics2D g = (Graphics2D) gOrig;

		Dimension size = getSize();
		Insets insets = getInsets();

		g.setColor(Color.WHITE);
		g.fillRect(0,0,size.width,size.height);
		size.width -= (insets.left + insets.right);
		size.height -= (insets.top + insets.bottom);

		g.setColor(Color.BLACK);

		MultichannelSampleSource sampleSource = plot.getSignalOutput();
		Point2D.Float signalFocus = plot.toSignalSpace(focusPoint);
		int pixelPerChannel = plot.getPixelPerChannel();

		int invisibleChannels = plot.getInvisibleChannelsBeforeChannel(channel);
		int channelCenter = (channel - invisibleChannels)*pixelPerChannel + pixelPerChannel/2; //

		double length = (((size.width) / plot.getPixelPerSecond()) / factor);
		double minTime = signalFocus.x - length/2;
		double maxTime = minTime + length;
		float samplingFrequency = plot.getSamplingFrequency();
		int firstSample = (int) Math.max(0, Math.floor(minTime * samplingFrequency));
		int lastSample = (int) Math.min(plot.getMaxSampleCount()-1, Math.ceil(maxTime * samplingFrequency));
		int sampleCnt = lastSample - firstSample;

		if (samples == null || samples.length < sampleCnt) {
			samples = new double[sampleCnt];
		}

		if (channel >= sampleSource.getChannelCount())
			return;

		try {
			sampleSource.getSamples(channel, samples, firstSample, sampleCnt, 0);
		} catch (RuntimeException ex) {
			setVisible(false);
			throw ex;
		}

		int i;
		double x, y;
		double pixelPerValue = plot.getPixelPerValue(channel) * factor;
		double timeZoomFactor = plot.getTimeZoomFactor() * factor;

		int centerOffset = Math.round((focusPoint.y - channelCenter) * factor);

		int leftOffset;
		if (minTime < 0) {
			leftOffset = insets.left + size.width - (int)((sampleCnt-1) * timeZoomFactor);
		} else {
			leftOffset = insets.left;
		}

		x = leftOffset;
		y = size.height/2 - samples[0] * pixelPerValue - centerOffset;

		int ix, iy, lastix, lastiy;

		ix = (int) StrictMath.floor(x+0.5);
		iy = (int) StrictMath.floor(y+0.5);

		generalPath.reset();
		generalPath.moveTo(x, y);

		lastix = ix;
		lastiy = iy;

		for (i=1; i<sampleCnt; i++) {

			y = size.height/2 - samples[i] * pixelPerValue - centerOffset;
			x = leftOffset + timeZoomFactor * i;

			ix = (int) StrictMath.floor(x+0.5);
			iy = (int) StrictMath.floor(y+0.5);

			if (lastix != ix || lastiy != iy) {
				generalPath.lineTo(ix, iy);
			}

			lastix = ix;
			lastiy = iy;

		}

		g.draw(generalPath);

		g.drawImage(zoomImage,insets.left+1,insets.top+1,null);

		String label = Float.toString(factor) + "x " + sampleSource.getLabel(channel);
		TextLayout textLayout = new TextLayout(label,g.getFont(),g.getFontRenderContext());
		Point labelPoint = new Point(insets.left+1+zoomImage.getWidth(null)+3, insets.top+1+zoomImage.getHeight(null)/2);
		Rectangle labelRect = textLayout.getPixelBounds(
								  null,
								  labelPoint.x,
								  labelPoint.y

							  );
		labelPoint.translate(0, labelRect.height/2);
		labelRect.translate(0, labelRect.height/2);
		g.setColor(Color.WHITE);
		g.fill(labelRect);
		g.setColor(Color.BLACK);
		textLayout.draw(
			g,
			labelPoint.x,
			labelPoint.y
		);

	}

	@Override
	public boolean isDoubleBuffered() {
		return true;
	}

	@Override
	public boolean isOpaque() {
		return true;
	}

	@Override
	public Dimension getPreferredSize() {
		Dimension size = super.getPreferredSize();
		if (size != null) {
			return size;
		}
		return getMinimumSize();
	}

	@Override
	public Dimension getMinimumSize() {
		return new Dimension(100,100);
	}

	@Override
	public Dimension getMaximumSize() {
		return getPreferredSize();
	}

	public Point getFocusPoint() {
		return focusPoint;
	}

	public void setFocusPoint(Point focusPoint) {
		if (!Util.equalsWithNulls(this.focusPoint, focusPoint)) {
			this.focusPoint = focusPoint;
			repaint();
		}
	}

	public int getChannel() {
		return channel;
	}

	public void setChannel(int channel) {
		if (this.channel != channel) {
			this.channel = channel;
			repaint();
		}
	}

	public SignalPlot getPlot() {
		return plot;
	}

	public void setPlot(SignalPlot plot) {
		if (this.plot != plot) {
			this.plot = plot;
			repaint();
		}
	}

	public void setParameters(SignalPlot plot, Point focusPoint, int channel) {
		this.plot = plot;
		this.focusPoint = focusPoint;
		this.channel = channel;
		repaint();
	}

	public void setParameters(Point focusPoint, int channel) {
		this.focusPoint = focusPoint;
		this.channel = channel;
		repaint();
	}

	public float getFactor() {
		return factor;
	}

	public void setFactor(float factor) {
		if (this.factor != factor) {
			this.factor = factor;
			repaint();
		}
	}

}
