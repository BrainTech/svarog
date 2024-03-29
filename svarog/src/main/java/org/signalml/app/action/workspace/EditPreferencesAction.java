/* EditPreferencesAction.java created 2007-09-10
 *
 */
package org.signalml.app.action.workspace;

import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import org.apache.log4j.Logger;
import org.signalml.app.config.ApplicationConfiguration;
import static org.signalml.app.util.i18n.SvarogI18n._;
import org.signalml.app.view.common.dialogs.AbstractDialog;
import org.signalml.plugin.export.view.AbstractSignalMLAction;

/** EditPreferencesAction
 *
 *
 * @author Michal Dobaczewski &copy; 2007-2008 CC Otwarte Systemy Komputerowe Sp. z o.o.
 */
public class EditPreferencesAction extends AbstractSignalMLAction {

	private static final long serialVersionUID = 1L;

	protected static final Logger logger = Logger.getLogger(EditPreferencesAction.class);

	private AbstractDialog preferencesDialog;
	private ApplicationConfiguration config;

	public EditPreferencesAction() {
		super();
		setText(_("Preferences..."));
		setToolTip(_("Edit preferences"));
		setMnemonic(KeyEvent.VK_F);
	}

	@Override
	public void actionPerformed(ActionEvent ev) {

		logger.debug("Edit preferences");

		boolean ok = preferencesDialog.showDialog(config, true);
		if (!ok) {
			return;
		}

	}

	public AbstractDialog getPreferencesDialog() {
		return preferencesDialog;
	}

	public void setPreferencesDialog(AbstractDialog preferencesDialog) {
		this.preferencesDialog = preferencesDialog;
	}

	public ApplicationConfiguration getConfig() {
		return config;
	}

	public void setConfig(ApplicationConfiguration config) {
		this.config = config;
	}

}
