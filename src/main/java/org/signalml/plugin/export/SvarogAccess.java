/**
 * 
 */
package org.signalml.plugin.export;

import org.signalml.plugin.export.change.SvarogAccessChangeSupport;
import org.signalml.plugin.export.config.SvarogAccessConfig;
import org.signalml.plugin.export.method.SvarogAccessMethod;
import org.signalml.plugin.export.signal.SvarogAccessSignal;
import org.signalml.plugin.export.view.SvarogAccessGUI;


/**
 * This interface is used by plug-ins to communicate with Svarog.
 * Every plug-in during the {@link Plugin#register(SvarogAccess) registration}
 * gets the instance of this interface and from it can obtain the sub-interfaces:
 * <ul>
 * <li>{@link SvarogAccessChangeSupport}</li>
 * <li>{@link SvarogAccessGUI}</li>
 * <li>{@link SvarogAccessSignal}</li>
 * </ul>
 * @author Marcin Szumski
 */
public interface SvarogAccess {
	
	/**
	 * Returns the interface to access GUI features.
	 * @return the interface to access GUI features
	 */
	SvarogAccessGUI getGUIAccess();
	
	/**
	 * Returns Svarog configuration facade.
	 * @return Svarog configuration facade 
	 */
	SvarogAccessConfig getConfigAccess();

    /**
     * Returns the methods and tasks in Svarog core facade.
     * @return the methods and tasks in Svarog core facade
     */
	SvarogAccessMethod getMethodAccess();
	
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
