/* ImageTransferable.java created 2007-12-18
 *
 */

package org.signalml.app.util;

import java.awt.Image;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.io.IOException;

/** ImageTransferable
 *
 *
 * @author Michal Dobaczewski &copy; 2007-2008 CC Otwarte Systemy Komputerowe Sp. z o.o.
 */
public class ImageTransferable implements Transferable {

	private static final DataFlavor[] DATA_FLAVORS = new DataFlavor[] { DataFlavor.imageFlavor };

	private Image image;

	public ImageTransferable(Image image) {
		this.image = image;
	}

	@Override
	public Object getTransferData(DataFlavor flavor) throws UnsupportedFlavorException, IOException {
		if (DATA_FLAVORS[0].equals(flavor)) {
			return image;
		}
		throw new UnsupportedFlavorException(flavor);
	}

	@Override
	public DataFlavor[] getTransferDataFlavors() {
		return DATA_FLAVORS;
	}

	@Override
	public boolean isDataFlavorSupported(DataFlavor flavor) {
		return (DATA_FLAVORS[0].equals(flavor));
	}

}
