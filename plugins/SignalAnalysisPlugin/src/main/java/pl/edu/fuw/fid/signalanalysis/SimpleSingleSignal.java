package pl.edu.fuw.fid.signalanalysis;

import java.util.Arrays;
import org.signalml.plugin.export.signal.ChannelSamples;

/**
 * @author ptr@mimuw.edu.pl
 */
public class SimpleSingleSignal implements SingleSignal {

	private final double[] data;
	private final double sampling;

	public SimpleSingleSignal(ChannelSamples samples) {
		this.data = samples.getSamples();
		this.sampling = samples.getSamplingFrequency();
	}

	@Override
	public void getSamples(int start, int length, double[] buffer) {
		length = Math.min(length, buffer.length);
		int maxLength = Math.max(0, data.length - start);
		if (maxLength < length) {
			Arrays.fill(buffer, maxLength, length, 0.0);
			length = maxLength;
		}
		int bufferOffset = 0;
		if (start < 0) {
			bufferOffset = -start;
			length -= bufferOffset;
			start = 0;
			Arrays.fill(buffer, 0, bufferOffset, 0.0);
		}
		if (length > 0) {
			System.arraycopy(data, start, buffer, bufferOffset, length);
		}
	}

	@Override
	public double getSamplingFrequency() {
		return sampling;
	}

};
