/* TimeDomainFrequencyResponseChartPanel.java created 2011-02-06
 *
 */
package org.signalml.app.view.montage.filters.charts.elements;

import static org.signalml.app.SvarogI18n._;
import java.awt.Color;
import java.awt.event.MouseEvent;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.title.TextTitle;
import org.jfree.ui.HorizontalAlignment;
import org.jfree.ui.RectangleEdge;
import org.jfree.ui.RectangleInsets;
import org.jfree.ui.VerticalAlignment;

/**
 * A {@link FrequencyResponseChartPanel} used to draw filter's frequency
 * response on it.
 * @author Piotr Szachewicz
 */
public class TimeDomainFilterFrequencyResponseChartPanel extends FrequencyResponseChartPanel {

	/**
	 * Constructor.
	 */
	public TimeDomainFilterFrequencyResponseChartPanel() {
		super();
		setTitle(_("Filter frequency response [decibels]"));
	}

	@Override
	public NumberAxis createRangeAxis() {
		return createLogarithmicAxis(-100, 0);
	}

	/**
	 * Sets a subitle for this plot. Used to show the filter order.
	 * @param subtitleText he text of the subtitle
	 */
	public void setSubtitle(String subtitleText) {
		JFreeChart chart = this.getChart();

		chart.clearSubtitles();

		chart.addSubtitle(
			new TextTitle(subtitleText,
			createDefaultFont(),
			Color.BLACK,
			RectangleEdge.TOP,
			HorizontalAlignment.RIGHT, VerticalAlignment.TOP,
			new RectangleInsets(0, 0, 0, 9)));
	}

	@Override
	public void mouseReleased(MouseEvent ev) {
		/* TODO: not implemented yet.
		 * these methods could be used to handle the selections made
		 * on the frequency response chart.
		 */
	}

	@Override
	public void mousePressed(MouseEvent e) {
	}

	@Override
	public void mouseDragged(MouseEvent ev) {
	}
}
