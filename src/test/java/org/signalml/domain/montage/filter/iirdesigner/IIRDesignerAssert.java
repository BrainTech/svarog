/* IIRDesignerAssert.java created 2010-09-12
 *
 */

package org.signalml.domain.montage.filter.iirdesigner;

import flanagan.complex.Complex;
import org.junit.Assert;

/**
 * This class is an extension of a JUnit class and is able to perform some additional
 * assertions.
 *
 * @author Piotr Szachewicz
 */
class IIRDesignerAssert extends Assert {

	/**
	 * Asserts that two Complex numbers are equal concerning a delta.
	 * 
	 * @param expected the expected value
	 * @param actual the actual value
	 * @param delta determines how much the actual value can be different from the expected
	 * value
	 */
	protected static void assertEquals(Complex expected, Complex actual, Complex delta) {

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
	protected static void assertArrayEquals(Complex[] expected, Complex[] actual, Complex delta) {

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
	protected static void assertArrayEquals(double[] expected, double[] actual, double delta) {

		assertEquals(expected.length, actual.length);
		for (int i = 0; i < expected.length; i++)
			assertEquals(expected[i],actual[i], delta);

	}

	/**
	 * Asserts that the two zeros, poles, gain representations of a filter
	 * are equal concerning given deltas.
	 *
	 * @param zpk1 expected zero, poles, gain representation of a filter
	 * @param zpk2 actual zero, poles, gain representation of a filter
	 * @param complexDelta determines how much an actual zero or a pole (which are
	 * Complex numbers) can be different from expected value for the assertion
	 * to hold true
	 * @param gainDelta determines how much the actual value of gain can be different
	 * from the expected value
	 */
	protected static void assertEquals(FilterZerosPolesGain zpk1, FilterZerosPolesGain zpk2, Complex complexDelta, double gainDelta) {

		assertArrayEquals(zpk1.getZeros(), zpk2.getZeros(), complexDelta);
		assertArrayEquals(zpk1.getPoles(), zpk2.getPoles(), complexDelta);
		assertEquals(zpk1.getGain(), zpk2.getGain(), gainDelta);

	}

	/**
	 * Asserts that the two zeros, poles, gain representations of a filter
	 * are equal concerning given delta.
	 *
	 * @param zpk1 expected zero, poles, gain representation of a filter
	 * @param zpk2 actual zero, poles, gain representation of a filter
	 * @param delta determines how much the actual value can be different from
	 * the expected value
	 */
	protected static void assertEquals(FilterZerosPolesGain zpk1, FilterZerosPolesGain zpk2, double delta) {
		assertEquals(zpk1, zpk2, new Complex(delta, delta), delta);
	}

	/**
	 * Asserts that the two zeros, poles, gain representations of a filter
	 * are equal.
	 *
	 * @param zpk1 expected zero, poles, gain representation of a filter
	 * @param zpk2 actual zero, poles, gain representation of a filter
	 */
	protected static void assertEquals(FilterZerosPolesGain zpk1, FilterZerosPolesGain zpk2) {
		assertEquals(zpk1, zpk2, 0.0001);
	}

	/**
	 * Asserts that the elements of two arrays are equal concerning a delta.
	 *
	 * @param array1 an array containing expected values
	 * @param array2 an array containing actual values
	 * @param delta how much the actual value can be different from the expected
	 * value.
	 */
	protected static void assertEquals(double[] array1, double[] array2, double delta) {

		assertEquals(array1.length, array2.length);
		for (int i = 0; i < array1.length; i++)
			assertEquals(array1[i], array2[i], delta);

	}

	/**
	 * Asserts that two {@link FilterCoefficients} representations of filters
	 * are equal concerning a delta.
	 *
	 * @param expected the expected filter coefficients
	 * @param coeffs the actual filter coefficients
	 * @param delta determines how much the actual values of the coefficients
	 * can be different from the expected values.
	 */
	protected static void assertEquals(FilterCoefficients expected, FilterCoefficients coeffs, double delta) {

		assertEquals(expected.getACoefficients(), coeffs.getACoefficients(), delta);
		assertEquals(expected.getBCoefficients(), coeffs.getBCoefficients(), delta);

	}

}