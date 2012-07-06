package org.signalml.domain.signal.filter.iir;

import org.signalml.math.iirdesigner.FilterCoefficients;
import org.signalml.math.iirdesigner.InitalStateCalculator;

public class IIRFilterEngineStabilized extends IIRFilterEngine {

	private boolean firstInvokeOfFilter = true;

	public IIRFilterEngineStabilized(double[] bCoefficients, double[] aCoefficients) {
		super(bCoefficients, aCoefficients);
	}

	@Override
	public double[] filter(double[] input) {
		if (firstInvokeOfFilter) {
			firstInvokeOfFilter = false;

			InitalStateCalculator initalStateCalculator = new InitalStateCalculator(new FilterCoefficients(bCoefficients, aCoefficients));
			double[] initialState = initalStateCalculator.getInitialState();
			double[] grownSignal = initalStateCalculator.growSignal(input, true);
			double[] filteredSamples;

			//right-wise
			double[] initialStateRightwise = new double[initialState.length];
			for (int i = 0; i < initialStateRightwise.length; i++) {
				initialStateRightwise[i] = initialState[i] * grownSignal[0];
			}
			this.initialConditions = initialStateRightwise;

			filteredSamples = super.filter(grownSignal);

			//shorten
			int padding = (grownSignal.length - input.length);
			double[] result = new double[input.length];
			System.arraycopy(filteredSamples, padding, result, 0, input.length);
			return result;
		} else {
			return super.filter(input);
		}
	}

}
