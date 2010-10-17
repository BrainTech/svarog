/**
 * 
 */
package org.signalml.plugin.export.change;

/**
 * Interface for a listener on close of Svarog.
 * @author Marcin Szumski
 */
public interface SvarogCloseListener extends SvarogListener {
	
	/**
	 * Gives notification that Svarog is closing.
	 */
	void applicationClosing();
}
