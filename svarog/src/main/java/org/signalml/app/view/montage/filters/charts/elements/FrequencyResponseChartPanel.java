/* FilterResponseChartPanel.java created 2011-02-06
 *
 */
package org.signalml.app.view.montage.filters.charts.elements;

import java.awt.Graphics;
import java.awt.event.MouseEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import org.jfree.chart.ChartPanel;
import static org.signalml.app.util.i18n.SvarogI18n._;
import org.signalml.app.view.montage.filters.charts.FrequencyRangeSelection;

/**
 * A class representing a {@link ChartPanel} which is used to display
 * the filter's frequency response and highlight currently selected
 * frequency bands.
 * @author Piotr Szachewicz
 */
public abstract class FrequencyResponseChartPanel extends ResponseChartPanel implements PropertyChangeListener {

	/**
	 * This object handles highlighting the frequency selections made
	 * on the plot.
	 */
	protected SelectionHighlightRenderer selectionHighlightRenderer;

	/**
	 * Constructor.
	 */
	public FrequencyResponseChartPanel() {
		super();
		selectionHighlightRenderer = new SelectionHighlightRenderer(this);
		selectionHighlightRenderer.addSelectionChangedListener(this);
	}

	@Override
	public String getDomainAxisName() {
		return _("Frequency [Hz]");
	}

	/**
	 * Adds a new listener which will be notified whenever the selection on
	 * the chart have been changed.
	 * @param listener a listener to be added
	 */
	public void addSelectionChangedListener(PropertyChangeListener listener) {
		selectionHighlightRenderer.addSelectionChangedListener(listener);
	}

	/**
	 * Removes the listener.
	 * @param listener listener to be removed
	 */
	public void removeSelectionChangedListener(PropertyChangeListener listener) {
		selectionHighlightRenderer.removeSelectionChangedListener(listener);
	}

	/**
	 * Returns the maximum frequency to be shown on the chart.
	 * @return maximum frequency to be shown
	 */
	public double getMaximumChartFrequency() {
		return getMaximumDomainAxisValue();
	}

	/**
	 * Sets a range of frequncies to be highlighted.
	 * @param frequencyRangeSelection range of frequencies to be highlighted
	 */
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

	@Override
	public void setMaximumDomainAxisValue(double maximum) {
		super.setMaximumDomainAxisValue(maximum);
		if (selectionHighlightRenderer != null)
			selectionHighlightRenderer.updateSelectionToScaleChange();
	}

}
