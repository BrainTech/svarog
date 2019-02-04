package org.signalml.plugin.loader;

import java.io.File;
import org.apache.log4j.Logger;
import org.signalml.plugin.export.Plugin;
import org.signalml.plugin.impl.PluginAccessClass;
import org.signalml.plugin.fftsignaltool.FFTSignalPlugin;

/**
 * Class responsible for loading plug-ins (high level). Its main functions are:
 * <ul>
 * <li>to read (and write when the application is closing) the list of
 * directories in which the plug-ins are stored,</li>
 * <li>to read (and write when the application is closing) the
 * {@link PluginState states} (whether they should be active or not) of
 * plug-ins,</li>
 * <li>to read {@link PluginDescription descriptions} of plug-ins and check
 * if their {@link PluginDependency dependencies} are satisfied,</li>
 * <li>to load active plug-ins using {@link PluginLoaderLo class loader},</li>
 * <li>to create the {@link PluginDialog dialog} to manage plug-in options.
 * </li>
 * </ul>
 *
 * @author Marcin Szumski
 * @author Stanislaw Findeisen (Eisenbits)
 */
public class PluginLoader {

	private final Logger logger = Logger.getLogger(PluginLoader.class);

	/**
	 * the shared (only) instance of this loader
	 */
	private static PluginLoader sharedInstance = null;

	/**
	 * Tells if the plugin loading process has already started.
	 */
	private volatile boolean startedLoading = false;

	/**
	 * Creates the shared instance.
	 * @param profileDir the profile directory
	 */
	public static void createInstance(File profileDir) {
		if (sharedInstance == null) {
			synchronized (PluginLoader.class) {
				if (sharedInstance == null)
					sharedInstance = new PluginLoader();
			}
		}
	}

	/**
	 * Returns the shared instance of this loader.
	 * @return the shared instance of this loader or null (if it is not initialized yet).
	 */
	public static PluginLoader getInstance() {
		// This method is called from SvarogSecurityManager in privileged mode!
		// NEVER give control to any plugin or untrusted code from here!
		return sharedInstance;
	}

	/**
	 * Creates a new ClassLoader and loads plug-ins using it.
	 * Invokes the {@link Plugin#register(org.signalml.plugin.export.SvarogAccess)}
	 * function of every plug-in to register the plug-in.
	 * Adds a {@code addPluginOptions()} button to tools menu.
	 */
	public void loadPlugins()
	{
		startedLoading = true;
		loadPlugin(FFTSignalPlugin.class);
	}

	/**
	 * Try to load a plugin.
	 * @param klass plugin class
	 * @returns true iff success
	 */
	protected boolean loadPlugin(Class<? extends Plugin> klass) {
		try {
			logger.debug("Loading plugin " + klass.getName());

			PluginAccessClass access = new PluginAccessClass(klass);
			Plugin plugin = klass.newInstance();
			plugin.register(access);
		} catch (Exception exc) {
			String errorMsg = "Failed to load plugin " + klass.getName();
			logger.error(errorMsg, exc);
			return false;
		}
		return true;
	}

	/**
	 * Performs operations necessary while closing the program.
	 * Writes the desired state of plug-ins to an XML file.
	 */
	public void onClose() {
		PluginAccessClass.onClose();
	}

	/**
	 * Returns true iff the plugin loading process has already started.
	 * @return {@link #startedLoading}
	 */
	public synchronized boolean hasStartedLoading() {
		return startedLoading;
	}
}
