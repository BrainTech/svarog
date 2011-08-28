/* InitialStateCalculatorTest.java created 2011-03-01
 *
 */

package org.signalml.domain.montage.filter.iirdesigner;

import org.apache.commons.math.linear.Array2DRowRealMatrix;
import org.apache.commons.math.linear.ArrayRealVector;
import org.apache.commons.math.linear.RealMatrix;
import org.apache.commons.math.linear.RealVector;
import org.junit.Test;
import static org.signalml.domain.montage.filter.iirdesigner.IIRDesignerAssert.*;

/**
 * This class performs unit tests on {@link InitalStateCalculator}.
 *
 * @author Piotr Szachewicz
 */
public class InitialStateCalculatorTest {

	/**
	 * Test method for {@link InitalStateCalculator#calculateZID() }.
	 */
	@Test
	public void testCalculateZID() {

		//coefficients 1
		InitalStateCalculator calculator = new InitalStateCalculator(getSampleFilterCoefficients1());
		RealVector actual = calculator.calculateZID();

		RealVector expected = new ArrayRealVector(new double[] {0.00236855, 0.00024174, 0.00072069});

		assertVectorEquals(expected, actual, 1e-7);

		//coefficients 2
		calculator = new InitalStateCalculator(getSampleFilterCoefficients2());
		actual = calculator.calculateZID();

		expected = new ArrayRealVector(new double[] {0.389, 0.35, 0.1, 0.7, 0.4});

		assertVectorEquals(expected, actual, 1e-7);

	}

	/**
	 * Returns the coefficients of the exemplary filter.
	 * @return the coefficients of the exemplary filter
	 */
	private FilterCoefficients getSampleFilterCoefficients1() {
		double[] bCoefficients = new double[] {0.00041655, 0.00124964, 0.00124964, 0.00041655};
		double[] aCoefficients = new double[] {1, -2.6861574, 2.41965511, -0.73016535};
		FilterCoefficients filterCoefficients = new FilterCoefficients(bCoefficients, aCoefficients);
		return filterCoefficients;
	}

	/**
	 * Returns the coefficients of another exemplary filter.
	 * @return the coefficients of the exemplary filter
	 */
	private FilterCoefficients getSampleFilterCoefficients2() {
		double[] bCoefficients = new double[] {0.1, 0.4, 0.3, 0.1, 0.7, 0.4};
		double[] aCoefficients = new double[] {1.0, 0.11, -0.5};
		FilterCoefficients filterCoefficients = new FilterCoefficients(bCoefficients, aCoefficients);
		return filterCoefficients;
	}

	/**
	 * Test method for {@link InitalStateCalculator#calculateZIN() }.
	 */
	@Test
	public void testCalculateZIN() {

		//coefficients 1
		InitalStateCalculator calculator = new InitalStateCalculator(getSampleFilterCoefficients1());
		RealMatrix actual = calculator.calculateZIN();

		RealMatrix expected = new Array2DRowRealMatrix(new double[][] {
			{-1.6861574, - 1, 0},
			{2.41965511, 1, -1,},
			{-0.73016535, 0, 1}});

		assertMatrixEquals(expected, actual, 1e-7);

		//coefficients 2
		calculator = new InitalStateCalculator(getSampleFilterCoefficients2());
		actual = calculator.calculateZIN();

		expected = new Array2DRowRealMatrix(new double[][] {
				{1.11, - 1, 0, 0, 0},
				{-0.5, 1, -1, 0, 0},
				{0, 0, 1, -1, 0},
				{0, 0, 0, 1, -1},
				{0, 0, 0, 0, 1}});

		assertMatrixEquals(expected, actual, 1e-7);
	}

	/**
	 * Test method for {@link InitalStateCalculator#calculateSubtrahendForZIN() }.
	 */
	@Test
	public void testCalculateSubtrahendForZIN() {

		//coefficients 1
		InitalStateCalculator calculator = new InitalStateCalculator(getSampleFilterCoefficients1());
		RealMatrix actual = calculator.calculateSubtrahendForZIN();

		RealMatrix expected = new Array2DRowRealMatrix(new double[][] {
			{2.6861574, 1, 0},
			{-2.41965511, 0, 1},
			{0.73016535, 0, 0}});

		assertMatrixEquals(expected, actual, 1e-7);

		//coefficients 2
		calculator = new InitalStateCalculator(getSampleFilterCoefficients2());
		actual = calculator.calculateSubtrahendForZIN();

		expected = new Array2DRowRealMatrix(new double[][] {
				{-0.11, 1, 0, 0, 0},
				{0.5, 0, 1, 0, 0},
				{-0, 0, 0, 1, 0},
				{-0, 0, 0, 0, 1},
				{-0, 0, 0, 0, 0}
		});

		assertMatrixEquals(expected, actual, 1e-7);
	}

	/**
	 * Test method for {@link InitalStateCalculator#getInitialState() }.
	 */
	@Test
	public void testGetInitialState() {

		//coefficients 1
		InitalStateCalculator calculator = new InitalStateCalculator(getSampleFilterCoefficients1());
		double[] actual = calculator.getInitialState();

		double[] expected = new double[] {0.99958345, -1.68782358,  0.73058189};

		assertArrayEquals(expected, actual, 1e-4);

		//coefficients 2
		calculator = new InitalStateCalculator(getSampleFilterCoefficients2());
		actual = calculator.getInitialState();

		expected = new double[] {3.17868852, 3.13934426, 1.2, 1.1, 0.4};

		assertArrayEquals(expected, actual, 1e-4);

	}

	/**
	 * Test method for {@link InitalStateCalculator#growSignal(double[])  }.
	 */
	@Test
	public void testGrowSignal() {
		double[] signal = new double[20]; //1, 2, 3, ...., 20
		for (int i = 0; i < 20; i++) {
			signal[i] = i + 1;
		}

		InitalStateCalculator calculator = new InitalStateCalculator(getSampleFilterCoefficients1());
		double[] grownSignal = calculator.growSignal(signal);
		double[] expected = new double[]{-10, -9, -8, -7, -6, -5, -4, -3, -2, -1, 0, 1, 2,
			3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15,
			16, 17, 18, 19, 20, 21, 22, 23, 24, 25, 26, 27, 28,
			29, 30, 31};

		assertEquals(42, grownSignal.length);
		assertArrayEquals(expected, grownSignal, 1e-4);
	}

	/**
	 * This method tests the whole procedure for the InitialStateCalculator:
	 * it calculates the initial state and then grows the signal, each step
	 * is verified.
	 */
	@Test
	public void wholeProcedureTest() {
		double[] signal = new double[] {
			-0.84293027, -0.92374498, -0.88684131, -0.84391936, -0.87729384,
		       -0.76973675, -0.77477867, -0.77924067, -0.68329653, -0.68361526,
		       -0.58494503, -0.73579347, -0.72676736, -0.61426226, -0.7348077 ,
		       -0.63514914, -0.61154161, -0.5558349 , -0.53932159, -0.51812691,
		       -0.47357742, -0.45951363, -0.48207101, -0.40786034, -0.36886221,
		       -0.32913236, -0.22319939, -0.30716421, -0.29695674, -0.30282762,
		       -0.1731335 , -0.29801377, -0.04960099, -0.10052872, -0.05416476};

		double[] grownSignal = new double[] {
			-0.95006707, -1.10091551, -1.00224528, -1.00256401, -0.90661987, -0.91108187,
			 -0.91612379, -0.8085667,  -0.84194118, -0.79901923, -0.76211556, -0.84293027,
			 -0.92374498, -0.88684131, -0.84391936, -0.87729384, -0.76973675, -0.77477867,
			 -0.77924067, -0.68329653, -0.68361526, -0.58494503, -0.73579347, -0.72676736,
			 -0.61426226, -0.7348077,  -0.63514914, -0.61154161, -0.5558349,  -0.53932159,
			 -0.51812691, -0.47357742, -0.45951363, -0.48207101, -0.40786034, -0.36886221,
			 -0.32913236, -0.22319939, -0.30716421, -0.29695674, -0.30282762, -0.1731335,
			 -0.29801377, -0.04960099, -0.10052872, -0.05416476, -0.0078008,  -0.05872853,
			  0.18968425,  0.06480398,  0.1944981,   0.18862722,  0.19883469,  0.11486987,
			  0.22080284,  0.26053269,  0.29953082};

		double[] bCoefficients = new double[]
			{0.00041655,  0.00124964,  0.00124964,  0.00041655};
		double[] aCoefficients = new double[]
			{1.        , -2.6861574 ,  2.41965511, -0.73016535};

		InitalStateCalculator calculator = new InitalStateCalculator(new FilterCoefficients(bCoefficients, aCoefficients));

		double[] actual = calculator.getInitialState();
		double[] expected = new double[] {0.99958345, -1.68782358, 0.73058189};
		assertArrayEquals(expected, actual, 1e-4);

		double[] actualGrownSignal = calculator.growSignal(signal);
		assertArrayEquals(grownSignal, actualGrownSignal, 1e-4);

	}

}
