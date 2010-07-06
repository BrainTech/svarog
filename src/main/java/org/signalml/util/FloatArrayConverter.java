/* FloatArrayConverter.java created 2007-10-18
 *
 */
package org.signalml.util;

import com.thoughtworks.xstream.converters.SingleValueConverter;

/** FloatArrayConverter
 * class provides conversion between array of floats and String.
 *
 * @author Michal Dobaczewski &copy; 2007-2008 CC Otwarte Systemy Komputerowe Sp. z o.o.
 */
public class FloatArrayConverter implements SingleValueConverter {

	/**
	 * Creates array of floats from specified String, using ", " as separator between following numbers
	 * @param s is String to read floats from
	 * @return array of floats
	 */
	@Override
	public Object fromString(String s) {
		if (s == null || s.isEmpty()) {
			return null;
		}
		String[] parts  = s.split(" *, *");
		float[] res = new float[parts.length];
		for (int i=0; i<parts.length; i++) {
			res[i] = Float.parseFloat(parts[i]);
		}
		return res;
	}

	/**
	 * Converts array of floats to String, using ", " as separator between following numbers
	 * @param obj the Object which is array of floats to convert
	 * @return String representation of array of floats
	 */
	@Override
	public String toString(Object obj) {
		if (obj == null) {
			return "";
		}
		float[] arr = (float[]) obj;
		StringBuilder sb = new StringBuilder();
		for (int i=0; i<arr.length; i++) {
			if (i > 0) {
				sb.append(", ");
			}
			sb.append(Float.toString(arr[i]));
		}
		return sb.toString();
	}

	/**
	 * Checks if the class which is represented by specified Class parameter is the array of floats.
	 * @param clazz the Class object to be checked
	 * @return true if clazz is of type float[], otherwise false
	 */
	@SuppressWarnings("unchecked")
	@Override
	public boolean canConvert(Class clazz) {
		return float[].class.isAssignableFrom(clazz);
	}

}
