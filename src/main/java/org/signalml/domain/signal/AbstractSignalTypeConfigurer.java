/* AbstractSignalTypeConfigurer.java created 2008-02-01
 *
 */

package org.signalml.domain.signal;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import org.signalml.domain.montage.Montage;

import org.signalml.domain.montage.filter.SampleFilterDefinition;
import org.signalml.domain.montage.filter.TimeDomainSampleFilter;
import org.signalml.domain.montage.filter.iirdesigner.ApproximationFunctionType;
import org.signalml.domain.montage.filter.iirdesigner.FilterType;

/**
 * This abstract class represents a configurer of {@link Montage montages}.
 * Contains 3 predefined filters ({@link FilterType#LOWPASS low-} and
 * {@link FilterType#HIGHPASS high}-pass).
 *
 * @author Michal Dobaczewski &copy; 2007-2008 CC Otwarte Systemy Komputerowe Sp. z o.o.
 */
public abstract class AbstractSignalTypeConfigurer implements SignalTypeConfigurer {

	private static final List<SampleFilterDefinition> predefinedFilters = getAllPredefinedFilters();

        /**
         * Creates a constant list of predefined
         * {@link SampleFilterDefinition filters}. It consists of predefined
         * low-pass, band-pass and high-pass sample filter.
         * @return a constant list of predefined filters
         */
	private static List<SampleFilterDefinition> getAllPredefinedFilters() {

		ArrayList<SampleFilterDefinition> filters = new ArrayList<SampleFilterDefinition>();

		filters.add(new TimeDomainSampleFilter(FilterType.LOWPASS, ApproximationFunctionType.BUTTERWORTH,
		                                       new double[] {20, 0}, new double[] {30, 0}, 5.0, 20.0));

		filters.add(new TimeDomainSampleFilter(FilterType.HIGHPASS, ApproximationFunctionType.CHEBYSHEV1,
		                                       new double[] {6, 0}, new double[] {3, 0}, 3.0, 40.0)
		           );

		return Collections.unmodifiableList(filters);
	}

	@Override
	public int getPredefinedFilterCount() {
		return predefinedFilters.size();
	}

	@Override
	public Collection<SampleFilterDefinition> getPredefinedFilters() {
		return predefinedFilters;
	}

	@Override
	public SampleFilterDefinition getPredefinedFilterAt(int index) {
		return predefinedFilters.get(index);
	}

}
