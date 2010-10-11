/**
 * 
 */
package org.signalml.plugin.export;

import org.signalml.plugin.export.change.SvarogAccessChangeSupport;
import org.signalml.plugin.export.signal.SvarogAccessSignal;
import org.signalml.plugin.export.view.SvarogAccessGUI;


/**
 * This interface is used by plug-ins to communicate with Svarog.
 * Every plug-in during the creation gets the implementation of this
 * interface.
 * Returns sub-interfaces.
 * 
 * @author Marcin Szumski
 */
public interface SvarogAccess {
	
	/**
	 * Returns the interface to access GUI features.
	 * @return the interface to access GUI features
	 */
	SvarogAccessGUI getGUIAccess();
	
	/**
	 * Returns the interface to access logic features.
	 * @return the interface to access logic features
	 */
	SvarogAccessSignal getSignalAccess();
	
	/**
	 * Returns the interface to listen of changes in the program.
	 * @return the interface to listen of changes
	 */
	SvarogAccessChangeSupport getChangeSupport();

}
