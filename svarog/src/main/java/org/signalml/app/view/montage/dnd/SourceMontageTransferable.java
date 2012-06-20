/* SourceMontageTransferable.java created 2008-01-04
 *
 */

package org.signalml.app.view.montage.dnd;

import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.io.IOException;

import org.signalml.domain.montage.SourceChannel;

/**
 * Wrapper used to transport {@link SourceChannelIndices}.
 * The transferred data have flavor {@link SourceMontageChannelsDataFlavor}.
 *
 * @author Michal Dobaczewski &copy; 2007-2008 CC Otwarte Systemy Komputerowe Sp. z o.o.
 */
public class SourceMontageTransferable implements Transferable {

	/**
	 * the "collection" of {@link SourceChannelIndices indexes} of
	 * {@link SourceChannel montage channels}
	 */
	private SourceChannelIndices indices;

	/**
	 * he {@link SourceMontageChannelsDataFlavor flavor} the transferred data
	 * can be provided in (the array contains only one flavor
	 * - SourceMontageChannelsDataFlavor)
	 */
	private DataFlavor[] dataFlavors;

	/**
	 * Constructor. Stores the {@link SourceChannelIndices indices} and creates
	 * the {@link SourceMontageChannelsDataFlavor flavor} for them.
	 * @param indices the indices to be transferred
	 */
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
