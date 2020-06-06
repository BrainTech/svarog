package org.signalml.app;

import java.io.FilePermission;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.security.BasicPermission;
import java.security.Permission;
import java.util.HashMap;
import java.util.PropertyPermission;
import org.apache.log4j.Logger;
import org.signalml.plugin.loader.PluginLoader;
import org.signalml.util.FastMutableInt;

/**
 * Svarog security manager.
 *
 * The policy permits everything that is permitted by the security policy
 * defined by the system administrator, as well as all operations that are
 * being requested <b>outside</b> of Svarog plugin context.
 *
 * This security manager can be configured to enforcing/permissive/off.
 * Set java property (e.g. mvn exec:java -Dsvarog.security_manager=permissive")
 * to change the mode. The default is off.
 *
 * In enforcing mode, disallowed access causes a RuntimeException.
 * In permissive mode, disallowed access only causes a message to be printed.
 * When mode is off, this security manager is not installed at all.
 *
 * @see java.lang.SecurityManager
 * @author Stanislaw Findeisen (Eisenbits)
 */
public class SvarogSecurityManager extends java.lang.SecurityManager {
	protected static final Logger log = Logger.getLogger(SvarogSecurityManager.class);

	/** Edit this should you ever rename SvarogApplication. */
	public static final String S_SvarogApplication = "org.signalml.app.SvarogApplication";

	private HashMap<Thread,org.signalml.util.FastMutableInt> recLevel =
		new HashMap<Thread,org.signalml.util.FastMutableInt>();
	public final boolean enforcing;

	/**
	 * Installs this security manager as the security manager for the application
	 * (in java.lang.System).
	 *
	 * @see java.lang.System.setSecurityManager
	 */
	protected static void install() {
		final String mode = System.getProperties().getProperty("svarog.security_manager",
							"off");
		if (mode.equals("off")) {
			log.debug("SvarogSecurityManager is off");
			return;
		}

		final boolean enforcing;
		if (mode.equals("permissive")) {
			enforcing = false;
		} else {
			if (!mode.equals("enforcing"))
				log.error("svarog.security_manager has invalid value "
						  + "'" + mode + "', ignoring");
			enforcing = true;
		}

		// Force class initialization
		new FastMutableInt(5);
		log.debug("SvarogSecurityManager init...");
		PluginLoader.getInstance();

		// create and install
		System.setSecurityManager(new SvarogSecurityManager(enforcing));
	}

	private SvarogSecurityManager(boolean enforcing) {
		super();
		this.enforcing = enforcing;
	}

	private synchronized void incRecLevel(Thread t) {
		FastMutableInt k = recLevel.get(t);

		if (k == null) {
			recLevel.put(t, new FastMutableInt(1));
		} else {
			k.inc();
		}
	}

	private synchronized void decRecLevel(Thread t) {
		FastMutableInt k = recLevel.get(t);
		k.dec();
		if (k.isZero())
			recLevel.remove(t);
	}

	private synchronized boolean recursionPresent(Thread t) {
		FastMutableInt k = recLevel.get(t);
		return ((null != k) && (k.isGE2()));
	}

	/**
	 * Checks if there is a plugin code on the stack of the given thread.
	 * If found, returns the first plugin code stack frame.
	 *
	 * @return the first stack frame determined to be plugin code (if found) or null (otherwise)
	 */
	private StackTraceElement findPluginCtx(Thread t) {
		PluginLoader pluginLoaderHi = PluginLoader.getInstance();
		if (pluginLoaderHi == null)
			return null;
		if (!(pluginLoaderHi.hasStartedLoading()))
			return null;

		StackTraceElement[] stack = t.getStackTrace();
		boolean hasRoot = false;

		for (final StackTraceElement frame : stack) {
			final String className = frame.getClassName();

			if (!hasRoot) {
				// TODO This is an excerpt from Thread.getStackTrace() API docs:
				//
				//    Some virtual machines may, under some circumstances,
				//    omit one or more stack frames from the stack trace.
				//    In the extreme case, a virtual machine that has no
				//    stack trace information concerning this thread is
				//    permitted to return a zero-length array from this
				//    method.
				//
				// This is the reason we do this rudimentary check for a root
				// class in the stack.
				if ("java.lang.Thread".equals(className) ||
						S_SvarogApplication.equals(className))
					hasRoot = true;
			}
		}

		if (hasRoot) {
			// Yeah, root has been found, no plugin, so we assume the stack is quite
			// consistent and that this is not a plugin context...
			//
			// TODO This is not 100% correct! (see the comment above).
			return null;
		} else {
			throw new java.lang.SecurityException("Stack root not found!");
		}
	}

	@Override
	/**
	 * For top-level calls (no recursion) the permission (p) is granted iff
	 * at least 1 of the following holds:
	 *
	 * <ul>
	 * <li>p is granted by the super call</li>
	 * <li>p is a java.lang.PropertyPermission with action eq. "read"</li>
	 * <li>p is a java.lang.RuntimePermission with name eq. "accessDeclaredMembers"</li>
	 * <li>p is not a plugin context</li>
	 * </ul>
	 *
	 * @param p requested permission
	 * @throws SecurityException iff access is denied
	 */
	public void checkPermission(Permission p) {
		final String pn = p.getName();
		final String pa = p.getActions();
		final Thread t = Thread.currentThread();
		boolean permit = true;
		StackTraceElement frame = null;

		try {
			incRecLevel(t);
			super.checkPermission(p);
		} catch (SecurityException e) {
			permit = false;

			if (recursionPresent(t)) {
				if (p instanceof BasicPermission) {
					if (p instanceof RuntimePermission) {
						if ("accessDeclaredMembers".equals(pn))
							permit = true;
					}
				} else if (p instanceof FilePermission) {
					if ("read".equals(pa))
						permit = true;
				}
			} else {
				frame = findPluginCtx(t);

				if (frame == null) {
					// Not a plugin context, grant permission!
					permit = true;
				} else {
					// we're in plugin context (untrusted code!)

					if (p instanceof BasicPermission) {
						if (p instanceof PropertyPermission) {
							if ("read".equals(pa))
								permit = true;
						}
					} else if (p instanceof FilePermission) {
						if ("read".equals(pa))
							permit = true;
					}
				}
			}

			if (!permit) {
				String errMsg = "Permission DENIED [" + t.getId() + "/" +
								t.getName() + "]: " + p;
				if (frame != null)
					errMsg += "; plugin ctx: " + toString(frame);
				permissionDenied(t, p, e, frame);
				if (this.enforcing)
					throw new SecurityException(errMsg, e);
			}
		} finally {
			//            if (permit)
			//                sl.permissionGranted(t, p);
			decRecLevel(t);
		}
	}

	//    @Override
	//    public void checkExit(int status) {
	//        try {
	//            super.checkExit(status);
	//            log.debug("checkExit GRANTED: " + status);
	//        } catch (SecurityException e) {
	//            log.debug("checkExit GRANTED (2): " + status);
	//        }
	//    }


	/** Used by {@link SvarogSecurityManager} to log security decisions. */
	protected void permissionDenied(Thread t, Permission p, SecurityException e,
									StackTraceElement frame) {
		String msg = "Permission DENIED [" + (t.getId()) + "/"
					 + (t.getName()) + "]: " + p + "; plugin ctx: " + frame;
		if (e != null)
			msg += "\n" + getExceptionMessage(e);
		log.warn(msg);
	}

	/** Used by {@link SvarogSecurityManager} to log security decisions. */
	protected void permissionGranted(Thread t, Permission p) {
		log.debug("Permission GRANTED [" + (t.getId()) + "/" + (t.getName()) + "]: " + p);
	}

	/** Provides an exception string containing the stack trace etc. */
	protected String getExceptionMessage(Throwable t) {
		// XXX: wtf?
		StringWriter ws = new StringWriter();
		PrintWriter wp = new PrintWriter(ws);
		t.printStackTrace(wp);
		return ws.toString();
	}

	protected String toString(StackTraceElement f) {
		return f.getClassName() + "#" + f.getMethodName() + "(" + f.getFileName() + ":"
			   + f.getLineNumber() + ")";
	}
}
