/**
 * 
 */
package org.signalml.plugin.export.change.listeners;

import org.signalml.plugin.export.change.SvarogAccessChangeSupport;

/**
 * Interface for a listener on close of Svarog.
 * 
 * @see SvarogAccessChangeSupport
 * @author Marcin Szumski
 */
public interface PluginCloseListener extends PluginListener {
	
	/**
	 * Gives notification that Svarog is closing.
	 */
	void applicationClosing();
}
