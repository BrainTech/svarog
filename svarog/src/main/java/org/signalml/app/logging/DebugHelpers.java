package org.signalml.app.logging;

import org.apache.log4j.Logger;

public abstract class DebugHelpers {

	/** Prints simple {@link java.lang.ThreadGroup} hierarchy information.
	 */
	public static void debugThreads(Logger log) {
		StringBuilder buf = new StringBuilder();
		ThreadGroup g = Thread.currentThread().getThreadGroup();

		if (g != null) {
			buf.append(g.toString());
			g = g.getParent();
		}

		while (g != null) {
			buf.append("; " + g);
			g = g.getParent();
		}

		log.debug("Thread groups (to root): " + buf);
	}
    
	/** Prints simple {@link java.lang.ClassLoader} hierarchy information.
	 */
	public static void debugCL(Logger log) {
		StringBuilder buf = new StringBuilder();
		ClassLoader cl = Thread.currentThread().getContextClassLoader();

		if (null != cl) {
			buf.append("" + cl);
			cl = cl.getParent();
		}

		while (null != cl) {
			buf.append("; " + cl);
			cl = cl.getParent();
		}

		log.debug("Class loaders (to root): " + buf);
	}
}