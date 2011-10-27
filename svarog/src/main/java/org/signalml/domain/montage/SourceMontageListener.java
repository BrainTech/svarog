/* SourceMontageEventListener.java created 2007-11-23
 *
 */

package org.signalml.domain.montage;

import java.util.EventListener;
import org.signalml.domain.montage.system.EegSystem;

/**
 * This class represents an event listener associated with a change
 * in a {@link SourceMontage source montage}.
 * Changes include change in a structure, adding/removing/changing a
 * {@link SourceChannel channel}.
 *
 * @author Michal Dobaczewski &copy; 2007-2008 CC Otwarte Systemy Komputerowe Sp. z o.o.
 */
public interface SourceMontageListener extends EventListener {

        /**
         * Invoked when a {@link SourceChannel source channel} is added
         * to a montage.
         * @param ev an event object describing a change
         */
	void sourceMontageChannelAdded(SourceMontageEvent ev);

        /**
         * Invoked when a {@link SourceChannel source channel} is removed
         * from a montage.
         * @param ev an event object describing a change
         */
	void sourceMontageChannelRemoved(SourceMontageEvent ev);

        /**
         * Invoked when a {@link SourceChannel source channel} is changed.
         * @param ev an event object describing a change
         */
	void sourceMontageChannelChanged(SourceMontageEvent ev);

	/**
         * Invoked when a {@link EegSystem} for the {@link SourceMontage} changes.
         * @param ev an event object describing a change
         */
	void sourceMontageEegSystemChanged(SourceMontageEvent ev);

}
