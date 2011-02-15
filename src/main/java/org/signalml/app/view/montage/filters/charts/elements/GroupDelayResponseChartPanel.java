/* GroupDelayFrequencyResponseChartPanel.java created 2011-02-12
 *
 */
package org.signalml.app.view.montage.filters.charts.elements;

import org.jfree.chart.axis.NumberAxis;
import org.springframework.context.support.MessageSourceAccessor;

/**
 *
 * @author Piotr Szachewicz
 */
public class GroupDelayResponseChartPanel extends ResponseChartPanel {

	public GroupDelayResponseChartPanel(MessageSourceAccessor messageSource) {
		super(messageSource);
		setTitle(messageSource.getMessage("editTimeDomainSampleFilter.groupDelayGraphTitle"));
	}

	@Override
	protected NumberAxis createRangeAxis() {
		return new NumberAxis("");
	}

	@Override
	public String getDomainAxisName() {
		return messageSource.getMessage("editSampleFilter.graphFrequencyLabel");
	}
}
