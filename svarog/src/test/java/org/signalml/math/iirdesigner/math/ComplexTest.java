/* Complex.java created 2011-02-20
 *
 */

package org.signalml.math.iirdesigner.math;

import org.apache.commons.math.complex.Complex;
import org.junit.Test;
import org.signalml.BaseTestCase;
import static org.signalml.SignalMLAssert.assertEquals;

/**
 * This class performes unit tests on {@link org.apache.commons.math.complex.Complex}.
 *
 * @author Piotr Szachewicz
 */
public class ComplexTest extends BaseTestCase {

	/**
	 * Test method for {@link org.apache.commons.math.complex.Complex#exp() }.
	 */
	@Test
	public void testExp() {
		Complex actual = (new Complex(1.0, 2.0)).exp();
		Complex expected = new Complex(-1.1312043837568135, 2.4717266720048188);
		assertEquals(expected, actual, new Complex(1e-6, 1e-6));

		actual = (new Complex(0.0, -0.4)).exp();
		expected = new Complex(0.9210609940028851, -0.38941834230865052);
		assertEquals(expected, actual, new Complex(1e-6, 1e-6));
	}
}
