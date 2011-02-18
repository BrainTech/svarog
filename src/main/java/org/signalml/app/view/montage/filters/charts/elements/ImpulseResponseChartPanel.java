/* ImpulseResponseChartPanel.java created 2011-02-12
 *
 */
package org.signalml.app.view.montage.filters.charts.elements;

import org.springframework.context.support.MessageSourceAccessor;

/**
 * A chart panel containing a filter impulse response.
 * @author Piotr Szachewicz
 */
public class ImpulseResponseChartPanel extends TimeDomainResponseChartPanel {

	/**
	 * Constructor.
	 * @param messageSource message source capable of resolving localized
	 * messages
	 */
	public ImpulseResponseChartPanel(MessageSourceAccessor messageSource) {
		super(messageSource);
		setTitle(messageSource.getMessage("editTimeDomainSampleFilter.impulseResponseGrapTitle"));
	}

}
