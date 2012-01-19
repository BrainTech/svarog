/* TransferFunctionTest.java created 2011-02-19
 *
 */

package org.signalml.math.iirdesigner;

import org.apache.commons.math.complex.Complex;
import org.junit.Test;
import org.signalml.math.iirdesigner.FilterCoefficients;
import org.signalml.math.iirdesigner.TransferFunction;

import static org.signalml.math.iirdesigner.IIRDesignerAssert.*;

/**
 * This class performs unit tests on {@link TransferFunction}.
 *
 * @author Piotr Szachewicz
 */
public class TransferFunctionTest {

	/**
	 * Test method for {@link TransferFunction#calculateTransferFunction() }.
	 */
	@Test
	public void testCalculateTransferFunction() {
		double[] bCoefficients = new double[] {0.2, 0.11};
		double[] aCoefficients = new double[] {1.0, 0.5};
		FilterCoefficients coefficients = new FilterCoefficients(bCoefficients, aCoefficients);
		TransferFunction transferFunction = new TransferFunction(10, coefficients);

		Complex[] calculatedGain = transferFunction.getGain();
		Complex[] expectedGain = new Complex[] {
			new Complex(0.20666667, 0),  new Complex(0.20659255,-0.00140395),
			new Complex(0.20635749, -0.00285469), new Complex(0.20591900, -0.00440213),
			new Complex(0.20518928, -0.00610036), new Complex(0.20400000, -0.008),
			new Complex(0.20202961, -0.01010705), new Complex(0.19867437, -0.01221684),
			new Complex(0.19299254, -0.01332898), new Complex(0.18491165, -0.01033697)
		};

		assertArrayEquals(expectedGain, calculatedGain, new Complex(1e-6, 1e-6));
	}

	/**
	 * Test method for {@link TransferFunction#calculateTransferFunction() }.
	 */
	@Test
	public void testCalculateTransferFunction2() {

		//b,a=signal.iirdesign(0.4,0.6,5,30, ftype='butter')
		FilterCoefficients coeffs = new FilterCoefficients(
		        new double[] {0.01875398, 0.0937699, 0.18753981, 0.18753981, 0.0937699, 0.01875398},
		        new double[] {1.0, -1.13877891, 1.08854509, -0.46710982, 0.13151932, -0.0140483});

		TransferFunction response = new TransferFunction(512, coeffs);

		assertEquals(new Complex(1,0), response.getGain()[0], new Complex(1e-6, 1e-6));
		assertEquals(new Complex(9.89651405e-01, -1.43492499e-01), response.getGain()[10], new Complex(1e-6, 1e-6));
		assertEquals(new Complex(2.94416493e-16, -4.24775580e-14), response.getGain()[511], new Complex(1e-6, 1e-6));

		assertEquals(0, response.getFrequencies()[0], 1e-6);
		assertEquals(0.02454369, response.getFrequencies()[4], 1e-6);
		assertEquals(3.12932081, response.getFrequencies()[510], 1e-6);

	}

}
