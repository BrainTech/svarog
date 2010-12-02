/* ConvolutionTest.java created 2010-12-02
 *
 */

package org.signalml.domain.montage.filter.iirdesigner;

import org.junit.Test;

import static org.signalml.domain.montage.filter.iirdesigner.IIRDesignerAssert.*;

/**
 * This method performs unit tests on {@link Convolution}.
 *
 * @author Piotr Szachewicz
 */
public class ArrayOperationsTest {

	@Test
	public void testPadWithZeros() {

		double[] array = {1, 2, 5, 4};
		double[] paddedArray;

		paddedArray = ArrayOperations.padWithZeros(array, 10);

		assertArrayEquals(paddedArray, new double[] {1, 2, 5, 4, 0, 0, 0, 0, 0, 0}, 0.01);

	}

	@Test
	public void testConvolve() {

		double[] array1;
		double[] array2;

		//test data set 1
		array1 = new double[] {1, 1, 1};
		array2 = new double[] {1, 2, 3};

		assertArrayEquals(ArrayOperations.convolve(array1, array2), new double[] {1, 3, 6, 5, 3}, 0.01);

		//test data set 2
		array1 = new double[] {0.1, 0.6, -5.432, 0.44};
		array2 = new double[] {9, -12, -0.33};

		assertArrayEquals(ArrayOperations.convolve(array1, array2), new double[] {0.9000, 4.2000, -56.1210, 68.9460, -3.4874, -0.1452}, 0.0001);

	}

	@Test
	public void testReverse() {
		double[] array = new double[] {1, 2, 3, 4, 5};

		assertArrayEquals(ArrayOperations.reverse(array), new double[] {5, 4, 3, 2, 1}, 0.001);
	}

	@Test
	public void testTrimArrayToSize() {

		double[] array = new double[] {1, 2, 3, 4, 5, 6};
		assertArrayEquals(ArrayOperations.trimArrayToSize(array, 4), new double[] {1, 2, 3, 4}, 0.001);

	}

}