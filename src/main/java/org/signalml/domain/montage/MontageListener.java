/* MontageEventListener.java created 2007-11-23
 *
 */

package org.signalml.domain.montage;

import java.util.EventListener;

/**
 * Class representing an event listener associated with a change
 * in a {@link Montage montage}.
 * Changes include change in a structure, adding/removing/changing a
 * {@link MontageChannel channel}.
 *
 * @author Michal Dobaczewski &copy; 2007-2008 CC Otwarte Systemy Komputerowe Sp. z o.o.
 */
public interface MontageListener extends EventListener {

        /**
         * Invoked when a structure of a montage is changed
         * @param ev event object describing change
         */
	void montageStructureChanged(MontageEvent ev);

        /**
         * Invoked when montage channels are added to a montage
         * @param ev an event object describing a change
         */
	void montageChannelsAdded(MontageEvent ev);

        /**
         * Invoked when montage channels are removed from a montage
         * @param ev an event object describing a change
         */
	void montageChannelsRemoved(MontageEvent ev);

        /**
         * Invoked when montage channels are changed
         * @param ev an event object describing a change
         */
	void montageChannelsChanged(MontageEvent ev);

        /**
         * Invoked when references of montage channels are changed
         * @param ev an event object describing a change
         */
	void montageReferenceChanged(MontageEvent ev);

}
