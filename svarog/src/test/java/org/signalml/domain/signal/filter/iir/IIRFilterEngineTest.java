package org.signalml.domain.signal.filter.iir;

import org.junit.Test;
import org.signalml.BaseTestCase;
import static org.signalml.SignalMLAssert.assertArrayEquals;
import org.signalml.domain.signal.filter.TestingSignals;

public class IIRFilterEngineTest extends BaseTestCase {

	@Test
	public void testFilter() {
		/*
		 * Tests if filtering the whole signal at once using the IIRFilter class
		 * gives same results as filtering two adjacent parts of the signal
		 * (the IIRFilter should remember its state between filtering).
		 */

		double[] bCoefficients = new double[]
				{0.00041655,  0.00124964,  0.00124964,  0.00041655};
		double[] aCoefficients = new double[]
				{1.        , -2.6861574 ,  2.41965511, -0.73016535};

		//whole at once
		IIRFilterEngine iirFilter = new IIRFilterEngine(bCoefficients, aCoefficients);
		double[] resultWhole = iirFilter.filter(TestingSignals.SHORT_SIGNAL);

		//in two parts
		double[] firstInput = new double[TestingSignals.SHORT_SIGNAL.length / 2];
		double[] secondInput = new double[TestingSignals.SHORT_SIGNAL.length - firstInput.length];
		System.arraycopy(TestingSignals.SHORT_SIGNAL, 0, firstInput, 0, firstInput.length);
		System.arraycopy(TestingSignals.SHORT_SIGNAL, firstInput.length, secondInput, 0, secondInput.length);

		iirFilter = new IIRFilterEngine(bCoefficients, aCoefficients);
		double[] firstResult = iirFilter.filter(firstInput);
		double[] secondResult = iirFilter.filter(secondInput);

		double[] resultParts = new double[firstResult.length + secondResult.length];
		System.arraycopy(firstResult, 0, resultParts, 0, firstResult.length);
		System.arraycopy(secondResult, 0, resultParts, firstResult.length, secondResult.length);

		//are equal?
		assertArrayEquals(resultWhole, resultParts, 1e-5);
	}
}
