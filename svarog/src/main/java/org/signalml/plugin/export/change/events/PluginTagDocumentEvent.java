/**
 * 
 */
package org.signalml.plugin.export.change.events;

import org.signalml.app.document.TagDocument;
import org.signalml.plugin.export.signal.Document;
import org.signalml.plugin.export.signal.ExportedTagDocument;

/**
 * Interface for an event associated with the change of an active
 * {@link TagDocument}.
 * @author Marcin Szumski
 */
public interface PluginTagDocumentEvent extends PluginEvent {
	
	/**
	 * Returns the old active {@link TagDocument} (the tag document that
	 * WAS active).
	 * @return the old active tag document (the tag document that WAS active)
	 */
	ExportedTagDocument getTagDocument();
	/**
	 * Returns the new active {@link Document} (the tag document that
	 * IS active).
	 * @return the new active document (the tag document that IS active)
	 */
	ExportedTagDocument getOldTagDocument();
}
