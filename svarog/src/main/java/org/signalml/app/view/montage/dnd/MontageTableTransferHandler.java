/* MontageTableTransferHandler.java created 2008-01-04
 *
 */

package org.signalml.app.view.montage.dnd;

import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.io.IOException;
import java.util.Arrays;
import javax.swing.JComponent;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.TransferHandler;
import org.apache.log4j.Logger;
import org.signalml.app.view.montage.MontageTable;
import org.signalml.domain.montage.Montage;
import org.signalml.domain.montage.MontageChannel;
import org.signalml.domain.montage.SourceChannel;

/**
 * The {@link TransferHandler} for {@link MontageTable}.
 * If {@link MontageChannel montage} (actually the {@link MontageChannelIndices})
 * or {@link SourceChannel source} (actually the {@link SourceChannelIndices})
 * channels are dragged and dropped on the element to which this transfer
 * handler is assigned, then the operation is performed according to the type
 * of the transferred object:
 * <ul>
 * <li>If transferred are {@link SourceChannelIndices}:
 * <ul>
 * <li>checks if the collection is of proper type and not empty,</li>
 * <li>{@link Montage#addMontageChannels(int[], int) adds} the new
 * {@link MontageChannel montage channels} (created on the basis of the
 * {@link SourceChannel source channels} from the collection) to the
 * {@link Montage montage} that is the model for the montage table.</li>
 * </ul></li>
 * <li>If transferred are {@link MontageChannelIndices}:
 * <ul>
 * <li>checks if the collection is of proper type and not empty,</li>
 * <li>checks if the collection is continuous (see
 * {@link MontageChannelsDataFlavor#isContinuous()})),</li>
 * <li>{@link Montage#moveMontageChannelRange(int, int, int) moves} the
 * specified channels to the specified position,</li>
 * <li>updates the selection in the montage table.</li></ul></li></ul>
 * <p>
 * The element to which this transfer handler is assigned can also be the
 * source of the transferable object. If the method {@link
 * #createTransferable(JComponent)} is called the {@link MontageTransferable}
 * is created with the channels selected in the table.
 *
 * @author Michal Dobaczewski &copy; 2007-2008 CC Otwarte Systemy Komputerowe Sp. z o.o.
 */
public class MontageTableTransferHandler extends TransferHandler {

	/**
	 * the serialization constant
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * the logger
	 */
	protected static final Logger logger = Logger.getLogger(MontageTableTransferHandler.class);

	/**
	 * the {@link SourceMontageChannelsDataFlavor flavor} for
	 * {@link SourceChannelIndices}
	 */
	private SourceMontageChannelsDataFlavor sourceFlavor = new SourceMontageChannelsDataFlavor();

	/**
	 * the {@link MontageChannelsDataFlavor flavor} for
	 * {@link MontageChannelIndices}
	 */
	private MontageChannelsDataFlavor montageFlavor = new MontageChannelsDataFlavor(false);

	/**
	 * @return {@link TransferHandler#MOVE}
	 */
	@Override
	public int getSourceActions(JComponent c) {
		return MOVE;
	}

	/**
	 * Checks which channels are selected and creates the array with their
	 * indexes. This array is used to create {@link MontageChannelIndices}
	 * and on the basis of it the {@link MontageTransferable}.
	 */
	@Override
	protected Transferable createTransferable(JComponent c) {

		MontageTable table = (MontageTable) c;

		ListSelectionModel model = table.getSelectionModel();

		int[] array;

		int minIndex = model.getMinSelectionIndex();
		if (minIndex < 0) {
			array = new int[0];
		} else {
			int maxIndex = model.getMaxSelectionIndex();
			if (maxIndex < 0) {
				array = new int[0];
			} else {

				int cnt = 0;
				int[] candidates = new int[maxIndex-minIndex+1];

				for (int i=minIndex; i<=maxIndex; i++) {
					if (model.isSelectedIndex(i)) {
						candidates[cnt] = i;
						cnt++;
					}
				}

				array = Arrays.copyOf(candidates, cnt);

			}
		}

		return new MontageTransferable(new MontageChannelIndices(array));

	}

	/**
	 * Returns <code>true</code> if at least one of the flavors in
	 * {@code transferFlavors} is either {@link SourceMontageChannelsDataFlavor}
	 * or {@link MontageChannelsDataFlavor} and <code>false</code> otherwise.
	 */
	@Override
	public boolean canImport(JComponent comp, DataFlavor[] transferFlavors) {

		logger.debug("Testing drop for [" + transferFlavors.length + "] flavors");

		for (int i=0; i<transferFlavors.length; i++) {

			logger.debug("Testing drop for flavor [" + transferFlavors[i].toString() + "]");

			if (transferFlavors[i].equals(sourceFlavor)) {
				logger.debug("Accepted source");
				return true;
			}
			if (transferFlavors[i].equals(montageFlavor)) {
				if (((MontageChannelsDataFlavor) transferFlavors[i]).isContinuous()) {
					logger.debug("Accepted target");
					return true;
				}
			}

		}

		logger.debug("Nothing interesting in this drop");
		return false;

	}

	/**
	 * Performs the operation according to the type of the transferred object:
	 * <ul>
	 * <li>If transferred are {@link SourceChannelIndices}:
	 * <ul>
	 * <li>checks if the collection is of proper type and not empty,</li>
	 * <li>{@link Montage#addMontageChannels(int[], int) adds} the new
	 * {@link MontageChannel montage channels} (created on the basis of the
	 * {@link SourceChannel source channels} from the collection) to the
	 * {@link Montage montage} that is the model for the montage table.</li>
	 * </ul></li>
	 * <li>If transferred are {@link MontageChannelIndices}:
	 * <ul>
	 * <li>checks if the collection is of proper type and not empty,</li>
	 * <li>checks if the collection is continuous (see
	 * {@link MontageChannelsDataFlavor#isContinuous()})),</li>
	 * <li>{@link Montage#moveMontageChannelRange(int, int, int) moves} the
	 * specified channels to the specified position,</li>
	 * <li>updates the selection in the montage table.</li></ul></li></ul>
	 */
	@Override
	public boolean importData(TransferSupport support) {

		if (!support.isDrop()) {
			return false;
		}

		MontageTable table = (MontageTable) support.getComponent();

		DataFlavor[] dataFlavors = support.getDataFlavors();
		if (dataFlavors == null || dataFlavors.length == 0) {
			return false;
		}
		if (!canImport(table, dataFlavors)) {
			return false;
		}

		JTable.DropLocation dropLocation = (JTable.DropLocation) support.getDropLocation();
		if (dropLocation == null) {
			return false;
		}

		Transferable transferable = support.getTransferable();

		if (transferable.isDataFlavorSupported(sourceFlavor)) {

			SourceChannelIndices indices = null;
			try {
				indices = (SourceChannelIndices) transferable.getTransferData(sourceFlavor);
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

			int[] sourceChannels = indices.getSourceChannels();
			if (sourceChannels == null || sourceChannels.length == 0) {
				logger.warn("Drop empty, no rows in int[] table");
				return false;
			}

			int row = dropLocation.getRow();

			Montage montage = table.getModel().getMontage();
			if (montage == null) {
				logger.warn("No montage");
				return false;
			}

			montage.addMontageChannels(sourceChannels, row);

		} else if (transferable.isDataFlavorSupported(montageFlavor)) {

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

			// check continuity
			for (int i=0; i<(montageChannels.length-1); i++) {
				if (montageChannels[i]+1 != montageChannels[i+1]) {
					logger.debug("Not contiguous");
					return false;
				}
			}

			int row = dropLocation.getRow();

			int lastMovedRow = montageChannels[0] + (montageChannels.length-1);

			int delta = 0;

			// analyze/correct the delta to make this more intuitive
			if (row >= montageChannels[0] && row <= lastMovedRow) {
				// the drop line is in the selection range, disregard
				return false;
			}
			else if (row < montageChannels[0]) {
				// the drop line is above, move normally
				delta = row - montageChannels[0];
			} else {
				// the drop line is below - make sure that the end effect is that
				// the dragged row end up between the rows between which the line was
				delta = (row - montageChannels[0]) - montageChannels.length;
			}

			Montage montage = table.getModel().getMontage();
			if (montage == null) {
				logger.warn("No montage");
				return false;
			}

			int movedDelta = montage.moveMontageChannelRange(montageChannels[0], montageChannels.length, delta);
			table.getSelectionModel().setSelectionInterval(montageChannels[0]+movedDelta, lastMovedRow+movedDelta);

		} else {
			return false;
		}

		return true;

	}

	@Override
	protected void exportDone(JComponent source, Transferable data, int action) {

	}

}
