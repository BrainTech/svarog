/* PredefinedFilterConfiguration.java created 2010-11-26
 *
 */
package org.signalml.app.config.preset;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import java.util.ArrayList;
import java.util.List;
import org.signalml.domain.montage.filter.SampleFilterDefinition;

/**
 * This class holds predefined filters for one given sampling frequency.
 *
 * @author Piotr Szachewicz
 */
@XStreamAlias("filterConfiguration")
public class PredefinedFiltersConfiguration<FilterDefinition extends SampleFilterDefinition> {

	/**
	 * the sampling frequency at which the filters stored in this class
	 * should operate
	 */
	private double samplingFrequency;
	/**
	 * a list containing predefined filters which can be used on
	 * signals having the given sampling frequency
	 */
	private List<FilterDefinition> predefinedFilters = new ArrayList<FilterDefinition>();
	/**
	 * a filter which can be used as a starting point when designing
	 * a new (custom) filter
	 */
	private FilterDefinition customFilterStartingPoint;

	public PredefinedFiltersConfiguration() {
	}

	/**
	 * Constructor. Creates an empty set of predefined filters for
	 * the given sampling frequency.
	 * @param samplingFrequency the sampling frequency at which filters
	 * stored in this class should operate.
	 */
	public PredefinedFiltersConfiguration(double samplingFrequency) {
		this.samplingFrequency = samplingFrequency;
	}

	/**
	 * Returns the sampling frequency at which the filters stored
	 * in this class should operate.
	 * @return the sampling frequency at which this filter configuration
	 * should operate.
	 */
	public double getSamplingFrequency() {
		return samplingFrequency;
	}

	/**
	 * Adds a {@link TimeDomainSampleFilter} to this class.
	 * @param filterDefinition a {@link TimeDomainSampleFilter}
	 * to be added
	 */
	public void addPredefinedFilter(FilterDefinition filterDefinition) {
		predefinedFilters.add(filterDefinition);
	}

	/**
	 * Adds a {@link TimeDomainSampleFilter} which should be used
	 * as a starting point to design new filters.
	 * @param filterDefinition a {@link TimeDomainSampleFilter}
	 * to be added
	 */
	public void addCustomFilterStartingPoint(FilterDefinition filterDefinition) {
		customFilterStartingPoint = filterDefinition;
	}

	/**
	 * Returns a list containing predefined filters stored in this
	 * class (except for customFilterStartingPoint).
	 * @return a list of filters
	 */
	public List<FilterDefinition> getPredefinedFilters() {
		return predefinedFilters;
	}

	/**
	 * Returns the number of predefined filters stored in this class
	 * (excluding customFilterStartingPoint).
	 * @return the number of filters stored
	 */
	public int getNumberOfPredefinedFilters() {
		return predefinedFilters.size();
	}

	/**
	 * Returns the custom filter starting point for designing new
	 * filters (this filter is shown in a filter designer after choosing
	 * 'Add custom filter' option).
	 * @return the {@link TimeDomainSampleFilter} which should be
	 * used as a starting point to design new filters.
	 */
	public FilterDefinition getCustomFilterStartingPoint() {
		return customFilterStartingPoint;
	}
}
