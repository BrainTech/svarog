/* MontageWasteBasketTransferHandler.java created 2008-01-04
 *
 */

package org.signalml.app.view.montage.dnd;

import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.io.IOException;

import javax.swing.JComponent;
import javax.swing.TransferHandler;

import org.apache.log4j.Logger;
import org.signalml.app.view.montage.MontageTable;
import org.signalml.domain.montage.Montage;

/** MontageWasteBasketTransferHandler
 *
 *
 * @author Michal Dobaczewski &copy; 2007-2008 CC Otwarte Systemy Komputerowe Sp. z o.o.
 */
public class MontageWasteBasketTransferHandler extends TransferHandler {

	private static final long serialVersionUID = 1L;

	protected static final Logger logger = Logger.getLogger(MontageWasteBasketTransferHandler.class);

	private MontageChannelsDataFlavor montageFlavor = new MontageChannelsDataFlavor(false);

	private MontageTable table;

	public MontageWasteBasketTransferHandler(MontageTable table) {
		super();
		this.table = table;
	}

	@Override
	public int getSourceActions(JComponent c) {
		return NONE;
	}

	@Override
	public boolean canImport(JComponent comp, DataFlavor[] transferFlavors) {

		logger.debug("Testing drop for [" + transferFlavors.length + "] flavors");

		for (int i=0; i<transferFlavors.length; i++) {

			logger.debug("Testing drop for flavor [" + transferFlavors[i].toString() + "]");

			if (transferFlavors[i].equals(montageFlavor)) {
				logger.debug("Accepted target");
				return true;
			}

		}

		logger.debug("Nothing interesting in this drop");
		return false;

	}

	@Override
	public boolean importData(TransferSupport support) {

		if (!support.isDrop()) {
			return false;
		}

		MontageWasteBasket basket = (MontageWasteBasket) support.getComponent();

		DataFlavor[] dataFlavors = support.getDataFlavors();
		if (dataFlavors == null || dataFlavors.length == 0) {
			return false;
		}
		if (!canImport(basket, dataFlavors)) {
			return false;
		}

		Transferable transferable = support.getTransferable();

		if (transferable.isDataFlavorSupported(montageFlavor)) {

			MontageChannelIndices indices = null;
			try {
				indices = (MontageChannelIndices) transferable.getTransferData(montageFlavor);
			} catch (UnsupportedFlavorException ex) {
				logger.error("Failed to drop", ex);
				return false;
			} catch (IOException ex) {
				logger.error("Failed to drop", ex);
				return false;
			}
			if (indices == null) {
				logger.warn("Drop empty, no indices");
				return false;
			}

			int[] montageChannels = indices.getMontageChannels();
			if (montageChannels == null || montageChannels.length == 0) {
				logger.warn("Drop empty, no rows in int[] table");
				return false;
			}

			Montage montage = table.getModel().getMontage();
			if (montage == null) {
				logger.warn("No montage");
				return false;
			}

			montage.removeMontageChannels(montageChannels);

		}

		return true;

	}

}
