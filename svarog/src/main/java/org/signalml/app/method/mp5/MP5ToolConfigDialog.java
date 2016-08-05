/* MP5ToolConfigDialog.java created 2008-02-15
 *
 */

package org.signalml.app.method.mp5;

import static org.signalml.app.util.i18n.SvarogI18n._;

import java.awt.Window;

import javax.swing.JComponent;

import org.signalml.app.model.components.validation.ValidationErrors;
import org.signalml.app.util.IconUtils;
import org.signalml.app.view.common.dialogs.AbstractDialog;
import org.signalml.plugin.export.SignalMLException;

import org.springframework.validation.Errors;

/** MP5ToolConfigDialog
 *
 *
 * @author Michal Dobaczewski &copy; 2007-2008 CC Otwarte Systemy Komputerowe Sp. z o.o.
 */
public class MP5ToolConfigDialog extends AbstractDialog  {

	private static final long serialVersionUID = 1L;

	private MP5ExecutorManager executorManager;

	private MP5LocalExecutorDialog localExecutorDialog;

	private MP5ToolConfigPanel configPanel;

	public MP5ToolConfigDialog() {
		super();
	}

	public MP5ToolConfigDialog(Window w, boolean isModal) {
		super(w, isModal);
	}

	@Override
	protected void initialize() {
		setTitle(_("MP configuration"));
		setIconImage(IconUtils.loadClassPathImage(MP5MethodDescriptor.ICON_PATH));
		setResizable(false);
		super.initialize();
	}

	@Override
	public JComponent createInterface() {
		return getConfigPanel();
	}

	public MP5ToolConfigPanel getConfigPanel() {
		if (configPanel == null) {
			configPanel = new MP5ToolConfigPanel(executorManager);
			configPanel.setLocalExecutorDialog(localExecutorDialog);
		}
		return configPanel;
	}

	@Override
	public void fillDialogFromModel(Object model) throws SignalMLException {

		// nothing to do

	}

	@Override
	public void fillModelFromDialog(Object model) throws SignalMLException {

		// nothing to do

	}

	@Override
	public void validateDialog(Object model, ValidationErrors errors) throws SignalMLException {
		super.validateDialog(model, errors);

		if (!errors.hasErrors()) {
			int executorCount = executorManager.getExecutorCount();
			if (executorCount == 0) {
				errors.addError(_("You must configure at least one executor"));
			}
		}

	}

	@Override
	public boolean supportsModelClass(Class<?> clazz) {
		return (clazz == null);
	}

	public MP5ExecutorManager getExecutorManager() {
		return executorManager;
	}

	public void setExecutorManager(MP5ExecutorManager executorManager) {
		this.executorManager = executorManager;
	}

	public MP5LocalExecutorDialog getLocalExecutorDialog() {
		return localExecutorDialog;
	}

	public void setLocalExecutorDialog(MP5LocalExecutorDialog localExecutorDialog) {
		this.localExecutorDialog = localExecutorDialog;
	}

}
