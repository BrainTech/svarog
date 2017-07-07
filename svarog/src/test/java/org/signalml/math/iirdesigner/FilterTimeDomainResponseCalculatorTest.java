/* FilterTimeResponseCalculatorTest.java created 2011-02-05
 *
 */

package org.signalml.math.iirdesigner;

import org.junit.Test;
import org.signalml.BaseTestCase;
import org.signalml.math.iirdesigner.FilterCoefficients;
import org.signalml.math.iirdesigner.FilterTimeDomainResponse;
import org.signalml.math.iirdesigner.FilterTimeDomainResponseCalculator;

import static org.signalml.math.iirdesigner.IIRDesignerAssert.*;

/**
 * This class performs unit tests on {@link FilterTimeDomainResponseCalculator} class.
 * The results obtained from the FilterTimeDomainResponseCalculator are compared
 * with MATLAB results given by the stepz(b,a,n) and impz(b,a,n) function.
 *
 * @author Piotr Szachewicz
 */
public class FilterTimeDomainResponseCalculatorTest extends BaseTestCase {

	/**
	 * Checks if the time vector of the time domain response is correctly
	 * calculated.
	 * @param response the response to be checked
	 * @param samplingFrequency the sampling frequency of the signal
	 */
	protected void checkTime(FilterTimeDomainResponse response, double samplingFrequency) {
		double step = 1.0 / samplingFrequency;
		double[] time = response.getTime();

		for (int i = 0; i < time.length; i++) {
			assertEquals(i * step, time[i], 1e-3);
		}

	}

	/**
	 * Checks if the step response is correctly calculated.
	 * @param bCoefficients feedforward filter coefficients
	 * @param aCoefficients feedback filter coefficients
	 * @param samplingFrequency the sampling frequency of the signal
	 * @param expectedResult the expected result of the step response
	 * calculation
	 */
	protected void checkStepResponse(double[] bCoefficients, double[] aCoefficients, double samplingFrequency, double[] expectedResult) {
		FilterCoefficients coeffs = new FilterCoefficients(bCoefficients, aCoefficients);
		FilterTimeDomainResponseCalculator calc = new FilterTimeDomainResponseCalculator(samplingFrequency, coeffs);

		FilterTimeDomainResponse stepResponse = calc.getStepResponse(expectedResult.length);

		assertArrayEquals(expectedResult, stepResponse.getValues(), 1e-3);

		checkTime(stepResponse, samplingFrequency);
	}

	/**
	 * Checks if the impulse response is correctly calculated.
	 * @param bCoefficients feedforward filter coefficients
	 * @param aCoefficients feedback filter coefficients
	 * @param samplingFrequency the sampling frequency of the signal
	 * @param expectedResult the expected result of the impulse response
	 * calculation
	 */
	protected void checkImpulseResponse(double[] bCoefficients, double[] aCoefficients, double samplingFrequency, double[] expectedResult) {
		FilterCoefficients coeffs = new FilterCoefficients(bCoefficients, aCoefficients);
		FilterTimeDomainResponseCalculator calc = new FilterTimeDomainResponseCalculator(samplingFrequency, coeffs);

		FilterTimeDomainResponse impulseResponse = calc.getImpulseResponse(expectedResult.length);

		assertArrayEquals(expectedResult, impulseResponse.getValues(), 1e-3);

		checkTime(impulseResponse, samplingFrequency);
	}

	/**
	 * Test method for {@link FilterTimeDomainResponseCalculator#getStepResponse(int)}.
	 */
	@Test
	public void testGetStepResponse() {

		checkStepResponse(
			new double[] {0.34,0.12, 0.6, 0.33},
			new double[] {1, 0.4, 0.01, 0.02},
			128.0,
			new double[] {  0.3400, 0.3240, 0.9270, 1.0092,
							0.9706, 0.9731, 0.9709, 0.9725,
							0.9718, 0.9721, 0.9720, 0.9721,
							0.9720
						 });

	}

	/**
	 * Test method for {@link FilterTimeDomainResponseCalculator#getStepResponse(int)}.
	 *
	 */
	@Test
	public void testGetStepResponse2() {

		checkStepResponse(new double[] {	0.6424289831734704, -1.9161341148935136,
											1.9161341148935136, -0.6424289831734704
									   },
						  new double[] {1.0, -2.1897185854179515,
										1.5884957734660146, -0.3389118372500017
									   },
						  128.0,
						  new double[] {0.6424, 0.1330, -0.0868, -0.1836,
										-0.2191, -0.2175, -0.1905, -0.1459,
										-0.0905, -0.0311, 0.0263, 0.0763,
										0.1148, 0.1390, 0.1479, 0.1420,
										0.1231, 0.0941, 0.0586, 0.0207
									   });
	}

	/**
	 * Test method for {@link FilterTimeDomainResponseCalculator#getImpulseResponse(int)}.
	 */
	@Test
	public void testGetImpulseResponse() {

		checkImpulseResponse(new double[] {0.34,0.12, 0.6, 0.33},
							 new double[] {1, 0.4, 0.01, 0.02},
							 128.0,
							 new double[] {0.3400, -0.0160, 0.6030, 0.0822,
										   -0.0386, 0.0025, -0.0023, 0.0017,
										   -0.0007, 0.0003, -0.0001, 0.0001,
										   -0.0000
										  });
	}

	/**
	 * Test method for {@link FilterTimeDomainResponseCalculator#getImpulseResponse(int)}.
	 */
	@Test
	public void testGetImpulseResponse2() {
		checkImpulseResponse(new double[] {0.6424289831734704, -1.9161341148935136,
										   1.9161341148935136, -0.6424289831734704
										  },
							 new double[] {1.0, -2.1897185854179515,
										   1.5884957734660146, -0.3389118372500017
										  },
							 128.0,
							 new double[] {0.6424, -0.5094, -0.2198, -0.0968,
										   -0.0355, 0.0016, 0.0270, 0.0446,
										   0.0553, 0.0595, 0.0574, 0.0500,
										   0.0385, 0.0242, 0.0089, -0.0059,
										   -0.0189, -0.0290, -0.0355, -0.0380
										  });
	}

	/**
	 * Test method for {@link FilterTimeDomainResponseCalculator#getImpulseResponse(int)}.
	 */
	@Test
	public void testGetImpulseResponse3() {
		checkImpulseResponse(new double[] {0.14899327766950346, -0.17797828020416506,
										   0.09665728555212459, -0.17797828020416506,
										   0.14899327766950346
										  },
							 new double[] {1.0, -1.9742328928331783,
										   2.25511165980123, -1.585332711251906,
										   0.6913267491120615
										  },
							 1024.0,
							 new double[] {0.1490, 0.1162, -0.0100, -0.2235,
										   -0.1885, 0.0357, 0.1481, 0.0677,
										   -0.0136, 0.0307, 0.0962, 0.0523,
										   -0.0556, -0.0964, -0.0486
										  });
	}

	/**
	 * Test method for {@link FilterTimeDomainResponseCalculator#getImpulseResponse(int)}.
	 */
	@Test
	public void testGetStepResponse3() {
		checkStepResponse(new double[] {	0.14899327766950346, -0.17797828020416506,
											0.09665728555212459, -0.17797828020416506,
											0.14899327766950346
									   },
						  new double[] {	1.0, -1.9742328928331783,
										  2.25511165980123, -1.585332711251906,
										  0.6913267491120615
									   },
						  128.0,
						  new double[] {	0.1490, 0.2652, 0.2552, 0.0317,
										  -0.1568, -0.1212, 0.0270, 0.0946,
										  0.0810, 0.1118, 0.2080, 0.2603,
										  0.2047, 0.1083, 0.0597
									   });
	}

	/**
	 * Test method for {@link FilterTimeDomainResponseCalculator#getImpulseResponse(int)}.
	 */
	@Test
	public void testGetImpulseResponse4() {
		checkImpulseResponse(new double[] {	0.10142869421084308, -0.33083385975110186,
											0.4614951916059849, -0.33083385975110186,
											0.10142869421084308
										  },
							 new double[] {	1.0, -3.453721537390917,
											 4.80260886705093, -3.162955657636732,
											 0.8409169332314156
										  },
							 128.0,
							 new double[] {	0.1014, 0.0195, 0.0416, 0.0402,
											 0.0167, -0.0201, -0.0575, -0.0831,
											 -0.0884, -0.0711, -0.0357, 0.0086,
											 0.0506, 0.0802, 0.0913
										  });
	}

	/**
	 * Test method for {@link FilterTimeDomainResponseCalculator#getImpulseResponse(int)}.
	 */
	@Test
	public void testGetStepResponse4() {
		checkStepResponse(new double[] {	0.10142869421084308, -0.33083385975110186,
											0.4614951916059849, -0.33083385975110186,
											0.10142869421084308
									   },
						  new double[] {	1.0, -3.453721537390917,
										  4.80260886705093, -3.162955657636732,
										  0.8409169332314156
									   },
						  1024.0,
						  new double[] {	0.1014, 0.1209, 0.1625, 0.2028,
										  0.2195, 0.1994, 0.1419, 0.0588,
										  -0.0296, -0.1008, -0.1365, -0.1279,
										  -0.0773, 0.0028, 0.0942
									   });
	}

}
