package org.signalml.domain.signal.filter.iir;

import org.signalml.domain.signal.samplesource.SampleSource;
import org.signalml.math.iirdesigner.FilterCoefficients;
import org.signalml.math.iirdesigner.InitialStateCalculator;

public class ExportIIRSinglechannelSampleFilter  extends AbstractIIRSinglechannelSampleFilter {

	private IIRFilterEngine iirFilter;
	private FilterCoefficients filterCoefficients;

	public ExportIIRSinglechannelSampleFilter(SampleSource source, FilterCoefficients coefficients) {
		super(source, coefficients);
		this.filterCoefficients = coefficients;

		iirFilter = new IIRFilterEngineStabilized(bCoefficients, aCoefficients);
	}

	public ExportIIRSinglechannelSampleFilter(SampleSource source, FilterCoefficients coefficients, boolean grow) {
		super(source, coefficients);
		this.filterCoefficients = coefficients;

		if (grow) {
			this.source = new GrowSignalSampleSource(source, coefficients);
		}


		InitialStateCalculator initalStateCalculator = new InitialStateCalculator(coefficients);
		double[] initialState = initalStateCalculator.getInitialState();

		//right-wise
		double[] initialStateRightwise = new double[initialState.length];
		for (int i = 0; i < initialStateRightwise.length; i++) {
			double[] firstSample = new double[1];
			this.source.getSamples(firstSample, 0, 1, 0);
			initialStateRightwise[i] = initialState[i] * firstSample[0];
		}

		iirFilter = new IIRFilterEngine(bCoefficients, aCoefficients, initialStateRightwise);
	}

	@Override
	public void getSamples(double[] target, int signalOffset, int count, int arrayOffset) {
		double[] samples = new double[count];
		source.getSamples(samples, signalOffset, count, 0);

		double[] filteredSamples = iirFilter.filter(samples);

		System.arraycopy(filteredSamples, 0, target, 0, count);
	}

}
