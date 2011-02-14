/* FilterResponseGraphsPanel.java created 2011-02-07
 *
 */

package org.signalml.app.view.montage.charts;

import javax.swing.JPanel;
import org.signalml.domain.montage.filter.SampleFilterDefinition;
import org.springframework.context.support.MessageSourceAccessor;

/**
 *
 * @author Piotr Szachewicz
 */
public abstract class FilterResponseChartGroupPanel<T extends SampleFilterDefinition> extends JPanel {

	protected T currentFilter;

	protected final MessageSourceAccessor messageSource;
	protected double samplingFrequency;

	public FilterResponseChartGroupPanel(MessageSourceAccessor messageSource, T currentFilter) {
		this.messageSource = messageSource;
		this.currentFilter = currentFilter;
		createInterface();
	}

	public void setFilter(T filter) {
		this.currentFilter = filter;
	}

	public double getSamplingFrequency() {
		return samplingFrequency;
	}

	protected abstract void createInterface();

}
