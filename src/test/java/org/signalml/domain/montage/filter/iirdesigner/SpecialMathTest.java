/* SpecialMathTest.java created 2010-09-12
 *
 */

package org.signalml.domain.montage.filter.iirdesigner;

import org.junit.Test;
import org.signalml.domain.montage.filter.iirdesigner.EllipticIIRDesigner.KRatio;

import static org.junit.Assert.*;

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
		assertEquals(1.1102230246251565E-16, SpecialMath.getMachineEpsilon(), 1e-32);
	}

	/**
	 * Test method for {@link SpecialMath#evaluatePolynomial(double, double[]) }.
	 */
	@Test
	public void testEvaluatePolynomial() {

		assertEquals(SpecialMath.evaluatePolynomial(5, new double[] {3, 0, 1 }), 76.0, 0.00001);
		assertEquals(SpecialMath.evaluatePolynomial(0.13, new double[] {0.7, -6.6, 4.1, 0.3, -3.43, -1}), -1.43368133549, 0.000001);

	}

	/**
	 * Test method for {@link SpecialMath#calculateCompleteEllipticIntegralOfTheFirstKind(double) }.
	 */
	@Test
	public void testCalculateCompleteEllipticIntegralOfTheFirstKind() {

		assertEquals(1.8540746773013719, SpecialMath.calculateCompleteEllipticIntegralOfTheFirstKind(0.5), 1e-16);
		assertEquals(1.5707963267948966, SpecialMath.calculateCompleteEllipticIntegralOfTheFirstKind(SpecialMath.getMachineEpsilon()), 1e-16);

	}

	/**
	 * Test method for {@link SpecialMath#calculateJacobianEllipticFunctionsValues(double, double) }.
	 */
	@Test
	public void testCalculateJacobianEllipticFunctions() {

		double result[] = SpecialMath.calculateJacobianEllipticFunctionsValues(0.5, 0.7);
		assertEquals(4, result.length);
		assertEquals(0.46729200535903359, result[0], 1e-16);//sn
		assertEquals(0.8841030379585475, result[1], 1e-16);//cn
		assertEquals(0.92040574053472368, result[2], 1e-16);//dn
		assertEquals(0.48622530445618917, result[3], 1e-16);//phi

	}

	/**
	 * Test method for {@link SpecialMath#minimizeFunction(flanagan.math.MinimisationFunction, double[], int) }.
	 */
	@Test
	public void testMinimizeFunction() {

		EllipticIIRDesigner.KRatio kRatio = new KRatio();
		kRatio.setKRatio(0.6);
		double[] m = SpecialMath.minimizeFunction(kRatio, new double[] {0.5}, 250);
		assertEquals(0.08164062, m[0], 0.001);

	}

	/**
	 * Test method for {@link SpecialMath#minimizeFunctionConstrained(flanagan.math.MinimisationFunction, double[], double[], int) }.
	 */
	@Test
	public void testMinimizeFunctionConstrained() {

		EllipticIIRDesigner.KRatio kRatio = new KRatio();

		kRatio.setKRatio(0.5);
		double[] m = SpecialMath.minimizeFunctionConstrained(kRatio, new double[] {0.0}, new double[] {1.0}, 250);

		assertEquals(0.02943873, m[0], 1e-4);

	}

	/**
	 * Test method for {@link SpecialMath#factorial(int) }.
	 */
	@Test
	public void testFactorialTest() {

		assertEquals(1, SpecialMath.factorial(0));
		assertEquals(1, SpecialMath.factorial(1));
		assertEquals(2, SpecialMath.factorial(2));
		assertEquals(6, SpecialMath.factorial(3));
		assertEquals(120, SpecialMath.factorial(5));

	}

	/**
	 * Test method for {@link SpecialMath#combinations(int, int) }.
	 */
	@Test
	public void testCombinationsTest() {

		assertEquals(10, SpecialMath.combinations(5, 3));
		assertEquals(6, SpecialMath.combinations(4, 2));
		assertEquals(220, SpecialMath.combinations(12, 3));

	}

}