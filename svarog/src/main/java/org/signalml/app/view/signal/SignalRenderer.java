package org.signalml.app.view.signal;

import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.geom.GeneralPath;
import static java.lang.Math.exp;
import java.util.Arrays;
import java.util.HashMap;
import sun.util.logging.resources.logging;

/**
 * Class responsible for signal rendering.
 * Objects of this class are instantiated and used by SignalPlot.
 * This is a stateless object with respect to the render() method.
 *
 * @author piotr.rozanski@braintech.pl
 */
public class SignalRenderer {

	private int pointCount = 0;
	private Point.Double[] points;
	private final GeneralPath shape = new GeneralPath(GeneralPath.WIND_EVEN_ODD, 2048);
	private HashMap<Integer, Double> dcOffsets = new HashMap<Integer, Double>();
	private boolean online = false;
	
	public void setOnline(boolean value)
	{
		online = value;
	}
	
	/**
	 * Perform signal rendering.
	 * Only the signal itself (time vs value) will be rendered,
	 * without any axes or additional labels.
	 *
	 * @param g  graphics object (canvas) to render the signal onto
	 * @param channel  channel number (first channel = 0)
	 * @param samples  array of sample values
	 * @param sampleCount  number of samples to use (sampleCount &le; samples.count)
	 * @param firstSample  actual index of the first sample relative to the start of the signal
	 * @param clip  clipping rectangle for rendering
	 * @param channelLevel  vertical offset of channel in pixels
	 * @param xScale  scale coefficient for time axis
	 * @param yScale  scale coefficient for value axis
	 * @param clampLimit  half of the channel height in pixels if signal is to be clamped, null otherwise
	 */
	public void render(Graphics2D g, int channel, double[] samples, int sampleCount, int firstSample, Rectangle clip, double channelLevel, double xScale, double yScale, Integer clampLimit, boolean dcOffsetRemove) {
		if (sampleCount > 2 * clip.width) {
			// we have much more samples than pixels,
			// so at each pixel we want to represent minimum and maximum values
			setPointCount(2 * clip.width);
			for (int p=0; p<clip.width; ++p) {
				int x = clip.x + p;
				// pixel at x corresponds to samples [i0..iN-1]
				int i0 = Math.max(0, (int) Math.round((x - 0.5) / xScale) - firstSample);
				int iN = Math.min(sampleCount-1, (int) Math.round((x + 0.5) / xScale) - firstSample);
				double min, max;
				min = max = samples[i0] * yScale;
				for (int i=i0+1; i<iN; ++i) {
					double sample = samples[i] * yScale;
					min = Math.min(min, sample);
					max = Math.max(max, sample);
				}
				int odd = p & 1;
				int even = 1 - odd;
				// if p is even, we draw a line from min to max
				// if p is odd, we draw a line from max to min
				points[2*p+odd].x = x;
				points[2*p+odd].y = min;
				points[2*p+even].x = x;
				points[2*p+even].y = max;
			}
		} else {
			// we have as much signal samples as pixels,
			// so we draw a line with all samples
			setPointCount(sampleCount);
			for (int p=0; p<sampleCount; ++p) {
				int i = p;
				points[p].x = (firstSample+i) * xScale;
				points[p].y = samples[i] * yScale;
			}
		}

		// drawing line from points[]
		shape.reset();
		double dcOffset = 0;
		if (dcOffsetRemove)
		{
			dcOffset = fastDCOffsetEstimator(channel);
		}
		shape.moveTo(points[0].x, translateY(channelLevel, points[0].y - dcOffset, clampLimit));
		for (int p=1; p<pointCount; ++p) {
			double y = translateY(channelLevel, points[p].y - dcOffset, clampLimit);
			shape.lineTo(points[p].x, y);
		}
		g.draw(shape);
	}

	private void setPointCount(int pointCount) {
		this.pointCount = pointCount;
		if (points == null || points.length < pointCount) {
			points = new Point.Double[pointCount];
			for (int i=0; i<pointCount; ++i) {
				points[i] = new Point.Double();
			}
		}
	}
	
	/**
	 * Finds meadian of 100 points (or less if Svarog window is smaller than 100 pixels or so)
	 * of the visible signal.
	 * Computing median for all points for high resolution screen might be
	 * excessively computationally expensive.
	 * @param index index of montage channel.
	 * @return estimate of DC-offset for channel in visible window
	 */
	private double fastDCOffsetEstimator(int channel)
	{
		int jump = (int)(pointCount/100.0);
		int nPointsUsed = 100;
		
		if (jump<1)
		{
			jump = 1;
			nPointsUsed = pointCount;
			
		}
		double[] pointsToUse = new double[nPointsUsed];
		
		for (int i=0;i<nPointsUsed;i++)
		{
			pointsToUse[i] = points[i*jump].y;
		}
		
		Arrays.sort(pointsToUse);
		double momentaryDCOffset = pointsToUse[nPointsUsed/2];
		if (!online)
		{
			//offline signal should be moved to baseline instantly
			//it doesn't bother us that it might "dance" or jitter
			return momentaryDCOffset;
		}
		//online signals should gradually move to baseline
		if (dcOffsets.containsKey(channel))
		{
			double oldMomentaryOffset = dcOffsets.get(channel);
			double newOffset = oldMomentaryOffset + (momentaryDCOffset-oldMomentaryOffset)*0.1;
			dcOffsets.put(channel, newOffset);
		}
		else
		{
			dcOffsets.put(channel, momentaryDCOffset);

		}
		return dcOffsets.get(channel);
	}
	
	private double translateY(double y0, double y, Integer clampLimit) {
		if (clampLimit != null) {
			if (y > clampLimit) {
				y = clampLimit;
			} else if (y < -clampLimit) {
				y = -clampLimit;
			}
		}
		return y0 - y;
	}

}
