/* PrettyStringLocaleWrapper.java created 2007-09-13
 * 
 */
package org.signalml.app.util;

import java.util.Locale;

/** PrettyStringLocaleWrapper
 *
 * 
 * @author Michal Dobaczewski &copy; 2007-2008 CC Otwarte Systemy Komputerowe Sp. z o.o.
 */
public class PrettyStringLocaleWrapper {
	
	private Locale locale;
	
	public PrettyStringLocaleWrapper(Locale locale) {
		this.locale = locale;
	}

	public Locale getLocale() {
		return locale;
	}
	
	@Override
	public String toString() {
		String name = locale.getDisplayLanguage(locale);
		name = name.substring(0, 1).toUpperCase(locale).concat(name.substring(1));
		return name;
	}
	
}
