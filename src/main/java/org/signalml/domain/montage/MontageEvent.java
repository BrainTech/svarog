/* MontageEvent.java created 2007-11-23
 *
 */

package org.signalml.domain.montage;

import java.util.EventObject;

/** MontageEvent
 * Class representing event associated with some change in a montage
 *
 * @author Michal Dobaczewski &copy; 2007-2008 CC Otwarte Systemy Komputerowe Sp. z o.o.
 */
public class MontageEvent extends EventObject {

	private static final long serialVersionUID = 1L;

        /**
         * array of indexes of montage channels associated with current object
         */
	private int[] channels;

        /**
         * array of indexes of source channels associated with current object
         */
	private int[] primaryChannels;

        /**
         * Constructor. Creates new event associated with some change in a montage
         * @param source montage with which event is associated
         * @param channels array of indexes of montage channels associated with some change in a montage
         * @param primaryChannels array of indexes of source channels associated with some change in a montage
         */
	public MontageEvent(Object source, int[] channels, int[] primaryChannels) {
		super(source);
		this.channels = channels;
		this.primaryChannels = primaryChannels;

	}

        /**
         * Returns first element of channels array, i.e. index of first montage channel given as parameter
         * @return first element of channels array, i.e. index of first montage channel given as parameter
         */
	public int getChannel() {
		return channels[0];
	}

        /**
         * Returns first element of primaryChannels array, i.e. index of first source channel given as parameter
         * @return first element of primaryChannels array, i.e. index of first source channel given as parameter
         */
	public int getPrimaryChannel() {
		return primaryChannels[0];
	}

        /**
         * Returns indexes of montage channels given as parameter of current object
         * @return indexes of montage channels given as parameter of current object
         */
	public int[] getChannels() {
		return channels;
	}

        /**
         * Returns indexes of source channels given as parameter of current object
         * @return indexes of source channels given as parameter of current object
         */
	public int[] getPrimaryChannels() {
		return primaryChannels;
	}

}
