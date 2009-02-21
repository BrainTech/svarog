/* AbstractSignalTypeConfigurer.java created 2008-02-01
 * 
 */

package org.signalml.domain.signal;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import org.signalml.domain.montage.filter.SampleFilterDefinition;
import org.signalml.domain.montage.filter.predefined.BandPassSampleFilter;
import org.signalml.domain.montage.filter.predefined.HighPassSampleFilter;
import org.signalml.domain.montage.filter.predefined.LowPassSampleFilter;

/** AbstractSignalTypeConfigurer
 *
 * 
 * @author Michal Dobaczewski &copy; 2007-2008 CC Otwarte Systemy Komputerowe Sp. z o.o.
 */
public abstract class AbstractSignalTypeConfigurer implements SignalTypeConfigurer {

	private static final LowPassSampleFilter lowPassSampleFilter = new LowPassSampleFilter();
	private static final BandPassSampleFilter bandPassSampleFilter = new BandPassSampleFilter();
	private static final HighPassSampleFilter highPassSampleFilter = new HighPassSampleFilter();

	private static final List<SampleFilterDefinition> predefinedFilters = getAllPredefinedFilters();

	private static List<SampleFilterDefinition> getAllPredefinedFilters() {
		ArrayList<SampleFilterDefinition> filters = new ArrayList<SampleFilterDefinition>();
		filters.add( lowPassSampleFilter );
		filters.add( bandPassSampleFilter );
		filters.add( highPassSampleFilter );
				
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
