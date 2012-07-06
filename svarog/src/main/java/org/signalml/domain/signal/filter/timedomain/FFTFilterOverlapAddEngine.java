package org.signalml.domain.signal.filter.timedomain;

import org.apache.commons.math.complex.Complex;
import org.signalml.domain.montage.filter.FFTSampleFilter;
import org.signalml.domain.montage.filter.SampleFilterDefinition;
import org.signalml.domain.signal.filter.FFTSampleFilterEngine;
import org.signalml.domain.signal.filter.SampleFilterEngine;
import org.signalml.domain.signal.samplesource.SampleSource;
import org.signalml.math.fft.FourierTransform;

public class FFTFilterOverlapAddEngine extends SampleFilterEngine {

	private FFTSampleFilter fftSampleFilter;

	private double[] overlapBuffer;

	public FFTFilterOverlapAddEngine(SampleSource source, FFTSampleFilter filter) {
		super(source);
		this.fftSampleFilter = filter;
	}

	@Override
	public void getSamples(double[] target, int signalOffset, int count, int arrayOffset) {

		double[] samples = new double[count];
		source.getSamples(samples, signalOffset, count, 0);

		FourierTransform fourierTransform = new FourierTransform();
		Complex[] samplesFFT = fourierTransform.forwardFFT(samples);
		FFTSampleFilterEngine.multiplyFFTByFFTSampleFilter(samplesFFT, fftSampleFilter, source.getSamplingFrequency());
		double[] filteredSignal = fourierTransform.inverseFFT(samplesFFT);

		if (overlapBuffer != null) {
			for (int i = 0; i < overlapBuffer.length; i++) {
				filteredSignal[i] += overlapBuffer[i];
			}
		}
		System.arraycopy(filteredSignal, 0, target, 0, count);

		overlapBuffer = new double[filteredSignal.length - count];
		System.arraycopy(filteredSignal, count, overlapBuffer, 0, overlapBuffer.length);
	}

	@Override
	public SampleFilterDefinition getFilterDefinition() {
		return fftSampleFilter;
	}

}
