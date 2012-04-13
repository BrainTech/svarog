package org.signalml.plugin.loader;

import java.net.URL;
import java.util.HashMap;
import java.util.HashSet;

import org.apache.log4j.Logger;

/**
 * A class loader that handles a single plugin (JAR file).
 *
 * The delegation model for loading classes is implemented in {@link #findClass(String)}
 * and features plugin dependencies.
 *
 * @author Stanislaw Findeisen (Eisenbits)
 */
public class PluginLoaderLo extends java.net.URLClassLoader {
	protected static final Logger log = Logger.getLogger(PluginLoaderLo.class);

	/***
	 * Plugin this loader is serving.
	 */
	private PluginHead pluginHead;

	/**
	 * List of classes loaded from THIS JAR FILE (no parent plugins!).
	 */
	private HashSet<String> classNamesCanonical = new HashSet<String>();

	/**
	 * List of classes loaded from THIS JAR FILE (no parent plugins!).
	 */
	private HashSet<String> classNames = new HashSet<String>();

	/**
	 * All classes found by {@link #findClass(String)}.
	 */
	private HashMap<String,Class<?>> foundClasses = new HashMap<String,Class<?>>();

	protected PluginLoaderLo(PluginHead head, ClassLoader parent) {
		super(new URL[] {head.getDescription().getJarFileURL()}, parent);
		this.pluginHead = head;
	}

	/**
	 * Finds the class with the specified binary name. The algorithm features
	 * plugin dependencies.
	 *
	 * <ol>
	 * <li>If the requested class is in {@link #foundClasses the cache}, it is returned.
	 * <li>If not, this method is called recursively on plugins this one depends on.
	 * <li>If class is not found, this plugin JAR file is searched.
	 * <ol>
	 *
	 * Each time the class is found, it is stored in {@link #foundClasses the cache}.
	 */
	@Override
	protected Class<?> findClass(String name) throws ClassNotFoundException {
		Class<?> clazz = null;
		synchronized (this) {
			clazz = foundClasses.get(name);
		}
		if (null != clazz)
			return clazz;

		// Class not found, search parent plugins...
		log.debug("searching parent plugins for class " + name);

		for (PluginHead ph : pluginHead.getDependencies()) {
			PluginLoaderLo pl = ph.getLoader();

			try {
				clazz = pl.findClass(name);
				if (null != clazz) {
					storeFC(name, clazz);
					return clazz;
				}
			} catch (ClassNotFoundException e) {
				// no class in this parent, continue to the next
			}
		}

		// Lookup this JAR file URL:
		log.debug("checking in JAR for " + name);
		clazz = super.findClass(name);
		store(clazz, name);
		return clazz;
	}

	/**
	 * Returns true iff this loader has loaded a class of the given name from the underlying JAR file.
	 * Classes retrieved from plugins this one depends on do not count.
	 *
	 * @param className
	 * @return true iff this loader has loaded a class of the name className
	 */
	protected boolean hasLoaded(String className) {
		synchronized (this) {
			if (classNamesCanonical.contains(className))
				return true;
			if (classNames.contains(className))
				return true;
		}
		return false;
	}

	private void storeFC(String name, Class<?> clazz) {
		synchronized (this) {
			foundClasses.put(name, clazz);
		}
	}

	private void store(Class<?> clazz, String fcName) {
		String nameCan  = clazz.getCanonicalName();
		String name     = clazz.getName();

		log.debug("PlugIn.store: " + nameCan + " / " + this);

		synchronized (this) {
			classNamesCanonical.add(nameCan);
			classNames.add(name);
			foundClasses.put(fcName, clazz);
		}
	}

	protected PluginHead getPluginHead() {
		return pluginHead;
	}

	@Override
	public String toString() {
		return (Integer.toHexString(hashCode()) + "/" + (pluginHead.getDescription().getName()));
	}
}
