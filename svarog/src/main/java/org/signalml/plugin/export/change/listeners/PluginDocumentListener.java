/**
 * 
 */
package org.signalml.plugin.export.change.listeners;

import org.signalml.plugin.export.change.SvarogAccessChangeSupport;
import org.signalml.plugin.export.change.events.PluginActiveDocumentEvent;
import org.signalml.plugin.export.change.events.PluginDocumentEvent;
import org.signalml.plugin.export.change.events.PluginDocumentViewEvent;
import org.signalml.plugin.export.signal.Document;

/**
 * Interface for a listener on changes associated with a {@link Document}:
 * <ul>
 * <li>addition</li>
 * <li>removal</li>
 * <li>change of an active document</li>
 * <li>change of a view associated with a document</li>
 * </ul>
 * 
 * @see SvarogAccessChangeSupport
 * @author Marcin Szumski
 */
public interface PluginDocumentListener extends PluginListener {

	/**
	 * Invoked when a document is added.
	 * @param e the document event
	 */
	void documentAdded(PluginDocumentEvent e);
	
	/**
	 * Invoked when a document is removed.
	 * @param e the document event
	 */
	void documentRemoved(PluginDocumentEvent e);
	
	/**
	 * Invoked when an active document changes.
	 * @param e the active document event
	 */
	void activeDocumentChanged(PluginActiveDocumentEvent e);
	
	/**
	 * Invoked when a view for a document is changed.
	 * @param e the document event
	 */
	void documentViewChanged(PluginDocumentViewEvent e);

}
