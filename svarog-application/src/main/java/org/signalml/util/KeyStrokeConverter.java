/* KeyStrokeConverter.java created 2007-09-28
 * 
 */

package org.signalml.util;

import javax.swing.KeyStroke;

import com.thoughtworks.xstream.converters.SingleValueConverter;

/** KeyStrokeConverter
 *
 * 
 * @author Michal Dobaczewski &copy; 2007-2008 CC Otwarte Systemy Komputerowe Sp. z o.o.
 */
public class KeyStrokeConverter implements SingleValueConverter {

	@Override
	public Object fromString(String s) {
		if( s == null || s.isEmpty() ) {
			return null;
		}
		return KeyStroke.getKeyStroke(s);
	}

	@Override
	public String toString(Object obj) {
		if( obj == null ) {
			return "";
		}
		String s = ((KeyStroke) obj).toString();
		s = s.replaceAll( "pressed *", "");
		
		return s;
	}

	@SuppressWarnings("unchecked")
	@Override
	public boolean canConvert(Class clazz) {
		return KeyStroke.class.isAssignableFrom(clazz);
	}
	
}
