/* GroupDelayFrequencyResponseChartPanel.java created 2011-02-12
 *
 */
package org.signalml.app.view.montage.filters.charts.elements;

import org.jfree.chart.axis.NumberAxis;
import static org.signalml.app.util.i18n.SvarogI18n._;

/**
 * A chart panel containing a group delay filter response.
 * @author Piotr Szachewicz
 */
public class GroupDelayResponseChartPanel extends ResponseChartPanel {

	/**
	 * Constructor.
	 */
	public GroupDelayResponseChartPanel() {
		super();
		setTitle(_("Group delay [samples]"));
	}

	@Override
	protected NumberAxis createRangeAxis() {
		return new NumberAxis("");
	}

	@Override
	public String getDomainAxisName() {
		return _("Frequency [Hz]");
	}

}
