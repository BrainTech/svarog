/**
 * 
 */
package org.signalml.plugin.export.change;

import org.signalml.plugin.export.signal.ExportedTagStyle;

/**
 * Interface for a listener on tag style changes (addition, removal, change).
 * @author Marcin Szumski
 */
public interface SvarogTagStyleListener extends SvarogListner {

	/**
	 * Invoked when a {@link ExportedTagStyle tag style} is added to the set.
	 * @param e the tag style event
	 */
	void tagStyleAdded(SvarogTagStyleEvent e);
	
	/**
	 * Invoked when a {@link ExportedTagStyle tag style} is removed from the set.
	 * @param e the tag style event
	 */
	void tagStyleRemoved(SvarogTagStyleEvent e);
	
	/**
	 * Invoked when a {@link ExportedTagStyle tag style} is changed.
	 * @param e the tag style event
	 */
	void tagStyleChanged(SvarogTagStyleEvent e);
}
