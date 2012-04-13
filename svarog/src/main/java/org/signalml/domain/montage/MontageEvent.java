/* MontageEvent.java created 2007-11-23
 *
 */

package org.signalml.domain.montage;

import java.util.EventObject;

/**
 * This class represents an event associated with some change in a
 * {@link Montage montage}.
 * Changes may involve adding, removing and changing the
 * {@link MontageChannel montage channel}, changing reference and changing
 * the montage structure.
 * Contains arrays of indexes of montage channels and
 * {@link SourceChannel primary channels} that were changed.
 *
 * @author Michal Dobaczewski &copy; 2007-2008 CC Otwarte Systemy Komputerowe Sp. z o.o.
 */
public class MontageEvent extends EventObject {

	private static final long serialVersionUID = 1L;

	/**
	 * an array of indexes of {@link MontageChannel montage channels}
	 * that are associated with this event
	 */
	private int[] channels;

	/**
	 * an array of indexes of {@link SourceChannel source channels}
	 * that are associated with this event
	 */
	private int[] primaryChannels;

	/**
	 * Constructor. Creates a new event associated with some change
	 * in a montage.
	 * @param source a montage with which event is associated
	 * @param channels an array of indexes of
	 * {@link MontageChannel montage channels} that are associated with
	 * this event
	 * @param primaryChannels an array of indexes of
	 * {@link SourceChannel source channels} that are associated with
	 * this event
	 */
	public MontageEvent(Object source, int[] channels, int[] primaryChannels) {
		super(source);
		this.channels = channels;
		this.primaryChannels = primaryChannels;

	}

	/**
	 * Returns the first element of {@link #channels channels array},
	 * that is the index of a first montage channel given as parameter.
	 * @return the first element of channels array,
	 * that is the index of a first montage channel given as parameter
	 */
	public int getChannel() {
		return channels[0];
	}

	/**
	 * Returns the first element of
	 * {@link #primaryChannels primaryChannels array}, that is the index of
	 * a first {@link SourceChannel source channel} given as parameter.
	 * @return the first element of  primaryChannels array, that is the
	 * index of a first source channel given as parameter
	 */
	public int getPrimaryChannel() {
		return primaryChannels[0];
	}

	/**
	 * Returns an array of indexes of {@link MontageChannel montage channels}
	 * that are associated with this event
	 * @return an array of indexes of montage channels
	 * that are associated with this event
	 */
	public int[] getChannels() {
		return channels;
	}

	/**
	 * Returns an array of indexes of {@link SourceChannel source channels}
	 * that are associated with this event
	 * @return an array of indexes of source channels
	 * that are associated with this event
	 */
	public int[] getPrimaryChannels() {
		return primaryChannels;
	}

}
