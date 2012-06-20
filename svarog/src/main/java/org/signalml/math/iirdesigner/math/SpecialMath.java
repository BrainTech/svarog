/* SpecialMath.java created 2010-09-12
 *
 */

package org.signalml.math.iirdesigner.math;

import org.apache.log4j.Logger;

/**
 * This class contains methods for performing specialistic mathematical operations
 * needed for the {@link IIRDesigner filter designers}.
 *
 * @author Piotr Szachewicz
 */
public class SpecialMath {

	/**
	 * Logger to save history of execution at.
	 */
	protected static final Logger logger = Logger.getLogger(SpecialMath.class);

	/**
	 * the machine epsilon for the type double - the largest positive value that,
	 * when added to 1.0, produces a sum equal to 1.0.
	 */
	static double machineEpsilon;

	/**
	 * Returns the value of the machine epsilon for the type double - i.e.
	 * the largest positive value that, when added to 1.0, produces a sum equal to 1.0.
	 *
	 * @return the machine epsilon for the type double
	 */
	public static double getMachineEpsilon() {

		if (machineEpsilon == 0.0) {
			machineEpsilon = 0.5;
			while (1+machineEpsilon > 1)
				machineEpsilon /= 2;
		}
		return machineEpsilon;

	}

	/**
	 * Returs the value of the polynomial represented by the given polynomial
	 * coefficients at the given value.
	 *
	 * @param x the value at which the polynomial will be evaluated
	 * @param coefficients the coefficients of the polynomial
	 * @return the value of the polynomial at the given x
	 */
	public static double evaluatePolynomial(double x, double[] coefficients) {

		double value = 0.0;

		for (int i = 0; i < coefficients.length; i++)
			value = value * x + coefficients[i];

		return value;

	}

	/**
	 * Calculates the value the complete elliptic integral of the first kind.
	 * (based on a coresponding function from the Cephes Math Library).
	 *
	 * @param m the value of an m argument at which the integral will be calculated
	 * @return the value of the complete elliptic integral of the first kind.
	 */
	public static double calculateCompleteEllipticIntegralOfTheFirstKind(double m) {

		double P[] =
		{
			1.37982864606273237150E-4,
			2.28025724005875567385E-3,
			7.97404013220415179367E-3,
			9.85821379021226008714E-3,
			6.87489687449949877925E-3,
			6.18901033637687613229E-3,
			8.79078273952743772254E-3,
			1.49380448916805252718E-2,
			3.08851465246711995998E-2,
			9.65735902811690126535E-2,
			1.38629436111989062502E0
		};

		double Q[] =
		{
			2.94078955048598507511E-5,
			9.14184723865917226571E-4,
			5.94058303753167793257E-3,
			1.54850516649762399335E-2,
			2.39089602715924892727E-2,
			3.01204715227604046988E-2,
			3.73774314173823228969E-2,
			4.88280347570998239232E-2,
			7.03124996963957469739E-2,
			1.24999999999870820058E-1,
			4.99999999999999999821E-1
		};

		double C1 = 1.3862943611198906188E0; // log(4)

		m = 1.0 - m;

		if ((m < 0.0) || (m > 1.0))
			throw new IllegalArgumentException("Bad Domain. m for the elliptic integral should be m=<0,1.0>");

		if (m > getMachineEpsilon())
			return evaluatePolynomial(m,P) - Math.log(m)*evaluatePolynomial(m,Q);
		else
		{
			if (m  ==  0.0) {
				logger.warn("Singular argument for the complete elliptic integral of the first kind (m=0)!");
				return Double.MAX_VALUE;
			}
			else
				return C1 - 0.5 * Math.log(m);
		}

	}

	/**
	 * Evaluates the Jacobian elliptic functions sn(u|m), cn(u|m),
	 * and dn(u|m) of parameter m between 0 and 1, and real
	 * argument u.
	 * (based on the function from the Cephes Math Library)
	 *
	 * @param u
	 * @param m
	 * @return an array containing the values of the functions sn(u|m), cn(u|m),
	 * dn(u|m) and phi, the amplitude of u
	 */
	public static double[] calculateJacobianEllipticFunctionsValues(double u, double m) {

		double ai, b, phi, t, twon;
		double sn, cn, ph, dn;
		double[] a = new double[9];
		double[] c = new double[9];
		int i;

		// Check for special cases
		if (m < 0.0 || m > 1.0 || Double.isNaN(m))
			throw new IllegalArgumentException("m should be in <0,1>");
		if (m < 1.0e-9) {

			t = Math.sin(u);
			b = Math.cos(u);
			ai = 0.25 * m * (u - t * b);
			sn = t - ai * b;
			cn = b + ai * t;
			ph = u - ai;
			dn = 1.0 - 0.5 * m * t * t;
			return new double[] {sn, cn, dn, ph};

		}
		if (m >= 0.9999999999) {
			ai = 0.25 * (1.0 - m);
			b = Math.cosh(u);
			t = Math.tanh(u);
			phi = 1.0 / b;
			twon = b * Math.sinh(u);

			sn = t + ai * (twon - u) / (b * b);
			ph = 2.0 * Math.atan(Math.exp(u)) - Math.PI / 2 + ai * (twon - u) / b;
			ai *= t * phi;
			cn = phi - ai * (twon - u);
			dn = phi + ai * (twon + u);
			return new double[] {sn, cn, dn, ph};
		}

		//	A. G. M. scale
		a[0] = 1.0;
		b = Math.sqrt(1.0 - m);
		c[0] = Math.sqrt(m);
		twon = 1.0;
		i = 0;

		while (Math.abs(c[i] / a[i]) > getMachineEpsilon()) {

			if (i > 7)
				throw new IllegalArgumentException("Jacobian elliptic functions cannot be calculated due to overflow range error");
			ai = a[i];
			i++;
			c[i] = (ai - b)/ 2.0;
			t = Math.sqrt(ai * b);
			a[i] = (ai + b)/ 2.0;
			b = t;
			twon *= 2.0;

		}

		// backward recurrence
		phi = twon * a[i] * u;
		do {

			t = c[i] * Math.sin(phi) / a[i];
			b = phi;
			phi = (Math.asin(t) + phi) / 2.0;
			i--;

		}
		while (i > 0);

		sn = Math.sin(phi);
		t = Math.cos(phi);
		cn = t;
		dn = t / Math.cos(phi - b);
		ph = phi;

		return new double[] {sn, cn, dn, ph};

	}



	/**
	 * Returns the value of the factorial n!
	 *
	 * @param n the argument of the factorial
	 * @return the value of n!
	 */
	public static int factorial(int n) {

		if (n < 0)
			throw new IllegalArgumentException("n must be >= 0");

		int value = 1;
		for (int i = 2; i <= n; i++)
			value *= i;
		return value;

	}

	/**
	 * Returns the number of combinations.
	 *
	 * @param n
	 * @param k
	 * @return the number of combinations
	 */
	public static int combinations(int n, int k) {

		if (!((0 <= k) && (k <= n)))
			throw new IllegalArgumentException("The input arguments should fulfill 0<=k<=n");

		int numerator = 1;
		for (int i = n; i > k; i--)
			numerator *= i;

		return numerator / factorial(n - k);

	}

	/**
	 * Returns the given angle changed to a 360 degrees complement.
	 * @param angle an angle to complement
	 * @return the 360 degrees complement of the angle
	 */
	public static double complement(double angle) {

		if (angle < 0)
			angle += 360;
		else if (angle > 0)
			angle -= 360;
		return angle;
	}

	/**
	 * Unwraps angles by converting it to a 360 degrees complements
	 * whenever the jumps between the values is larger than 180 degrees.
	 * @param angles an array of angles to be unwrapped
	 * @return the unwrapped copy of the angles
	 */
	public static double[] unwrap(double[] angles) {

		double[] unwrapped = new double[angles.length];

		unwrapped[0] = angles[0];

		for (int i = 1; i < unwrapped.length; i++) {
			if (Math.abs(angles[i] - unwrapped[i - 1]) > 180.0)
				unwrapped[i] = complement(angles[i]);
			else
				unwrapped[i] = angles[i];
		}
		return unwrapped;

	}

	/**
	 * Returns if the given number is odd.
	 * @param x the number to be checked
	 * @return true if the number is odd, false otherwise
	 */
	public static boolean isOdd(int x) {
		if (x % 2 == 1)
			return true;
		return false;
	}

	/**
	 * Returns if the given number is even.
	 * @param x the number to be checked
	 * @return true if the number is even, false otherwise
	 */
	public static boolean isEven(int x) {
		if (x % 2 == 0)
			return true;
		return false;
	}

	/** Compute the inverse hyperbolic cosine of a number.
	 * * (Note: This method is copy-pasted from
	 * apache.commons.math.util.FastMath under Apache License v2.0.
	 * After releasing version 3.0 of commons.math library
	 * this code should be deleted and all calls to this method
	 * should substituted by calls to apache.commons.math.util.FastMath
	 * equivalent method).
	 *
	 * @param a number on which evaluation is done
	 * @return inverse hyperbolic cosine of a
	 */
	public static double acosh(final double a) {
		return Math.log(a + Math.sqrt(a * a - 1));
	}

	/** Compute the inverse hyperbolic sine of a number.
	* (Note: This method is copy-pasted from
	* apache.commons.math.util.FastMath under Apache License v2.0.
	* After releasing version 3.0 of commons.math library
	* this code should be deleted and all calls to this method
	* should substituted by calls to apache.commons.math.util.FastMath
	* equivalent method).
	*
	* @param a number on which evaluation is done
	* @return inverse hyperbolic sine of a
	*/
	public static double asinh(double a) {

		boolean negative = false;
		if (a < 0) {
			negative = true;
			a = -a;
		}

		double absAsinh;
		if (a > 0.167) {
			absAsinh = Math.log(Math.sqrt(a * a + 1) + a);
		} else {
			final double a2 = a * a;
			if (a > 0.097) {
				absAsinh = a * (1 - a2 * (1 / 3.0 - a2 * (1 / 5.0 - a2 * (1 / 7.0 - a2 * (1 / 9.0 - a2 * (1.0 / 11.0 - a2 * (1.0 / 13.0 - a2 * (1.0 / 15.0 - a2 * (1.0 / 17.0) * 15.0 / 16.0) * 13.0 / 14.0) * 11.0 / 12.0) * 9.0 / 10.0) * 7.0 / 8.0) * 5.0 / 6.0) * 3.0 / 4.0) / 2.0);
			} else if (a > 0.036) {
				absAsinh = a * (1 - a2 * (1 / 3.0 - a2 * (1 / 5.0 - a2 * (1 / 7.0 - a2 * (1 / 9.0 - a2 * (1.0 / 11.0 - a2 * (1.0 / 13.0) * 11.0 / 12.0) * 9.0 / 10.0) * 7.0 / 8.0) * 5.0 / 6.0) * 3.0 / 4.0) / 2.0);
			} else if (a > 0.0036) {
				absAsinh = a * (1 - a2 * (1 / 3.0 - a2 * (1 / 5.0 - a2 * (1 / 7.0 - a2 * (1 / 9.0) * 7.0 / 8.0) * 5.0 / 6.0) * 3.0 / 4.0) / 2.0);
			} else {
				absAsinh = a * (1 - a2 * (1 / 3.0 - a2 * (1 / 5.0) * 3.0 / 4.0) / 2.0);
			}
		}

		return negative ? -absAsinh : absAsinh;

	}

}