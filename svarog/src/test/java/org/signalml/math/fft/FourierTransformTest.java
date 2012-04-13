package org.signalml.math.fft;

import org.apache.commons.math.complex.Complex;
import org.junit.Test;
import org.signalml.math.fft.FourierTransform;

import static org.signalml.SignalMLAssert.*;

/**
 * Applies unit tests to the {@link FourierTransform}.
 *
 * @author Piotr Szachewicz
 */
public class FourierTransformTest {

	@Test
	public void testForwardFFT() {
		FourierTransform transform = new FourierTransform();
		double[] signal = new double[] {1,4,2,-1,7,4,5,2};
		Complex[] expectedFFT = new Complex[] {
			new Complex(24.00000000,0),
			new Complex(-3.87867966, 5.12132034),
			new Complex(1.00000000,-7),
			new Complex(-8.12132034,-0.87867966),
			new Complex(6.00000000, 0),
			new Complex(-8.12132034, 0.87867966),
			new Complex(1.00000000,7),
			new Complex(-3.87867966, -5.12132034)
		};
		Complex[] actualFFT = transform.forwardFFT(signal);
		assertArrayEquals(expectedFFT, actualFFT, new Complex(1e-5, 1e-5));
	}

	@Test
	public void testGetPowerOfTwoSize() {
		int actual = FourierTransform.getPowerOfTwoSize(13);
		assertEquals(16, actual);

		assertEquals(256, FourierTransform.getPowerOfTwoSize(256));
	}

	@Test
	public void testForwardFFTNotPowerOf2() {
		FourierTransform transform = new FourierTransform();
		double[] signal = new double[] {1,  8,  3, -5,  4};

		//these expectedFFT data was obtained from python using the following code:
		//from numpy import *
		//a = array((1, 8, 3, -5, 4, 0, 0, 0));
		//fft.fft(a)
		Complex[] expectedFFT = new Complex[] {
			new Complex(11.00000000, 0),
			new Complex(6.19238816, -5.12132034),
			new Complex(2.00000000, -13),
			new Complex(-12.19238816, +0.87867966),
			new Complex(5.00000000, 0),
			new Complex(-12.19238816, -0.87867966),
			new Complex(2.00000000, 13),
			new Complex(6.19238816, +5.12132034)
		};
		Complex[] actualFFT = transform.forwardFFT(signal);
		assertArrayEquals(expectedFFT, actualFFT, new Complex(1e-5, 1e-5));
	}

	@Test
	public void testInverseFFT() {
		FourierTransform transform = new FourierTransform();
		double[] signal = new double[] {1,4,2,-1,7,4,5,2};
		Complex[] actualFFT = transform.forwardFFT(signal);

		double[] signalAfterInverseFFT = transform.inverseFFT(actualFFT);
		assertArrayEquals(signal, signalAfterInverseFFT, 1e-5);
	}

	@Test
	public void testPadWithZeroIfNecessary() {
		double[] signal = new double[] {1, 2, 3, 4, 5};
		double[] paddedSignal = FourierTransform.padWithZeroIfNecessary(signal);
		double[] expected = new double[] {1, 2, 3, 4, 5, 0, 0, 0};
		assertArrayEquals(expected, paddedSignal, 1e-5);

		signal = new double[] {1, 2, 3, 4};
		paddedSignal = FourierTransform.padWithZeroIfNecessary(signal);
		expected = new double[] {1, 2, 3, 4};
		assertArrayEquals(expected, paddedSignal, 1e-5);
	}

	@Test
	public void testGetFrequencies() {
		double[] signal = new double[8];
		double[] expectedFrequencies = new double[] {0, 18.05, 36.1, 54.15};
		double[] actualFrequencies = FourierTransform.getFrequencies(signal, 144.4);

		assertArrayEquals(expectedFrequencies, actualFrequencies, 1e-5);

		signal = new double[11]; //should be padded to N=16
		expectedFrequencies = new double[] {0, 9.025, 18.05, 27.075, 36.1, 45.125, 54.15, 63.175};
		actualFrequencies = FourierTransform.getFrequencies(signal, 144.4);
		assertArrayEquals(expectedFrequencies, actualFrequencies, 1e-5);
	}

}
