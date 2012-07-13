package org.signalml.domain.signal.samplesource;

public abstract class SampleSourceEngine implements SampleSource {

	/**
	 * the {@link SampleSource source} of samples
	 */
	protected SampleSource source;

	public SampleSourceEngine(SampleSource source) {
		this.source = source;
	}

	@Override
	public boolean isSamplingFrequencyCapable() {
		return source.isSamplingFrequencyCapable();
	}

	@Override
	public boolean isChannelCountCapable() {
		return source.isChannelCountCapable();
	}

	@Override
	public float getSamplingFrequency() {
		return source.getSamplingFrequency();
	}

	@Override
	public int getSampleCount() {
		return source.getSampleCount();
	}

	@Override
	public String getLabel() {
		return source.getLabel();
	}

}
