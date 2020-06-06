package org.signalml.plugin.export.change.listeners;

import java.util.EventListener;
import org.signalml.plugin.export.change.events.PluginSignalChangeEvent;

/**
 * Interface for listeners for signal changes, e.g. when new samples are added.
 *
 * @author Piotr Szachewicz
 */
public interface PluginSignalChangeListener extends EventListener {

	/**
	 * Invoked when new samples were added to the signal.
	 * @param e
	 */
	void newSamplesAdded(PluginSignalChangeEvent e);

}
