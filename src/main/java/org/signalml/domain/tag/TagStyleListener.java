/* TagStyleListener.java created 2007-10-01
 *
 */

package org.signalml.domain.tag;

import java.util.EventListener;

/**
 * This is an interface for an event listeners associated with adding, removing
 * or changing the {@link TagStyle tag style} in a
 * {@link StyledTagSet set}.
 *
 * @author Michal Dobaczewski &copy; 2007-2008 CC Otwarte Systemy Komputerowe Sp. z o.o.
 */
public interface TagStyleListener extends EventListener {

        /**
         * Invoked when the {@link TagStyle tag style} is added to the
         * {@link StyledTagSet set}.
         * @param e an event object describing the addition
         */
	void tagStyleAdded(TagStyleEvent e);

        /**
         * Invoked when the {@link TagStyle tag style} is removed from the
         * {@link StyledTagSet set}.
         * @param e an event object describing the removal
         */
	void tagStyleRemoved(TagStyleEvent e);

        /**
         * Invoked when the {@link TagStyle tag style} is changed in the
         * {@link StyledTagSet set}.
         * @param e an event object describing the change
         */
	void tagStyleChanged(TagStyleEvent e);

}
