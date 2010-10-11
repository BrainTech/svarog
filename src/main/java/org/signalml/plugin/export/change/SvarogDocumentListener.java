/**
 * 
 */
package org.signalml.plugin.export.change;

import org.signalml.plugin.export.signal.Document;

/**
 * Interface for a listener on changes associated with a {@link Document}:
 * <ul>
 * <li>addition</li>
 * <li>removal</li>
 * <li>change of an active document</li>
 * <li>change of a view associated with a document</li>
 * </ul>
 * @author Marcin Szumski
 */
public interface SvarogDocumentListener extends SvarogListner {

	/**
	 * Invoked when a document is added.
	 * @param e the document event
	 */
	void documentAdded(SvarogDocumentEvent e);
	
	/**
	 * Invoked when a document is removed.
	 * @param e the document event
	 */
	void documentRemoved(SvarogDocumentEvent e);
	
	/**
	 * Invoked when an active document changes.
	 * @param e the active document event
	 */
	void activeDocumentChanged(SvarogActiveDocumentEvent e);
	
	/**
	 * Invoked when a view for a document is changed.
	 * @param e the document event
	 */
	void documentViewChanged(SvarogDocumentViewEvent e);

}
