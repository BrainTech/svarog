/* SignalFFTPlot.java created 2007-12-16
 * 
 */
package org.signalml.app.view.signal;

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
import org.signalml.app.config.SignalFFTSettings;
import org.signalml.domain.signal.MultichannelSampleSource;
import org.signalml.exception.SanityCheckException;
import org.signalml.fft.WindowType;
import org.signalml.util.Util;
import org.springframework.context.support.MessageSourceAccessor;

import flanagan.math.FourierTransform;

/** SignalFFTPlot
 *
 * 
 * @author Michal Dobaczewski &copy; 2007-2008 CC Otwarte Systemy Komputerowe Sp. z o.o.
 */
public class SignalFFTPlot extends JComponent {

	private static final long serialVersionUID = 1L;
	
	protected static final Logger logger = Logger.getLogger(SignalFFTPlot.class);
	
	// how many FFT result points to cutoff at the left - discards the mean to improve plot clarity
	private static final int LEFT_CUTOFF = 1;
	
	private SignalPlot plot;
	
	private Point focusPoint;
	private int channel;
	
	private Dimension plotSize;
	private int windowWidth;
	private WindowType windowType;
	private double windowParameter;
	private boolean logarithmic;
	private boolean spline;
	private boolean antialias;
	private boolean titleVisible;
	private boolean frequencyAxisLabelsVisible;
	private boolean powerAxisLabelsVisible;

	private SignalFFTSettings fftSettings = new SignalFFTSettings();
	
	private boolean calculated = false;
	
	private double[] samples = null;
	
	private double[][] powerSpectrum;
	
	private NumberAxis xAxis;

	private NumberAxis normalYAxis;
	private LogAxis logYAxis;
	
	private XYLineAndShapeRenderer normalRenderer;
	private XYSplineRenderer splineRenderer;
	
	private XYPlot powerSpectrumPlot;
	private JFreeChart powerSpectrumChart;
							
	private Font titleFont;
	private String error;
	
	private MessageSourceAccessor messageSource;
	
	public SignalFFTPlot(MessageSourceAccessor messageSource) {
		super();
		this.messageSource = messageSource;
		setBorder(new LineBorder(Color.LIGHT_GRAY,3,false));
		titleFont = new Font( Font.DIALOG, Font.PLAIN, 12 );
	}

	private void calculate() {
		
		if( calculated ) {
			return;
		}
		calculated = true;
		
		error = null;
		
		MultichannelSampleSource sampleSource = plot.getSignalOutput();
		double timeZoomFactor = plot.getTimeZoomFactor();
		int firstSample = (int) Math.floor( ( focusPoint.x / timeZoomFactor ) - windowWidth/2 );
		if( firstSample < 0 ) {
			error = messageSource.getMessage("fft.notEnoughSignalPoints");
		}
		int lastSample = firstSample + windowWidth;		
		if( lastSample >= plot.getMaxSampleCount() ) {
			error = messageSource.getMessage("fft.notEnoughSignalPoints");			
		}
		if( error != null ) {
			return;
		}
		int sampleCnt = lastSample - firstSample;
		if( sampleCnt != windowWidth ) {
			throw new SanityCheckException( "Sanity failed - sample count different than window size" );
		}
				
    	if( samples == null || samples.length != sampleCnt ) {
    		samples = new double[sampleCnt];
    	}
		
    	try {
    		sampleSource.getSamples(channel, samples, firstSample, sampleCnt, 0);
    	} catch( RuntimeException ex ) {
    		setVisible(false);
    		throw ex;    		
    	}
    	
    	try {
    	
    		logger.debug( "Samples requested [" + sampleCnt + "] array size [" + samples.length + "]" );
    		
	    	FourierTransform fourierTransform = new FourierTransform(samples);
	    	fourierTransform.setDeltaT( 1 / plot.getSamplingFrequency() );
	    	windowType.apply( fourierTransform, windowParameter );
	    		    		    	
	    	powerSpectrum = fourierTransform.powerSpectrum();
	    	if( powerSpectrum == null ) {
	    		throw new NullPointerException( "Null spectrum returned" );
	    	}
	    	
	    	logger.debug( "PS[0] size [" + powerSpectrum[0].length + "] PS[1] size [" + powerSpectrum[1].length + "]" );
	    	
    	} catch( RuntimeException ex ) {
    		setVisible(false);
    		throw ex;    		
    	}
    	
    	if( powerSpectrumPlot == null ) {
    		
    		xAxis = new NumberAxis();
    		xAxis.setAutoRange(false);
    		/*xAxis.setTickUnit(new NumberTickUnit(4));*/
    		    		    	    	
    		normalRenderer = new XYLineAndShapeRenderer(true, false);
    		
    		splineRenderer = new XYSplineRenderer();
    		splineRenderer.setSeriesShapesVisible(0, false);
    		splineRenderer.setPrecision(10);    		
    		
    		powerSpectrumPlot = new XYPlot( null, xAxis, null, null );
    		
    		normalYAxis = new NumberAxis();
    		normalYAxis.setAutoRange(false);
    		
    		logYAxis = new LogAxis();
    		logYAxis.setAutoRange(false);

    	}
    	
    	if( powerSpectrumChart == null ) {
    		
    		powerSpectrumChart = new JFreeChart(null, titleFont, powerSpectrumPlot, false);
    		powerSpectrumChart.setBorderVisible(false);
    		powerSpectrumChart.setBackgroundPaint(Color.WHITE);
    		
    	}
    	
    	double pixelPerSecond = plot.getPixelPerSecond();
    	float minTime = (float) ((firstSample * timeZoomFactor) / pixelPerSecond); 
    	float maxTime = (float) ((lastSample * timeZoomFactor) / pixelPerSecond); 
    	
    	StringBuilder minTimeSb = new StringBuilder(20);
    	Util.addTime( minTime, minTimeSb );
    	
    	StringBuilder maxTimeSb = new StringBuilder(20);
    	Util.addTime( maxTime, maxTimeSb );
    	    	
    	String title = messageSource.getMessage(
    			"fft.chartTitle",
    			new Object[] {
    					new Integer(windowWidth),
    					minTimeSb.toString(),
    					maxTimeSb.toString(),
    					sampleSource.getLabel(channel)
    			}
    	);
    	
    	if( titleVisible ) {
    		powerSpectrumChart.setTitle(new TextTitle(title, titleFont));
    	} else {
    		powerSpectrumChart.setTitle((String) null);
    	}
		powerSpectrumChart.setAntiAlias(antialias);

    	int startIndex = LEFT_CUTOFF;
    	int endIndex = powerSpectrum[1].length;
		
		{
			//FIXME: check
			double rangeStart = powerSpectrum[0][LEFT_CUTOFF];
			double rangeEnd = plot.getSamplingFrequency()/2;
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
				if (step < 1) step = 1;
				xAxis.setTickUnit(new NumberTickUnit(Math.round(step)));
			}

			if (rangeEnd < rangeStart + 1) {
				rangeEnd = rangeStart + 1;
			}
			
	    	if (fftSettings.isScaleToView()) {
	    		
	    		double sampleDist = endIndex-startIndex;
	    		
	    		int shift = (int) (((rangeEnd - oldRangeStart) / rangeSize) * sampleDist);	    		
	    		endIndex = startIndex + shift;
	    		
	    		shift = (int) (((rangeStart - oldRangeStart) / rangeSize) * sampleDist);
	    		startIndex += shift;

	    	}

			
			xAxis.setRange( rangeStart, rangeEnd );			
		}
		
    	
    	
    	double max = 0;
    	double min = Double.MAX_VALUE;
    	    	
    	for( int i=startIndex; i<endIndex; i++ ) {
    		if( max < powerSpectrum[1][i] ) {
    			max = powerSpectrum[1][i];
    		}
    		if( min > powerSpectrum[1][i] ) {
    			min = powerSpectrum[1][i];
    		}
    	}
    	
    	max *= 1.15 ; // scale up by 15% as per ZFB request (related to spline overshooting points). 
    	
    	if( logarithmic ) {
    		logYAxis.setTickLabelsVisible(powerAxisLabelsVisible);
    		logYAxis.setRange( min, max );
    		powerSpectrumPlot.setRangeAxis(logYAxis);
    	} else {
    		normalYAxis.setTickLabelsVisible(powerAxisLabelsVisible);
    		normalYAxis.setRange( 0, max );
    		powerSpectrumPlot.setRangeAxis(normalYAxis);
    	}

    	xAxis.setTickLabelsVisible(frequencyAxisLabelsVisible);
    	
    	if( spline ) {
    		splineRenderer.setSeriesPaint(0, Color.RED);
    		powerSpectrumPlot.setRenderer(splineRenderer);
    	} else {
    		normalRenderer.setSeriesPaint(0, Color.RED);
    		powerSpectrumPlot.setRenderer(normalRenderer);
    	}
    	
    	DefaultXYDataset dataset = new DefaultXYDataset();
    	dataset.addSeries("data", powerSpectrum);
    	powerSpectrumPlot.setDataset( dataset );
    	        			
	}
	
	@Override
	protected void paintComponent(Graphics gOrig) {

		if( plot == null || focusPoint == null ) {
			return;
		}
	
		calculate();
		
		Graphics2D g = (Graphics2D) gOrig;
		
		Dimension size = getSize();
		Insets insets = getInsets();
						
		g.setColor(Color.WHITE);
		g.fillRect(0,0,size.width,size.height);
		size.width -= (insets.left + insets.right);
		size.height -= (insets.top + insets.bottom);
		
		if( error != null ) {
			
			g.setColor(Color.RED);
			
			FontMetrics fontMetrics = g.getFontMetrics();
			Rectangle2D stringBounds = fontMetrics.getStringBounds(error, g);
			
			int width = (int) stringBounds.getWidth();
			int height = (int) stringBounds.getHeight();
			
			int x;
			if( width > size.width ) {
				x = 0;
			} else {
				x = (size.width-width) / 2;
			}
			
			g.drawString(error, insets.left + x, insets.top + (size.height-height) / 2 + fontMetrics.getAscent() );
			return;
			
		}
		
		powerSpectrumChart.draw(g, new Rectangle(insets.left, insets.top, size.width, size.height));
				
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
		return plotSize;
	}
	
	@Override
	public Dimension getMinimumSize() {
		return new Dimension(200,100);
	}
	
	@Override
	public Dimension getMaximumSize() {
		return getPreferredSize();
	}
	
	public Point getFocusPoint() {
		return focusPoint;
	}

	public void setFocusPoint(Point focusPoint) {
		if( !Util.equalsWithNulls(this.focusPoint, focusPoint) ) {
			this.focusPoint = focusPoint;
			calculated = false;
			repaint();
		}
	}
	
	public int getChannel() {
		return channel;
	}

	public void setChannel(int channel) {
		if( this.channel != channel ) {
			this.channel = channel;
			calculated = false;
			repaint();
		}
	}
	
	public SignalPlot getPlot() {
		return plot;
	}

	public void setPlot(SignalPlot plot) {
		if( this.plot != plot ) {
			this.plot = plot;
			calculated = false;
			repaint();
		}
	}

	public void setParameters(SignalPlot plot, Point focusPoint, int channel) {
		this.plot = plot;
		this.focusPoint = focusPoint;
		this.channel = channel;
		calculated = false;
		repaint();
	}
	
	public void setParameters(Point focusPoint, int channel) {
		this.focusPoint = focusPoint;
		this.channel = channel;
		calculated = false;
		repaint();
	}
			
	public Dimension getPlotSize() {
		return plotSize;
	}

	public void setPlotSize(Dimension plotSize) {
		if( plotSize == null ) {
			throw new NullPointerException("No size");
		}
		if( !plotSize.equals(this.plotSize) ) {
			this.plotSize = plotSize;
			calculated = false;
			revalidate();
			repaint();
		}
	}

	public int getWindowWidth() {
		return windowWidth;
	}

	public void setWindowWidth(int windowWidth) {
		if( this.windowWidth != windowWidth ) {
			this.windowWidth = windowWidth;
			calculated = false;
			repaint();
		}
	}

	public WindowType getWindowType() {
		return windowType;
	}

	public void setWindowType(WindowType windowType) {
		if( this.windowType != windowType ) {
			this.windowType = windowType;
			calculated = false;
			repaint();
		}
	}

	public double getWindowParameter() {
		return windowParameter;
	}

	public void setWindowParameter(double windowParameter) {
		if( this.windowParameter != windowParameter ) {
			this.windowParameter = windowParameter;
			calculated = false;
			repaint();
		}
	}

	public boolean isLogarithmic() {
		return logarithmic;
	}

	public void setLogarithmic(boolean logarithmic) {
		if( this.logarithmic != logarithmic ) {
			this.logarithmic = logarithmic;
			calculated = false;
			repaint();
		}
	}

	public boolean isSpline() {
		return spline;
	}

	public void setSpline(boolean spline) {
		if( this.spline != spline ) {
			this.spline = spline;
			calculated = false;
			repaint();
		}
	}

	public boolean isAntialias() {
		return antialias;
	}

	public void setAntialias(boolean antialias) {
		if( this.antialias != antialias ) {
			this.antialias = antialias;
			calculated = false;
			repaint();
		}
	}

	public boolean isTitleVisible() {
		return titleVisible;
	}

	public void setTitleVisible(boolean titleVisible) {
		if( this.titleVisible != titleVisible ) {
			this.titleVisible = titleVisible;
			calculated = false;
			repaint();
		}
	}

	public boolean isFrequencyAxisLabelsVisible() {
		return frequencyAxisLabelsVisible;
	}

	public void setFrequencyAxisLabelsVisible(boolean frequencyAxisLabelsVisible) {
		if( this.frequencyAxisLabelsVisible != frequencyAxisLabelsVisible ) {
			this.frequencyAxisLabelsVisible = frequencyAxisLabelsVisible;
			calculated = false;
			repaint();
		}
	}

	public boolean isPowerAxisLabelsVisible() {
		return powerAxisLabelsVisible;
	}

	public void setPowerAxisLabelsVisible(boolean powerAxisLabelsVisible) {
		if( this.powerAxisLabelsVisible != powerAxisLabelsVisible ) {
			this.powerAxisLabelsVisible = powerAxisLabelsVisible;
			calculated = false;
			repaint();
		}
	}
	
	public void setSettings( SignalFFTSettings settings ) {
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
	
	
}
