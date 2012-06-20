/**
 *
 */
package org.signalml.plugin.export.change.events;

import org.signalml.plugin.export.signal.ExportedTag;

/**
 * This is an interface for an event associated with a change of an active
 * {@link ExportedTag tag}.
 * @author Marcin Szumski
 */
public interface PluginActiveTagEvent extends PluginEvent {

	/**
	 * Returns a currently active {@link ExportedTag tag} (new active tag).
	 * @return a currently active tag (new active tag)
	 */
	ExportedTag getTag();

	/**
	 * Returns a formerly active {@link ExportedTag tag} (new active tag).
	 * @return a formerly active tag (old active tag)
	 */
	ExportedTag getOldTag();
}
