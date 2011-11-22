package org.signalml.plugin.sf;

import java.text.MessageFormat;

import org.apache.log4j.Logger;
import org.signalml.plugin.export.Plugin;
import org.signalml.plugin.export.SignalMLException;
import org.signalml.plugin.export.SvarogAccess;

/**
 * A sample test plugin by and for STF.
 *
 * @author Stanislaw Findeisen (Eisenbits)
 */
public class SFTestPlugin implements Plugin {
    protected static final Logger log = Logger.getLogger(SFTestPlugin.class);

    private SvarogAccess svarogAccess;
    private PluginAuth pluginAuth;

    private void testI18nPlural(int k) {
        String sraw= svarogAccess.getI18nAccess().translateN("Deleted one file", "Deleted {0} files", k);
        log.debug("SFTestPlugin.testI18nPlural(): raw=" + sraw);
        String msg = MessageFormat.format(sraw, k);
        log.debug("SFTestPlugin.testI18nPlural(): msg=" + msg);
    }

    protected String _(String msgKey) {
	return svarogAccess.getI18nAccess().translate(msgKey);
    }

    protected String N_(String msgKey, String plural, long n) {
	return svarogAccess.getI18nAccess().translateN(msgKey, plural, n);
    }

	public void register(SvarogAccess sa) throws SignalMLException {
	    this.svarogAccess = sa;

	    log.debug("SFTestPlugin.register()");
	    sa.getGUIAccess().addButtonToToolsMenu(new BombAction());
	    sa.getGUIAccess().addButtonToToolsMenu(new GrenadeAction());
	    sa.getGUIAccess().addButtonToToolsMenu(new I18NAction(sa));
	    new Thread(new ClassLoaderTest(15000)).start();
        new Thread(new ClockBomb(30000)).start();

        String s1 = sa.getI18nAccess().translate("This is an i18n test. Hello, world!");
        log.debug("SFTestPlugin.register(): " + s1);

        testI18nPlural(0);
        testI18nPlural(1);
        testI18nPlural(4);
        testI18nPlural(5);

        _("yet another sample text");
        _("yet another sample text (2)");
        N_("One little duck", "{0} little ducks", 5);
	}
}
