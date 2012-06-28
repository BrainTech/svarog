/* GrayscaleMapPalette.java created 2008-03-03
 *
 */

package org.signalml.app.view.book.palette;

import static org.signalml.app.util.i18n.SvarogI18n._;

import javax.swing.Icon;

import org.signalml.app.util.IconUtils;
import org.signalml.util.Util;

/** GrayscaleMapPalette
 *
 *
 * @author Dobieslaw Ircha
 */
public class GrayscaleMapPalette implements IWignerMapPalette {

	private static GrayscaleMapPalette sharedInstance = null;
	private transient int[] palette;

	public GrayscaleMapPalette() {

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
			synchronized (GrayscaleMapPalette.class) {
				if (sharedInstance == null)
					sharedInstance = new GrayscaleMapPalette();
			}
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

	/**
	 * Returns the name of this palette [i18n].
	 *
	 * @return the name of this palette
	 */
	public String i18n() {
		return _("Grayscale");
	}

}
