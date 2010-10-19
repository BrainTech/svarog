/**
 * 
 */
package org.signalml.plugin.export.change;

import org.signalml.plugin.export.signal.Document;
import org.signalml.plugin.export.signal.ExportedTag;
import org.signalml.plugin.export.signal.ExportedTagStyle;

/**
 * Super-interface for all events in the plug-in interface.
 * Implementations of these events are passed to listeners when a change
 * occurs. Contain objects ({@link ExportedTag tags},
 * {@link Document documents}, {@link ExportedTagStyle styles} and so on)
 * associated with that change.
 * @author Marcin Szumski
 */
public interface SvarogEvent {

}
