/* TimeDomainSampleFiltersConfiguration.java created 2010-11-25
 *
 */
package org.signalml.app.config.preset.managers;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.annotations.XStreamAlias;
import java.io.BufferedInputStream;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import org.signalml.app.util.XMLUtils;
import org.signalml.domain.montage.filter.SampleFilterDefinition;
import org.signalml.domain.montage.filter.TimeDomainSampleFilter;

import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;

/**
 * This class holds presets a list of predefined {@link TimeDomainSampleFilter filters}
 * for each sampling frequency.
 *
 * @author Piotr Szachewicz
 */
@XStreamAlias("predefinedFilters")
public class PredefinedTimeDomainFiltersPresetManager extends PredefinedFiltersPresetManager {

	/**
	 * A path pointing a file at which the default predefined filters
	 * configuration is stored.
	 */
	protected static final String DEFAULT_PREDEFINED_FILTERS_PATH = "org/signalml/app/config/default_predefined_td_filters.xml";

	/**
	 * Returns the list of predefined {@link TimeDomainSampleFilter filters}
	 * for the given sampling frequency.
	 * @param samplingFrequency the sampling frequency for which the filters
	 * were designed
	 * @return the list of predefined filters
	 */
	@Override
	public List<SampleFilterDefinition> getPredefinedFilters(double samplingFrequency) {
		return super.getPredefinedFilters(samplingFrequency);
	}

	/**
	 * Returns the filter which should be the starting point when designing
	 * custom filter in the filter designer's window. Modifying this filter
	 * does not affect the filter contained in this preset manager (this
	 * method returns a copy of the filter).
	 * @param samplingFrequency sampling frequency at which the filter will
	 * be designed
	 * @return the {@link TimeDomainSampleFilter filter} which should be
	 * displayed when creating custom filter
	 */
	@Override
	public TimeDomainSampleFilter getCustomFilterStartingPoint(double samplingFrequency) {
		return (TimeDomainSampleFilter) super.getCustomFilterStartingPoint(samplingFrequency);
	}

	/**
	 * Returns a custom filter design starting point. This method should
	 * be used only if method
	 * {@link PredefinedTimeDomainFiltersPresetManager#getCustomFilterStartingPoint(double)}
	 * does not return any filter. Modifying this filter
	 * does not affect the filter contained in this preset manager (this
	 * method returns a copy of the filter).
	 * @return custom starting point which could be used while starting to
	 * design a custom filter
	 */
	@Override
	public TimeDomainSampleFilter getCustomStartingPoint() {
		return (TimeDomainSampleFilter) super.getCustomStartingPoint();
	}

	/**
	 * Returns the predefined {@link TimeDomainSampleFilter filters} of
	 * a specified index defined for the specified sampling frequency.
	 * Modifying the filters returned by this method
	 * does not affect the filters contained in this preset manager (this
	 * method returns copies of the filters).
	 * @param samplingFrequency sampling frequency for which the returned
	 * filter should be designed
	 * @param index the index of the filter to be returned
	 * @return the predefined filter of the specified index, designed for
	 * the given sampling frequency
	 */
	@Override
	public TimeDomainSampleFilter getPredefinedFilterAt(double samplingFrequency, int index) {
		return (TimeDomainSampleFilter) super.getPredefinedFilterAt(samplingFrequency, index);
	}

	/**
	 * This method can be used in the future to support predefined filters
	 * lists editing etc. For now - it always throws an {@link UnsupportedOperationException}.
	 * @return always throws an exception
	 */
	@Override
	public String getStandardFilename() {
		//TODO: complete this functionality or remove this code
		throw new UnsupportedOperationException("This operation is not supported yet");
	}

	/**
	 * Creates an {@link XStream} which should be used with this preset manager.
	 * @return a streamer to be used to stream preset data to XML
	 */
	@Override
	protected XStream createFilterPresetManagerStreamer() {

		XStream xstreamer = XMLUtils.getDefaultStreamer();
		XMLUtils.configureStreamerForPredefinedTimeDomainSampleFilter(xstreamer);
		xstreamer.setMode(XStream.XPATH_RELATIVE_REFERENCES);

		return xstreamer;

	}

	/**
	 * Loads default predefined filters from a file.
	 * @throws IOException when an error occurs while reading the file containing
	 * default predefined filters
	 */
	@Override
	public void loadDefaults() throws IOException {

		Resource resource = new ClassPathResource(DEFAULT_PREDEFINED_FILTERS_PATH);
		InputStream inputStream = new BufferedInputStream(resource.getInputStream());
		readFromInputStream(inputStream);

	}
}
