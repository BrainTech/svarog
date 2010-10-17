/**
 * 
 */
package org.signalml.plugin.export.change;

import org.signalml.plugin.export.signal.Document;

/**
 * This is an interface for an event associated with a change of an active
 * {@link Document}.
 * Allows to return old and new active document.
 * @author Marcin Szumski
 */
public interface SvarogActiveDocumentEvent extends SvarogDocumentEvent{
	
	/**
	 * Returns an old active {@link Document} (the document that WAS active). 
	 * @return an old active document (the document that WAS active)
	 */
	Document getOldDocument();
}
