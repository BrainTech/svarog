/* Util.java created 2007-09-24
 *
 */

package org.signalml.test;

import static junit.framework.Assert.assertEquals;

/** Util
 *
 *
 * @author Michal Dobaczewski &copy; 2007-2008 CC Otwarte Systemy Komputerowe Sp. z o.o.
 */
public class Util {

	public static void assertFloatArrayEquals(float[] arr1, float[] arr2) {
		assertEquals(arr1.length, arr2.length);
		for (int i=0; i<arr1.length; i++) {
			assertEquals("Difference at " +i, arr1[i], arr2[i], 0F);
		}
	}

	public static void assertDoubleArrayEquals(double[] arr1, double[] arr2) {
		assertEquals(arr1.length, arr2.length);
		for (int i=0; i<arr1.length; i++) {
			assertEquals("Difference at " +i, arr1[i], arr2[i], 0F);
		}
	}

}
