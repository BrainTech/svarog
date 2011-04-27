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


}
