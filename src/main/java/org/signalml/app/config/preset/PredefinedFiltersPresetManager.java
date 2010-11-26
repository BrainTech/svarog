/* PredefinedFiltersPresetManager.java created 2010-11-26
 *
 */

package org.signalml.app.config.preset;

import com.thoughtworks.xstream.XStream;

import java.io.IOException;
import java.util.List;

import org.signalml.app.config.AbstractXMLConfiguration;
import org.signalml.domain.montage.filter.SampleFilterDefinition;
import org.signalml.domain.montage.filter.TimeDomainSampleFilter;

/**
 * This class holds presets a list of predefined {@link PredefinedFiltersConfiguration}
 * for each sampling frequency.
 *
 * @author Piotr Szachewicz
 */
public abstract class PredefinedFiltersPresetManager extends AbstractXMLConfiguration {

	private List<PredefinedFiltersConfiguration> predefinedTimeDomainFilters;

	/**
	 * Finds a {@link PredefinedFilterConfiguration} for the given sampling
	 * frequency.
	 * @param samplingFrequency sampling frequency for which the returned
	 * {@link PredefinedFilterConfiguration} must operate.
	 * @return a {@link PredefinedFilterConfiguration} for the given sampling
	 * frequency. If no {@link PredefinedFilterConfiguration} exists for
	 * the given sampling frequency, null is returned.
	 */
	private PredefinedFiltersConfiguration findConfiguration(double samplingFrequency) {

		for (PredefinedFiltersConfiguration p: predefinedTimeDomainFilters) {
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
	public List<SampleFilterDefinition> getPredefinedFilters(double samplingFrequency) {

		PredefinedFiltersConfiguration p = findConfiguration(samplingFrequency);

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
	public SampleFilterDefinition getCustomFilterStartingPoint(double samplingFrequency) {

		PredefinedFiltersConfiguration p = findConfiguration(samplingFrequency);

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
	public SampleFilterDefinition getCustomStartingPoint() {

		if (predefinedTimeDomainFilters.size() > 0) {
			return predefinedTimeDomainFilters.get(0).getCustomFilterStartingPoint();
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

		PredefinedFiltersConfiguration p = findConfiguration(samplingFrequency);

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
	public SampleFilterDefinition getPredefinedFilterAt(double samplingFrequency, int index) {

		PredefinedFiltersConfiguration p = findConfiguration(samplingFrequency);

		if (p != null)
			return (SampleFilterDefinition) p.getPredefinedFilters().get(index);
		return null;

	}

	/**
	 * Returns the {@link XStream} which will be used with this
	 * {@link AbstractXMLConfiguration}.
	 * @return the streamer to be used
	 */
	@Override
	public XStream getStreamer() {

		if (streamer == null)
			streamer = createFilterPresetManagerStreamer();
		return streamer;

	}

	/**
	 * Creates an {@link XStream} which should be used with this preset manager.
	 * @return a streamer to be used to stream preset data to XML
	 */
	protected abstract XStream createFilterPresetManagerStreamer();

	/**
	 * Loads default predefined filters from a file.
	 * @throws IOException when an error occurs while reading the file containing
	 * default predefined filters
	 */
	public abstract void loadDefaults() throws IOException;

}
