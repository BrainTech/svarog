/* TagListener.java created 2007-10-01
 *
 */

package org.signalml.domain.tag;

import java.util.EventListener;

/**
 * This is an interface for event listeners associated with adding, removing
 * or changing the tag in a {@link StyledTagSet StyledTagSet}.
 *
 * @author Michal Dobaczewski &copy; 2007-2008 CC Otwarte Systemy Komputerowe Sp. z o.o.
 */
public interface TagListener extends EventListener {

        /**
         * Invoked when the tag is added to the set
         * @param e an event object describing the addition
         */
	void tagAdded(TagEvent e);

        /**
         * Invoked when the tag is removed from the set
         * @param e an event object describing the removal
         */
	void tagRemoved(TagEvent e);

        /**
         * Invoked when the tag is changed in the set
         * @param e an event object describing the change
         */
	void tagChanged(TagEvent e);

}
