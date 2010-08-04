/* MontageEventListener.java created 2007-11-23
 *
 */

package org.signalml.domain.montage;

import java.util.EventListener;

/**
 * This class represents an event listener associated with a change
 * in a {@link Montage montage}.
 * Changes include change in a structure, adding/removing/changing a
 * {@link MontageChannel channel}.
 *
 * @author Michal Dobaczewski &copy; 2007-2008 CC Otwarte Systemy Komputerowe Sp. z o.o.
 */
public interface MontageListener extends EventListener {

        /**
         * Invoked when a structure of a {@link Montage montage} is changed.
         * @param ev event object describing change
         */
	void montageStructureChanged(MontageEvent ev);

        /**
         * Invoked when {@link MontageChannel montage channels} are added to
         * a {@link Montage montage}.
         * @param ev an event object describing a change
         */
	void montageChannelsAdded(MontageEvent ev);

        /**
         * Invoked when {@link MontageChannel montage channels} are removed from
         * a {@link Montage montage}.
         * @param ev an event object describing a change
         */
	void montageChannelsRemoved(MontageEvent ev);

        /**
         * Invoked when {@link MontageChannel montage channels} are changed.
         * @param ev an event object describing a change
         */
	void montageChannelsChanged(MontageEvent ev);

        /**
         * Invoked when references of {@link MontageChannel montage channels}
         * are changed.
         * @param ev an event object describing a change
         */
	void montageReferenceChanged(MontageEvent ev);

}
