/* MontageTransferable.java created 2008-01-04
 *
 */

package org.signalml.app.view.montage.dnd;

import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.io.IOException;

/** MontageTransferable
 *
 *
 * @author Michal Dobaczewski &copy; 2007-2008 CC Otwarte Systemy Komputerowe Sp. z o.o.
 */
public class MontageTransferable implements Transferable {

	private MontageChannelIndices indices;
	private DataFlavor[] dataFlavors;

	public MontageTransferable(MontageChannelIndices indices) {

		if (indices == null) {
			throw new NullPointerException("No montage channels");
		}
		this.indices = indices;

		int[] montageChannels = indices.getMontageChannels();

		boolean continuous = true;
		for (int i=0; i<(montageChannels.length-1); i++) {
			if (montageChannels[i]+1 != montageChannels[i+1]) {
				continuous =  false;
				break;
			}
		}

		dataFlavors = new DataFlavor[] { new MontageChannelsDataFlavor(continuous) };

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
