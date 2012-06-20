/**
 *
 */
package org.signalml.plugin.export.change.events;

import org.signalml.plugin.export.signal.ExportedTag;
import org.signalml.plugin.export.signal.ExportedTagDocument;

/**
 * Interface for an event associated with changes of {@link ExportedTag tags}
 * (their addition, removal or change).
 * @author Marcin Szumski
 */
public interface PluginTagEvent extends PluginEvent {

	/**
	 * Returns the {@link ExportedTagDocument document} in which the changed
	 * {@link ExportedTag tag} was located.
	 * @return the document in which the changed tag was located
	 */
	ExportedTagDocument getDocument();

	/**
	 * Returns the {@link ExportedTag tag} that has been changed.
	 * @return the tag that has been changed
	 */
	ExportedTag getTag();

}
