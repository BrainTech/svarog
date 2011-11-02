/* StepResponseChartPanel.java created 2011-02-12
 *
 */
package org.signalml.app.view.montage.filters.charts.elements;

import static org.signalml.app.SvarogApplication._;

/**
 * A chart panel used to show the step response of a filter.
 * @author Piotr Szachewicz
 */
public class StepResponseChartPanel extends TimeDomainResponseChartPanel {

	/**
	 * Constructor.
	 */
	public  StepResponseChartPanel() {
		super();
		setTitle(_("Step response [amplitude]"));
	}

}
