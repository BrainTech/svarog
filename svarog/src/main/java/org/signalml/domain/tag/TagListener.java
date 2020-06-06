/* TagListener.java created 2007-10-01
 *
 */

package org.signalml.domain.tag;

import java.util.EventListener;
import org.signalml.plugin.export.signal.Tag;

/**
 * This is an interface for an event listeners associated with adding, removing
 * or changing the {@link Tag tag} in a {@link StyledTagSet StyledTagSet}.
 *
 * @author Michal Dobaczewski &copy; 2007-2008 CC Otwarte Systemy Komputerowe Sp. z o.o.
 */
public interface TagListener extends EventListener {

	/**
	 * Invoked when the {@link Tag tag} is added to
	 * the {@link StyledTagSet set}.
	 * @param e an event object describing the addition
	 */
	void tagAdded(TagEvent e);

	/**
	 * Invoked when the {@link Tag tag} is removed from
	 * the {@link StyledTagSet set}.
	 * @param e an event object describing the removal
	 */
	void tagRemoved(TagEvent e);

	/**
	 * Invoked when the {@link Tag tag} is changed in
	 * the {@link StyledTagSet set}.
	 * @param e an event object describing the change
	 */
	void tagChanged(TagEvent e);

}
