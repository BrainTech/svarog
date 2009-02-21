/* SignalFFTSettings.java created 2007-12-17
 * 
 */

package org.signalml.app.config;

import java.awt.Dimension;

import org.signalml.fft.WindowType;

import com.thoughtworks.xstream.annotations.XStreamAlias;

/** SignalFFTSettings
 *
 * 
 * @author Michal Dobaczewski &copy; 2007-2008 CC Otwarte Systemy Komputerowe Sp. z o.o.
 */
@XStreamAlias("fftsettings")
public class SignalFFTSettings implements FFTWindowTypeSettings {

	private boolean channelSwitching;	
	private Dimension plotSize;
	private int windowWidth;
	private WindowType windowType;
	private double windowParameter;
	private boolean logarithmic;
	private boolean antialias;
	private boolean spline;
	private boolean titleVisible;
	private boolean frequencyAxisLabelsVisible;
	private boolean powerAxisLabelsVisible;
	
	private int visibleRangeStart = Integer.MIN_VALUE;
	private int visibleRangeEnd = Integer.MAX_VALUE;
	private int maxLabelCount = Integer.MAX_VALUE;		
	private boolean scaleToView = false;
	
	public SignalFFTSettings() {
		channelSwitching = false;
		plotSize = new Dimension(600,200);
		windowWidth = 256;
		windowType = WindowType.RECTANGULAR;
		windowParameter = 0;
		logarithmic = false;
		spline = true;
		antialias = true;		
	}
	
	public Dimension getPlotSize() {
		return plotSize;
	}
	
	public void setPlotSize(Dimension size) {
		if( size == null ) {
			throw new NullPointerException("No size");
		}
		plotSize = size;
	}
			
	public boolean isChannelSwitching() {
		return channelSwitching;
	}

	public void setChannelSwitching(boolean channelSwitching) {
		this.channelSwitching = channelSwitching;
	}
	
	public int getWindowWidth() {
		return windowWidth;
	}

	public void setWindowWidth(int width) {
		this.windowWidth = width;
	}

	@Override
	public WindowType getWindowType() {
		return windowType;
	}

	@Override
	public void setWindowType(WindowType windowType) {
		if( windowType == null ) {
			throw new NullPointerException( "No window type" );
		}
		this.windowType = windowType;
	}
		
	@Override
	public double getWindowParameter() {
		return windowParameter;
	}

	@Override
	public void setWindowParameter(double windowParameter) {
		this.windowParameter = windowParameter;
	}

	public boolean isLogarithmic() {
		return logarithmic;
	}

	public void setLogarithmic(boolean logarithmic) {
		this.logarithmic = logarithmic;
	}	

	public boolean isSpline() {
		return spline;
	}	

	public void setSpline(boolean spline) {
		this.spline = spline;
	}	

	public boolean isAntialias() {
		return antialias;
	}

	public void setAntialias(boolean antialias) {
		this.antialias = antialias;
	}

	public boolean isTitleVisible() {
		return titleVisible;
	}

	public void setTitleVisible(boolean titleVisible) {
		this.titleVisible = titleVisible;
	}

	public boolean isFrequencyAxisLabelsVisible() {
		return frequencyAxisLabelsVisible;
	}

	public void setFrequencyAxisLabelsVisible(boolean frequencyAxisLabelsVisible) {
		this.frequencyAxisLabelsVisible = frequencyAxisLabelsVisible;
	}

	public boolean isPowerAxisLabelsVisible() {
		return powerAxisLabelsVisible;
	}

	public void setPowerAxisLabelsVisible(boolean powerAxisLabelsVisible) {
		this.powerAxisLabelsVisible = powerAxisLabelsVisible;
	}

	/**
	 * @return the visibleRangeStart
	 */
	public int getVisibleRangeStart() {
		return visibleRangeStart;
	}

	/**
	 * @param visibleRangeStart the visibleRangeStart to set
	 */
	public void setVisibleRangeStart(int visibleRangeStart) {
		this.visibleRangeStart = visibleRangeStart;
	}

	/**
	 * @return the visibleRangeEnd
	 */
	public int getVisibleRangeEnd() {
		return visibleRangeEnd;
	}

	/**
	 * @param visibleRangeEnd the visibleRangeEnd to set
	 */
	public void setVisibleRangeEnd(int visibleRangeEnd) {
		this.visibleRangeEnd = visibleRangeEnd;
	}

	/**
	 * @return the maxLabelCount
	 */
	public int getMaxLabelCount() {
		return maxLabelCount;
	}

	/**
	 * @param maxLabelCount the maxLabelCount to set
	 */
	public void setMaxLabelCount(int maxLabelCount) {
		this.maxLabelCount = maxLabelCount;
	}

	/**
	 * @return the scaleToView
	 */
	public boolean isScaleToView() {
		return scaleToView;
	}

	/**
	 * @param scaleToView the scaleToView to set
	 */
	public void setScaleToView(boolean scaleToView) {
		this.scaleToView = scaleToView;
	}
		
}
