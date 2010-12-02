/* ArrayOperations.java created 2010-12-02
 *
 */
package org.signalml.domain.montage.filter.iirdesigner;

/**
 * This class contains various methods operating on arrays for
 * convolution, reversing the order of elements in array etc.
 *
 * @author Piotr Szachewicz
 */
public class ArrayOperations {

	/**
	 * Returns the array extended to a new size. New elements are filled with
	 * zeros. New array size must be greater or equal to the original
	 * array size.
	 * @param array the array to be resized
	 * @param newSize the new size for the array
	 * @return the resized array
	 */
	protected static double[] padWithZeros(double[] array, int newSize) {

		assert (newSize >= array.length);

		double[] paddedArray = new double[newSize];

		System.arraycopy(array, 0, paddedArray, 0, array.length);

		for (int i = array.length; i < paddedArray.length; i++) {
			paddedArray[i] = 0;
		}

		return paddedArray;

	}

	/**
	 * Returns the convolution of the two arrays.
	 *
	 * @param array1 first input array
	 * @param array2 second input array
	 * @return an array containing the discrete convolution of array1
	 * and array2
	 */
	public static double[] convolve(double[] array1, double[] array2) {

		int n = array1.length + array2.length - 1;
		double[] result = new double[n];
		double[] f = padWithZeros(array1, n);
		double[] g = padWithZeros(array2, n);

		int i;
		int fpos, gpos;

		for (i = 0; i < result.length; i++) {

			fpos = 0;
			gpos = i;
			for (; fpos <= i && gpos >= 0; fpos++, gpos--) {
				result[i] += f[fpos] * g[gpos];
			}

		}

		return result;
	}

	/**
	 * Returns an reversed copy of an array.
	 *
	 * @param array an array to be reversed
	 * @return the copy of the array having the order of its elements
	 * reversed
	 */
	public static double[] reverse(double[] array) {

		double[] newArray = new double[array.length];

		for (int i = 0; i < array.length; i++) {
			newArray[i] = array[array.length - 1 - i];
		}

		return newArray;

	}

	/**
	 * Returns a trimmed copy of a given array.
	 *
	 * @param array an array to be trimmed
	 * @param size the size to which the array should be trimmed (must be
	 * less or equal to the original size of the array)
	 * @return a trimmed copy of the given array
	 */
	public static double[] trimArrayToSize(double[] array, int size) {

		assert (array.length >= size);

		double[] trimmedArray = new double[size];
		System.arraycopy(array, 0, trimmedArray, 0, size);
		return trimmedArray;

	}
}
