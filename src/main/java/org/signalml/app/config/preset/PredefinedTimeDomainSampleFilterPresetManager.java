/* TimeDomainSampleFiltersConfiguration.java created 2010-11-25
 *
 */

package org.signalml.app.config.preset;

import java.util.ArrayList;
import java.util.List;
import org.signalml.domain.montage.filter.TimeDomainSampleFilter;
import org.signalml.domain.montage.filter.iirdesigner.ApproximationFunctionType;
import org.signalml.domain.montage.filter.iirdesigner.FilterType;

/**
 * This class holds presets a list of predefined {@link TimeDomainSampleFilter filters}
 * for each sampling frequency.
 *
 * @author Piotr Szachewicz
 */
public class PredefinedTimeDomainSampleFilterPresetManager {

	private List<PredefinedFilterConfiguration> predefinedFilterConfiguration = getPredefinedFilterConfiguration();

	private List<PredefinedFilterConfiguration> getPredefinedFilterConfiguration() {

		PredefinedFilterConfiguration filterConfiguration = new PredefinedFilterConfiguration(128.0);

		filterConfiguration.addPredefinedFilter(new TimeDomainSampleFilter(FilterType.LOWPASS,
			ApproximationFunctionType.BUTTERWORTH, new double[] {20, 0}, new double[] {30, 0}, 5.0, 20.0));

		filterConfiguration.addPredefinedFilter(new TimeDomainSampleFilter(FilterType.HIGHPASS,
			ApproximationFunctionType.CHEBYSHEV1, new double[] {6, 0}, new double[] {3, 0}, 3.0, 40.0));

		filterConfiguration.addCustomFilterStartingPoint(new TimeDomainSampleFilter(FilterType.HIGHPASS,
			ApproximationFunctionType.CHEBYSHEV2, new double[] {6, 0}, new double[] {3, 0}, 3.0, 40.0));


		List<PredefinedFilterConfiguration> list = new ArrayList<PredefinedFilterConfiguration>();
		list.add(filterConfiguration);
		return list;

	}

	/**
	 * Finds a {@link PredefinedFilterConfiguration} for the given sampling
	 * frequency.
	 * @param samplingFrequency sampling frequency for which the returned
	 * {@link PredefinedFilterConfiguration} must operate.
	 * @return a {@link PredefinedFilterConfiguration} for the given sampling
	 * frequency. If no {@link PredefinedFilterConfiguration} exists for
	 * the given sampling frequency, null is returned.
	 */
	private PredefinedFilterConfiguration findConfiguration(double samplingFrequency) {

		for (PredefinedFilterConfiguration p: predefinedFilterConfiguration) {
			if (p.getSamplingFrequency() == samplingFrequency) {
				return p;
			}
		}
		return null;

	}

	/**
	 * Returns the list of predefined {@link TimeDomainSampleFilter filters}
	 * for the given sampling frequency.
	 * @param samplingFrequency the sampling frequency for which the filters
	 * were designed
	 * @return the list of predefined filters
	 */
	public List<TimeDomainSampleFilter> getPredefinedFilters(double samplingFrequency) {

		PredefinedFilterConfiguration p = findConfiguration(samplingFrequency);

		if (p != null)
			return p.getPredefinedFilters();
		return null;


	}

	/**
	 * Returns the filter which should be the starting point when designing
	 * custom filter in the filter designer's window.
	 * @param samplingFrequency sampling frequency at which the filter will
	 * be designed
	 * @return the {@link TimeDomainSampleFilter filter} which should be
	 * displayed when creating custom filter
	 */
	public TimeDomainSampleFilter getCustomFilterStartingPoint(double samplingFrequency) {

		PredefinedFilterConfiguration p = findConfiguration(samplingFrequency);

		if (p != null)
			return p.getCustomFilterStartingPoint();
		return null;

	}

	/**
	 * Returns a custom filter design starting point. This method should
	 * be used only if method
	 * {@link PredefinedTimeDomainSampleFilterPresetManager#getCustomFilterStartingPoint(double)}
	 * does not return any filter.
	 * @return custom starting point which could be used while starting to
	 * design a custom filter
	 */
	public TimeDomainSampleFilter getCustomStartingPoint() {

		if (predefinedFilterConfiguration.size() > 0) {
			return predefinedFilterConfiguration.get(0).getCustomFilterStartingPoint();
		}
		return null;

	}

	/**
         * Returns the number of {@link TimeDomainSampleFilter filters} predefined
	 * for the given sampling frequency.
         *
         * @return the number of predefined filters
         */
	public int getPredefinedFilterCount(double samplingFrequency) {

		PredefinedFilterConfiguration p = findConfiguration(samplingFrequency);

		if (p != null)
			return p.getNumberOfPredefinedFilters();
		return 0;

	}

	/**
	 * Returns the predefined {@link TimeDomainSampleFilter filters} of
	 * a specified index defined for the specified sampling frequency.
	 * @param samplingFrequency sampling frequency for which the returned
	 * filter should be designed
	 * @param index the index of the filter to be returned
	 * @return the predefined filter of the specified index, designed for
	 * the given sampling frequency
	 */
	public TimeDomainSampleFilter getPredefinedFilterAt(double samplingFrequency, int index) {

		PredefinedFilterConfiguration p = findConfiguration(samplingFrequency);

		if (p != null)
			return p.getPredefinedFilters().get(index);
		return null;

	}

	/**
	 * This class holds predefined filters for one given sampling frequency.
	 */
	private class PredefinedFilterConfiguration {

		/**
		 * the sampling frequency at which the filters stored in this class
		 * should operate
		 */
		private double samplingFrequency;

		/**
		 * a list containing predefined filters which can be used on
		 * signals having the given sampling frequency
		 */
		private List<TimeDomainSampleFilter> predefinedFilters = new ArrayList<TimeDomainSampleFilter>();

		/**
		 * a filter which can be used as a starting point when designing
		 * a new (custom) filter
		 */
		private TimeDomainSampleFilter customFilterStartingPoint;

		/**
		 * Constructor. Creates an empty set of predefined filters for
		 * the given sampling frequency.
		 * @param samplingFrequency the sampling frequency at which filters
		 * stored in this class should operate.
		 */
		public PredefinedFilterConfiguration(double samplingFrequency) {
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
		 * @param timeDomainSampleFilter a {@link TimeDomainSampleFilter}
		 * to be added
		 */
		public void addPredefinedFilter(TimeDomainSampleFilter timeDomainSampleFilter) {
			predefinedFilters.add(timeDomainSampleFilter);
		}

		/**
		 * Adds a {@link TimeDomainSampleFilter} which should be used
		 * as a starting point to design new filters.
		 * @param timeDomainSampleFilter a {@link TimeDomainSampleFilter}
		 * to be added
		 */
		public void addCustomFilterStartingPoint(TimeDomainSampleFilter timeDomainSampleFilter) {
			customFilterStartingPoint = timeDomainSampleFilter;
		}

		/**
		 * Returns a list containing predefined filters stored in this
		 * class (except for customFilterStartingPoint).
		 * @return a list of filters
		 */
		public List<TimeDomainSampleFilter> getPredefinedFilters() {
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
		public TimeDomainSampleFilter getCustomFilterStartingPoint() {
			return customFilterStartingPoint;
		}

	}
}
