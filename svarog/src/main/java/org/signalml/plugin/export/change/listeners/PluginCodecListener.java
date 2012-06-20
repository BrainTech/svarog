/**
 *
 */
package org.signalml.plugin.export.change.listeners;

import org.signalml.plugin.export.change.SvarogAccessChangeSupport;
import org.signalml.plugin.export.change.events.PluginCodecEvent;

/**
 * Interface for a listener on codec changes (addition and removal).
 *
 * @see SvarogAccessChangeSupport
 * @author Marcin Szumski
 */
public interface PluginCodecListener extends PluginListener {

	/**
	 * Gives notification that codec was added.
	 * @param e the codec event
	 */
	void codecAdded(PluginCodecEvent e);

	/**
	 * Gives notification that codec was removed.
	 * @param e the codec event
	 */
	void codecRemoved(PluginCodecEvent e);
}
