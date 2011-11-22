package org.signalml.plugin.sf;

import java.net.URL;
import java.net.URLClassLoader;

import org.apache.log4j.Logger;

/**
 * @author Stanislaw Findeisen (Eisenbits)
 */
class ClassLoaderTest extends Timer implements java.lang.Runnable {
    protected static final Logger log = Logger.getLogger(ClassLoaderTest.class);

    public ClassLoaderTest(int millis) {
        super(millis);
    }

    @Override
    public void run() {
        super.run();
        
        log.debug("ClassLoaderTest: start");
        URLClassLoader cl1 = new URLClassLoader(new URL[0]);
        log.debug("ClassLoaderTest: cl1=" + cl1);
    }
}
