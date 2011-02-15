/* FFTFilterFrequencyResponseChartPanel.java created 2011-02-06
 *
 */
package org.signalml.app.view.montage.filters.charts.elements;

import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.axis.NumberTickUnit;
import org.springframework.context.support.MessageSourceAccessor;

/**
 *
 * @author Piotr Szachewicz
 */
public class FFTFrequencyResponseChartPanel extends FrequencyResponseChartPanel {

	public FFTFrequencyResponseChartPanel(MessageSourceAccessor messageSource) {
		super(messageSource);
		setTitle(messageSource.getMessage("editFFTSampleFilter.graphTitle"));
	}

	@Override
	public NumberAxis createRangeAxis() {
		NumberAxis axis = createAxis(0, 1);
		axis.setTickUnit(new NumberTickUnit(1));
		return axis;
	}
}
