/* SelectionHighlight.java created 2011-02-13
 *
 */
package org.signalml.app.view.montage.charts.elements;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.event.MouseEvent;
import java.awt.geom.Rectangle2D;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import org.signalml.app.view.montage.charts.FrequencyRangeSelection;

/**
 *
 * @author Piotr Szachewicz
 */
public class SelectionHighlightRenderer {

	public static final String SELECTION_CHANGED_PROPERTY = "selectionChangedProperty";
	private PropertyChangeSupport pcSupport = new PropertyChangeSupport(this);
	private static Color draggingColor = new Color(0.5F, 0.5F, 0.5F, 0.5F);
	private static Color normalColor = new Color(0.55F, 1.0F, 0.55F, 0.5F);
	private FrequencyResponseChartPanel chartPanel;
	private Selection selection;

	public SelectionHighlightRenderer(FrequencyResponseChartPanel chartPanel) {
		this.chartPanel = chartPanel;
		selection = new Selection();
	}

	public Rectangle2D getChartArea() {
		return chartPanel.getScreenDataArea();
	}

	public double getMaximumChartFrequency() {
		double maximumFrequency = chartPanel.getMaximumChartFrequency();
		return maximumFrequency;
	}

	protected int getMaximumChartPosition() {
		return (int) Math.ceil(getChartArea().getX() + getChartArea().getWidth());
	}

	protected int getMinimumChartPosition() {
		return (int) Math.floor(getChartArea().getX());
	}

	protected int constrainPositionWithRegardToChartSize(int xPosition) {
		if (xPosition > getMaximumChartPosition()) {
			xPosition = getMaximumChartPosition();
		} else if (xPosition < getMinimumChartPosition()) {
			xPosition = getMinimumChartPosition();
		}
		return xPosition;
	}

	public FrequencyRangeSelection getFrequencyRangeSelection() {
		int lowerPosition = selection.getLowerPosition();
		int higherPosition = selection.getHigherPosition();

		double lowerFrequency = convertPositionToFrequency(lowerPosition);
		double higherFrequency = convertPositionToFrequency(higherPosition);

		return new FrequencyRangeSelection(lowerFrequency, higherFrequency);
	}

	public void setFrequencyRangeSelection(FrequencyRangeSelection frequencyRangeSelection) {

		if (selection.isDragging()) {
			return;
		}

		double startFrequency = frequencyRangeSelection.getLowerFrequency();
		double endFrequency = frequencyRangeSelection.getHigherFrequency();

		int startPosition = convertFrequencyToPosition(startFrequency);
		int endPosition = convertFrequencyToPosition(endFrequency);
		selection = new Selection(startPosition, endPosition);

		fireSelectionChanged();
	}

	protected double convertPointToFrequency(Point p) {
		return convertPositionToFrequency(p.x);
	}

	protected double convertPositionToFrequency(double xPosition) {
		Rectangle2D area = getChartArea();

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

	protected double getNumberOfPixelsPerHertz() {
		int xMax = getMaximumChartPosition();
		int xMin = getMinimumChartPosition();
		double perHz = ((double) (xMax - xMin)) / getMaximumChartFrequency();
		return perHz;
	}

	protected int convertFrequencyToPosition(double frequency) {
		int xMin = getMinimumChartPosition();
		double perHz = getNumberOfPixelsPerHertz();
		return (int) Math.round(xMin + frequency * perHz);
	}

	public void mousePressed(MouseEvent ev) {
		int xPosition = ev.getPoint().x;
		if (xPosition >= getMinimumChartPosition() && xPosition <= getMaximumChartPosition()) {
			selection.startDragging(ev.getPoint().x);
			fireSelectionChanged();
		}
	}

	public void mouseReleased(MouseEvent ev) {
		int xPosition = ev.getPoint().x;
		xPosition = constrainPositionWithRegardToChartSize(xPosition);
		selection.stopDragging(xPosition);
		fireSelectionChanged();
	}

	public void mouseClicked(MouseEvent ev) {
	}

	public void mouseDragged(MouseEvent ev) {
		int xPosition = ev.getPoint().x;
		xPosition = constrainPositionWithRegardToChartSize(xPosition);
		selection.dragTo(xPosition);
		fireSelectionChanged();
	}

	public void paintComponent(Graphics gOrig) {

		if (!selection.isVisible()) {
			return;
		}

		Graphics2D g = (Graphics2D) gOrig;
		Rectangle2D area = getChartArea();

		int selectionHighlightStart = selection.getLowerPosition();
		int selectionHighlightEnd = selection.getHigherPosition();

		if (selection.isDragging()) {
			g.setColor(draggingColor);
		} else {
			g.setColor(normalColor);
		}

		g.fillRect(selectionHighlightStart, (int) area.getY(), selectionHighlightEnd - selectionHighlightStart, (int) area.getHeight());

	}

	public void addSelectionChangedListener(PropertyChangeListener listener) {
		pcSupport.addPropertyChangeListener(SELECTION_CHANGED_PROPERTY, listener);
	}

	public void removeSelectionChangedListener(PropertyChangeListener listener) {
		pcSupport.removePropertyChangeListener(SELECTION_CHANGED_PROPERTY, listener);
	}

	protected void fireSelectionChanged() {
		FrequencyRangeSelection currentSelection = getFrequencyRangeSelection();
		pcSupport.firePropertyChange(SELECTION_CHANGED_PROPERTY, null, currentSelection);
	}
}
