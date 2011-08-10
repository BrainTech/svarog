package org.signalml.plugin.sf;

import java.net.URL;
import java.net.URLClassLoader;

import org.signalml.app.logging.SvarogLogger;

/**
 * @author Stanislaw Findeisen (Eisenbits)
 */
class ClassLoaderTest extends Timer implements java.lang.Runnable {
    public ClassLoaderTest(int millis) {
        super(millis);
    }

    @Override
    public void run() {
        super.run();
        
        SvarogLogger sl = SvarogLogger.getSharedInstance();
        sl.debug("ClassLoaderTest: start");
        URLClassLoader cl1 = new URLClassLoader(new URL[0]);
        sl.debug("ClassLoaderTest: cl1=" + cl1);
    }
}
