/* MontageChannelsDataFlavor.java created 2008-01-04
 *
 */

package org.signalml.app.view.montage.dnd;

import java.awt.datatransfer.DataFlavor;

/** MontageChannelsDataFlavor
 *
 *
 * @author Michal Dobaczewski &copy; 2007-2008 CC Otwarte Systemy Komputerowe Sp. z o.o.
 */
public class MontageChannelsDataFlavor extends DataFlavor {

	private boolean continuous;

	public MontageChannelsDataFlavor(boolean continuous) {
		super(MontageChannelIndices.class, "montageChannels");
		this.continuous = continuous;
	}

	public boolean isContinuous() {
		return continuous;
	}

}
