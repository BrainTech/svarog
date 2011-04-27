/**
 *
 */
package org.signalml.app.util;


/**
 * @author Oskar Kapala &copy; 2007-2008 CC Otwarte Systemy Komputerowe Sp. z o.o.
 *
 */
public class MatlabUtil {

	private static boolean INITIALIZED = false;

	public static void initialize() {

		if (!INITIALIZED) {
			// Prevent Matlab from replacing L&F
			System.setProperty("mathworks.DisableSetLookAndFeel", "true");
			INITIALIZED = true;
		}
	}


}
