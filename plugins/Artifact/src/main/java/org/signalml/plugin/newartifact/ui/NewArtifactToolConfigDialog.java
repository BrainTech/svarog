/* ArtifactToolConfigDialog.java created 2008-02-08
 *
 */

package org.signalml.plugin.newartifact.ui;

import java.awt.Window;
import java.io.File;

import javax.swing.JComponent;

import org.signalml.app.model.components.validation.ValidationErrors;
import org.signalml.app.util.IconUtils;
import org.signalml.plugin.data.PluginConfigForMethod;
import org.signalml.plugin.exception.PluginException;
import org.signalml.plugin.export.SignalMLException;
import org.signalml.plugin.export.view.FileChooser;
import org.signalml.plugin.newartifact.data.NewArtifactConfiguration;
import org.signalml.plugin.tool.PluginResourceRepository;
import org.signalml.plugin.newartifact.NewArtifactPlugin;
import static org.signalml.plugin.newartifact.NewArtifactPlugin._;

import org.springframework.validation.Errors;

/**
 * ArtifactToolConfigDialog
 *
 *
 * @author Michal Dobaczewski &copy; 2007-2008 CC Otwarte Systemy Komputerowe
 *         Sp. z o.o.
 */
public class NewArtifactToolConfigDialog extends org.signalml.plugin.export.view.AbstractPluginDialog  {

	private static final long serialVersionUID = 1L;

	private FileChooser fileChooser;

	private NewArtifactToolConfigPanel configPanel;

	public NewArtifactToolConfigDialog() {
		super();
	}

	public NewArtifactToolConfigDialog(Window w, boolean isModal) {
		super(w, isModal);
	}

	@Override
	protected void initialize() {
		setTitle(_("Artifact configuration"));
		setIconImage(IconUtils.loadClassPathImage(NewArtifactPlugin.iconPath));
		setResizable(false);
		super.initialize();
	}

	@Override
	public JComponent createInterface() {
		return getConfigPanel();
	}

	public NewArtifactToolConfigPanel getConfigPanel() {
		if (configPanel == null) {
			configPanel = new NewArtifactToolConfigPanel(
					fileChooser);
		}
		return configPanel;
	}

	@Override
	public void fillDialogFromModel(Object model) throws SignalMLException {

		getConfigPanel().fillPanelFromModel((NewArtifactConfiguration) model);

	}

	@Override
	public void fillModelFromDialog(Object model) throws SignalMLException {

		getConfigPanel().fillModelFromPanel((NewArtifactConfiguration) model);

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
		return NewArtifactConfiguration.class.isAssignableFrom(clazz);
	}

	public FileChooser getFileChooser() {
		return fileChooser;
	}

	public void setFileChooser(FileChooser fileChooser) {
		this.fileChooser = fileChooser;
	}
}
