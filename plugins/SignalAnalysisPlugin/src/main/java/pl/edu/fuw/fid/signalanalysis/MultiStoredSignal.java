package pl.edu.fuw.fid.signalanalysis;

import org.signalml.domain.signal.samplesource.MultichannelSampleSource;

/**
 * @author ptr@mimuw.edu.pl
 */
public class MultiStoredSignal implements MultiSignal {

	private final double freqSampling;
	private final StoredSignal[] channels;

	public MultiStoredSignal(MultichannelSampleSource source, int[] selected) {
		freqSampling = source.getSamplingFrequency();
		channels = new StoredSignal[selected.length];
		for (int i=0; i<selected.length; ++i) {
			channels[i] = new StoredSignal(source, selected[i]);
		}
	}

	@Override
	public int getChannelCount() {
		return channels.length;
	}

	@Override
	public double[] getData(int channel) {
		return channels[channel].getData();
	}

	@Override
	public int getSampleCount() {
		return channels.length==0 ? 0 : channels[0].getData().length;
	}

	@Override
	public double getSamplingFrequency() {
		return freqSampling;
	}

}
