/* FFTFilterGraphsPanel.java created 2011-02-07
 *
 */

package org.signalml.app.view.montage.filters.charts;

import org.signalml.app.view.montage.filters.charts.elements.FFTFrequencyResponseChartPanel;
import org.signalml.app.view.montage.filters.charts.elements.FilterResponseChartPanelsWithGraphScaleSpinner;
import org.signalml.app.view.montage.filters.charts.elements.ResponseChartPanel;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import org.signalml.domain.montage.filter.FFTSampleFilter;
import org.signalml.domain.montage.filter.FFTSampleFilter.Range;
import org.springframework.context.support.MessageSourceAccessor;

/**
 *
 * @author Piotr Szachewicz
 */
public class FFTFilterResponseChartGroupPanel extends FilterResponseChartGroupPanel<FFTSampleFilter> {

	protected FilterResponseChartPanelsWithGraphScaleSpinner chartPanelWithSpinner;
	protected FFTFrequencyResponseChartPanel frequencyResponseChartPanel;

	public FFTFilterResponseChartGroupPanel(MessageSourceAccessor messageSource, FFTSampleFilter currentFilter) {
		super(messageSource, currentFilter);
	}

	@Override
	public void createInterface() {
		frequencyResponseChartPanel = new FFTFrequencyResponseChartPanel(messageSource);

		List<ResponseChartPanel> chartsList = new ArrayList<ResponseChartPanel>();
		chartsList.add(frequencyResponseChartPanel);

		chartPanelWithSpinner = new FilterResponseChartPanelsWithGraphScaleSpinner(chartsList, messageSource.getMessage("editSampleFilter.graphFrequencySpinnerLabel"));

		this.add(chartPanelWithSpinner);
	}

	public void setSamplingFrequency(double samplingFrequency) {
		this.samplingFrequency = samplingFrequency;
		chartPanelWithSpinner.setMaximumSpinnerValue(samplingFrequency / 2);
	}

	public void setHighlightedSelection(FrequencyRangeSelection selection) {
		frequencyResponseChartPanel.setHighlightedSelection(selection);
	}

	public void addSelectionChangedListener(PropertyChangeListener listener) {
		frequencyResponseChartPanel.addSelectionChangedListener(listener);
	}

	public void removeSelectionChangedListener(PropertyChangeListener listener) {
		frequencyResponseChartPanel.removeSelectionChangedListener(listener);
	}

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

				index = (int) (frequency / frequencyStepSize);
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


}
