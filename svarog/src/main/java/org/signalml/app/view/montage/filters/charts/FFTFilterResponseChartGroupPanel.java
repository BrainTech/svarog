/* FFTFilterGraphsPanel.java created 2011-02-07
 *
 */

package org.signalml.app.view.montage.filters.charts;

import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import javax.swing.JPanel;
import static org.signalml.app.util.i18n.SvarogI18n._;
import org.signalml.app.view.montage.filters.charts.elements.FFTFrequencyResponseChartPanel;
import org.signalml.app.view.montage.filters.charts.elements.FilterResponseChartPanelsWithGraphScaleSpinner;
import org.signalml.app.view.montage.filters.charts.elements.ResponseChartPanel;
import org.signalml.domain.montage.filter.FFTSampleFilter;
import org.signalml.domain.montage.filter.FFTSampleFilter.Range;

/**
 * This panel represents a panel containing all chart-connected components
 * for visualizing the FFTSampleFilter. That is: an FFT frequency response graph
 * and a spinner which controls the maximum frequency shown on the graph.
 *
 * @author Piotr Szachewicz
 */
public class FFTFilterResponseChartGroupPanel extends FilterResponseChartGroupPanel<FFTSampleFilter> {

	/**
	 * The chart panel containing the ideal frequency response of the
	 * FFT filter.
	 */
	protected FFTFrequencyResponseChartPanel frequencyResponseChartPanel;

	/**
	 * This panel contains the frequencyResponseChartPanel and an
	 * associated graph scale spinner to controls maximum frequency
	 * shown on the chart.
	 */
	protected FilterResponseChartPanelsWithGraphScaleSpinner chartPanelWithSpinner;

	/**
	 * Constructor.
	 * @param currentFilter the filter to be visualized
	 */
	public FFTFilterResponseChartGroupPanel(FFTSampleFilter currentFilter) {
		super(currentFilter);
	}

	@Override
	protected JPanel createChartGroupPanel() {
		frequencyResponseChartPanel = new FFTFrequencyResponseChartPanel();

		List<ResponseChartPanel> chartsList = new ArrayList<ResponseChartPanel>();
		chartsList.add(frequencyResponseChartPanel);

		chartPanelWithSpinner = new FilterResponseChartPanelsWithGraphScaleSpinner(chartsList, _("Maximum graph frequency [Hz]"));

		return chartPanelWithSpinner;
	}

	@Override
	public void setSamplingFrequency(double samplingFrequency) {
		super.setSamplingFrequency(samplingFrequency);
		chartPanelWithSpinner.setMaximumSpinnerValue(samplingFrequency / 2);
		chartPanelWithSpinner.setCurrentSpinnerValue(samplingFrequency / 2);
	}

	/**
	 * Sets the frequency range to be highlighted on the frequency response
	 * chart.
	 * @param selection selection to be highlighted
	 */
	public void setHighlightedSelection(FrequencyRangeSelection selection) {
		frequencyResponseChartPanel.setHighlightedSelection(selection);
	}

	/**
	 * Adds a listener which will be notified whenever the highlighted selection
	 * on the frequency response chart changes.
	 * @param listener listener to be notified
	 */
	public void addSelectionChangedListener(PropertyChangeListener listener) {
		frequencyResponseChartPanel.addSelectionChangedListener(listener);
	}

	/**
	 * Removes the listener.
	 * @param listener listener to be removed
	 */
	public void removeSelectionChangedListener(PropertyChangeListener listener) {
		frequencyResponseChartPanel.removeSelectionChangedListener(listener);
	}

	/**
	 * Updates the FFT frequency response graph.
	 * @param filter a filter to be visualized on the graph
	 */
	public void updateGraphs(FFTSampleFilter filter) {

		currentFilter = filter;

		if (currentFilter == null) {
			return;
		}

		double graphFrequencyMax = chartPanelWithSpinner.getGraphScaleSpinnerValue();
		double frequencyStepSize = FilterResponseChartPanelsWithGraphScaleSpinner.SPINNER_STEP_SIZE;
		int frequencyCnt = (int) Math.ceil(graphFrequencyMax / frequencyStepSize) + 1;
		double[] frequencies = new double[frequencyCnt];
		double[] coefficients = new double[frequencyCnt];
		int i;
		double frequency = 0;

		for (i = 0; i < frequencyCnt; i++) {
			frequencies[i] = frequency;
			frequency += frequencyStepSize;
		}

		Iterator<Range> it = currentFilter.getRangeIterator();
		Range range;
		double limit;
		float lowFrequency;
		float highFrequency;
		double coefficient;
		double maxCoefficient = 0;

		while (it.hasNext()) {

			range = it.next();

			lowFrequency = range.getLowFrequency();
			if (lowFrequency > graphFrequencyMax) {
				break;
			}

			highFrequency = range.getHighFrequency();
			coefficient = range.getCoefficient();

			if (highFrequency <= lowFrequency) {
				limit = graphFrequencyMax;
			} else {
				limit = Math.min(highFrequency, graphFrequencyMax);
			}

			int index;
			for (frequency=lowFrequency; frequency<=limit; frequency += frequencyStepSize) {

				index = (int)(frequency / frequencyStepSize);
				coefficients[index] = coefficient;

			}

			if (coefficient > maxCoefficient) {
				maxCoefficient = coefficient;
			}

		}

		maxCoefficient *= 1.1;
		if (maxCoefficient < 1) {
			maxCoefficient  = 1;
		}

		frequencyResponseChartPanel.setData(frequencies, coefficients);
	}

	@Override
	protected String getChartGroupPanelTitle() {
		return _("Filter design graph");
	}

}
