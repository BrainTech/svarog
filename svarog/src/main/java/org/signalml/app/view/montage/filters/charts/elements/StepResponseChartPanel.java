/* StepResponseChartPanel.java created 2011-02-12
 *
 */
package org.signalml.app.view.montage.filters.charts.elements;

import org.springframework.context.support.MessageSourceAccessor;

/**
 * A chart panel used to show the step response of a filter.
 * @author Piotr Szachewicz
 */
public class StepResponseChartPanel extends TimeDomainResponseChartPanel {

	/**
	 * Constructor.
	 * @param messageSource message source capable of resolving localized
	 * messages
	 */
	public StepResponseChartPanel(MessageSourceAccessor messageSource) {
		super(messageSource);
		setTitle(messageSource.getMessage("editTimeDomainSampleFilter.stepResponseGrapTitle"));
	}

}
