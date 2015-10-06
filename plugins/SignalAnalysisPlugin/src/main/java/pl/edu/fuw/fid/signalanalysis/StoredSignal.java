package pl.edu.fuw.fid.signalanalysis;

import org.signalml.domain.signal.samplesource.MultichannelSampleSource;

/**
 * @author ptr@mimuw.edu.pl
 */
public class StoredSignal implements SimpleSignal {

	private final double frequency;
	private final double[] samples;

	public StoredSignal(MultichannelSampleSource source, int channel) {
		this(source, channel, 0, source.getSampleCount(channel));
	}

	public StoredSignal(MultichannelSampleSource source, int channel, int start, int length) {
		if (start < 0 || length < 0 || start + length > source.getSampleCount(channel)) {
			throw new IllegalArgumentException("invalid time selection");
		}
		frequency = source.getSamplingFrequency();
		samples = new double[length];
		source.getSamples(channel, samples, start, length, 0);
	}

	@Override
	public double[] getData() {
		return samples;
	}

	@Override
	public double getSamplingFrequency() {
		return frequency;
	}

}
