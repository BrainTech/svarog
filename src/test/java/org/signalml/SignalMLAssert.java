/* SignalMLAssert.java created 2011-02-05
 *
 */

package org.signalml;

import flanagan.complex.Complex;
import org.junit.Assert;

/**
 * This class is an extension of a JUnit class and is able to perform some additional
 * assertions needed in Svarog test.
 *
 * @author Piotr Szachewicz
 */
public class SignalMLAssert extends Assert {

	/**
	 * Asserts that two Complex numbers are equal concerning a delta.
	 *
	 * @param expected the expected value
	 * @param actual the actual value
	 * @param delta determines how much the actual value can be different from the expected
	 * value
	 */
	public static void assertEquals(Complex expected, Complex actual, Complex delta) {

		assertEquals(expected.getReal(), actual.getReal(), delta.getReal());
		assertEquals(expected.getImag(), actual.getImag(), delta.getImag());

	}

	/**
	 * Asserts that the elements of two Complex number arrays are equal concerning
	 * a delta.
	 *
	 * @param expected the expected values
	 * @param actual the actual values
	 * @param delta determines how much the actual value can be different from the expected
	 * value
	 */
	public static void assertArrayEquals(Complex[] expected, Complex[] actual, Complex delta) {

		assertEquals(expected.length, actual.length);
		for (int i = 0; i < expected.length; i++)
			assertEquals(expected[i],actual[i], delta);

	}

	/**
	 * Asserts that the elements of two double arrays are equal concerning
	 * a delta.
	 *
	 * @param expected the expected values
	 * @param actual the actual values
	 * @param delta determines how much the actual value can be different from the expected
	 * value for the assertion to hold true
	 */
	public static void assertArrayEquals(double[] expected, double[] actual, double delta) {

		assertEquals(expected.length, actual.length);
		for (int i = 0; i < expected.length; i++)
			assertEquals(expected[i],actual[i], delta);

	}

}