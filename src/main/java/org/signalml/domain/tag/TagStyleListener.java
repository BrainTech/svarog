/* TagStyleListener.java created 2007-10-01
 *
 */

package org.signalml.domain.tag;

import java.util.EventListener;

/**
 * This is an interface for event listeners associated with adding, removing
 * or changing the tag style in a {@link StyledTagSet StyledTagSet}.
 *
 * @author Michal Dobaczewski &copy; 2007-2008 CC Otwarte Systemy Komputerowe Sp. z o.o.
 */
public interface TagStyleListener extends EventListener {

        /**
         * Invoked when the tag style is added to the set
         * @param e an event object describing the addition
         */
	void tagStyleAdded(TagStyleEvent e);

        /**
         * Invoked when the tag style is removed from the set
         * @param e an event object describing the removal
         */
	void tagStyleRemoved(TagStyleEvent e);

        /**
         * Invoked when the tag style is changed in the set
         * @param e an event object describing the change
         */
	void tagStyleChanged(TagStyleEvent e);

}
