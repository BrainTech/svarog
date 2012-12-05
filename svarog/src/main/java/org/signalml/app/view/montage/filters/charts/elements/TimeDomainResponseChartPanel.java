/* TimeDomainResponseChartPanel.java created 2011-02-12
 *
 */
package org.signalml.app.view.montage.filters.charts.elements;

import static org.signalml.app.util.i18n.SvarogI18n._;

import org.jfree.chart.axis.NumberAxis;
import org.signalml.math.iirdesigner.FilterTimeDomainResponse;

/**
 * A chart panel used for displaying time domain responses (impulse response
 * or step response) of a filter.
 * @author Piotr Szachewicz
 */
public class TimeDomainResponseChartPanel extends ResponseChartPanel {

	/**
	 * Constructor.
	 */
	public TimeDomainResponseChartPanel() {
		super();
	}

	@Override
	public String getDomainAxisName() {
		return _("Time [s]");
	}

	@Override
	protected NumberAxis createRangeAxis() {
		return new NumberAxis("");
	}

	/**
	 * Sets the time domain response to be shown on the chart.
	 * @param timeDomainResponse response to be shown
	 */
	public void setData(FilterTimeDomainResponse timeDomainResponse) {

		double[] values = timeDomainResponse.getValues();
		double[] fixedValues = limitTheValuesIfInstableFilter(values);

		this.setData(timeDomainResponse.getTime(), fixedValues);
	}

	/**
	 * Limits the largest value in the time response to {@link FilterTimeDomainResponse#INSTABILITY_THRESHOLD}.
	 * The values larger than that are set to be equal to {@link Double#NaN}.
	 *
	 * @param values the time response values
	 * @return the array containing the limited values
	 */
	protected double[] limitTheValuesIfInstableFilter(double[] values) {
		double[] limitedValues = new double[values.length];

		for (int i = 0; i < values.length; i++)
			if (Math.abs(values[i]) > FilterTimeDomainResponse.INSTABILITY_THRESHOLD)
				limitedValues[i] = Double.NaN;
			else
				limitedValues[i] = values[i];

		return limitedValues;
	}

}
