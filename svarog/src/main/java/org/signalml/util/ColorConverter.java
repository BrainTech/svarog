/* ColorConverter.java created 2007-09-28
 *
 */

package org.signalml.util;

import com.thoughtworks.xstream.converters.SingleValueConverter;
import java.awt.Color;
import java.util.Formatter;

/**
 * ColorConverter provides conversion between Color and String.
 *
 * @author Michal Dobaczewski &copy; 2007-2008 CC Otwarte Systemy Komputerowe Sp. z o.o.
 */
public class ColorConverter implements SingleValueConverter {

	/**
	 * Creates Color from specified String (String must be in format rrggbb*, where rr, gg, bb are nonnegative numbers in base sixteen).
	 * @param s String representation of Color in RGB model
	 * @return Color which is represented by specified String
	 */
	@Override
	public Object fromString(String s) {
		return new Color(
				   Integer.parseInt(s.substring(0, 2), 16),
				   Integer.parseInt(s.substring(2, 4), 16),
				   Integer.parseInt(s.substring(4, 6), 16)
			   );
		//TODO maybe throw an error when s has more than 6 elements
		// and/or when one of numbers is negative
	}

	/**
	 * Converts Color to String.
	 * @param obj the Object which is Color to convert
	 * @return String representation of color in RGB model (in format rrggbb, where rr, gg, bb are nonnegative numbers in base sixteen with leading zeros)
	 */
	@Override
	public String toString(Object obj) {
		Color c = (Color) obj;
		Formatter formatter = new Formatter();
		formatter.format("%02x%02x%02x", c.getRed(), c.getGreen(), c.getBlue());
		return formatter.toString();
	}

	/**
	 * Check if Color is either the same as, or is a superclass of the class which is represented by specified Class parameter.
	 * @param clazz the Class object to be checked
	 * @return true if objects of type clazz can be assigned to objects of Color class, otherwise false
	 */
	@SuppressWarnings("unchecked")
	@Override
	public boolean canConvert(Class clazz) {
		return Color.class.isAssignableFrom(clazz);
	}

}
