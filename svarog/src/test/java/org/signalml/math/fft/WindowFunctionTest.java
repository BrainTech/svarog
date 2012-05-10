package org.signalml.math.fft;

import static org.signalml.SignalMLAssert.assertArrayEquals;

import java.util.Arrays;

import org.junit.Test;

/**
 * Applies unit tests to the {@link WindowFunction}.
 *
 * @author Piotr Szachewicz
 */
public class WindowFunctionTest {

	@Test
	public void testCalculateWeightsHanning() {

		WindowFunction windowFunction = new WindowFunction(WindowType.HANN);
		windowFunction.calculateWeights(20);

		double[] windowWeights = new double[] {
			0, 0.02709138,  0.10542975,  0.22652592,  0.37725726,
			0.54128967,  0.70084771,  0.83864079,  0.93973688,  0.99318065,
			0.99318065,  0.93973688,  0.83864079,  0.70084771,  0.54128967,
			0.37725726,  0.22652592,  0.10542975,  0.02709138,  0
		};
		assertArrayEquals(windowWeights, windowFunction.windowWeights, 1e-5);
	}

	@Test
	public void testCalculateWeightsHamming() {

		WindowFunction windowFunction = new WindowFunction(WindowType.HAMMING);
		windowFunction.calculateWeights(20);

		double[] windowWeights = new double[] {
			0.08      ,  0.10492407,  0.17699537,  0.28840385,  0.42707668,
			0.5779865 ,  0.7247799 ,  0.85154952,  0.94455793,  0.9937262 ,
			0.9937262 ,  0.94455793,  0.85154952,  0.7247799 ,  0.5779865 ,
			0.42707668,  0.28840385,  0.17699537,  0.10492407,  0.08
		};
		assertArrayEquals(windowWeights, windowFunction.windowWeights, 1e-5);
	}

	@Test
	public void testApplyWindow() {

		//generate bartlett window
		WindowFunction windowFunction = new WindowFunction(WindowType.BARTLETT);
		windowFunction.calculateWeights(10);

		double[] windowWeights = new double[] {
			0, 0.22222222, 0.44444444, 0.66666667, 0.88888889,
			0.88888889, 0.66666667, 0.44444444, 0.22222222, 0
		};
		assertArrayEquals(windowWeights, windowFunction.windowWeights, 1e-5);

		//generate signal
		double[] signal = new double[10];
		Arrays.fill(signal, 3.0);

		double[] windowedSignal = windowFunction.applyWindow(signal);
		double[] expectedWindowedSignal = new double[] {
			0, 0.66666667, 1.33333333, 2, 2.66666667,
			2.66666667, 2, 1.33333333, 0.66666667, 0
		};
		assertArrayEquals(expectedWindowedSignal, windowedSignal, 1e-5);

	}

}
