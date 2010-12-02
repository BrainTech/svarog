/* Convolution.java created 2010-12-02
 *
 */
package org.signalml.domain.montage.filter.iirdesigner;

/**
 *
 * @author Piotr Szachewicz
 */
public class ArrayOperations {

	protected static double[] padWithZeros(double[] array, int n) {

		assert (n >= array.length);

		double[] paddedArray = new double[n];

		System.arraycopy(array, 0, paddedArray, 0, array.length);

		for (int i = array.length; i < paddedArray.length; i++) {
			paddedArray[i] = 0;
		}

		return paddedArray;

	}

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

	public static double[] reverse(double[] array) {

		double[] newArray = new double[array.length];

		for (int i = 0; i < array.length; i++) {
			newArray[i] = array[array.length - 1 - i];
		}

		return newArray;

	}

	public static double[] trimArrayToSize(double[] array, int size) {

		assert (array.length > size);

		double[] trimmedArray = new double[size];
		System.arraycopy(array, 0, trimmedArray, 0, size);
		return trimmedArray;

	}
}
