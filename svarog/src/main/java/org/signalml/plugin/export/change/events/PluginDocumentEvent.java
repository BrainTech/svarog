/**
 *
 */
package org.signalml.plugin.export.change.events;

import org.signalml.plugin.export.signal.Document;

/**
 * Interface for an event associated with a change on a {@link Document}.
 * Allows to return a document associated with this change.
 * @author Marcin Szumski
 */
public interface PluginDocumentEvent extends PluginEvent {

	/**
	 * Returns a {@link Document} associated with this event.
	 * @return a document associated with this event
	 */
	Document getDocument();
}
