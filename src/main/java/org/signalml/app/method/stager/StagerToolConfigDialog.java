/* StagerToolConfigDialog.java created 2008-02-08
 *
 */

package org.signalml.app.method.stager;

import java.awt.Window;
import java.io.File;

import javax.swing.JComponent;

import org.signalml.app.util.IconUtils;
import org.signalml.app.view.ViewerFileChooser;
import org.signalml.plugin.export.SignalMLException;
import org.signalml.plugin.export.view.AbstractDialog;
import org.springframework.context.support.MessageSourceAccessor;
import org.springframework.validation.Errors;

/** StagerToolConfigDialog
 *
 *
 * @author Michal Dobaczewski &copy; 2007-2008 CC Otwarte Systemy Komputerowe Sp. z o.o.
 */
public class StagerToolConfigDialog extends AbstractDialog {

	private static final long serialVersionUID = 1L;

	private ViewerFileChooser fileChooser;

	private StagerToolConfigPanel configPanel;

	public StagerToolConfigDialog(MessageSourceAccessor messageSource) {
		super(messageSource);
	}

	public StagerToolConfigDialog(MessageSourceAccessor messageSource, Window w, boolean isModal) {
		super(messageSource, w, isModal);
	}

	@Override
	protected void initialize() {
		setTitle(messageSource.getMessage("stagerMethod.config.title"));
		setIconImage(IconUtils.loadClassPathImage(StagerMethodDescriptor.ICON_PATH));
		setResizable(false);
		super.initialize();
	}

	@Override
	public JComponent createInterface() {
		return getConfigPanel();
	}

	public StagerToolConfigPanel getConfigPanel() {
		if (configPanel == null) {
			configPanel = new StagerToolConfigPanel(messageSource,fileChooser);
		}
		return configPanel;
	}

	@Override
	public void fillDialogFromModel(Object model) throws SignalMLException {

		getConfigPanel().fillPanelFromModel((StagerConfiguration) model);

	}

	@Override
	public void fillModelFromDialog(Object model) throws SignalMLException {

		getConfigPanel().fillModelFromPanel((StagerConfiguration) model);

	}

	@Override
	public void validateDialog(Object model, Errors errors) throws SignalMLException {
		super.validateDialog(model, errors);

		getConfigPanel().validatePanel(errors);

		if (!errors.hasErrors()) {
			File file = getConfigPanel().getWorkingDirectoryPanel().getDirectory();
			if (file == null || !file.exists() || !file.canWrite()) {
				errors.rejectValue("workingDirectoryPath", "error.stager.noWorkingDirectory");
			}
		}

	}

	@Override
	public boolean supportsModelClass(Class<?> clazz) {
		return StagerConfiguration.class.isAssignableFrom(clazz);
	}

	public ViewerFileChooser getFileChooser() {
		return fileChooser;
	}

	public void setFileChooser(ViewerFileChooser fileChooser) {
		this.fileChooser = fileChooser;
	}

}
