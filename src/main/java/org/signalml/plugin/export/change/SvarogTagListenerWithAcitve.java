/**
 * 
 */
package org.signalml.plugin.export.change;

import org.signalml.plugin.export.signal.ExportedTag;


/**
 * Interface for a listener on {@link ExportedTag tag} changes (addition,
 * removal, change) including changes of an active tag.
 * 
 * @see SvarogAccessChangeSupport
 * @author Marcin Szumski
 */
public interface SvarogTagListenerWithAcitve extends SvarogTagListener {
	
	/**
	 * Invoked when the active tag has changed.
	 * @param e the active tag event
	 */
	void activeTagChanged(SvarogActiveTagEvent e);
}
