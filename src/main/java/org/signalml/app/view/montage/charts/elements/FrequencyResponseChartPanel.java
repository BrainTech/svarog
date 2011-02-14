/* FilterResponseChartPanel.java created 2011-02-06
 *
 */
package org.signalml.app.view.montage.charts.elements;

import java.awt.Graphics;
import java.awt.event.MouseEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import org.jfree.chart.ChartPanel;
import org.signalml.app.view.montage.charts.FrequencyRangeSelection;
import org.springframework.context.support.MessageSourceAccessor;

/**
 * A class representing a {@link ChartPanel} which is used to display
 * the filter's frequency response and highlight currently selected
 * frequency bands.
 * @author Piotr Szachewicz
 */
public abstract class FrequencyResponseChartPanel extends ResponseChartPanel implements PropertyChangeListener {

	protected SelectionHighlightRenderer selectionHighlightRenderer;

	public FrequencyResponseChartPanel(MessageSourceAccessor messageSource) {
		super(messageSource);
		selectionHighlightRenderer = new SelectionHighlightRenderer(this);
		selectionHighlightRenderer.addSelectionChangedListener(this);
	}

	@Override
	public String getDomainAxisName() {
		return messageSource.getMessage("editSampleFilter.graphFrequencyLabel");
	}

	public void addSelectionChangedListener(PropertyChangeListener listener) {
		selectionHighlightRenderer.addSelectionChangedListener(listener);
	}

	public void removeSelectionChangedListener(PropertyChangeListener listener) {
		selectionHighlightRenderer.removeSelectionChangedListener(listener);
	}

	public double getMaximumChartFrequency() {
		return getMaximumDomainAxisValue();
	}

	public void setHighlightedSelection(FrequencyRangeSelection frequencyRangeSelection) {
		selectionHighlightRenderer.setFrequencyRangeSelection(frequencyRangeSelection);
	}

	@Override
	public void paintComponent(Graphics gOrig) {

		super.paintComponent(gOrig);
		selectionHighlightRenderer.paintComponent(gOrig);
	}

	@Override
	public void mousePressed(MouseEvent e) {
		super.mousePressed(e);
		selectionHighlightRenderer.mousePressed(e);
	}

	@Override
	public void mouseReleased(MouseEvent e) {
		super.mouseReleased(e);
		selectionHighlightRenderer.mouseReleased(e);
	}

	@Override
	public void mouseDragged(MouseEvent e) {
		super.mouseDragged(e);
		selectionHighlightRenderer.mouseDragged(e);
	}

	@Override
	public void propertyChange(PropertyChangeEvent evt) {
		repaint();
	}
}
