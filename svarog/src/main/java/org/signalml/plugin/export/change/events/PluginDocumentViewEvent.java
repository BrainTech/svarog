/**
 *
 */
package org.signalml.plugin.export.change.events;

import org.signalml.plugin.export.signal.Document;
import org.signalml.plugin.export.view.DocumentView;

/**
 * Interface for an event associated with a change of a {@link DocumentView view}
 * for a {@link Document}.
 * Allows to return a document and the old view (the view before the change).
 * The new value of a view can be {@link Document#getDocumentView() obtained}
 * from document.
 * @author Marcin Szumski
 */
public interface PluginDocumentViewEvent extends PluginDocumentEvent {

	/**
	 * Returns a {@link DocumentView view} associated with this event.
	 * It is the old view (the view before the change).
	 * @return a {@link DocumentView view} associated with this event
	 */
	DocumentView getView();
}
