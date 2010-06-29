/* MP5ToolConfigDialog.java created 2008-02-15
 *
 */

package org.signalml.app.method.mp5;

import java.awt.Window;

import javax.swing.JComponent;

import org.signalml.app.util.IconUtils;
import org.signalml.app.view.dialog.AbstractDialog;
import org.signalml.exception.SignalMLException;
import org.springframework.context.support.MessageSourceAccessor;
import org.springframework.validation.Errors;

/** MP5ToolConfigDialog
 *
 *
 * @author Michal Dobaczewski &copy; 2007-2008 CC Otwarte Systemy Komputerowe Sp. z o.o.
 */
public class MP5ToolConfigDialog extends AbstractDialog {

	private static final long serialVersionUID = 1L;

	private MP5ExecutorManager executorManager;

	private MP5LocalExecutorDialog localExecutorDialog;
	private MP5RemoteExecutorDialog remoteExecutorDialog;

	private MP5ToolConfigPanel configPanel;

	public MP5ToolConfigDialog(MessageSourceAccessor messageSource) {
		super(messageSource);
	}

	public MP5ToolConfigDialog(MessageSourceAccessor messageSource, Window w, boolean isModal) {
		super(messageSource, w, isModal);
	}

	@Override
	protected void initialize() {
		setTitle(messageSource.getMessage("mp5Method.config.title"));
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
			configPanel = new MP5ToolConfigPanel(messageSource,executorManager);
			configPanel.setLocalExecutorDialog(localExecutorDialog);
			configPanel.setRemoteExecutorDialog(remoteExecutorDialog);
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
	public void validateDialog(Object model, Errors errors) throws SignalMLException {
		super.validateDialog(model, errors);

		if (!errors.hasErrors()) {
			int executorCount = executorManager.getExecutorCount();
			if (executorCount == 0) {
				errors.reject("error.mp5.executorRequired");
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

	public MP5RemoteExecutorDialog getRemoteExecutorDialog() {
		return remoteExecutorDialog;
	}

	public void setRemoteExecutorDialog(MP5RemoteExecutorDialog remoteExecutorDialog) {
		this.remoteExecutorDialog = remoteExecutorDialog;
	}

}
