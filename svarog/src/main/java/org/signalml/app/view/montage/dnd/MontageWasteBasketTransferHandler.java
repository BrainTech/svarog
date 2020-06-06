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
import org.signalml.domain.montage.MontageChannel;

/**
 * The {@link TransferHandler} for {@link MontageWasteBasket}.
 * If {@link MontageChannel montage channels} (actually the
 * {@link MontageChannelIndices}) are dragged and dropped on the element to
 * which this transfer handler is assigned, then it
 * {@link Montage#removeMontageChannels(int[]) removes} them from the
 * {@link MontageTable}.
 *
 * @author Michal Dobaczewski &copy; 2007-2008 CC Otwarte Systemy Komputerowe Sp. z o.o.
 */
public class MontageWasteBasketTransferHandler extends TransferHandler {

	/**
	 * the default serialization constant
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * the logger
	 */
	protected static final Logger logger = Logger.getLogger(MontageWasteBasketTransferHandler.class);

	/**
	 * the {@link MontageChannelsDataFlavor flavor} for
	 * {@link MontageChannelIndices}
	 */
	private MontageChannelsDataFlavor montageFlavor = new MontageChannelsDataFlavor(false);

	/**
	 * the {@link MontageTable table} which allows to edit the labels and the order
	 * (the indexes) of {@link MontageChannel montage channels}
	 */
	private MontageTable table;

	/**
	 * Constructor. Creates this handler and stores the {@link MontageTable}.
	 * @param table the montage table which will be updated if the data is
	 * transferred to this handler
	 */
	public MontageWasteBasketTransferHandler(MontageTable table) {
		super();
		this.table = table;
	}

	/**
	 * Returns that no data can be transfered from this handler.
	 */
	@Override
	public int getSourceActions(JComponent c) {
		return NONE;
	}


	/**
	 * @return <code>true</code> if the data described by this flavors can be
	 * imported by this handler
	 */
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

	/**
	 * Removes the {@link MontageChannel montage channels} that are transferred
	 * to this component from {@link #table}:
	 * <ul>
	 * <li>{@link #canImport(JComponent, DataFlavor[]) checks} if the
	 * {@link TransferHandler.TransferSupport#getTransferable() data} can be
	 * imported (they have a proper flavor),</li>
	 * <li>checks if the {@link MontageChannelIndices indices} are not empty,
	 * </li>
	 * <li>{@link Montage#removeMontageChannels(int[]) removes} the channels
	 * from the {@link Montage montage} that is a model for the {@link #table}.
	 * </li></ul>
	 */
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
