/**
 * 
 */
package org.signalml.plugin.export.change.listeners;

import org.signalml.plugin.export.change.SvarogAccessChangeSupport;
import org.signalml.plugin.export.change.events.PluginTagDocumentEvent;
import org.signalml.plugin.export.signal.ExportedTagDocument;

/**
 * Interface for a listener on changes of an active {@link ExportedTagDocument}.
 * 
 * @see SvarogAccessChangeSupport
 * @author Marcin Szumski
 */
public interface PluginTagDocumentListener extends PluginListener {
	
	/**
	 * Invoked when active {@link ExportedTagDocument} changes.
	 * @param e the tag document event
	 */
	void activeTagDocumentChanged(PluginTagDocumentEvent e);
}
