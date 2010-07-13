/* MontageEvent.java created 2007-11-23
 *
 */

package org.signalml.domain.montage;

import java.util.EventObject;

/**
 * Class representing event associated with some change in a montage.
 * Contains arrays of indexes of channels and primaryChannels that were changed.
 *
 * @author Michal Dobaczewski &copy; 2007-2008 CC Otwarte Systemy Komputerowe Sp. z o.o.
 */
public class MontageEvent extends EventObject {

	private static final long serialVersionUID = 1L;

        /**
         * an array of indexes of montage channels associated with the current object
         */
	private int[] channels;

        /**
         * ab array of indexes of source channels associated with the current object
         */
	private int[] primaryChannels;

        /**
         * Constructor. Creates a new event associated with some change in a montage
         * @param source a montage with which event is associated
         * @param channels an array of indexes of montage channels associated with some change in a montage
         * @param primaryChannels an array of indexes of source channels associated with some change in a montage
         */
	public MontageEvent(Object source, int[] channels, int[] primaryChannels) {
		super(source);
		this.channels = channels;
		this.primaryChannels = primaryChannels;

	}

        /**
         * Returns the first element of channels array, i.e. the index of a first montage channel given as parameter
         * @return the first element of channels array, i.e. the index of a first montage channel given as parameter
         */
	public int getChannel() {
		return channels[0];
	}

        /**
         * Returns the first element of primaryChannels array, i.e. the index of a first source channel given as parameter
         * @return the first element of primaryChannels array, i.e. the index of a first source channel given as parameter
         */
	public int getPrimaryChannel() {
		return primaryChannels[0];
	}

        /**
         * Returns indexes of montage channels given as a parameter of the current object
         * @return indexes of montage channels given as a parameter of the current object
         */
	public int[] getChannels() {
		return channels;
	}

        /**
         * Returns indexes of source channels given as a parameter of the current object
         * @return indexes of source channels given as a parameter of the current object
         */
	public int[] getPrimaryChannels() {
		return primaryChannels;
	}

}
