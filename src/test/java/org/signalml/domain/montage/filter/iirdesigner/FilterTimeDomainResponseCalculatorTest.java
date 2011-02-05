/* FilterTimeResponseCalculatorTest.java created 2011-02-05
 *
 */

package org.signalml.domain.montage.filter.iirdesigner;

import org.junit.Test;
import static org.signalml.domain.montage.filter.iirdesigner.IIRDesignerAssert.*;

/**
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

}
