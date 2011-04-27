/**
 * 
 */
package org.signalml.plugin.loader;

import java.util.ArrayList;

/**
 * This class represents the dependency of the plug-in.
 * Contains the name and the minimum version of the dependency
 * (dependency = the plug-in that is needed to start the described
 * plug-in).
 * <p>
 * Allows to:
 * <ul>
 * <li>{@link #satisfied(ArrayList)} check} if this dependency is satisfied
 * (if it is a dependency on Svarog - {@link #svarogVersion predefined version}
 * is used)</li>
 * <li>{@link #versionToString() convert} the minimum version to string</li>
 * </ul>
 * @author Marcin Szumski
 */
public class PluginDependency {
	/**
	 * the name of this dependency
	 */
	private String name;
	
	/**
	 * the minimum version of the needed plug-in.
	 * Version is described as an array of integers.
	 * First elements of this array are more important:
	 * {@code 1.2.1 < 2.1.2}
	 */
	private int[] minimumVersion;
	
	/**
	 * the name of the main application
	 */
	public static String svarogName = "Svarog API";
	
	/**
	 * the version of the main application
	 */
	public static int[] svarogVersion = new int[]{0,5};
	
	/**
	 * Returns if this dependency is satisfied by a given
	 * plug-in.
	 * @param plugin the description of the plug-in
	 * @return true if this dependency is satisfied by a given
	 * plug-in, false otherwise 
	 */
	private boolean satisfiesDependency(PluginDescription plugin){
		if (!plugin.isActive()) return false;
		return satisfiesDependency(plugin.getName(), plugin.getVersion());
	}
	
	/**
	 * Returns if this dependency is satisfied by a plug-in of a given name
	 * and version.
	 * @param pluginName the name of the plug-in
	 * @param pluginVersion the {@link #minimumVersion version} of the plug-in
	 * @return true if this dependency is satisfied by a plug-in of
	 * a given name and version, false otherwise
	 */
	private boolean satisfiesDependency(String pluginName, int[] pluginVersion){
		if (!pluginName.equals(name)) return false;
		for (int i = 0; i < minimumVersion.length; ++i){
			if (i>pluginVersion.length) return false;
			if (pluginVersion[i] < minimumVersion[i])
				return false;
			if (pluginVersion[i] > minimumVersion[i])
				return true;
		}
		return true;
	}
	
	/**
	 * Returns if this dependency is satisfied by any plug-in
	 * from the given list.
	 * @param plugins the list of the plug-ins.
	 * @return true if this dependency is satisfied by any plug-in
	 * from the given list, false otherwise
	 */
	public boolean satisfied(ArrayList<PluginDescription> plugins){
		if (satisfiesDependency(svarogName, svarogVersion)) return true;
		for (PluginDescription plugin : plugins){
			if (satisfiesDependency(plugin)) return true;
		}
		return false;
	}
	
	/**
	 * Constructor. Creates the dependency of the given name
	 * and minimum version.
	 * @param name the name of the dependency
	 * @param minimumVersionString the minimum version of
	 * the dependency
	 */
	PluginDependency(String name, String minimumVersionString){
		this.name = name;
		int i = 0;
		String[] splited = minimumVersionString.split("[.]");
		minimumVersion = new int[splited.length];
		for (String s : splited)
			minimumVersion[i++] = Integer.parseInt(s);
	}
	
	/**
	 * Converts the minimum version to the textual representation.
	 * @return the string with the textual representation
	 * of the minimum version.
	 */
	private String versionToString(){
		String ver = "";
		for (int i = 0; i < minimumVersion.length; ++i){
			ver = ver.concat(Integer.toString(minimumVersion[i]));
			if (i < minimumVersion.length -1) ver = ver.concat(".");
		}
		return ver;
	}
	
	@Override
	public String toString(){
		String result = new String();
		result += name;
		result += " v";
		result += versionToString();
		return result;
	}

	/**
	 * @return the name of the plug-in
	 */
	public String getName() {
		return name;
	}
}
