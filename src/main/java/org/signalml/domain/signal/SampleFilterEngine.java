/* SampleFilterEngine.java created 2008-02-04
 * 
 */

package org.signalml.domain.signal;

import org.signalml.domain.montage.filter.SampleFilterDefinition;

/** SampleFilterEngine
 *
 * 
 * @author Michal Dobaczewski &copy; 2007-2008 CC Otwarte Systemy Komputerowe Sp. z o.o.
 */
public abstract class SampleFilterEngine implements SampleSource {

        /*
         * the {@link SampleFilterDefinition definition} of the filter
         */
        SampleFilterDefinition definition;

	protected SampleSource source;
		
	public SampleFilterEngine(SampleSource source) {
		this.source = source;
	}

	@Override
	public float getCalibration() {
		return source.getCalibration();
	}

        /**
         * Returs the (@link SampleFilterDefinition definition of the filter) used
         * by the filtering engine.
         * @return (@link SampleFilterDefinition the definition of the filter)
         */
        public abstract SampleFilterDefinition getFilterDefinition();

	@Override
	public String getLabel() {
		return source.getLabel();
	}

	@Override
	public int getSampleCount() {
		return source.getSampleCount();
	}

	@Override
	public float getSamplingFrequency() {
		return source.getSamplingFrequency();
	}

	@Override
	public boolean isCalibrationCapable() {
		return source.isCalibrationCapable();
	}

	@Override
	public boolean isChannelCountCapable() {
		return source.isChannelCountCapable();
	}

	@Override
	public boolean isSamplingFrequencyCapable() {
		return source.isSamplingFrequencyCapable();
	}

}
