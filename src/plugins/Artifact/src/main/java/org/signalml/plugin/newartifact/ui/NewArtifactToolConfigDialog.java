/* ArtifactToolConfigDialog.java created 2008-02-08
 *
 */

package org.signalml.plugin.newartifact.ui;

import java.awt.Window;
import java.io.File;

import javax.swing.JComponent;

import org.signalml.app.util.IconUtils;
import org.signalml.app.view.ViewerFileChooser;
import org.signalml.plugin.data.PluginConfigForMethod;
import org.signalml.plugin.data.PluginConfigMethodData;
import org.signalml.plugin.exception.PluginException;
import org.signalml.plugin.export.SignalMLException;
import org.signalml.plugin.export.view.AbstractDialog;
import org.signalml.plugin.newartifact.data.NewArtifactConfiguration;
import org.signalml.plugin.tool.PluginResourceRepository;
import org.springframework.context.support.MessageSourceAccessor;
import org.springframework.validation.Errors;

/**
 * ArtifactToolConfigDialog
 *
 *
 * @author Michal Dobaczewski &copy; 2007-2008 CC Otwarte Systemy Komputerowe
 *         Sp. z o.o.
 */
public class NewArtifactToolConfigDialog extends AbstractDialog {

	private static final long serialVersionUID = 1L;

	private ViewerFileChooser fileChooser;

	private NewArtifactToolConfigPanel configPanel;

	public NewArtifactToolConfigDialog(MessageSourceAccessor messageSource) {
		super(messageSource);
	}

	public NewArtifactToolConfigDialog(MessageSourceAccessor messageSource,
					   Window w, boolean isModal) {
		super(messageSource, w, isModal);
	}

	@Override
	protected void initialize() {
		setTitle(messageSource.getMessage("newArtifactMethod.config.title"));
		PluginConfigMethodData config;
		try {
			config = ((PluginConfigForMethod) PluginResourceRepository
				  .GetResource("config")).getMethodConfig();
		} catch (PluginException e) {
			config = null;
		}
		if (config != null) {
			setIconImage(IconUtils.loadClassPathImage(config.getIconPath()));
		}
		setResizable(false);
		super.initialize();
	}

	@Override
	public JComponent createInterface() {
		return getConfigPanel();
	}

	public NewArtifactToolConfigPanel getConfigPanel() {
		if (configPanel == null) {
			configPanel = new NewArtifactToolConfigPanel(messageSource,
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
	public void validateDialog(Object model, Errors errors)
	throws SignalMLException {
		super.validateDialog(model, errors);

		getConfigPanel().validatePanel(errors);

		if (!errors.hasErrors()) {
			File file = getConfigPanel().getWorkingDirectoryPanel()
				    .getDirectory();
			if (file == null || !file.exists() || !file.canWrite()) {
				errors.rejectValue("workingDirectoryPath",
						   "error.artifact.noWorkingDirectory");
			}
		}

	}

	@Override
	public boolean supportsModelClass(Class<?> clazz) {
		return NewArtifactConfiguration.class.isAssignableFrom(clazz);
	}

	public ViewerFileChooser getFileChooser() {
		return fileChooser;
	}

	public void setFileChooser(ViewerFileChooser fileChooser) {
		this.fileChooser = fileChooser;
	}

}