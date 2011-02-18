/* TimeDomainResponseChartPanel.java created 2011-02-12
 *
 */
package org.signalml.app.view.montage.filters.charts.elements;

import org.jfree.chart.axis.NumberAxis;
import org.signalml.domain.montage.filter.iirdesigner.FilterTimeDomainResponse;
import org.springframework.context.support.MessageSourceAccessor;

/**
 * A chart panel used for displaying time domain responses (impulse response
 * or step response) of a filter.
 * @author Piotr Szachewicz
 */
public class TimeDomainResponseChartPanel extends ResponseChartPanel {

	/**
	 * Constructor.
	 * @param messageSource message source capable of resolving localized
	 * messages
	 */
	public TimeDomainResponseChartPanel(MessageSourceAccessor messageSource) {
		super(messageSource);
	}

	@Override
	public String getDomainAxisName() {
		return messageSource.getMessage("editTimeDomainSampleFilter.graphTimeLabel");
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
