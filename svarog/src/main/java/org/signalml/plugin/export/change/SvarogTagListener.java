/**
 * 
 */
package org.signalml.plugin.export.change;

import org.signalml.plugin.export.signal.ExportedSignalDocument;
import org.signalml.plugin.export.signal.ExportedTag;
import org.signalml.plugin.export.signal.ExportedTagDocument;

/**
 * Interface for a listener on {@link ExportedTag tag} changes (addition,
 * removal, change).
 * Can be added only to listen on changes concerning with a selected document
 * ({@link ExportedTagDocument tag} or {@link ExportedSignalDocument signal}).
 * To listen on all changes {@link SvarogTagListenerWithAcitve} is required.
 * 
 * @see SvarogAccessChangeSupport
 * @author Marcin Szumski
 */
public interface SvarogTagListener extends SvarogListener {

	/**
	 * Invoked when the {@link ExportedTag tag} is added to the set.
	 * @param e the tag event
	 */
	void tagAdded(SvarogTagEvent e);

	/**
	 * Invoked when the {@link ExportedTag tag} is removed form the set.
	 * @param e the tag event
	 */
	void tagRemoved(SvarogTagEvent e);

	/**
	 * Invoked when the {@link ExportedTag tag} is changed.
	 * @param e the tag event
	 */
	void tagChanged(SvarogTagEvent e);

}