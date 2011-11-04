/* TimeDomainFilterResponseGraphsPanel.java created 2011-02-07
 *
 */

package org.signalml.app.view.montage.filters.charts;

import static org.signalml.app.SvarogApplication._;
import static org.signalml.app.SvarogApplication._R;
import org.signalml.app.view.montage.filters.charts.elements.StepResponseChartPanel;
import org.signalml.app.view.montage.filters.charts.elements.ImpulseResponseChartPanel;
import org.signalml.app.view.montage.filters.charts.elements.FilterResponseChartPanelsWithGraphScaleSpinner;
import org.signalml.app.view.montage.filters.charts.elements.TimeDomainFilterFrequencyResponseChartPanel;
import org.signalml.app.view.montage.filters.charts.elements.GroupDelayResponseChartPanel;
import org.signalml.app.view.montage.filters.charts.elements.ResponseChartPanel;
import java.awt.GridLayout;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JPanel;

import org.signalml.domain.montage.filter.TimeDomainSampleFilter;
import org.signalml.domain.montage.filter.iirdesigner.ArrayOperations;
import org.signalml.domain.montage.filter.iirdesigner.BadFilterParametersException;
import org.signalml.domain.montage.filter.iirdesigner.FilterCoefficients;
import org.signalml.domain.montage.filter.iirdesigner.FilterFrequencyResponse;
import org.signalml.domain.montage.filter.iirdesigner.FilterFrequencyResponseCalculator;
import org.signalml.domain.montage.filter.iirdesigner.FilterTimeDomainResponse;
import org.signalml.domain.montage.filter.iirdesigner.FilterTimeDomainResponseCalculator;
import org.signalml.domain.montage.filter.iirdesigner.IIRDesigner;

/**
 * This class represents a panel containing all the components needed for visualizing
 * a {@link TimeDomainSampleFilter}, that is: filter frequency response, group
 * delay response, impulse response, step response and associated spinners
 * to control the maximum x-axis values shown on the charts.
 *
 * @author Piotr Szachewicz
 */
public class TimeDomainFilterResponseChartGroupPanel extends FilterResponseChartGroupPanel<TimeDomainSampleFilter> {

	/**
	 * The size of the calculated time domain responses (impulse and step responses)
	 * in seconds. Impulse and step response is calculated for the given
	 * number of seconds.
	 */
	protected static int TIME_DOMAIN_RESPONSES_SIZE_IN_SECONDS = 100;

	/**
	 * The initial value of the impulse and step response graph scale spinner.
	 */
	protected static int INITIAL_TIME_DOMAIN_RESPONSES_MAXIMUM_TIME_VALUE_IN_SECONDS = 4;

	/**
	 * A chart panel containing the filter frequency response chart.
	 */
	protected TimeDomainFilterFrequencyResponseChartPanel frequencyResponseChartPanel;

	/**
	 * A chart panel containing the filter group delay response chart.
	 */
	protected GroupDelayResponseChartPanel groupDelayResponseChartPanel;

	/**
	 * A chart panel containing the filter impulse response chart.
	 */
	protected ImpulseResponseChartPanel impulseResponseChartPanel;

	/**
	 * A chart panel containing the filter step response chart.
	 */
	protected StepResponseChartPanel stepResponseChartPanel;

	/**
	 * A panel containing frequency response chart panels (frequencyResponseChartPanel
	 * and groupDelayResponseChartPanel) with an associated spinner.
	 */
	protected FilterResponseChartPanelsWithGraphScaleSpinner frequencyResponseChartPanelWithSpinner;

	/**
	 * A panel containing time domain response chart panels
	 * (impulseResponseChartPanel and stepResponseChartPanel) with
	 * with an associated spinner.
	 */
	protected FilterResponseChartPanelsWithGraphScaleSpinner timeDomainResponseChartPanelWithSpinner;

	/**
	 * Calculator for calculating the frequency responses of a filter.
	 */
	protected FilterFrequencyResponseCalculator frequencyResponseCalculator;

	/**
	 * Calculator for calculating the time domain responses of a filter.
	 */
	protected FilterTimeDomainResponseCalculator timeDomainResponseCalculator;

	/**
	 * Constructor.
	 * @param currentFilter the filter to be visualized
	 */
	public  TimeDomainFilterResponseChartGroupPanel( TimeDomainSampleFilter currentFilter) {
		super( currentFilter);
	}

	@Override
	public void setSamplingFrequency(double samplingFrequency) {
		super.setSamplingFrequency(samplingFrequency);
		frequencyResponseChartPanelWithSpinner.setMaximumSpinnerValue(samplingFrequency / 2);
		frequencyResponseChartPanelWithSpinner.setCurrentSpinnerValue(samplingFrequency / 2);

	}

	@Override
	protected JPanel createChartGroupPanel() {

		JPanel panel = new JPanel(new GridLayout(1, 2, 5, 5));

		frequencyResponseChartPanelWithSpinner = createFrequencyResponsesPanel();
		timeDomainResponseChartPanelWithSpinner = createTimeDomainResponsesPanel();

		panel.add(frequencyResponseChartPanelWithSpinner);
		panel.add(timeDomainResponseChartPanelWithSpinner);

		return panel;

	}

	/**
	 * Creates the panel containing the filter frequency responses
	 * (frequency response and group delay).
	 * @return panel containing the filter frequency responses
	 */
	protected FilterResponseChartPanelsWithGraphScaleSpinner createFrequencyResponsesPanel() {

		frequencyResponseChartPanel = new TimeDomainFilterFrequencyResponseChartPanel();
		groupDelayResponseChartPanel = new GroupDelayResponseChartPanel();

		List<ResponseChartPanel> chartsList = new ArrayList<ResponseChartPanel>();
		chartsList.add(frequencyResponseChartPanel);
		chartsList.add(groupDelayResponseChartPanel);

		return new FilterResponseChartPanelsWithGraphScaleSpinner(chartsList, _("Maximum graph frequency [Hz]"));

	}

	/**
	 * Creates a panel containing the filter time domain responses
	 * (impulse response and step response).
	 * @return panel containing time domain responses
	 */
	protected FilterResponseChartPanelsWithGraphScaleSpinner createTimeDomainResponsesPanel() {

		impulseResponseChartPanel = new ImpulseResponseChartPanel();
		stepResponseChartPanel = new StepResponseChartPanel();

		List<ResponseChartPanel> chartsList = new ArrayList<ResponseChartPanel>();
		chartsList.add(impulseResponseChartPanel);
		chartsList.add(stepResponseChartPanel);
		FilterResponseChartPanelsWithGraphScaleSpinner chartPanel = new FilterResponseChartPanelsWithGraphScaleSpinner(chartsList, _("Maximum graph time value [s]"));
		chartPanel.setMaximumSpinnerValue(TIME_DOMAIN_RESPONSES_SIZE_IN_SECONDS);
		chartPanel.setCurrentSpinnerValue(INITIAL_TIME_DOMAIN_RESPONSES_MAXIMUM_TIME_VALUE_IN_SECONDS);
		return chartPanel;

	}

	/**
	 * Updates the filter responses shown on the charts.
	 * @param currentFilter the filter to be visualized
	 * @throws BadFilterParametersException thrown when the filter cannot
	 * be designed
	 */
	public void updateGraphs(TimeDomainSampleFilter currentFilter) throws BadFilterParametersException {

		FilterCoefficients coeffs = coeffs = IIRDesigner.designDigitalFilter(currentFilter);

		frequencyResponseCalculator = new FilterFrequencyResponseCalculator(512, getSamplingFrequency(), coeffs);
		calculateAndDrawFilterFrequencyResponse();
		calculateAndDrawGroupDelayResponse();

		timeDomainResponseCalculator = new FilterTimeDomainResponseCalculator(samplingFrequency, coeffs);
		calculateAndDrawImpulseResponse();
		calculateAndDrawStepResponse();

	}

	/**
	 * Calculates and plots current filter frequency response.
	 */
	protected void calculateAndDrawFilterFrequencyResponse() {

		FilterFrequencyResponse frequencyResponse = frequencyResponseCalculator.getMagnitudeResponse();

		double[] frequencies = frequencyResponse.getFrequencies();
		double[] values = frequencyResponse.getValues();

		frequencyResponseChartPanel.setData(frequencies, values);

		int filterOrder = frequencyResponseCalculator.getFilterCoefficients().getFilterOrder();
		String subtitleText = _R(("filter order = {0}"), new Object[]{filterOrder});

		frequencyResponseChartPanel.setSubtitle(subtitleText);

	}

	/**
	 * Calculates and plots current filter group delay response.
	 */
	protected void calculateAndDrawGroupDelayResponse() {

		FilterFrequencyResponse groupDelayResponse = frequencyResponseCalculator.getGroupDelayResponse();
		double[] frequencies = groupDelayResponse.getFrequencies();
		double[] values = groupDelayResponse.getValues();

		// removing the first element which is (very often) a singularity and spoils the plot
		frequencies = ArrayOperations.removeFirstElements(frequencies, 1);
		values = ArrayOperations.removeFirstElements(values, 1);

		groupDelayResponseChartPanel.setData(frequencies, values);

	}

	/**
	 * Calculates and plots current filter impulse response.
	 */
	protected void calculateAndDrawImpulseResponse() {
		FilterTimeDomainResponse impulseResponse = timeDomainResponseCalculator.getImpulseResponse(getNumberOfPointsForTimeDomainResponse());
		impulseResponseChartPanel.setData(impulseResponse);
	}

	/**
	 * Calculates and plots current filter step response.
	 */
	protected void calculateAndDrawStepResponse() {
		FilterTimeDomainResponse stepResponse = timeDomainResponseCalculator.getStepResponse(getNumberOfPointsForTimeDomainResponse());
		stepResponseChartPanel.setData(stepResponse);
	}

	/**
	 * Returns the number of points for which the time domain responses
	 * should be calculated.
	 * @return number of points which should be calculated for the time
	 * domain responses
	 */
	protected int getNumberOfPointsForTimeDomainResponse() {
		return (int) (TIME_DOMAIN_RESPONSES_SIZE_IN_SECONDS * samplingFrequency);
	}

	@Override
	protected String getChartGroupPanelTitle() {
		return _("Filter design graphs");
	}

}
