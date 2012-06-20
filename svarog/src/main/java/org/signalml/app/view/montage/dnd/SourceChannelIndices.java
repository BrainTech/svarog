/* SourceChannelIndices.java created 2008-01-04
 *
 */

package org.signalml.app.view.montage.dnd;

import org.signalml.domain.montage.SourceChannel;

/**
 * The "collection" of indexes of {@link SourceChannel source channels}.
 * Actually contains only the array of these indexes and can
 * {@link #getSourceChannels() return} that array.
 *
 * @author Michal Dobaczewski &copy; 2007-2008 CC Otwarte Systemy Komputerowe Sp. z o.o.
 */
public class SourceChannelIndices {

	/**
	 * the array of indexes of {@link SourceChannel source channels} that
	 * are stored in this "collection".
	 */
	private int[] sourceChannels;

	/**
	 * Constructor. Stores the array of indexes of {@link SourceChannel
	 * source channels}.
	 * @param sourceChannels the array of indexes of source channels
	 */
	public SourceChannelIndices(int[] sourceChannels) {
		if (sourceChannels == null) {
			throw new NullPointerException("No source channels");
		}
		this.sourceChannels = sourceChannels;
	}

	/**
	 * Gets the array of indexes of {@link SourceChannel source channels} that
	 * are stored in this "collection".
	 *
	 * @return the array of indexes of source channels
	 */
	public int[] getSourceChannels() {
		return sourceChannels;
	}

}
