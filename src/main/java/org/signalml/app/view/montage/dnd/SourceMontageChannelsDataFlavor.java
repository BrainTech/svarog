/* SourceMontageChannelsDataFlavor.java created 2008-01-04
 *
 */

package org.signalml.app.view.montage.dnd;

import java.awt.datatransfer.DataFlavor;

/**
 * The {@link DataFlavor data flavor} for {@link SourceChannelIndices}.
 *
 * @author Michal Dobaczewski &copy; 2007-2008 CC Otwarte Systemy Komputerowe Sp. z o.o.
 */
public class SourceMontageChannelsDataFlavor extends DataFlavor {

	/**
	 * Creates the data flavor for {@link SourceChannelIndices}.
	 */
	public SourceMontageChannelsDataFlavor() {
		super(SourceChannelIndices.class, "sourceChannels");
	}

}
