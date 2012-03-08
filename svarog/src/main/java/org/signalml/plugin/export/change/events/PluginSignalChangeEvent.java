package org.signalml.plugin.export.change.events;

import org.signalml.plugin.export.signal.ExportedSignalDocument;

/**
 * Interface for events related to signal changes, e.g. adding new samples. 
 *
 * @author Piotr Szachewicz
 */
public interface PluginSignalChangeEvent extends PluginEvent {

	/**
	 * Returns the document for which the event had happened.
	 * @return the document
	 */
	public ExportedSignalDocument getDocument();
}
