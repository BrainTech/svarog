/* SourceMontageTableTransferHandler.java created 2008-01-04
 *
 */

package org.signalml.app.view.montage.dnd;

import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.util.Arrays;

import javax.swing.JComponent;
import javax.swing.ListSelectionModel;
import javax.swing.TransferHandler;

import org.apache.log4j.Logger;
import org.signalml.app.view.montage.SourceMontageTable;

/** SourceMontageTableTransferHandler
 *
 *
 * @author Michal Dobaczewski &copy; 2007-2008 CC Otwarte Systemy Komputerowe Sp. z o.o.
 */
public class SourceMontageTableTransferHandler extends TransferHandler {

	private static final long serialVersionUID = 1L;

	protected static final Logger logger = Logger.getLogger(SourceMontageTableTransferHandler.class);

	@Override
	public int getSourceActions(JComponent c) {
		return COPY;
	}

	@Override
	protected Transferable createTransferable(JComponent c) {

		SourceMontageTable table = (SourceMontageTable) c;

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

		logger.debug("Exporting [" + array.length + "] rows as drag");

		return new SourceMontageTransferable(new SourceChannelIndices(array));

	}

	@Override
	public boolean canImport(JComponent comp, DataFlavor[] transferFlavors) {
		// imports nothing
		return false;
	}

}
