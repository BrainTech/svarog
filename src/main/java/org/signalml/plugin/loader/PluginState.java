/**
 * 
 */
package org.signalml.plugin.loader;

import java.util.ArrayList;

/**
 * This class describes the state of the plug-in.
 * In the state are included:
 * <ul>
 * <li>the name of the plug-in,</li>
 * <li>the version of the plug-in,</li>
 * <li>boolean if the plug-in is active,</li>
 * <li>boolean if loading the plug-in failed,</li>
 * <li>a list of dependencies that can't be loaded
 * (either are missing, inactive or their loading
 * failed).</li> 
 * </ul>
 * 
 * @author Marcin Szumski
 */
public class PluginState {
	/**
	 * the name of the plug-in
	 */
	protected String name;
	/**
	 * boolean if the plug-in is active
	 */
	protected boolean active = true;
	/**
	 * the version of the plug-in
	 */
	protected int[] version;
	/**
	 * a list of dependencies that can't be loaded
	 * (either are missing, inactive or their loading
	 * failed).
	 */
	private ArrayList<PluginDependency> missingDependencies;
	/**
	 * boolean if loading the plug-in failed
	 */
	protected boolean failedToLoad = false;
	
	/**
	 * Empty constructor.
	 */
	public PluginState(){
		
	}
	
	/**
	 * Constructor. Sets the name and if plug-in
	 * is active.
	 * @param name the name of the plug-in
	 * @param active information if plug-in is active
	 */
	public PluginState(String name, boolean active) {
		this.name = name;
		this.active = active;
	}
	
	/**
	 * @return the name of the plug-in
	 */
	public String getName() {
		return name;
	}
	
	/**
	 * @param active the information if the plug-in should be active
	 */
	public void setActive(boolean active) {
		this.active = active;
	}
	
	/**
	 * @return the information if the plug-in should be active
	 */
	public boolean isActive() {
		return active;
	}
	
	/**
	 * @return the version of the plug-in
	 */
	public int[] getVersion() {
		return version;
	}
	
	/**
	 * @param versionString string containing the version of
	 * the plug-in
	 */
	protected void setVersion(String versionString) {
		int i = 0;
		String[] splited = versionString.split("[.]");
		version = new int[splited.length];
		for (String s : splited)
			version[i++] = Integer.parseInt(s);
	}
	
	/**
	 * Converts the version of the plug-in to textual
	 * representation
	 * @return the textual representation of the version
	 * of the plug-in
	 */
	public String versionToString(){
		String ver = "";
		for (int i = 0; i < version.length; ++i){
			ver = ver.concat(Integer.toString(version[i]));
			if (i < version.length -1) ver = ver.concat(".");
		}
		return ver;
	}
	
	/**
	 * @param version the version of the plug-in
	 */
	public void setVersion(int[] version){
		this.version = version;
	}

	/**
	 * @return a list of dependencies that can't be loaded
	 * (either are missing, inactive or their loading
	 * failed).
	 */
	public ArrayList<PluginDependency> getMissingDependencies() {
		return missingDependencies;
	}

	/**
	 * Adds a {@link PluginDependency dependency} to the
	 * list of missing dependencies. 
	 * @param missingDependency the dependency to add
	 */
	public void addMissingDependency(PluginDependency missingDependency){
		missingDependencies.add(missingDependency);
	}
	
	/**
	 * Creates a string enlisting the dependencies that are
	 * missing.
	 * @return a string enlisting the dependencies that are
	 * missing
	 */
	public String missingDependenciesToString(){
		String result = new String();
		int number = missingDependencies.size();
		for (int i = 0; i < number; ++i){
			result+= missingDependencies.get(i).toString();
			if (i < number - 1) result += ", ";
		}
		return result;
	}

	/**
	 * @param missingDependencies a list of dependencies that
	 * can't be loaded (either are missing, inactive or their loading
	 * failed).
	 */
	public void setMissingDependencies(ArrayList<PluginDependency> missingDependencies) {
		this.missingDependencies = missingDependencies;
	}

	/**
	 * @param failedToLoad the information if loading
	 * the plug-in failed
	 */
	public void setFailedToLoad(boolean failedToLoad) {
		this.failedToLoad = failedToLoad;
	}

	/**
	 * @return the information if loading the plug-in failed
	 */
	public boolean isFailedToLoad() {
		return failedToLoad;
	}
	
}
