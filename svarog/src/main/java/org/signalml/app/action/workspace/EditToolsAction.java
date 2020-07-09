package org.signalml.app.action.workspace;

import java.awt.event.ActionEvent;
import org.apache.log4j.Logger;
import org.signalml.app.config.ApplicationConfiguration;
import static org.signalml.app.util.i18n.SvarogI18n._;
import org.signalml.app.view.preferences.ApplicationToolsDialog;
import org.signalml.plugin.export.view.AbstractSignalMLAction;

/**
 * Display the Preferences dialog with "Tools" page active.
 *
 * @author ptr@mimuw.edu.pl
 */
public class EditToolsAction extends AbstractSignalMLAction {

	private static final long serialVersionUID = 1L;

	protected static final Logger logger = Logger.getLogger(EditPreferencesAction.class);

	private final ApplicationToolsDialog toolsDialog;
	private ApplicationConfiguration config;

	public EditToolsAction(ApplicationToolsDialog toolsDialog) {
		super();
		this.toolsDialog = toolsDialog;
		setText(_("Configure MP executable path"));
	}

	@Override
	public void actionPerformed(ActionEvent ev) {

		logger.debug("Edit tools");

		boolean ok = toolsDialog.showDialog(config, true);
		if (!ok) {
			return;
		}

	}

	public ApplicationConfiguration getConfig() {
		return config;
	}

	public void setConfig(ApplicationConfiguration config) {
		this.config = config;
	}
}
