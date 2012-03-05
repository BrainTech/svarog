/**
 * 
 */
package org.signalml.plugin.export.change.listeners;

import org.signalml.plugin.export.change.SvarogAccessChangeSupport;
import org.signalml.plugin.export.change.events.PluginTagStyleEvent;
import org.signalml.plugin.export.signal.ExportedTagStyle;

/**
 * Interface for a listener on tag style changes (addition, removal, change).
 * 
 * @see SvarogAccessChangeSupport
 * @author Marcin Szumski
 */
public interface PluginTagStyleListener extends PluginListener {

	/**
	 * Invoked when a {@link ExportedTagStyle tag style} is added to the set.
	 * @param e the tag style event
	 */
	void tagStyleAdded(PluginTagStyleEvent e);
	
	/**
	 * Invoked when a {@link ExportedTagStyle tag style} is removed from the set.
	 * @param e the tag style event
	 */
	void tagStyleRemoved(PluginTagStyleEvent e);
	
	/**
	 * Invoked when a {@link ExportedTagStyle tag style} is changed.
	 * @param e the tag style event
	 */
	void tagStyleChanged(PluginTagStyleEvent e);
}
