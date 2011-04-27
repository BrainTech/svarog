/* ArrayOperationsTest.java created 2010-12-02
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

	/**
	 * A test method for {@link ArrayOperations#padWithZeros(double[], int) }.
	 */
	@Test
	public void testPadWithZeros() {

		double[] array;
		double[] paddedArray;

		array = new double[] {};
		paddedArray = ArrayOperations.padArrayWithZerosToSize(array, 0);
		assertEquals(0, paddedArray.length);

		paddedArray = ArrayOperations.padArrayWithZerosToSize(array, 2);
		assertEquals(2, paddedArray.length);
		assertArrayEquals(new double[] {0.0, 0.0}, paddedArray, 1e-10);

		array = new double[] {1.0};
		paddedArray = ArrayOperations.padArrayWithZerosToSize(array, 1);
		assertEquals(1, paddedArray.length);
		assertArrayEquals(new double[] {1.0}, paddedArray, 1e-10);

		paddedArray = ArrayOperations.padArrayWithZerosToSize(array, 4);
		assertEquals(4, paddedArray.length);
		assertArrayEquals(new double[] {1.0, 0.0, 0.0, 0.0}, paddedArray, 1e-10);

		array = new double[] {1.0, 0.2, 0.3};
		paddedArray = ArrayOperations.padArrayWithZerosToSize(array, 5);
		assertEquals(5, paddedArray.length);
		assertArrayEquals(new double[] {1.0, 0.2, 0.3, 0.0, 0.0}, paddedArray, 1e-10);

		array = new double[] {1, 2, 5, 4};
		paddedArray = ArrayOperations.padArrayWithZerosToSize(array, 10);
		assertEquals(10, paddedArray.length);
		assertArrayEquals(paddedArray, new double[] {1, 2, 5, 4, 0, 0, 0, 0, 0, 0}, 0.01);

	}

	/**
	 * A test method for {@link ArrayOperations#convolve(double[], double[]) }.
	 */
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

	/**
	 * A test method for {@link ArrayOperations#reverse(double[]) }.
	 */
	@Test
	public void testReverse() {
		double[] array = new double[] {1, 2, 3, 4, 5};

		assertArrayEquals(ArrayOperations.reverse(array), new double[] {5, 4, 3, 2, 1}, 0.001);
	}

	/**
	 * A test method for {@link ArrayOperations#trimArrayToSize(double[], int) }.
	 */
	@Test
	public void testTrimArrayToSize() {

		double[] array = new double[] {1, 2, 3, 4, 5, 6};
		assertArrayEquals(ArrayOperations.trimArrayToSize(array, 4), new double[] {1, 2, 3, 4}, 0.001);

	}

	/**
	 * A test method for {@link ArrayOperations#removeFirstElements(double[], int) }.
	 */
	@Test
	public void testRemoveFirstElements() {

		double[] array = new double[] {1, 2, 3, 4, 5, 6};
		double[] trimmed = ArrayOperations.removeFirstElements(array, 2);

		assertEquals(4, trimmed.length);
		assertArrayEquals(trimmed, new double[] {3, 4, 5, 6}, 0.001);

	}

}