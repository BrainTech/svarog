/* MontageChannelsDataFlavor.java created 2008-01-04
 *
 */

package org.signalml.app.view.montage.dnd;

import java.awt.datatransfer.DataFlavor;

/**
 * The {@link DataFlavor data flavor} for {@link MontageChannelIndices}.
 * The flavor may be continuous, which means that the channel indexes in
 * MontageChannelIndices are the consecutive numbers
 * ({@code i, i+1, i+2, ..., i+k}).
 *
 * @author Michal Dobaczewski &copy; 2007-2008 CC Otwarte Systemy Komputerowe Sp. z o.o.
 */
public class MontageChannelsDataFlavor extends DataFlavor {

	/**
	 * <code>true</code> if the channel indexes in {@link MontageChannelIndices}
	 * are the consecutive numbers ({@code i, i+1, i+2, ..., i+k}),
	 * <code>false</code> otherwise
	 */
	private boolean continuous;

	/**
	 * Creates the data flavor for {@link MontageChannelIndices}.
	 * @param continuous <code>true</code> if the channel indexes in
	 * MontageChannelIndices are the consecutive numbers
	 * ({@code i, i+1, i+2, ..., i+k}), <code>false</code> otherwise
	 */
	public MontageChannelsDataFlavor(boolean continuous) {
		super(MontageChannelIndices.class, "montageChannels");
		this.continuous = continuous;
	}

	/**
	 * Checks if the channel indexes in {@link MontageChannelIndices} are the
	 * consecutive numbers ({@code i, i+1, i+2, ..., i+k}).
	 *
	 * @return the <code>true</code> if the channel indexes in
	 * MontageChannelIndices are the consecutive numbers
	 * ({@code i, i+1, i+2, ..., i+k}),
	 * <code>false</code> otherwise
	 */
	public boolean isContinuous() {
		return continuous;
	}

}
