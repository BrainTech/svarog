/* SourceMontageEvent.java created 2007-11-23
 *
 */

package org.signalml.domain.montage;

import java.util.EventObject;

/** SourceMontageEvent
 * Class representing event associated with some change in source montage channels
 *
 * @author Michal Dobaczewski &copy; 2007-2008 CC Otwarte Systemy Komputerowe Sp. z o.o.
 */
public class SourceMontageEvent extends EventObject {


	private static final long serialVersionUID = 1L;

        /**
         * number of a Source Channel that is connected
         */
	private int channel;

        /**
         * index of a source channel that has been added/removed/changed
         * @param source source montage on which the Event initially occurred.
         * @param channel channel that has been added/removed/changed
         */
	public SourceMontageEvent(Object source, int channel) {
		super(source);
		this.channel = channel;
	}

        /**
         *
         * @return channel that has been added/removed/changed
         */
	public int getChannel() {
		return channel;
	}

}
