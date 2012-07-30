/* NewStagerToolConfigDialog.java created 2008-02-08
 *
 */

package org.signalml.plugin.newstager.ui;

import static org.signalml.plugin.i18n.PluginI18n._;

import java.awt.Window;
import java.io.File;

import javax.swing.JComponent;

import org.signalml.app.model.components.validation.ValidationErrors;
import org.signalml.app.util.IconUtils;
import org.signalml.plugin.export.SignalMLException;
import org.signalml.plugin.export.view.AbstractPluginDialog;
import org.signalml.plugin.export.view.FileChooser;
import org.signalml.plugin.newstager.NewStagerPlugin;
import org.signalml.plugin.newstager.data.NewStagerConfiguration;

/**
 * NewStagerToolConfigDialog
 *
 *
 * @author Michal Dobaczewski &copy; 2007-2008 CC Otwarte Systemy Komputerowe
 *         Sp. z o.o.
 */
public class NewStagerToolConfigDialog extends AbstractPluginDialog {

	private static final long serialVersionUID = 1L;

	private FileChooser fileChooser;

	private NewStagerToolConfigPanel configPanel;

	public NewStagerToolConfigDialog() {
		super();
	}

	public NewStagerToolConfigDialog(Window w, boolean isModal) {
		super(w, isModal);
	}

	@Override
	protected void initialize() {
		setTitle(_("Stager configuration"));
		setIconImage(IconUtils
					 .loadClassPathImage(NewStagerPlugin.iconPath));
		setResizable(false);
		super.initialize();
	}

	@Override
	public JComponent createInterface() {
		return getConfigPanel();
	}

	public NewStagerToolConfigPanel getConfigPanel() {
		if (configPanel == null) {
			configPanel = new NewStagerToolConfigPanel(fileChooser);
		}
		return configPanel;
	}

	@Override
	public void fillDialogFromModel(Object model) throws SignalMLException {
		getConfigPanel().fillPanelFromModel((NewStagerConfiguration) model);
	}

	@Override
	public void fillModelFromDialog(Object model) throws SignalMLException {
		getConfigPanel().fillModelFromPanel((NewStagerConfiguration) model);
	}

	@Override
	public void validateDialog(Object model, ValidationErrors errors)
	throws SignalMLException {
		super.validateDialog(model, errors);

		getConfigPanel().validatePanel(errors);

		if (!errors.hasErrors()) {
			File file = getConfigPanel().getWorkingDirectoryPanel()
						.getDirectory();
			if (file == null || !file.exists() || !file.canWrite()) {
				errors.addError(_("Working directory not set or unusable"));
			}
		}

	}

	@Override
	public boolean supportsModelClass(Class<?> clazz) {
		return NewStagerConfiguration.class.isAssignableFrom(clazz);
	}

	public FileChooser getFileChooser() {
		return fileChooser;
	}

	public void setFileChooser(FileChooser fileChooser) {
		this.fileChooser = fileChooser;
	}

}
