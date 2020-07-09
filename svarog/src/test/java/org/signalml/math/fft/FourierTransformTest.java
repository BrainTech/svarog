package org.signalml.math.fft;

import org.apache.commons.math.complex.Complex;
import static org.junit.Assert.assertEquals;
import org.junit.Test;
import org.signalml.BaseTestCase;
import static org.signalml.SignalMLAssert.assertArrayEquals;

/**
 * Applies unit tests to the {@link FourierTransform}.
 *
 * @author Piotr Szachewicz
 */
public class FourierTransformTest extends BaseTestCase {

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

	@Test
	public void testCalculatePowerSpectrum() {
		double[] signal = new double[] {
				1.2945659058474952, -0.3345509351321665, 5.686547339065295, -8.251057190829304, 6.782008754863941, -9.134070114429775, -12.259336118683912, 13.486409636503414,
				1.8258866901508277, 0.4308346756427337, -18.640142221783385, -7.815740922810197, 35.705738662583414, 7.632716335419829, 1.2143921649235434, 0.6845342901367637,
				-20.37850112097658, 9.85023416020675, 14.78950345263582, 2.7650244844863296, -6.942829611294604, -16.40622299713456, 3.082133356005155, -7.242053767287787,
				-2.197578472736221, 17.385956045818038, 6.162402093861749, 10.838229891253318, -12.724725517976047, -23.84119303031666, -1.5768588095737623, -5.900298186852732,
				10.202159448157966, -2.615649371592628, -21.682798991223805, 14.165208986842746, 17.853755293871323, 4.704799142360598, -21.755783846205357, -24.902217334838383,
				31.78449300189404, 23.343600178346634, -12.750695801273517, -16.622844135624327, -9.384465577200022, 8.929057623584448, -11.965855209828595, 1.955062908265024,
				24.329981556146993, -21.213543509571668, -12.584172736229025, 10.43515216846423, -13.408886913972406, -36.02748448905436, -34.20473261490329, 10.417435036954839,
				-10.186784862756335, -57.55700608412204, -35.64481864122739, -42.031481578404325, -53.12914668440834, -100.42137643435781, -153.05710437623753, 14.050410151715724
		};

		double[] expectedSpectrum = new double[] {
				7881.704916587174, 7.673766100809436, 12.187623700910711, 0.8159553743630922,1.4770931373325087, 6.498600593661602, 10.85091081725627, 1.061426422745983,
				26.88128120985778, 21.78562336940601, 3.4244437826030345, 32.21854497792773, 22.91863874862273, 55.16914569310824, 83.10465624126286, 16.042215100310283,
				31.3136707302396, 38.36589514960133, 18.474755969059, 1.384323391051688, 3.254238160709387, 3.306617793557015, 0.8176794118837908, 0.8308016702724461,
				7.60029232776595, 63.8659508925275, 8.439245833668412, 0.0704436056803683,0.1361250292097137, 0.09222877638116617, 0.08975138063516105, 0.08593685429305786
		};
		double[] expectedFrequencies = new double[] {
				0.0, 2.0, 4.0, 6.0, 8.0, 10.0, 12.0, 14.0, 16.0,
				18.0, 20.0, 22.0, 24.0, 26.0, 28.0, 30.0, 32.0,
				34.0, 36.0, 38.0, 40.0, 42.0, 44.0, 46.0, 48.0,
				50.0, 52.0, 54.0, 56.0, 58.0, 60.0, 62.0
		};

		FourierTransform fourierTransform = new FourierTransform(WindowType.HAMMING);
		double[] actualSpectrum = fourierTransform.calculatePowerSpectrum(signal);
		double[] actualFrequencies = fourierTransform.getFrequencies(signal, 128.0);

		assertArrayEquals(expectedSpectrum, actualSpectrum, 1e-5);
		assertArrayEquals(expectedFrequencies, actualFrequencies, 1e-5);
	}

}
