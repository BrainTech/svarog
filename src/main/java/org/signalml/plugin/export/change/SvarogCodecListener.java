/**
 * 
 */
package org.signalml.plugin.export.change;

/**
 * Interface for a listener on codec changes (addition and removal).
 * @author Marcin Szumski
 */
public interface SvarogCodecListener extends SvarogListner {
	
	/**
	 * Gives notification that codec was added.
	 * @param e the codec event
	 */
	void codecAdded(SvarogCodecEvent e);
	
	/**
	 * Gives notification that codec was removed.
	 * @param e the codec event
	 */
	void codecRemoved(SvarogCodecEvent e);
}
