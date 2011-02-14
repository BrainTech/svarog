/* TimeDomainFilterResponseGraphsPanel.java created 2011-02-07
 *
 */

package org.signalml.app.view.montage.charts;

import org.signalml.app.view.montage.charts.elements.StepResponseChartPanel;
import org.signalml.app.view.montage.charts.elements.ImpulseResponseChartPanel;
import org.signalml.app.view.montage.charts.elements.FilterResponseChartPanelsWithGraphScaleSpinner;
import org.signalml.app.view.montage.charts.elements.TimeDomainFilterFrequencyResponseChartPanel;
import org.signalml.app.view.montage.charts.elements.GroupDelayResponseChartPanel;
import org.signalml.app.view.montage.charts.elements.ResponseChartPanel;
import java.awt.GridLayout;
import java.util.ArrayList;
import java.util.List;

import org.signalml.domain.montage.filter.TimeDomainSampleFilter;
import org.signalml.domain.montage.filter.iirdesigner.ArrayOperations;
import org.signalml.domain.montage.filter.iirdesigner.BadFilterParametersException;
import org.signalml.domain.montage.filter.iirdesigner.FilterCoefficients;
import org.signalml.domain.montage.filter.iirdesigner.FilterFrequencyResponse;
import org.signalml.domain.montage.filter.iirdesigner.FilterFrequencyResponseCalculator;
import org.signalml.domain.montage.filter.iirdesigner.FilterTimeDomainResponse;
import org.signalml.domain.montage.filter.iirdesigner.FilterTimeDomainResponseCalculator;
import org.signalml.domain.montage.filter.iirdesigner.IIRDesigner;
import org.springframework.context.support.MessageSourceAccessor;

/**
 *
 * @author Piotr Szachewicz
 */
public class TimeDomainFilterResponseChartGroupPanel extends FilterResponseChartGroupPanel<TimeDomainSampleFilter> {

	protected FilterResponseChartPanelsWithGraphScaleSpinner frequencyResponseChartPanelWithSpinner;
	protected FilterResponseChartPanelsWithGraphScaleSpinner timeDomainResponseChartPanelWithSpinner;

	protected TimeDomainFilterFrequencyResponseChartPanel frequencyResponseChartPanel;
	protected GroupDelayResponseChartPanel groupDelayResponseChartPanel;
	protected ImpulseResponseChartPanel impulseResponseChartPanel;
	protected StepResponseChartPanel stepResponseChartPanel;

	protected FilterFrequencyResponseCalculator frequencyResponseCalculator;
	protected FilterTimeDomainResponseCalculator timeDomainResponseCalculator;

	public TimeDomainFilterResponseChartGroupPanel(MessageSourceAccessor messageSource, TimeDomainSampleFilter currentFilter) {
		super(messageSource, currentFilter);
	}

	public void setSamplingFrequency(double samplingFrequency) {
		this.samplingFrequency = samplingFrequency;
		frequencyResponseChartPanelWithSpinner.setMaximumSpinnerValue(samplingFrequency / 2);
	}

	@Override
	protected void createInterface() {

		this.setLayout(new GridLayout(1, 2, 5, 5));
		frequencyResponseChartPanelWithSpinner = createFrequencyResponsesPanel();
		timeDomainResponseChartPanelWithSpinner = createTimeDomainResponsesPanel();

		this.add(frequencyResponseChartPanelWithSpinner);
		this.add(timeDomainResponseChartPanelWithSpinner);

	}

	protected FilterResponseChartPanelsWithGraphScaleSpinner createFrequencyResponsesPanel() {

		frequencyResponseChartPanel = new TimeDomainFilterFrequencyResponseChartPanel(messageSource);
		groupDelayResponseChartPanel = new GroupDelayResponseChartPanel(messageSource);

		List<ResponseChartPanel> chartsList = new ArrayList<ResponseChartPanel>();
		chartsList.add(frequencyResponseChartPanel);
		chartsList.add(groupDelayResponseChartPanel);

		return new FilterResponseChartPanelsWithGraphScaleSpinner(chartsList, messageSource.getMessage("editSampleFilter.graphFrequencySpinnerLabel"));

	}

	protected FilterResponseChartPanelsWithGraphScaleSpinner createTimeDomainResponsesPanel() {

		impulseResponseChartPanel = new ImpulseResponseChartPanel(messageSource);
		stepResponseChartPanel = new StepResponseChartPanel(messageSource);

		List<ResponseChartPanel> chartsList = new ArrayList<ResponseChartPanel>();
		chartsList.add(impulseResponseChartPanel);
		chartsList.add(stepResponseChartPanel);
		FilterResponseChartPanelsWithGraphScaleSpinner chartPanel = new FilterResponseChartPanelsWithGraphScaleSpinner(chartsList, messageSource.getMessage("editTimeDomainSampleFilter.graphTimeSpinnerLabel"));
		chartPanel.setMaximumSpinnerValue(5.0);
		return chartPanel;

	}

	public void updateGraphs(TimeDomainSampleFilter currentFilter) throws BadFilterParametersException {

		FilterCoefficients coeffs = coeffs = IIRDesigner.designDigitalFilter(currentFilter);

		frequencyResponseCalculator = new FilterFrequencyResponseCalculator(512, getSamplingFrequency(), coeffs);
		calculateAndDrawFilterFrequencyResponse();
		calculateAndDrawGroupDelayResponse();

		timeDomainResponseCalculator = new FilterTimeDomainResponseCalculator(samplingFrequency, coeffs);
		calculateAndDrawImpulseResponse();
		calculateAndDrawStepResponse();

	}

	protected void calculateAndDrawFilterFrequencyResponse() {

		FilterFrequencyResponse frequencyResponse = frequencyResponseCalculator.getMagnitudeResponse();

		double[] frequencies = frequencyResponse.getFrequencies();
		double[] values = frequencyResponse.getValues();

		frequencyResponseChartPanel.setData(frequencies, values);

		int filterOrder = frequencyResponseCalculator.getFilterCoefficients().getFilterOrder();
		String subtitleText = messageSource.getMessage("editTimeDomainSampleFilter.filterOrderSubtitle", new Object[]{filterOrder});

		frequencyResponseChartPanel.setSubtitle(subtitleText);

	}

	protected void calculateAndDrawGroupDelayResponse() {

		FilterFrequencyResponse groupDelayResponse = frequencyResponseCalculator.getGroupDelayResponse();
		double[] frequencies = groupDelayResponse.getFrequencies();
		double[] values = groupDelayResponse.getValues();

		// removing the first element which is (very often) a singularity and spoils the plot
		frequencies = ArrayOperations.removeFirstElements(frequencies, 1);
		values = ArrayOperations.removeFirstElements(values, 1);

		groupDelayResponseChartPanel.setData(frequencies, values);

	}

	protected void calculateAndDrawImpulseResponse() {
		FilterTimeDomainResponse impulseResponse = timeDomainResponseCalculator.getImpulseResponse(getNumberOfPointsForTimeDomainResponse());
		impulseResponseChartPanel.setData(impulseResponse);
	}

	protected void calculateAndDrawStepResponse() {
		FilterTimeDomainResponse stepResponse = timeDomainResponseCalculator.getStepResponse(getNumberOfPointsForTimeDomainResponse());
		stepResponseChartPanel.setData(stepResponse);
	}

	public int getNumberOfPointsForTimeDomainResponse() {
		return (int) (4 * samplingFrequency);
	}

}
