/* SourceMontageTransferable.java created 2008-01-04
 *
 */

package org.signalml.app.view.montage.dnd;

import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.io.IOException;

/** SourceMontageTransferable
 *
 *
 * @author Michal Dobaczewski &copy; 2007-2008 CC Otwarte Systemy Komputerowe Sp. z o.o.
 */
public class SourceMontageTransferable implements Transferable {

	private SourceChannelIndices indices;
	private DataFlavor[] dataFlavors;

	public SourceMontageTransferable(SourceChannelIndices indices) {
		if (indices == null) {
			throw new NullPointerException("No source channels");
		}
		this.indices = indices;
		dataFlavors = new DataFlavor[] { new SourceMontageChannelsDataFlavor() };
	}

	@Override
	public Object getTransferData(DataFlavor flavor) throws UnsupportedFlavorException, IOException {
		if (flavor.equals(dataFlavors[0])) {
			return indices;
		}
		throw new UnsupportedFlavorException(flavor);
	}

	@Override
	public DataFlavor[] getTransferDataFlavors() {
		return dataFlavors;
	}

	@Override
	public boolean isDataFlavorSupported(DataFlavor flavor) {
		return flavor.equals(dataFlavors[0]);
	}

}
