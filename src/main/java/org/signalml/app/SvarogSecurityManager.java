package org.signalml.app;

import java.io.FilePermission;
import java.security.BasicPermission;
import java.security.Permission;
//import java.util.Arrays;
//import java.util.HashSet;
import java.util.PropertyPermission;

import org.signalml.app.logging.SvarogLogger;

/**
 * Svarog security manager.
 * 
 * The policy permits everything that is permitted by the security policy
 * defined by the system administrator, as well as all operations that are
 * being requested outside of Svarog plugin context.
 * 
 * TODO Current implementation is somewhat rudimentary.
 *
 * @see java.lang.SecurityManager
 * @author Stanislaw Findeisen (Eisenbits)
 */
public class SvarogSecurityManager extends java.lang.SecurityManager {
    public static final String S_SvarogApplication   = "org.signalml.app.SvarogApplication";
    public static final String S_PluginRoot          = "org.signalml.plugin.";
    public static final int    C_PluginRoot_Len      = S_PluginRoot.length();
    public static final String S_export              = "export.";
    public static final String S_impl                = "impl.";
    public static final String S_loader              = "loader.";
    
    /**
     * Installs this security manager in java.lang.System.
     * 
     * @see java.lang.System.setSecurityManager
     */
    protected static void install() {
        java.lang.System.setSecurityManager(new SvarogSecurityManager());
    }
    
    private SvarogSecurityManager() {
        super();
    }

    /**
     * Checks if there is a plugin code on the stack.
     * 
     * TODO NOTE: the current implementation is incorrect: it is possible
     * for a plugin code on the stack to go undetected.
     * 
     * @return true iff there is a stack frame on the current stack which represents
     * code from {@link #S_PluginRoot}; however {@link #S_export}, {@link #S_link} and
     * {@link #S_loader} subpackages do not count.
     */
    private boolean isPluginCtx() {
        StackTraceElement[] stack = Thread.currentThread().getStackTrace();
        boolean hasRoot = false;

        for (StackTraceElement frame : stack) {
            final String className = frame.getClassName();

            if (className.startsWith(S_PluginRoot)) {
                String classNameSfx = className.substring(C_PluginRoot_Len);
                if ((classNameSfx.startsWith(S_impl)) || (classNameSfx.startsWith(S_export)) || (classNameSfx.startsWith(S_loader))) {
                } else {
                    // A plugin code frame!
                    return true;
                }
            }

            if (! hasRoot) {
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
                if (("java.lang.Thread".equals(className)) || (S_SvarogApplication.equals(className)))
                    hasRoot = true;
            }
        }

        if (hasRoot) {
            // Yeah, root has been found, no plugin, so we assume the stack is quite
            // consistent and that this is not a plugin context.
            //
            // TODO This is not 100% correct! (see the comments above).
            return false;
        } else {
            throw new java.lang.SecurityException("Stack root not found!");
        }
    }

    @Override
    public void checkPermission(Permission p) {
        final Thread t  = Thread.currentThread();
//      final String pn = p.getName();
        final String pa = p.getActions();
        SvarogLogger sl = SvarogLogger.getSharedInstance();

        try {
            super.checkPermission(p);
        } catch (SecurityException e) {
            boolean permit = false;

            if (isPluginCtx()) {
                if (p instanceof BasicPermission) {
                    if (p instanceof PropertyPermission) {
                        if ("read".equals(pa))
                            permit = true;
//                    } else if (p instanceof RuntimePermission) {
//                        if ("modifyThreadGroup".equals(pn)) {
//                        } else if ("setDefaultUncaughtExceptionHandler".equals(pn)) {
//                        }
                    }
                } else if (p instanceof FilePermission) {
                    if ("read".equals(pa)) {
                        permit = true;
                    }
                }
            } else {
                // not a plugin context, grant permission
                permit = true;
            }

            if (permit) {
                sl.permissionGranted(t, p);
            } else {
                sl.permissionDenied(t, p, e);
                throw e;
            }
        }
    }

    @Override
    public void checkExit(int status) {
        try {
            super.checkExit(status);
            SvarogLogger.getSharedInstance().debug("checkExit GRANTED: " + status);
        } catch (SecurityException e) {
            // TODO more checks
            SvarogLogger.getSharedInstance().debug("checkExit GRANTED (2): " + status);
        }
    }
}
