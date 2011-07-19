/* RainbowMapPalette.java created 2008-03-03
 *
 */

package org.signalml.app.view.book;

import javax.swing.Icon;

import org.apache.log4j.Logger;
import org.signalml.app.util.IconUtils;
import org.signalml.util.Util;

/** RainbowMapPalette
 *
 *
 * @author Dobieslaw Ircha
 */
public class RainbowMapPalette implements WignerMapPalette {

	protected static final Logger logger = Logger.getLogger(RainbowMapPalette.class);

	private static String[] CODES = new String[] { "wignerMapPalette.rainbow" };
	private static Object[] ARGUMENTS = new Object[0];

	private static RainbowMapPalette sharedInstance = null;

	private transient int[] palette;

	private RainbowMapPalette() {

		double hh=255.0;
		double ww=1.0F/(74.0*75.0);
		int i;

		palette = new int[256];

		int r;
		int g;
		int b;
		for (i = 0; i<256; i++) {

			r = (int)(hh * Math.exp(-ww * Util.sqr(i - 64.0)));
			g = (int)(hh * Math.exp(-ww * Util.sqr(i - 128.0)));
			b = (int)(hh * Math.exp(-ww * Util.sqr(i - 192.0)));

			palette[255-i] = Util.RGBToInteger(r, g, b);

		}

	}

	public static RainbowMapPalette getInstance() {
		if (null == sharedInstance) {
		    synchronized (RainbowMapPalette.class) {
		        if (null == sharedInstance)
		            sharedInstance = new RainbowMapPalette();
		    }
		}

		return sharedInstance;
	}

	@Override
	public Icon getIcon() {
		return IconUtils.loadClassPathIcon("org/signalml/app/icon/rainbow.png");
	}

	@Override
	public int[] getPalette() {
		return palette;
	}

	@Override
	public Object[] getArguments() {
		return ARGUMENTS;
	}

	@Override
	public String[] getCodes() {
		return CODES;
	}

	@Override
	public String getDefaultMessage() {
		return "Rainbow";
	}

}
