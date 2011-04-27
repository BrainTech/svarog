/**
 * 
 */
package org.signalml.plugin.export.change;

import org.signalml.plugin.export.signal.ExportedTagDocument;

/**
 * Interface for a listener on changes of an active {@link ExportedTagDocument}.
 * 
 * @see SvarogAccessChangeSupport
 * @author Marcin Szumski
 */
public interface SvarogTagDocumentListener extends SvarogListener {
	
	/**
	 * Invoked when active {@link ExportedTagDocument} changes.
	 * @param e the tag document event
	 */
	void activeTagDocumentChanged(SvarogTagDocumentEvent e);
}
