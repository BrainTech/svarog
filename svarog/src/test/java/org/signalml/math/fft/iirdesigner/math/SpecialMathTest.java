/* SpecialMathTest.java created 2010-09-12
 *
 */

package org.signalml.math.fft.iirdesigner.math;

import org.junit.Test;
import org.signalml.math.iirdesigner.math.SpecialMath;

import static org.junit.Assert.*;
import static org.signalml.math.iirdesigner.math.SpecialMath.*;

/**
 * This class performs unit tests for {@link SpecialMath} class.
 *
 * @author Piotr Szachewicz
 */
public class SpecialMathTest {

	/**
	 * Test method for {@link SpecialMath#getMachineEpsilon() }.
	 */
	@Test
	public void testGetMachineEpsilon() {
		assertEquals(1.1102230246251565E-16, getMachineEpsilon(), 1e-32);
	}

	/**
	 * Test method for {@link SpecialMath#evaluatePolynomial(double, double[]) }.
	 */
	@Test
	public void testEvaluatePolynomial() {

		assertEquals(evaluatePolynomial(5, new double[] {3, 0, 1 }), 76.0, 0.00001);
		assertEquals(evaluatePolynomial(0.13, new double[] {0.7, -6.6, 4.1, 0.3, -3.43, -1}), -1.43368133549, 0.000001);

	}

	/**
	 * Test method for {@link SpecialMath#calculateCompleteEllipticIntegralOfTheFirstKind(double) }.
	 */
	@Test
	public void testCalculateCompleteEllipticIntegralOfTheFirstKind() {

		assertEquals(1.8540746773013719, calculateCompleteEllipticIntegralOfTheFirstKind(0.5), 1e-16);
		assertEquals(1.5707963267948966, calculateCompleteEllipticIntegralOfTheFirstKind(getMachineEpsilon()), 1e-16);

	}

	/**
	 * Test method for {@link SpecialMath#calculateJacobianEllipticFunctionsValues(double, double) }.
	 */
	@Test
	public void testCalculateJacobianEllipticFunctions() {

		double result[] = calculateJacobianEllipticFunctionsValues(0.5, 0.7);
		assertEquals(4, result.length);
		assertEquals(0.46729200535903359, result[0], 1e-16);//sn
		assertEquals(0.8841030379585475, result[1], 1e-16);//cn
		assertEquals(0.92040574053472368, result[2], 1e-16);//dn
		assertEquals(0.48622530445618917, result[3], 1e-16);//phi

	}

	/**
	 * Test method for {@link SpecialMath#factorial(int) }.
	 */
	@Test
	public void testFactorialTest() {

		assertEquals(1, factorial(0));
		assertEquals(1, factorial(1));
		assertEquals(2, factorial(2));
		assertEquals(6, factorial(3));
		assertEquals(120, factorial(5));

	}

	/**
	 * Test method for {@link SpecialMath#combinations(int, int) }.
	 */
	@Test
	public void testCombinationsTest() {

		assertEquals(10, combinations(5, 3));
		assertEquals(6, combinations(4, 2));
		assertEquals(220, combinations(12, 3));

	}

	/**
	 * Test method for {@link SpecialMath#isOdd(int) }.
	 */
	@Test
	public void testIsOdd() {
		assertTrue(isOdd(3));
		assertFalse(isOdd(12));
	}

	/**
	 * Test method for {@link SpecialMath#isEven(int) }.
	 */
	@Test
	public void testIsEven() {
		assertTrue(isEven(2));
		assertFalse(isEven(5));
	}

}