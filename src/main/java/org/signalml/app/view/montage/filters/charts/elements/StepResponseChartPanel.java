/* StepResponseChartPanel.java created 2011-02-12
 *
 */
package org.signalml.app.view.montage.filters.charts.elements;

import org.springframework.context.support.MessageSourceAccessor;

/**
 *
 * @author Piotr Szachewicz
 */
public class StepResponseChartPanel extends TimeDomainResponseChartPanel {

	public StepResponseChartPanel(MessageSourceAccessor messageSource) {
		super(messageSource);
		setTitle(messageSource.getMessage("editTimeDomainSampleFilter.stepResponseGrapTitle"));
	}
}
