/**
 *
 */
package org.signalml.plugin.export.change.events;

import org.signalml.plugin.export.signal.ExportedTagStyle;

/**
 * Interface for an event associated with tag style changes.
 * @author Marcin Szumski
 */
public interface PluginTagStyleEvent extends PluginEvent {
	/**
	 * Returns the changed {@link ExportedTagStyle style}.
	 * @return the changed style
	 */
	ExportedTagStyle getTagStyle();
}
