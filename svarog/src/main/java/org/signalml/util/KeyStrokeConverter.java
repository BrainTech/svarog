/* KeyStrokeConverter.java created 2007-09-28
 *
 */

package org.signalml.util;

import javax.swing.KeyStroke;

import com.thoughtworks.xstream.converters.SingleValueConverter;

/**
 * KeyStrokeConverter provides conversion between KeyStroke and String.
 *
 * @author Michal Dobaczewski &copy; 2007-2008 CC Otwarte Systemy Komputerowe Sp. z o.o.
 */
public class KeyStrokeConverter implements SingleValueConverter {

	/**
	 * Creates KeyStroke from String. The string must have the following syntax: <br>
	 * &lt modifiers &gt* (&lt typedID &gt | &lt pressedReleasedID &gt) <br>
	 * modifiers := shift | control | ctrl | meta | alt | button1 | button2 | button3 <br>
	 * typedID := typed &lt typedKey &gt <br>
	 * typedKey := string of length 1 giving Unicode character. <br>
	 * pressedReleasedID := (pressed | released) key :<br>
	 * key := KeyEvent key code name, i.e. the name following "VK_".
	 * @param s String representation of KeyStroke
	 * @return KeyStroke which is represented by String, or null if specified String is null, or is formatted incorrectly
	 */
	@Override
	public Object fromString(String s) {
		if (s == null || s.isEmpty()) {
			return null;
		}
		return KeyStroke.getKeyStroke(s);
	}

	/**
	 * Converts KeyStroke to String.
	 * @param obj is Object which is KeyStroke to convert
	 * @return String representation of KeyStroke
	 */
	@Override
	public String toString(Object obj) {
		if (obj == null) {
			return "";
		}
		String s = ((KeyStroke) obj).toString();
		//TODO unsafe type cast
		s = s.replaceAll("pressed *", "");

		return s;
	}

	/**
	 * Check if KeyStroke is either the same as, or is a superclass of the class which is represented by specified Class parameter.
	 * @param clazz the Class object to be checked
	 * @return true if objects of type clazz can be assigned to objects of KeyStroke class, otherwise false
	 */
	@SuppressWarnings("unchecked")
	@Override
	public boolean canConvert(Class clazz) {
		return KeyStroke.class.isAssignableFrom(clazz);
	}

}
