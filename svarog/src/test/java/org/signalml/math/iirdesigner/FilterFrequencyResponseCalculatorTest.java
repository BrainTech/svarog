/* FilterFrequencyResponseCalculatorTest.java created 2010-12-02
 *
 */
package org.signalml.math.iirdesigner;

import org.junit.Test;
import org.signalml.math.iirdesigner.ArrayOperations;
import org.signalml.math.iirdesigner.FilterCoefficients;
import org.signalml.math.iirdesigner.FilterFrequencyResponse;
import org.signalml.math.iirdesigner.FilterFrequencyResponseCalculator;
import org.signalml.math.iirdesigner.FilterResponseCalculator;

import static org.signalml.math.iirdesigner.IIRDesignerAssert.*;

/**
 * This method performs unit tests on {@link FilterResponseCalculator}.
 *
 * @author Piotr Szachewicz
 */
public class FilterFrequencyResponseCalculatorTest {

	/**
	 * A test method for {@link FilterResponseCalculator#getGroupDelayResponse() }.
	 */
	@Test
	public void testGetGroupDelayResponse() {

		FilterCoefficients coeffs = new FilterCoefficients(
			new double[] {0.0562, -0.0260, -0.0603, -0.0260, 0.0562},
			new double[] {1.0000, -3.0870, 3.7266, -2.1103, 0.4806});
		FilterFrequencyResponseCalculator calc = new FilterFrequencyResponseCalculator(512, 128, coeffs);

		FilterFrequencyResponse groupDelay = calc.getGroupDelayResponse();

		assertEquals(512, groupDelay.getValues().length);
		double[] actual = ArrayOperations.trimArrayToSize(groupDelay.getValues(), 10);
		assertArrayEquals(new double[] {6.2727, 6.2833, 6.3153, 6.3688, 6.4443, 6.5423, 6.6636, 6.8091, 6.9796, 7.1763}, actual, 0.001);

		assertEquals(512, groupDelay.getFrequencies().length);
		actual = ArrayOperations.trimArrayToSize(groupDelay.getFrequencies(), 10);
		assertArrayEquals(new double[] {0, 0.1250, 0.2500, 0.3750, 0.5000, 0.6250, 0.7500, 0.8750, 1.0000, 1.1250}, actual, 0.001);

	}


}
