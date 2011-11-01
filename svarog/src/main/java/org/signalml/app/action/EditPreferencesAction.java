/* EditPreferencesAction.java created 2007-09-10
 *
 */
package org.signalml.app.action;

import java.awt.event.ActionEvent;

import org.apache.log4j.Logger;
import org.signalml.app.config.ApplicationConfiguration;
import org.signalml.app.view.dialog.AbstractDialog;
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

	public  EditPreferencesAction() {
		super();
		setText("action.editPreferences");
		setToolTip("action.editPreferencesToolTip");
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
