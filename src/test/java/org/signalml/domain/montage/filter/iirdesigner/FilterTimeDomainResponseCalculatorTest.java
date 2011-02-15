/* FilterTimeResponseCalculatorTest.java created 2011-02-05
 *
 */

package org.signalml.domain.montage.filter.iirdesigner;

import org.junit.Test;
import static org.signalml.domain.montage.filter.iirdesigner.IIRDesignerAssert.*;

/**
 * This class performs unit tests on {@link FilterTimeDomainResponseCalculator} class.
 *
 * @author Piotr Szachewicz
 */
public class FilterTimeDomainResponseCalculatorTest {

	/**
	 * Test method for {@link FilterTimeDomainResponseCalculator#getStepResponse(int)}.
	 */
	@Test
	public void testGetStepResponse() {
		FilterCoefficients coeffs = new FilterCoefficients(
			new double[] {0.34,0.12, 0.6, 0.33},
			new double[] {1, 0.4, 0.01, 0.02});
		FilterTimeDomainResponseCalculator calc = new FilterTimeDomainResponseCalculator(128.0, coeffs);

		FilterTimeDomainResponse stepResponse = calc.getStepResponse(13);

		assertArrayEquals(new double[] {0.3400, 0.3240, 0.9270, 1.0092,
						0.9706, 0.9731, 0.9709, 0.9725,
						0.9718, 0.9721, 0.9720, 0.9721,
						0.9720},
				  stepResponse.getValues(), 1e-3);

		double[] time = stepResponse.getTime();
		assertEquals(0.0, time[0], 1e-3);
		assertEquals(1.0/128.0, time[1], 1e-3);
		assertEquals(2.0/128.0, time[2], 1e-3);
		assertEquals(12.0/128.0, time[12], 1e-3);

	}

	/**
	 * Test method for {@link FilterTimeDomainResponseCalculator#getStepResponse(int)}.
	 * Comparing with MATLAB results given by the stepz(b,a) function.
	 */
	@Test
	public void testGetStepResponse2() {
		FilterCoefficients coeffs = new FilterCoefficients(
			new double[]{	0.6424289831734704, -1.9161341148935136,
					1.9161341148935136, -0.6424289831734704},
			new double[] {	1.0, -2.1897185854179515,
					1.5884957734660146, -0.3389118372500017});
		FilterTimeDomainResponseCalculator calc = new FilterTimeDomainResponseCalculator(128.0, coeffs);

		FilterTimeDomainResponse stepResponse = calc.getStepResponse(20);

		assertArrayEquals(new double[] {0.6424, 0.1330, -0.0868, -0.1836,
						-0.2191, -0.2175, -0.1905, -0.1459,
						-0.0905, -0.0311, 0.0263, 0.0763,
						0.1148, 0.1390, 0.1479, 0.1420,
						0.1231, 0.0941, 0.0586, 0.0207},
				  stepResponse.getValues(), 1e-3);
	}

	/**
	 * Test method for {@link FilterTimeDomainResponseCalculator#getImpulseResponse(int)}.
	 */
	@Test
	public void testGetImpulseResponse() {
		FilterCoefficients coeffs = new FilterCoefficients(
			new double[] {0.34,0.12, 0.6, 0.33},
			new double[] {1, 0.4, 0.01, 0.02});
		FilterTimeDomainResponseCalculator calc = new FilterTimeDomainResponseCalculator(
			128.0, coeffs);

		FilterTimeDomainResponse impulseResponse = calc.getImpulseResponse(13);

		assertArrayEquals(new double[] {0.3400, -0.0160, 0.6030, 0.0822,
						-0.0386, 0.0025, -0.0023, 0.0017,
						-0.0007, 0.0003, -0.0001, 0.0001,
						-0.0000},
				  impulseResponse.getValues(), 1e-3);

		double[] time = impulseResponse.getTime();
		assertEquals(0.0, time[0], 1e-3);
		assertEquals(1.0/128.0, time[1], 1e-3);
		assertEquals(2.0/128.0, time[2], 1e-3);
		assertEquals(12.0/128.0, time[12], 1e-3);
	}

	/**
	 * Test method for {@link FilterTimeDomainResponseCalculator#getImpulseResponse(int)}.
	 * Comparing with MATLAB's impz function results.
	 */
	@Test
	public void testGetImpulseResponse2() {
		FilterCoefficients coeffs = new FilterCoefficients(
			new double[]{	0.6424289831734704, -1.9161341148935136,
					1.9161341148935136, -0.6424289831734704},
			new double[] {	1.0, -2.1897185854179515,
					1.5884957734660146, -0.3389118372500017});
		FilterTimeDomainResponseCalculator calc = new FilterTimeDomainResponseCalculator(
			128.0, coeffs);

		FilterTimeDomainResponse impulseResponse = calc.getImpulseResponse(20);

		assertArrayEquals(new double[] {0.6424, -0.5094, -0.2198, -0.0968,
						-0.0355, 0.0016, 0.0270, 0.0446,
						0.0553, 0.0595, 0.0574, 0.0500,
						0.0385, 0.0242, 0.0089, -0.0059,
						-0.0189, -0.0290, -0.0355, -0.0380},
				  impulseResponse.getValues(), 1e-3);

		double[] time = impulseResponse.getTime();
		assertEquals(0.0, time[0], 1e-3);
		assertEquals(1.0/128.0, time[1], 1e-3);
		assertEquals(2.0/128.0, time[2], 1e-3);
		assertEquals(12.0/128.0, time[12], 1e-3);
	}

}
