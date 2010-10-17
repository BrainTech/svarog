/**
 * 
 */
package org.signalml.plugin.export.change;

import org.signalml.plugin.export.signal.Document;
import org.signalml.plugin.export.view.DocumentView;

/**
 * Interface for an event associated with a change of a {@link DocumentView view}
 * for a {@link Document}.
 * Allows to return a document and the old view (the view before the change).
 * @author Marcin Szumski
 */
public interface SvarogDocumentViewEvent extends SvarogDocumentEvent {
	
	/**
	 * Returns a {@link DocumentView view} associated with this event.
	 * It is the old view (the view before the change).
	 * @return a {@link DocumentView view} associated with this event
	 */
	DocumentView getView();
}
