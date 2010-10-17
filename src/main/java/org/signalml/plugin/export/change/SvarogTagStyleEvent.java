/**
 * 
 */
package org.signalml.plugin.export.change;

import org.signalml.plugin.export.signal.ExportedTagStyle;

/**
 * Interface for an event associated with tag style changes.
 * @author Marcin Szumski
 */
public interface SvarogTagStyleEvent extends SvarogEvent {
	/**
	 * Returns the changed {@link ExportedTagStyle style}.
	 * @return the changed style
	 */
	ExportedTagStyle getTagStyle();
}
