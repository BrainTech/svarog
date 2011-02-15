/* GroupDelayFrequencyResponseChartPanel.java created 2011-02-12
 *
 */
package org.signalml.app.view.montage.filters.charts.elements;

import org.jfree.chart.axis.NumberAxis;
import org.springframework.context.support.MessageSourceAccessor;

/**
 * A chart panel containing a group delay filter response.
 * @author Piotr Szachewicz
 */
public class GroupDelayResponseChartPanel extends ResponseChartPanel {

	/**
	 * Constructor.
	 * @param messageSource message source capable of resolving localized
	 * messages
	 */
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
