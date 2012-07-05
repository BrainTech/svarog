package org.signalml.domain.signal.filter.timedomain;

import org.signalml.domain.signal.filter.AbstractTimeDomainSampleFilterEngine;
import org.signalml.domain.signal.samplesource.SampleSource;
import org.signalml.math.iirdesigner.FilterCoefficients;

public class TimeDomainSampleFilterExportEngine  extends AbstractTimeDomainSampleFilterEngine {

	private IIRFilter iirFilter;

	public TimeDomainSampleFilterExportEngine(SampleSource source, FilterCoefficients coefficients) {
		super(source, coefficients);

		iirFilter = new IIRFilter(bCoefficients, aCoefficients);
	}

	@Override
	public void getSamples(double[] target, int signalOffset, int count, int arrayOffset) {
		double[] samples = new double[count];
		source.getSamples(samples, signalOffset, count, 0);

		double[] filteredSamples = iirFilter.filter(samples);

		System.arraycopy(filteredSamples, 0, target, 0, count);
	}

}
