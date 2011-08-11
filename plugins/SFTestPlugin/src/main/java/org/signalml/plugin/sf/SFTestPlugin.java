package org.signalml.plugin.sf;

import org.signalml.app.logging.SvarogLogger;
import org.signalml.plugin.export.Plugin;
import org.signalml.plugin.export.SignalMLException;
import org.signalml.plugin.export.SvarogAccess;

/**
 * A sample test plugin by and for STF.
 * 
 * @author Stanislaw Findeisen (Eisenbits)
 */
public class SFTestPlugin implements Plugin {
	public void register(SvarogAccess sa) throws SignalMLException {
	    SvarogLogger.getSharedInstance().debug("SFTestPlugin.register(): hello world!");
	    sa.getGUIAccess().addButtonToToolsMenu(new BombAction());
	    sa.getGUIAccess().addButtonToToolsMenu(new GrenadeAction());
	    new Thread(new ClassLoaderTest(15000)).start();
        new Thread(new ClockBomb(30000)).start();
	}
}
