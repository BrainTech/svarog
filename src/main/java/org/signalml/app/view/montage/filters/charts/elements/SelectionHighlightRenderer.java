/* SelectionHighlight.java created 2011-02-13
 *
 */
package org.signalml.app.view.montage.filters.charts.elements;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.event.MouseEvent;
import java.awt.geom.Rectangle2D;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import org.signalml.app.view.montage.filters.charts.FrequencyRangeSelection;

/**
 * This class is capable of handling and rendering selections made on
 * a {@link FrequencyResponseChartPanel}.
 *
 * @author Piotr Szachewicz
 */
public class SelectionHighlightRenderer {

	/**
	 * A string representing a property which is changed whenever
	 * the selection on the chart is changed.
	 */
	public static final String SELECTION_CHANGED_PROPERTY = "selectionChangedProperty";

	/**
	 * A {@link PropertyChangeSupport} which handles notifying all its
	 * listeners whenever the selection changed.
	 */
	private PropertyChangeSupport pcSupport = new PropertyChangeSupport(this);

	/**
	 * The color used the render the selection which is currently being made.
	 */
	private static Color draggingColor = new Color(0.5F, 0.5F, 0.5F, 0.5F);

	/**
	 * The color used to render the selection after it has been made.
	 */
	private static Color normalColor = new Color(0.55F, 1.0F, 0.55F, 0.5F);

	/**
	 * The chart panel on which the selections are being made.
	 */
	private FrequencyResponseChartPanel chartPanel;

	/**
	 * The currently selected frequency range.
	 */
	private FrequencyRangeSelection frequencyRangeSelection;

	/**
	 * The currently made selection in pixels.
	 */
	private PixelRangeSelection pixelRangeSelection;

	/**
	 * Constructor.
	 * @param chartPanel the chart panel on which the selections will be made.
	 */
	public SelectionHighlightRenderer(FrequencyResponseChartPanel chartPanel) {
		this.chartPanel = chartPanel;
		pixelRangeSelection = new PixelRangeSelection();
	}

	/**
	 * Returns a {@link Rectangle2D} which represents the boundaries
	 * of the chart.
	 * @return a rectangle representing the chart area
	 */
	protected Rectangle2D getChartArea() {
		return chartPanel.getScreenDataArea();
	}

	/**
	 * Returns the maximum chart frequency.
	 * @return the maximum chart frequency
	 */
	protected double getMaximumChartFrequency() {
		double maximumFrequency = chartPanel.getMaximumChartFrequency();
		return maximumFrequency;
	}

	/**
	 * Returns the maximum x-position (in pixels) which is still in the
	 * chart.
	 * @return maximum x-position in the chart
	 */
	protected int getMaximumChartPosition() {
		return (int) Math.ceil(getChartArea().getX() + getChartArea().getWidth());
	}

	/**
	 * Returns the minimum x-position (in pixels) which is still in the chart.
	 * @return the minimum x-position in the chart
	 */
	protected int getMinimumChartPosition() {
		return (int) Math.floor(getChartArea().getX());
	}

	/**
	 * If the given position is bigger the the maximum position on the chart,
	 * then it returns the maximum chart position. If it is smaller, it
	 * returns the minimum chart position. If none of above is true, it
	 * returns the given position.
	 * @param xPosition the x-position to be constrained
	 * @return the constrained x-position
	 */
	protected int constrainPositionWithRegardToChartSize(int xPosition) {
		if (xPosition > getMaximumChartPosition()) {
			xPosition = getMaximumChartPosition();
		} else if (xPosition < getMinimumChartPosition()) {
			xPosition = getMinimumChartPosition();
		}
		return xPosition;
	}

	/**
	 * Returns the selection which is currently highlighed on the chart.
	 * @return the highlighted frequency range
	 */
	public FrequencyRangeSelection getFrequencyRangeSelection() {
		return convertPixelSelectionToFrequencyRangeSelection(pixelRangeSelection);
	}

	/**
	 * Sets a frequency range to be highlighted on the plot.
	 * @param frequencyRangeSelection the frequency range to be highlighted
	 */
	public void setFrequencyRangeSelection(FrequencyRangeSelection frequencyRangeSelection) {

		if (pixelRangeSelection.isDragging()) {
			return;
		}

		this.frequencyRangeSelection = frequencyRangeSelection;
		pixelRangeSelection = convertFrequencyRangeSelectionToPixelSelection(frequencyRangeSelection);
		fireSelectionChanged();
	}

	/**
	 * Converts a selection given in the frequency domain to one given
	 * in the pixel domain.
	 * @param frequencyRangeSelection selection to be converted
	 * @return the result of the conversion
	 */
	private PixelRangeSelection convertFrequencyRangeSelectionToPixelSelection(FrequencyRangeSelection frequencyRangeSelection) {
		double startFrequency = frequencyRangeSelection.getLowerFrequency();
		double endFrequency = frequencyRangeSelection.getHigherFrequency();

		int startPosition = convertFrequencyToPosition(startFrequency);
		int endPosition = convertFrequencyToPosition(endFrequency);

		PixelRangeSelection pixelSelection = new PixelRangeSelection(startPosition, endPosition);
		return pixelSelection;
	}

	/**
	 * Converts a selection in the pixel domain to a selection in the
	 * frequency domain.
	 * @param pixelSelection selection to be converted
	 * @return the result of the conversion
	 */
	private FrequencyRangeSelection convertPixelSelectionToFrequencyRangeSelection(PixelRangeSelection pixelSelection) {
		int lowerPosition = pixelSelection.getLowerPosition();
		int higherPosition = pixelSelection.getHigherPosition();

		double lowerFrequency = convertPositionToFrequency(lowerPosition);
		double higherFrequency = convertPositionToFrequency(higherPosition);

		return new FrequencyRangeSelection(lowerFrequency, higherFrequency);
	}

	/**
	 * Converts the given point (pixel) to an appropriate frequency.
	 * @param a point to be converted
	 * @return the frequency which corresponds to the given point on the
	 * chart
	 */
	protected double convertPointToFrequency(Point p) {
		return convertPositionToFrequency(p.x);
	}

	 /**
	  * Converts the given x-position to an appropriate frequency.
	  * @param xPosition the x-position to be converted
	  * @return the frequency which corresponds to the given x-position
	  * on the chart
	  */
	protected double convertPositionToFrequency(double xPosition) {

		int xMin = getMinimumChartPosition();
		int xMax = getMaximumChartPosition();

		if (xPosition < xMin) {
			return 0;
		}
		if (xPosition > xMax) {
			return getMaximumChartFrequency();
		}

		double freq = getMaximumChartFrequency() * (((double) (xPosition - xMin)) / ((double) (xMax - xMin)));

		return ((double) Math.round(freq * 4)) / 4.0;
	}

	/**
	 * Converts the given frequency to an appropriate x-position on the chart.
	 * @param frequency the frequency to be converted
	 * @return an x-position which corresponds to the given frequency
	 * on the chart
	 */
	protected int convertFrequencyToPosition(double frequency) {
		int xMin = getMinimumChartPosition();
		double perHz = getNumberOfPixelsPerHertz();
		return (int) Math.round(xMin + frequency * perHz);
	}

	/**
	 * Returns the number of pixels which falls on one hertz on the chart.
	 * @return number of pixels per hertz
	 */
	protected double getNumberOfPixelsPerHertz() {
		int xMax = getMaximumChartPosition();
		int xMin = getMinimumChartPosition();
		double perHz = ((double) (xMax - xMin)) / getMaximumChartFrequency();
		return perHz;
	}

	/**
	 * This method should be called by the chart panel to which this
	 * renderer is connected (the one given in the constructor) whenever
	 * a mousePressed event has happened in that chart panel.
	 * @param ev an object describing the mouse event that has happened
	 */
	public void mousePressed(MouseEvent ev) {
		int xPosition = ev.getPoint().x;
		if (xPosition >= getMinimumChartPosition() && xPosition <= getMaximumChartPosition()) {
			pixelRangeSelection.startDragging(ev.getPoint().x);
			fireSelectionChanged();
		}
	}

	/**
	 * This method should be called by the chart panel to which this
	 * renderer is connected (the one given in the constructor) whenever
	 * a mouseReleased event has happened in that chart panel.
	 * @param ev an object describing the mouse event that has happened
	 */
	public void mouseReleased(MouseEvent ev) {
		int xPosition = ev.getPoint().x;
		xPosition = constrainPositionWithRegardToChartSize(xPosition);
		pixelRangeSelection.stopDragging(xPosition);
		frequencyRangeSelection = convertPixelSelectionToFrequencyRangeSelection(pixelRangeSelection);
		fireSelectionChanged();
	}

	/**
	 * This method should be called by the chart panel to which this
	 * renderer is connected (the one given in the constructor) whenever
	 * a mouseDragged event has happened in that chart panel.
	 * @param ev an object describing the mouse event that has happened
	 */
	public void mouseDragged(MouseEvent ev) {
		int xPosition = ev.getPoint().x;
		xPosition = constrainPositionWithRegardToChartSize(xPosition);
		pixelRangeSelection.dragTo(xPosition);
		fireSelectionChanged();
	}

	/**
	 * This method should be called by the chart panel to which this renderer
	 * is connected whenever the chart scale changes in order to keep the
	 * same frequency range selected.
	 */
	public void updateSelectionToScaleChange() {
		if (frequencyRangeSelection != null) {
			pixelRangeSelection = convertFrequencyRangeSelectionToPixelSelection(frequencyRangeSelection);
			fireSelectionChanged();
		}
	}

	/**
	 * Paints the selections made.
	 * @param gOrig
	 */
	public void paintComponent(Graphics gOrig) {

		if (!pixelRangeSelection.isVisible()) {
			return;
		}

		Graphics2D g = (Graphics2D) gOrig;
		Rectangle2D area = getChartArea();

		int selectionHighlightStart = pixelRangeSelection.getLowerPosition();
		int selectionHighlightEnd = pixelRangeSelection.getHigherPosition();

		if (pixelRangeSelection.isDragging()) {
			g.setColor(draggingColor);
		} else {
			g.setColor(normalColor);
		}

		g.fillRect(selectionHighlightStart, (int) area.getY(), selectionHighlightEnd - selectionHighlightStart, (int) area.getHeight());

	}

	/**
	 * Adds a new listener which will be notified whenever the selection
	 * have been changed.
	 * @param listener a listener to be added
	 */
	public void addSelectionChangedListener(PropertyChangeListener listener) {
		pcSupport.addPropertyChangeListener(SELECTION_CHANGED_PROPERTY, listener);
	}

	/**
	 * Removes a new listener which will be notified whenever the selection
	 * have been changed.
	 * @param listener a listener to be removed
	 */
	public void removeSelectionChangedListener(PropertyChangeListener listener) {
		pcSupport.removePropertyChangeListener(SELECTION_CHANGED_PROPERTY, listener);
	}

	/**
	 * Notifies all listeners that the selection has changed.
	 */
	protected void fireSelectionChanged() {
		FrequencyRangeSelection currentSelection = getFrequencyRangeSelection();
		pcSupport.firePropertyChange(SELECTION_CHANGED_PROPERTY, null, currentSelection);
	}

}
