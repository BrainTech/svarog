/* MontageEvent.java created 2007-11-23
 *
 */

package org.signalml.domain.montage;

import java.util.EventObject;

/** MontageEvent
 *
 *
 * @author Michal Dobaczewski &copy; 2007-2008 CC Otwarte Systemy Komputerowe Sp. z o.o.
 */
public class MontageEvent extends EventObject {

	private static final long serialVersionUID = 1L;

	private int[] channels;
	private int[] primaryChannels;

	public MontageEvent(Object source, int[] channels, int[] primaryChannels) {
		super(source);
		this.channels = channels;
		this.primaryChannels = primaryChannels;

	}

	public int getChannel() {
		return channels[0];
	}

	public int getPrimaryChannel() {
		return primaryChannels[0];
	}

	public int[] getChannels() {
		return channels;
	}

	public int[] getPrimaryChannels() {
		return primaryChannels;
	}

}
