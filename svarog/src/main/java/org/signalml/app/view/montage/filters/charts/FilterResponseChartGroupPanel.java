/* FilterResponseGraphsPanel.java created 2011-02-07
 *
 */

package org.signalml.app.view.montage.filters.charts;

import java.awt.BorderLayout;
import javax.swing.JPanel;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
import org.signalml.domain.montage.filter.SampleFilterDefinition;

/**
 * This class represents a panel containing chart panels with graph scale spinners.
 * which can be used to plot filter responses for various filter types.
 *
 * @author Piotr Szachewicz
 */
public abstract class FilterResponseChartGroupPanel<T extends SampleFilterDefinition> extends JPanel {

	/**
	 * The filter which is currently visualized on the plots.
	 */
	protected T currentFilter;

	/**
	 * The sampling frequency for which the plots are being drawn.
	 */
	protected double samplingFrequency;

	/**
	 * Constructor.
	 * @param currentFilter the filter which will be visualized
	 */
	public FilterResponseChartGroupPanel(T currentFilter) {
		this.currentFilter = currentFilter;
		createInterface();
	}

	/**
	 * Sets a filter to be visualized on the charts.
	 * @param filter the filter to be visualized
	 */
	public void setFilter(T filter) {
		this.currentFilter = filter;
	}

	/**
	 * Returns the sampling frequency which is used to calculate the charts.
	 * @return sampling frequency which is used to calculate the charts
	 */
	public double getSamplingFrequency() {
		return samplingFrequency;
	}

	/**
	 * Sets the sampling frequency which will be used to calculate the
	 * responses shown on the charts
	 * @param samplingFrequency new value of sampling frequency
	 */
	public void setSamplingFrequency(double samplingFrequency) {
		this.samplingFrequency = samplingFrequency;
	}

	/**
	 * Creates and configures all components for this panel.
	 */
	protected void createInterface() {
		this.setLayout(new BorderLayout());
		JPanel chartsPanel = createChartGroupPanel();
		JPanel chartsPanelWithBorder = addBorderToThePanel(chartsPanel, getChartGroupPanelTitle());
		this.add(chartsPanelWithBorder);
	}

	/**
	 * Adds a titled border to the given panel.
	 * @param panel a panel to which the border will be added
	 * @param title title of the border
	 * @return the panel with a border
	 */
	protected JPanel addBorderToThePanel(JPanel panel, String title) {
		JPanel borderedPanel = new JPanel(new BorderLayout(6, 6));

		CompoundBorder border = new CompoundBorder(
			new TitledBorder(title),
			new EmptyBorder(3, 3, 3, 3));
		borderedPanel.setBorder(border);

		borderedPanel.add(panel);
		return borderedPanel;
	}

	/**
	 * Creates a panel containing all charts and controls but without
	 * a border.
	 * @return a panel containing all charts and controls
	 */
	protected abstract JPanel createChartGroupPanel();

	/**
	 * Returns the title for the chart group.
	 * @return the title for the chart group
	 */
	protected abstract String getChartGroupPanelTitle();

}
