/* SourceMontageEvent.java created 2007-11-23
 *
 */

package org.signalml.domain.montage;

import java.util.EventObject;

/**
 * This class represents an event associated with change, removal or addition of
 * a {@link SourceChannel source channel} to a {@link SourceMontage source montage}.
 * Contains an index of this channel.
 *
 * @author Michal Dobaczewski &copy; 2007-2008 CC Otwarte Systemy Komputerowe Sp. z o.o.
 */
public class SourceMontageEvent extends EventObject {


	private static final long serialVersionUID = 1L;

        /**
         * an index of a {@link SourceChannel source channel} that was changed
         */
	private int channel;

	public SourceMontageEvent(Object source) {
		super(source);
	}

        /**
         * Creates an event associated with change, removal or addition of
         * a {@link SourceChannel source channel}.
         * @param source a source montage on which the Event initially occurred.
         * @param channel an index of a source channel that has been
         * added/removed/changed
         */
	public SourceMontageEvent(Object source, int channel) {
		super(source);
		this.channel = channel;
	}

        /**
         * Returns an index of a {@link SourceChannel source channel} that
         * is associated with this event.
         * @return an index of a source channel that is associated with
         * this event.
         */
	public int getChannel() {
		return channel;
	}

}
