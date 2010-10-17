/* FilterZerosPolesGainTest.java created 2010-09-12
 *
 */

package org.signalml.domain.montage.filter.iirdesigner;

import static org.signalml.domain.montage.filter.iirdesigner.IIRDesignerAssert.*;
import flanagan.complex.Complex;
import org.junit.Test;

/**
 * This class performs unit tests on {@link FilterZerosPolesGain} class.
 *
 * @author Piotr Szachewicz
 */
public class FilterZerosPolesGainTest {

	/**
	 * Test method for {@link FilterZerosPolesGain#convertToBACoefficients() }.
	 */
	@Test
	public void testConvertToABCoefficients() {

		Complex[] zeros = new Complex[0];
		Complex[] poles = new Complex[] {new Complex(-0.08517040, 0.94648443),
		                                 new Complex(-0.20561953, 0.39204669), new Complex(-0.20561953, -0.39204669),
		                                 new Complex(-0.08517040, -0.94648443)
		                                };
		double gain = 0.125297161626;

		FilterZerosPolesGain zpk1 = new FilterZerosPolesGain(zeros, poles, gain);

		double[] pythonB = new double[] {0.12529716};
		double[] pythonA = new double[] {1.00000000, 0.58157986, 1.16911757, 0.40476795, 0.17698695};
		FilterCoefficients pycoeffs = new FilterCoefficients(pythonB, pythonA);
		FilterCoefficients coeffs = zpk1.convertToBACoefficients();

		assertEquals(pycoeffs, coeffs, 1e-6);

	}

}