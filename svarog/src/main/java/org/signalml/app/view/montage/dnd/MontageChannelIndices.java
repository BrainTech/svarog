/* SourceChannelIndices.java created 2008-01-04
 *
 */

package org.signalml.app.view.montage.dnd;

import org.signalml.domain.montage.MontageChannel;

/**
 * The "collection" of indexes of {@link MontageChannel montage channels}.
 * Actually contains only the array of these indexes and can
 * {@link #getMontageChannels() return} that array.
 *
 * @author Michal Dobaczewski &copy; 2007-2008 CC Otwarte Systemy Komputerowe Sp. z o.o.
 */
public class MontageChannelIndices {

	/**
	 * the array of indexes of {@link MontageChannel montage channels} that
	 * are stored in this "collection".
	 */
	private int[] montageChannels;

	/**
	 * Constructor. Stores the array of indexes of {@link MontageChannel
	 * montage channels}.
	 * @param montageChannels the array of indexes of montage channels
	 */
	public MontageChannelIndices(int[] montageChannels) {
		if (montageChannels == null) {
			throw new NullPointerException("No montage channels");
		}
		this.montageChannels = montageChannels;
	}

	/**
	 * Gets the array of indexes of {@link MontageChannel montage channels} that
	 * are stored in this "collection".
	 *
	 * @return the array of indexes of montage channels
	 */
	public int[] getMontageChannels() {
		return montageChannels;
	}

}
