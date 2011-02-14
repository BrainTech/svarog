/* TimeDomainFrequencyResponseChartPanel.java created 2011-02-06
 *
 */
package org.signalml.app.view.montage.charts.elements;

import java.awt.Color;
import java.awt.event.MouseEvent;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.title.TextTitle;
import org.jfree.ui.HorizontalAlignment;
import org.jfree.ui.RectangleEdge;
import org.jfree.ui.RectangleInsets;
import org.jfree.ui.VerticalAlignment;
import org.springframework.context.support.MessageSourceAccessor;

/**
 * A {@link FrequencyResponseChartPanel} used to draw filter's frequency
 * response on it.
 * @author Piotr Szachewicz
 */
public class TimeDomainFilterFrequencyResponseChartPanel extends FrequencyResponseChartPanel {

	public TimeDomainFilterFrequencyResponseChartPanel(MessageSourceAccessor messageSource) {
		super(messageSource);
		setTitle(messageSource.getMessage("editTimeDomainSampleFilter.frequencyResponseGraphTitle"));
	}

	@Override
	public NumberAxis createRangeAxis() {
		return createLogarithmicAxis(-100, 0);
	}

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
	}

	@Override
	public void mouseClicked(MouseEvent ev) {
	}

	@Override
	public void mouseDragged(MouseEvent ev) {
	}
}
