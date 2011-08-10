package org.signalml.plugin.loader;

import java.net.URL;
import java.util.HashSet;

import org.signalml.app.logging.SvarogLoggerStdErr;

/**
 * URLClassLoader that is able to trace what files it has loaded.
 * 
 * This is somewhat generic, however it is named non-generic out of
 * the frustration that C++ template metaprogramming is unavailable
 * in Java.
 * 
 * @author Stanislaw Findeisen (Eisenbits)
 */
public class PluginLoaderLo extends java.net.URLClassLoader {
    
    private HashSet<String> classNamesCanonical = new HashSet<String>();
    private HashSet<String> classNames          = new HashSet<String>();

    protected PluginLoaderLo(URL[] urls, ClassLoader parent) {
        super(urls, parent);
    }
    
    protected Class<?> findClass(String name) throws ClassNotFoundException {
        Class<?> clazz = super.findClass(name);
        store(clazz);
        return clazz;
    }
    
    protected boolean hasLoaded(String className) {
        synchronized (this) {
            if (classNamesCanonical.contains(className))
                return true;
            if (classNames.contains(className))
                return true;
        }
        return false;
    }
    
    private void store(Class<?> clazz) {
        String nameCan  = clazz.getCanonicalName();
        String name     = clazz.getName();

        SvarogLoggerStdErr.getInstance().debug("PlugIn: " + nameCan);

        synchronized (this) {
            classNamesCanonical.add(nameCan);
            classNames.add(name);
        }
    }
}
