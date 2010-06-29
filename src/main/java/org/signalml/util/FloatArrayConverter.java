/* FloatArrayConverter.java created 2007-10-18
 *
 */
package org.signalml.util;

import com.thoughtworks.xstream.converters.SingleValueConverter;

/** FloatArrayConverter
 *
 *
 * @author Michal Dobaczewski &copy; 2007-2008 CC Otwarte Systemy Komputerowe Sp. z o.o.
 */
public class FloatArrayConverter implements SingleValueConverter {

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

	@SuppressWarnings("unchecked")
	@Override
	public boolean canConvert(Class clazz) {
		return float[].class.isAssignableFrom(clazz);
	}

}
