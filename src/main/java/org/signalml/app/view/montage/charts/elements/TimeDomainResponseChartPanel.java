/* TimeDomainResponseChartPanel.java created 2011-02-12
 *
 */
package org.signalml.app.view.montage.charts.elements;

import org.jfree.chart.axis.NumberAxis;
import org.signalml.domain.montage.filter.iirdesigner.FilterTimeDomainResponse;
import org.springframework.context.support.MessageSourceAccessor;

/**
 *
 * @author Piotr Szachewicz
 */
public class TimeDomainResponseChartPanel extends ResponseChartPanel {

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

	public void setData(FilterTimeDomainResponse timeDomainResponse) {
		this.setData(timeDomainResponse.getTime(), timeDomainResponse.getValues());
	}
}
