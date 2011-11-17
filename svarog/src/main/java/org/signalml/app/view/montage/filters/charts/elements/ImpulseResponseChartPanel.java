/* ImpulseResponseChartPanel.java created 2011-02-12
 *
 */
package org.signalml.app.view.montage.filters.charts.elements;

import static org.signalml.app.SvarogI18n._;

/**
 * A chart panel containing a filter impulse response.
 * @author Piotr Szachewicz
 */
public class ImpulseResponseChartPanel extends TimeDomainResponseChartPanel {

	/**
	 * Constructor.
	 */
	public ImpulseResponseChartPanel() {
		super();
		setTitle(_("Impulse response [amplitude]"));
	}

}
