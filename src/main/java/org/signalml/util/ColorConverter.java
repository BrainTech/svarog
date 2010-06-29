/* ColorConverter.java created 2007-09-28
 *
 */

package org.signalml.util;

import java.awt.Color;
import java.util.Formatter;

import com.thoughtworks.xstream.converters.SingleValueConverter;

/** ColorConverter
 *
 *
 * @author Michal Dobaczewski &copy; 2007-2008 CC Otwarte Systemy Komputerowe Sp. z o.o.
 */
public class ColorConverter implements SingleValueConverter {

	@Override
	public Object fromString(String s) {
		return new Color(
		               Integer.parseInt(s.substring(0, 2), 16),
		               Integer.parseInt(s.substring(2, 4), 16),
		               Integer.parseInt(s.substring(4, 6), 16)
		       );
	}

	@Override
	public String toString(Object obj) {
		Color c = (Color) obj;
		Formatter formatter = new Formatter();
		formatter.format("%02x%02x%02x", c.getRed(), c.getGreen(), c.getBlue());
		return formatter.toString();
	}

	@SuppressWarnings("unchecked")
	@Override
	public boolean canConvert(Class clazz) {
		return Color.class.isAssignableFrom(clazz);
	}

}
