/* ArtifactToolConfigDialog.java created 2008-02-08
 *
 */

package org.signalml.plugin.newartifact.ui;

import java.awt.Window;
import java.io.File;

import javax.swing.JComponent;

import org.signalml.app.util.IconUtils;
import org.signalml.plugin.data.PluginConfigForMethod;
import org.signalml.plugin.data.PluginConfigMethodData;
import org.signalml.plugin.export.SignalMLException;
import org.signalml.plugin.export.SvarogAccess;
import org.signalml.plugin.export.view.AbstractDialog;
import org.signalml.plugin.export.view.FileChooser;
import org.signalml.plugin.newartifact.data.NewArtifactConfiguration;
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

	private FileChooser fileChooser;

	private NewArtifactToolConfigPanel configPanel;

    private SvarogAccess svarogAccess;

	public NewArtifactToolConfigDialog(SvarogAccess svarogAccess, MessageSourceAccessor messageSource) {
		super(messageSource);
		this.svarogAccess = svarogAccess;
	}

	public NewArtifactToolConfigDialog(SvarogAccess svarogAccess, MessageSourceAccessor messageSource,
					   Window w, boolean isModal) {
		super(messageSource, w, isModal);
		this.svarogAccess = svarogAccess;
	}

	@Override
	protected void initialize() {
		setTitle(messageSource.getMessage("newArtifactMethod.config.title"));
		PluginConfigMethodData config;
		try {
			config = ((PluginConfigForMethod) getSvarogAccess().getConfigAccess()
				  .getResource("config")).getMethodConfig();
		} catch (SignalMLException e) {
			config = null;
		}
		if (config != null) {
			setIconImage(IconUtils.loadClassPathImage(config.getIconPath()));
		}
		setResizable(false);
		super.initialize();
	}

	private SvarogAccess getSvarogAccess() {
	    return svarogAccess;
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

	public FileChooser getFileChooser() {
		return fileChooser;
	}

	public void setFileChooser(FileChooser fileChooser) {
		this.fileChooser = fileChooser;
	}
}
