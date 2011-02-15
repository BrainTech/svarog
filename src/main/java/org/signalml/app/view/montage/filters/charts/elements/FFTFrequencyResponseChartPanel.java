/* FFTFilterFrequencyResponseChartPanel.java created 2011-02-06
 *
 */
package org.signalml.app.view.montage.filters.charts.elements;

import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.axis.NumberTickUnit;
import org.springframework.context.support.MessageSourceAccessor;

/**
 * A chart panel containing the FFT frequency response chart panel
 * (i.e. coefficients for each frequency).
 * @author Piotr Szachewicz
 */
public class FFTFrequencyResponseChartPanel extends FrequencyResponseChartPanel {

	/**
	 * Constructor.
	 * @param messageSource message source capable of resolving localized
	 * messages
	 */
	public FFTFrequencyResponseChartPanel(MessageSourceAccessor messageSource) {
		super(messageSource);
		setTitle(messageSource.getMessage("editFFTSampleFilter.graphTitle"));
	}

	@Override
	public NumberAxis createRangeAxis() {
		NumberAxis axis = createNonLogarithmicAxis(0, 1);
		axis.setTickUnit(new NumberTickUnit(1));
		return axis;
	}

}
