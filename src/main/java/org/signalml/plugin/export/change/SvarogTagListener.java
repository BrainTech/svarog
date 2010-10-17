/**
 * 
 */
package org.signalml.plugin.export.change;

import org.signalml.plugin.export.signal.ExportedTag;

/**
 * Interface for a listener on {@link ExportedTag tag} changes (addition,
 * removal, change).
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