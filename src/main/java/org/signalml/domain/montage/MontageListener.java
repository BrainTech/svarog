/* MontageEventListener.java created 2007-11-23
 *
 */

package org.signalml.domain.montage;

import java.util.EventListener;

/** MontageEventListener
 * Class representing event associated with a change in a montage
 *
 * @author Michal Dobaczewski &copy; 2007-2008 CC Otwarte Systemy Komputerowe Sp. z o.o.
 */
public interface MontageListener extends EventListener {

        /**
         * Invoked when structure of a montage is changed
         * @param ev event object describing change
         */
	void montageStructureChanged(MontageEvent ev);

        /**
         * Invoked when montage channels are added to a montage
         * @param ev event object describing change
         */
	void montageChannelsAdded(MontageEvent ev);

        /**
         * Invoked when montage channels are removed from a montage
         * @param ev event object describing change
         */
	void montageChannelsRemoved(MontageEvent ev);

        /**
         * Invoked when montage channels are changed
         * @param ev event object describing change
         */
	void montageChannelsChanged(MontageEvent ev);

        /**
         * Invoked when references of a montage channels are changed
         * @param ev event object describing change
         */
	void montageReferenceChanged(MontageEvent ev);

}
