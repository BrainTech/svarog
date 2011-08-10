package org.signalml.app.logging;

import org.signalml.app.SvarogApplication;
import org.signalml.app.view.ViewerConsolePane;
import org.signalml.app.view.ViewerElementManager;

/**
 * Svarog logger aware of multithreading. This logs to STDERR
 * and to the GUI console.
 * 
 * @author Stanislaw Findeisen (Eisenbits)
 */
public class SvarogLogger extends SvarogLoggerStdErr {
    private static SvarogLogger instance = null;

    public static SvarogLogger getSharedInstance() {
        if (null == instance) {
            synchronized (SvarogLogger.class) {
                if (null == instance)
                    instance = new SvarogLogger();
            }
        }

        return instance;
    }

    private SvarogLogger() {
        super();
    }

    private synchronized void appendMsgToConsole(String msg) {
        SvarogApplication sa = SvarogApplication.getSharedInstance();
        if (null != sa) {
            ViewerElementManager mgr = sa.getViewerElementManager();
            if (null != mgr) {
                ViewerConsolePane console = mgr.getConsole();
                if (null != console)
                    console.addTextNL(msg);
            }
        }
    }

    @Override
    protected synchronized String printMsg(String prefix, String msg) {
        String logMsg = super.printMsg(prefix, msg);
        appendMsgToConsole(logMsg);
        return logMsg;
    }
}
