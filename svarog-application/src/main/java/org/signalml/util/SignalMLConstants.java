/* SignalMLConstants.java created 2008-01-17
 * 
 */

package org.signalml.util;

import java.awt.Dimension;
import java.util.Locale;

/** SignalMLConstants
 *
 * 
 * @author Michal Dobaczewski &copy; 2007-2008 CC Otwarte Systemy Komputerowe Sp. z o.o.
 */
public abstract class SignalMLConstants {

	public static final String VERSION = "0.4.7";
	
	public static final Dimension MIN_ASSUMED_DESKTOP_SIZE = new Dimension(1024,768);
	
	public static Locale[] AVAILABLE_LOCALES = new Locale[] {Locale.ENGLISH, new Locale("pl")};	
	
}
