/**
 * 
 */
package org.signalml.plugin.export;


/**
 * This is the interface every plug-in must implement.
 * 
 * @author Marcin Szumski
 */
public interface Plugin {
	
	/**
	 * Function called when the plug-in is loaded.
	 * It should initialize all necessary structures, register document types,
	 * add buttons etc.
	 * @param access the instance of the implementation of {@link SvarogAccess} interface,
	 * which should be used to communicate with Svarog.
	 */
	public void register(SvarogAccess access) throws SignalMLException;	
	
}
