/* GrayscaleMapPalette.java created 2008-03-03
 *
 */

package org.signalml.app.view.book;

import javax.swing.Icon;

import org.signalml.app.util.IconUtils;
import org.signalml.util.Util;

/** GrayscaleMapPalette
 *
 *
 * @author Dobieslaw Ircha
 */
public class GrayscaleMapPalette implements WignerMapPalette {

	private static String[] CODES = new String[] { "wignerMapPalette.grayscale" };
	private static Object[] ARGUMENTS = new Object[0];

	private static GrayscaleMapPalette sharedInstance = null;

	private transient int[] palette;

	private GrayscaleMapPalette() {

		int i;
		palette = new int[256];

		int itmp;
		for (i = 0; i<256; i++) {

			itmp=255-i;
			palette[i] = Util.RGBToInteger(itmp,itmp,itmp);

		}

	}

	public static GrayscaleMapPalette getInstance() {
		if (sharedInstance == null) {
			sharedInstance = new GrayscaleMapPalette();
		}
		return sharedInstance;
	}

	@Override
	public Icon getIcon() {
		return IconUtils.loadClassPathIcon("org/signalml/app/icon/grayscale.png");
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
		return "Grayscale";
	}

}
