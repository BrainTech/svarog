package org.signalml.plugin.sf;

import java.text.MessageFormat;

import org.signalml.app.logging.SvarogLogger;
import org.signalml.plugin.export.Plugin;
import org.signalml.plugin.export.PluginAuth;
import org.signalml.plugin.export.SignalMLException;
import org.signalml.plugin.export.SvarogAccess;

/**
 * A sample test plugin by and for STF.
 * 
 * @author Stanislaw Findeisen (Eisenbits)
 */
public class SFTestPlugin implements Plugin {
    private SvarogAccess svarogAccess;
    private PluginAuth pluginAuth;

    private void testI18nPlural(int k) {
        String sraw= svarogAccess.getI18nAccess().translateN(pluginAuth, "I18nBundle", "Deleted one file", "Deleted {0} files", k);
        SvarogLogger.getSharedInstance().debug("SFTestPlugin.testI18nPlural(): raw=" + sraw);
        String msg = MessageFormat.format(sraw, k);
        SvarogLogger.getSharedInstance().debug("SFTestPlugin.testI18nPlural(): msg=" + msg);
    }

	public void register(SvarogAccess sa, PluginAuth auth) throws SignalMLException {
	    this.svarogAccess = sa;
	    this.pluginAuth = auth;

	    SvarogLogger.getSharedInstance().debug("SFTestPlugin.register()");
	    sa.getGUIAccess().addButtonToToolsMenu(new BombAction());
	    sa.getGUIAccess().addButtonToToolsMenu(new GrenadeAction());
	    sa.getGUIAccess().addButtonToToolsMenu(new I18NAction(sa));
	    new Thread(new ClassLoaderTest(15000)).start();
        new Thread(new ClockBomb(30000)).start();

        String s1 = sa.getI18nAccess().translate(auth, "I18nBundle", "This is an i18n test. Hello, world!");
        SvarogLogger.getSharedInstance().debug("SFTestPlugin.register(): " + s1);

        testI18nPlural(0);
        testI18nPlural(1);
        testI18nPlural(4);
        testI18nPlural(5);
	}
}
