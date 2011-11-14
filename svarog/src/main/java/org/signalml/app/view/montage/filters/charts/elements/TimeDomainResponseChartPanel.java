/* TimeDomainResponseChartPanel.java created 2011-02-12
 *
 */
package org.signalml.app.view.montage.filters.charts.elements;

import static org.signalml.app.SvarogApplication._;
import org.jfree.chart.axis.NumberAxis;
import org.signalml.domain.montage.filter.iirdesigner.FilterTimeDomainResponse;

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
		this.setData(timeDomainResponse.getTime(), timeDomainResponse.getValues());
	}

}
