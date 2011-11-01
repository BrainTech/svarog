/* FFTFilterFrequencyResponseChartPanel.java created 2011-02-06
 *
 */
package org.signalml.app.view.montage.filters.charts.elements;

import static org.signalml.app.SvarogApplication._;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.axis.NumberTickUnit;

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
	public  FFTFrequencyResponseChartPanel() {
		super();
		setTitle(_("FFT coefficients"));
	}

	@Override
	public NumberAxis createRangeAxis() {
		NumberAxis axis = createNonLogarithmicAxis(0, 1);
		axis.setTickUnit(new NumberTickUnit(1));
		return axis;
	}

}
