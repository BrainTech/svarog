/* SourceMontageEventListener.java created 2007-11-23
 *
 */

package org.signalml.domain.montage;

import java.util.EventListener;

/** SourceMontageEventListener
 * Class representing some change to a SourceMontage
 *
 * @author Michal Dobaczewski &copy; 2007-2008 CC Otwarte Systemy Komputerowe Sp. z o.o.
 */
public interface SourceMontageListener extends EventListener {

        /**
         * Invoked when source channel is added to a montage
         * @param ev event object describing change
         */
	void sourceMontageChannelAdded(SourceMontageEvent ev);

        /**
         * Invoked when source channel is removed from a montage
         * @param ev event object describing change
         */
	void sourceMontageChannelRemoved(SourceMontageEvent ev);

        /**
         * Invoked when source channel is changed
         * @param ev event object describing change
         */
	void sourceMontageChannelChanged(SourceMontageEvent ev);

}
