/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pl.edu.fuw.fid.signalanalysis.ica;

import org.signalml.domain.signal.samplesource.MultichannelSampleSource;
import pl.edu.fuw.fid.signalanalysis.SimpleSignal;

/**
 *
 * @author piotr
 */
public class StoredSignal implements SimpleSignal {

	private final double frequency;
	private final double[] samples;

	public StoredSignal(MultichannelSampleSource source, int channel) {
		int length = source.getSampleCount(channel);
		frequency = source.getSamplingFrequency();
		samples = new double[length];
		source.getSamples(channel, samples, 0, length, 0);
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
