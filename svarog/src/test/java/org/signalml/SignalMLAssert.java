/* SignalMLAssert.java created 2011-02-05
 *
 */

package org.signalml;

import org.apache.commons.math.complex.Complex;
import org.apache.commons.math.linear.RealMatrix;
import org.apache.commons.math.linear.RealVector;
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
		assertEquals(expected.getImaginary(), actual.getImaginary(), delta.getImaginary());

	}

	/**
	 * Asserts that two Complex numbers are equal concerning a delta equal to 1e-5.
	 *
	 * @param expected the expected value
	 * @param actual the actual value
	 */
	public static void assertEquals(Complex expected, Complex actual) {
		assertEquals(expected, actual, new Complex(1e-5, 1e-5));
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

	/**
	 * Asserts that the elements of two double arrays are equal concerning
	 * a delta.
	 *
	 * @param expected the expected values
	 * @param actual the actual values
	 * @param delta determines how much the actual value can be different from the expected
	 * value for the assertion to hold true
	 */
	public static void assertArrayEquals(double[][] expected, double[][] actual, double delta) {
		assertEquals(expected.length, actual.length);

		for (int i = 0; i < expected.length; i++)
			assertArrayEquals(expected[i], actual[i], delta);
	}

	/**
	 * Asserts that the elements of two double matrices are equal concerning
	 * a delta.
	 *
	 * @param expected the expected values
	 * @param actual the actual values
	 * @param delta determines how much the actual value can be different from the expected
	 * value for the assertion to hold true
	 */
	public static void assertMatrixEquals(RealMatrix expected, RealMatrix actual, double delta) {
		double[][] expectedData = expected.getData();
		double[][] actualData = actual.getData();
		assertArrayEquals(expectedData, actualData, delta);
	}

	/**
	 * Asserts that the elements of two vectors are equal concerning
	 * a delta.
	 *
	 * @param expected the expected values
	 * @param actual the actual values
	 * @param delta determines how much the actual value can be different from the expected
	 * value for the assertion to hold true
	 */
	public static void assertVectorEquals(RealVector expected, RealVector actual, double delta) {
		double[] expectedData = expected.getData();
		double[] actualData = actual.getData();

		assertArrayEquals(expectedData, actualData, delta);
	}

}