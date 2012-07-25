package org.signalml.domain.signal.filter.iir;

import org.signalml.domain.signal.samplesource.SampleSource;
import org.signalml.math.iirdesigner.FilterCoefficients;
import org.signalml.math.iirdesigner.InitialStateCalculator;

/**
 * This class is able to filter a sample source with a given IIR filter.
 * It could be used to filter a very long signal.
 *
 * @author Piotr Szachewicz
 */
public class ExportIIRSinglechannelSampleFilter extends AbstractIIRSinglechannelSampleFilter {

	private IIRFilterEngine iirFilter;
	private boolean firstRun = true;

	public ExportIIRSinglechannelSampleFilter(SampleSource source, FilterCoefficients coefficients) {
		super(source, coefficients);

		InitialStateCalculator initalStateCalculator = new InitialStateCalculator(coefficients);
		double[] initialState = initalStateCalculator.getInitialState();

		//calculate intial state
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
		if (firstRun) {
			firstRun = false;
			if (signalOffset != 0) {
				//so that initial state is used for the first samples.
				double[] samples = new double[signalOffset];
				source.getSamples(samples, 0, signalOffset, 0);
				iirFilter.filter(samples);
			}
		}

		double[] samples = new double[count];
		source.getSamples(samples, signalOffset, count, 0);

		double[] filteredSamples = iirFilter.filter(samples);

		System.arraycopy(filteredSamples, 0, target, 0, count);
	}

}
