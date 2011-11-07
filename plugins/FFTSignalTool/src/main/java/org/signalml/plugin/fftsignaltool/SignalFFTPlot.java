/* SignalFFTPlot.java created 2007-12-16
 *
 */
package org.signalml.plugin.fftsignaltool;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Insets;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.geom.Rectangle2D;

import javax.swing.JComponent;
import javax.swing.border.LineBorder;

import org.apache.log4j.Logger;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.LogAxis;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.axis.NumberTickUnit;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.chart.renderer.xy.XYSplineRenderer;
import org.jfree.chart.title.TextTitle;
import org.jfree.data.xy.DefaultXYDataset;
import org.signalml.exception.SanityCheckException;
import org.signalml.plugin.export.NoActiveObjectException;
import org.signalml.plugin.export.SvarogAccess;
import org.signalml.plugin.export.signal.ChannelSamples;
import org.signalml.plugin.export.signal.SvarogAccessSignal;
import org.signalml.plugin.export.view.ExportedSignalPlot;
import org.signalml.plugin.fft.export.FourierTransform;
import org.signalml.plugin.fft.export.WindowType;
import org.signalml.util.Util;
import static org.signalml.plugin.fftsignaltool.FFTSignalTool._;
import static org.signalml.plugin.fftsignaltool.FFTSignalTool._R;

/**
 * Plot on which the power spectrum of the signal (fragment near cursor) is
 * displayed:
 * <ul><li>
 * The parameters of the display are stored in {@link SignalFFTSettings}.</li>
 * <li>The power spectrum is calculated using the {@link FourierTransform FFT}.
 * </li></ul> 
 * 
 * @author Michal Dobaczewski &copy; 2007-2008 CC Otwarte Systemy Komputerowe
 *         Sp. z o.o., Marcin Szumski
 */
public class SignalFFTPlot extends JComponent {

	/**
	 * serialization constant
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * the logger
	 */
	protected static final Logger logger = Logger
			.getLogger(SignalFFTPlot.class);

	/**
	 * how many FFT result points to cutoff at the left - discards the mean to
	 * improve plot clarity
	 */
	private static final int LEFT_CUTOFF = 1;

	/**
	 * the {@link ExportedSignalPlot signal plot} for which this FFT plot is
	 * calculated
	 */
	private ExportedSignalPlot plot;

	/**
	 * the {@link SvarogAccessSignal access} to Svarog logic
	 */
	private SvarogAccessSignal signalAccess;

	/**
	 * the currently clicked point on the signal plot
	 */
	private Point focusPoint;
	/**
	 * the number of the channel for which the FFT is drawn
	 */
	private int channel;

	/**
	 * the size of this plot
	 */
	private Dimension plotSize;
	/**
	 * the number of samples in the window
	 */
	private int windowWidth;
	/**
	 * the {@link WindowType type} of the window function
	 */
	private WindowType windowType;
	/**
	 * the parameter of the window function
	 */
	private double windowParameter;
	/**
	 * boolean which tells if the axis with logarithmic scale should be used
	 * ({@code true}) or the normal scale should be used
	 */
	private boolean logarithmic;
	/**
	 * boolean which tells if the points should be connected using splines
	 * ({@code true}) or lines ({@code false})
	 */
	private boolean spline;
	/**
	 * boolean which tells if the chart should be antialiased
	 */
	private boolean antialias;
	/**
	 * boolean which tells if the title of this plot should be displayed
	 */
	private boolean titleVisible;
	/**
	 * boolean which tells if the labels with frequencies should be displayed
	 */
	private boolean frequencyAxisLabelsVisible;
	/**
	 * boolean which tells if the labels with power values should be displayed
	 */
	private boolean powerAxisLabelsVisible;

	/**
	 * {@link SignalFFTSettings settings} how the power spectrum should be
	 * displayed
	 */
	private SignalFFTSettings fftSettings = new SignalFFTSettings();

	/**
	 * boolean which tells if the power spectrum has already been calculated
	 * for the current parameters (if {@code true} - the calculated spectrum
	 * will be stored in {@link #powerSpectrum})
	 */
	private boolean calculated = false;

	/**
	 * the stored samples of the signal that are used to calculate power
	 * spectrum 
	 */
	private double[] samples = null;

	/**
	 * the calculated power spectrum - the array with two rows:
	 * <ul>
	 * <li>first row ({@code powerSpectrum[0][i]} - frequencies,</li>
	 * <li>second row ({@code powerSpectrum[1][i]}) - estimates of the
	 * power spectrum for these frequencies</li>
	 */
	private double[][] powerSpectrum;

	/**
	 * the axis with frequencies
	 */
	private NumberAxis xAxis;

	/**
	 * the axis with powers with normal scale
	 */
	private NumberAxis normalYAxis;
	/**
	 * the axis with powers with logarithmic scale
	 */
	private LogAxis logYAxis;

	/**
	 * the renderer that connects points with lines
	 */
	private XYLineAndShapeRenderer normalRenderer;
	/**
	 * the renderer that connects points using splines
	 */
	private XYSplineRenderer splineRenderer;

	/**
	 * the actual plot with power spectrum
	 */
	private XYPlot powerSpectrumPlot;
	/**
	 * the chart with {@link #powerSpectrumPlot}
	 */
	private JFreeChart powerSpectrumChart;

	/**
	 * the font used in title
	 */
	private Font titleFont;
	/**
	 * the message of the error that has occured
	 */
	private String error;

	/**
	 * Constructor. Sets the source of messages, title font and border.
	 */
	public  SignalFFTPlot() {
		super();
		setBorder(new LineBorder(Color.LIGHT_GRAY, 3, false));
		titleFont = new Font(Font.DIALOG, Font.PLAIN, 12);
	}

	/**
	 * Calculates the powers spectrum and displays it:
	 * <ul>
	 * <li>obtains the samples near the selected point,</li>
	 * <li>{@link FourierTransform#powerSpectrumReal(double[], double)
	 * calculates} the power spectrum,</li>
	 * <li>displays the result on the chart.</li>
	 * </ul>
	 */
	private void calculate() {

		if (calculated) {
			return;
		}
		calculated = true;

		error = null;

		double timeZoomFactor = plot.getTimeZoomFactor();
		int firstSample = (int) Math.floor((focusPoint.x / timeZoomFactor)
				- windowWidth / 2);
		if (firstSample < 0) {
			error = _("Not enough signal points");
		}
		int lastSample = firstSample + windowWidth;
		if (lastSample >= plot.getMaxSampleCount()) {
			error = _("Not enough signal points");
		}
		if (error != null) {
			return;
		}
		int sampleCnt = lastSample - firstSample;
		if (sampleCnt != windowWidth) {
			throw new SanityCheckException(
					"Sanity failed - sample count different than window size");
		}

		if (samples == null || samples.length != sampleCnt) {
			samples = new double[sampleCnt];
		}

		ChannelSamples channelSamples = null;

		try {
			channelSamples = signalAccess.getActiveProcessedSignalSamples(
					channel, firstSample, sampleCnt);
			samples = channelSamples.getSamples();
		} catch (RuntimeException ex) {
			setVisible(false);
			throw ex;
		} catch (NoActiveObjectException e) {
			setVisible(false);
			throw new RuntimeException(e);
		}

		try {

			logger.debug("Samples requested [" + sampleCnt + "] array size ["
					+ samples.length + "]");

			FourierTransform fourierTransform = new FourierTransform();
			fourierTransform.setWindowType(windowType, windowParameter);

			powerSpectrum = fourierTransform.powerSpectrumReal(samples,
					((float) 1) / channelSamples.getSamplingFrequency());
			if (powerSpectrum == null) {
				throw new NullPointerException("Null spectrum returned");
			}

			logger.debug("PS[0] size [" + powerSpectrum[0].length
					+ "] PS[1] size [" + powerSpectrum[1].length + "]");

		} catch (RuntimeException ex) {
			setVisible(false);
			throw ex;
		}

		if (powerSpectrumPlot == null) {

			xAxis = new NumberAxis();
			xAxis.setAutoRange(false);
			/* xAxis.setTickUnit(new NumberTickUnit(4)); */

			normalRenderer = new XYLineAndShapeRenderer(true, false);

			splineRenderer = new XYSplineRenderer();
			splineRenderer.setSeriesShapesVisible(0, false);
			splineRenderer.setPrecision(10);

			powerSpectrumPlot = new XYPlot(null, xAxis, null, null);

			normalYAxis = new NumberAxis();
			normalYAxis.setAutoRange(false);

			logYAxis = new LogAxis();
			logYAxis.setAutoRange(false);

		}

		if (powerSpectrumChart == null) {

			powerSpectrumChart = new JFreeChart(null, titleFont,
					powerSpectrumPlot, false);
			powerSpectrumChart.setBorderVisible(false);
			powerSpectrumChart.setBackgroundPaint(Color.WHITE);

		}

		double pixelPerSecond = plot.getPixelPerSecond();
		float minTime = (float) ((firstSample * timeZoomFactor) / pixelPerSecond);
		float maxTime = (float) ((lastSample * timeZoomFactor) / pixelPerSecond);

		StringBuilder minTimeSb = new StringBuilder(20);
		Util.addTime(minTime, minTimeSb);

		StringBuilder maxTimeSb = new StringBuilder(20);
		Util.addTime(maxTime, maxTimeSb);

		String title = _R("FFT over {0} points {1} - {2} ({3})",
				  windowWidth, minTimeSb.toString(),
				  maxTimeSb.toString(), channelSamples.getName());

		if (titleVisible) {
			powerSpectrumChart.setTitle(new TextTitle(title, titleFont));
		} else {
			powerSpectrumChart.setTitle((String) null);
		}
		powerSpectrumChart.setAntiAlias(antialias);

		int startIndex = LEFT_CUTOFF;
		int endIndex = powerSpectrum[1].length;

		{
			// FIXME: check
			double rangeStart = powerSpectrum[0][LEFT_CUTOFF];
			double rangeEnd = channelSamples.getSamplingFrequency() / 2.0D;
			double rangeSize = rangeEnd - rangeStart;

			double oldRangeStart = rangeStart;

			if (fftSettings.getVisibleRangeStart() > rangeStart) {
				rangeStart = fftSettings.getVisibleRangeStart();
			}
			if (fftSettings.getVisibleRangeEnd() < rangeEnd) {
				rangeEnd = fftSettings.getVisibleRangeEnd();
			}

			if (fftSettings.getMaxLabelCount() > 0) {
				double dist = rangeEnd - rangeStart;
				double step = dist / fftSettings.getMaxLabelCount();
				if (step < 1)
					step = 1;
				xAxis.setTickUnit(new NumberTickUnit(Math.round(step)));
			}

			if (rangeEnd < rangeStart + 1) {
				rangeEnd = rangeStart + 1;
			}

			if (fftSettings.isScaleToView()) {

				double sampleDist = endIndex - startIndex;

				int shift = (int) (((rangeEnd - oldRangeStart) / rangeSize) * sampleDist);
				endIndex = startIndex + shift;

				shift = (int) (((rangeStart - oldRangeStart) / rangeSize) * sampleDist);
				startIndex += shift;

			}

			xAxis.setRange(rangeStart, rangeEnd);
		}

		double max = 0;
		double min = Double.MAX_VALUE;
		for (int i = startIndex; i < endIndex; i++) {
			max = Math.max(max, powerSpectrum[1][i]);
			min = Math.min(min, powerSpectrum[1][i]);
		}
		max *= 1.15; // scale up by 15% as per ZFB request (related to spline
						// overshooting points).

		if (logarithmic) {
			logYAxis.setTickLabelsVisible(powerAxisLabelsVisible);
			logYAxis.setRange(min, max);
			powerSpectrumPlot.setRangeAxis(logYAxis);
		} else {
			normalYAxis.setTickLabelsVisible(powerAxisLabelsVisible);
			normalYAxis.setRange(0, max);
			powerSpectrumPlot.setRangeAxis(normalYAxis);
		}

		xAxis.setTickLabelsVisible(frequencyAxisLabelsVisible);

		if (spline) {
			splineRenderer.setSeriesPaint(0, Color.RED);
			powerSpectrumPlot.setRenderer(splineRenderer);
		} else {
			normalRenderer.setSeriesPaint(0, Color.RED);
			powerSpectrumPlot.setRenderer(normalRenderer);
		}

		DefaultXYDataset dataset = new DefaultXYDataset();
		dataset.addSeries("data", powerSpectrum);
		powerSpectrumPlot.setDataset(dataset);

	}

	/**
	 * Paints this plot:
	 * <ul>
	 * <li>if the error occurred while calculating the power spectrum - draws
	 * the message with the error,</li>
	 * <li>otherwise draw the chart with the power spectrum.</li></ul>
	 */
	@Override
	protected void paintComponent(Graphics gOrig) {

		if (plot == null || focusPoint == null) {
			return;
		}

		calculate();

		Graphics2D g = (Graphics2D) gOrig;

		Dimension size = getSize();
		Insets insets = getInsets();

		g.setColor(Color.WHITE);
		g.fillRect(0, 0, size.width, size.height);
		size.width -= (insets.left + insets.right);
		size.height -= (insets.top + insets.bottom);

		if (error != null) {

			g.setColor(Color.RED);

			FontMetrics fontMetrics = g.getFontMetrics();
			Rectangle2D stringBounds = fontMetrics.getStringBounds(error, g);

			int width = (int) stringBounds.getWidth();
			int height = (int) stringBounds.getHeight();

			int x;
			if (width > size.width) {
				x = 0;
			} else {
				x = (size.width - width) / 2;
			}

			g.drawString(error, insets.left + x, insets.top
					+ (size.height - height) / 2 + fontMetrics.getAscent());
			return;

		}

		powerSpectrumChart.draw(g, new Rectangle(insets.left, insets.top,
				size.width, size.height));

	}

	/**
	 * @return true
	 */
	@Override
	public boolean isDoubleBuffered() {
		return true;
	}

	/**
	 * @return true
	 */
	@Override
	public boolean isOpaque() {
		return true;
	}

	/**
	 * @return the size of this plot
	 */
	@Override
	public Dimension getPreferredSize() {
		return plotSize;
	}

	/**
	 * @return 200x100 pixel
	 */
	@Override
	public Dimension getMinimumSize() {
		return new Dimension(200, 100);
	}

	/**
	 * @return the size of this plot
	 */
	@Override
	public Dimension getMaximumSize() {
		return getPreferredSize();
	}

	/**
	 * Returns the selected point.
	 * @return the selected point
	 */
	public Point getFocusPoint() {
		return focusPoint;
	}

	/**
	 * Sets the selected point and, if it has changed, invalidates the
	 * calculated power spectrum.
	 * @param focusPoint the selected point
	 */
	public void setFocusPoint(Point focusPoint) {
		if (!Util.equalsWithNulls(this.focusPoint, focusPoint)) {
			this.focusPoint = focusPoint;
			calculated = false;
			repaint();
		}
	}

	/**
	 * Returns the number of the channel for which the FFT is drawn.
	 * @return the number of the channel for which the FFT is drawn
	 */
	public int getChannel() {
		return channel;
	}

	/**
	 * Sets the number of the channel for which the FFT is drawn and if it has
	 * changed invalidates the calculated power spectrum.
	 * @param channel the number of the channel for which the FFT is drawn
	 */
	public void setChannel(int channel) {
		if (this.channel != channel) {
			this.channel = channel;
			calculated = false;
			repaint();
		}
	}

	/**
	 * Returns the signal plot for which this FFT plot is calculated.
	 * @return the signal plot for which this FFT plot is calculated
	 */
	public ExportedSignalPlot getPlot() {
		return plot;
	}

	/**
	 * Sets the signal plot for which this FFT plot is calculated and if it
	 * has changed invalidates the calculated power spectrum.
	 * @param plot the signal plot for which this FFT plot is calculated
	 */
	public void setPlot(ExportedSignalPlot plot) {
		if (this.plot != plot) {
			this.plot = plot;
			calculated = false;
			repaint();
		}
	}

	/**
	 * Sets the following parameters and invalidated the calculated power
	 * spectrum.
	 * @param plot the signal plot for which this FFT plot is calculated
	 * @param focusPoint the selected point
	 * @param channel the number of the channel for which the FFT is calculated
	 */
	public void setParameters(ExportedSignalPlot plot, Point focusPoint,
			int channel) {
		this.plot = plot;
		this.focusPoint = focusPoint;
		this.channel = channel;
		calculated = false;
		repaint();
	}

	/**
	 * Sets the following parameters and invalidated the calculated power
	 * spectrum.
	 * @param focusPoint the selected point
	 * @param channel the number of the channel for which the FFT is calculated
	 */
	public void setParameters(Point focusPoint, int channel) {
		this.focusPoint = focusPoint;
		this.channel = channel;
		calculated = false;
		repaint();
	}

	/**
	 * Returns the size of this plot.
	 * @return the size of this plot
	 */
	public Dimension getPlotSize() {
		return plotSize;
	}

	/**
	 * Sets the size of this plot and invalidates the calculated power
	 * spectrum.
	 * @param plotSize the size of this plot
	 */
	public void setPlotSize(Dimension plotSize) {
		if (plotSize == null) {
			throw new NullPointerException("No size");
		}
		if (!plotSize.equals(this.plotSize)) {
			this.plotSize = plotSize;
			calculated = false;
			revalidate();
			repaint();
		}
	}

	/**
	 * Returns the number of samples in the window.
	 * @return the number of samples in the window
	 */
	public int getWindowWidth() {
		return windowWidth;
	}

	/**
	 * Sets the number of samples in the window and invalidates the calculated
	 * power spectrum.
	 * @param windowWidth the number of samples in the window
	 */
	public void setWindowWidth(int windowWidth) {
		if (this.windowWidth != windowWidth) {
			this.windowWidth = windowWidth;
			calculated = false;
			repaint();
		}
	}

	/**
	 * Returns the {@link WindowType type} of the window function.
	 * @return the type of the window function
	 */
	public WindowType getWindowType() {
		return windowType;
	}

	/**
	 * Sets the {@link WindowType type} of the window function and invalidates
	 * the calculated power spectrum.
	 * @param windowType the type of the window function
	 */
	public void setWindowType(WindowType windowType) {
		if (this.windowType != windowType) {
			this.windowType = windowType;
			calculated = false;
			repaint();
		}
	}

	/**
	 * Returns the parameter of the window function.
	 * @return the parameter of the window function
	 */
	public double getWindowParameter() {
		return windowParameter;
	}

	/**
	 * Sets the parameter of the window function and invalidates
	 * the calculated power spectrum.
	 * @param windowParameter the parameter of the window function
	 */
	public void setWindowParameter(double windowParameter) {
		if (this.windowParameter != windowParameter) {
			this.windowParameter = windowParameter;
			calculated = false;
			repaint();
		}
	}

	/**
	 * Returns if the axis with logarithmic scale should be used
	 * ({@code true}) or the normal scale should be used.
	 * @return if the axis with logarithmic scale should be used
	 */
	public boolean isLogarithmic() {
		return logarithmic;
	}

	/**
	 * Sets if the axis with logarithmic scale should be used
	 * ({@code true}) or the normal scale should be used and if the value
	 * has changed invalidates the calculated power spectrum.
	 * @param logarithmic if the axis with logarithmic scale should be used
	 */
	public void setLogarithmic(boolean logarithmic) {
		if (this.logarithmic != logarithmic) {
			this.logarithmic = logarithmic;
			calculated = false;
			repaint();
		}
	}

	/**
	 * Returns if the points should be connected using splines
	 * ({@code true}) or lines ({@code false})
	 * @return if the points should be connected using splines
	 * ({@code true}) or lines ({@code false})
	 */
	public boolean isSpline() {
		return spline;
	}

	/**
	 * Returns if the points should be connected using splines
	 * ({@code true}) or lines ({@code false}).
	 * If the value has changed invalidates the calculated power spectrum.
	 * @param spline if the points should be connected using splines
	 * ({@code true}) or lines ({@code false})
	 */
	public void setSpline(boolean spline) {
		if (this.spline != spline) {
			this.spline = spline;
			calculated = false;
			repaint();
		}
	}

	/**
	 * Returns if the chart should be antialiased.
	 * @return if the chart should be antialiased
	 */
	public boolean isAntialias() {
		return antialias;
	}

	/**
	 * Sets if the chart should be antialiased.
	 * If the value has changed invalidates the calculated power spectrum.
	 * @param antialias if the chart should be antialiased
	 */
	public void setAntialias(boolean antialias) {
		if (this.antialias != antialias) {
			this.antialias = antialias;
			calculated = false;
			repaint();
		}
	}

	/**
	 * Returns if the title of this plot should be displayed.
	 * @return if the title of this plot should be displayed
	 */
	public boolean isTitleVisible() {
		return titleVisible;
	}

	/**
	 * Sets if the title of this plot should be displayed
	 * If the value has changed invalidates the calculated power spectrum.
	 * @param titleVisible if the title of this plot should be displayed
	 */
	public void setTitleVisible(boolean titleVisible) {
		if (this.titleVisible != titleVisible) {
			this.titleVisible = titleVisible;
			calculated = false;
			repaint();
		}
	}

	/**
	 * Returns if the labels with frequencies should be displayed.
	 * @return if the labels with frequencies should be displayed
	 */
	public boolean isFrequencyAxisLabelsVisible() {
		return frequencyAxisLabelsVisible;
	}

	/**
	 * Sets if the labels with frequencies should be displayed
	 * If the value has changed invalidates the calculated power spectrum.
	 * @param frequencyAxisLabelsVisible if the labels with frequencies should
	 * be displayed
	 */
	public void setFrequencyAxisLabelsVisible(boolean frequencyAxisLabelsVisible) {
		if (this.frequencyAxisLabelsVisible != frequencyAxisLabelsVisible) {
			this.frequencyAxisLabelsVisible = frequencyAxisLabelsVisible;
			calculated = false;
			repaint();
		}
	}

	/**
	 * Returns if the labels with power values should be displayed.
	 * @return if the labels with power values should be displayed
	 */
	public boolean isPowerAxisLabelsVisible() {
		return powerAxisLabelsVisible;
	}

	/**
	 * Sets if the labels with power values should be displayed
	 * If the value has changed invalidates the calculated power spectrum.
	 * @param powerAxisLabelsVisible if the labels with power values should
	 * be displayed
	 */
	public void setPowerAxisLabelsVisible(boolean powerAxisLabelsVisible) {
		if (this.powerAxisLabelsVisible != powerAxisLabelsVisible) {
			this.powerAxisLabelsVisible = powerAxisLabelsVisible;
			calculated = false;
			repaint();
		}
	}

	/**
	 * Sets the {@link SignalFFTSettings settings} how the power spectrum should be
	 * displayed.
	 * Invalidates the calculated power spectrum.
	 * @param settings the settings how the power spectrum should be
	 * displayed
	 */
	public void setSettings(SignalFFTSettings settings) {
		fftSettings = settings;
		plotSize = settings.getPlotSize();
		windowWidth = settings.getWindowWidth();
		windowType = settings.getWindowType();
		windowParameter = settings.getWindowParameter();
		logarithmic = settings.isLogarithmic();
		antialias = settings.isAntialias();
		spline = settings.isSpline();
		titleVisible = settings.isTitleVisible();
		frequencyAxisLabelsVisible = settings.isFrequencyAxisLabelsVisible();
		powerAxisLabelsVisible = settings.isPowerAxisLabelsVisible();
		calculated = false;
		revalidate();
		repaint();
	}

	/**
	 * Sets the {@link SvarogAccessSignal access} to Svarog logic.
	 * @param access the access to Svarog logic
	 */
	public void setSvarogAccess(SvarogAccess access) {
		signalAccess = access.getSignalAccess();
	}

}
