/* FilterResponseGraphsPanel.java created 2011-02-07
 *
 */

package org.signalml.app.view.montage.filters.charts;

import javax.swing.JPanel;
import org.signalml.domain.montage.filter.SampleFilterDefinition;
import org.springframework.context.support.MessageSourceAccessor;

/**
 * This class represents a panel containing chart panels with graph scale spinners.
 * which can be used to plot filter responses for various filter types.
 *
 * @author Piotr Szachewicz
 */
public abstract class FilterResponseChartGroupPanel<T extends SampleFilterDefinition> extends JPanel {

	/**
	 * Message source capable of resolving localized messages.
	 */
	protected final MessageSourceAccessor messageSource;

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
	 * @param messageSource message source capable of resolving localized messages
	 * @param currentFilter the filter which will be visualized
	 */
	public FilterResponseChartGroupPanel(MessageSourceAccessor messageSource, T currentFilter) {
		this.messageSource = messageSource;
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
	protected abstract void createInterface();

}
