/**
 * 
 */
package org.signalml.plugin.export;

import org.signalml.plugin.export.change.SvarogAccessChangeSupport;
import org.signalml.plugin.export.view.SvarogAccessGUI;


/**
 * This is the interface every plug-in must implement in its
 * starting class.
 * Contains only one method {@link #register(SvarogAccess)}, which
 * is called right after the plug-in is loaded.
 * 
 * @author Marcin Szumski
 */
public interface Plugin {
	
	/**
	 * Function called when the plug-in is loaded.
	 * It should initialize all necessary structures, add buttons, sub-menus
	 * and signal tools.
	 * Some functions of the {@link SvarogAccessGUI GUI interface} can be
	 * performed only in this function (adding buttons, sub-menus and signal tools).
	 * <p>
	 * In order not to miss any changes in Svarog it is also suggested to
	 * {@link SvarogAccessChangeSupport register} listeners here.
	 * @param access the instance of the implementation of {@link SvarogAccess} interface,
	 * which should be used to communicate with Svarog.
	 * It is advised to store it for later use.
	 * @param auth plugin auth object to be used with certain plugin API methods
	 * (just pass it back as is)
	 * @throws Exception if the registration process fails
	 */
	public void register(SvarogAccess access, PluginAuth auth) throws Exception;
}
