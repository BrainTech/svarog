/* ArtifactExpertTagDialog.java created 2008-03-31
 *
 */

package org.signalml.app.method.artifact;

import java.awt.BorderLayout;
import java.awt.Window;

import javax.swing.JComponent;
import javax.swing.JPanel;

import org.signalml.app.util.IconUtils;
import org.signalml.app.view.ViewerFileChooser;
import org.signalml.plugin.export.SignalMLException;
import org.signalml.plugin.export.view.AbstractDialog;
import org.springframework.context.support.MessageSourceAccessor;
import org.springframework.validation.Errors;

/** ArtifactExpertTagDialog
 *
 *
 * @author Michal Dobaczewski &copy; 2007-2008 CC Otwarte Systemy Komputerowe Sp. z o.o.
 */
public class ArtifactExpertTagDialog extends AbstractDialog {

	private static final long serialVersionUID = 1L;

	private ViewerFileChooser fileChooser;

	private ArtifactExpertTagPanel expertTagPanel;

	public ArtifactExpertTagDialog(MessageSourceAccessor messageSource, Window w, boolean isModal) {
		super(messageSource, w, isModal);
	}

	public ArtifactExpertTagDialog(MessageSourceAccessor messageSource) {
		super(messageSource);
	}

	@Override
	protected void initialize() {
		setTitle(messageSource.getMessage("artifactMethod.dialog.iteration.title"));
		setIconImage(IconUtils.loadClassPathImage(ArtifactMethodDescriptor.ITERATION_ICON_PATH));
		setResizable(false);
		super.initialize();
	}

	@Override
	protected JComponent createInterface() {

		JPanel interfacePanel = new JPanel(new BorderLayout());

		interfacePanel.add(getExpertTagPanel(), BorderLayout.CENTER);

		return interfacePanel;

	}

	public ArtifactExpertTagPanel getExpertTagPanel() {
		if (expertTagPanel == null) {
			expertTagPanel = new ArtifactExpertTagPanel(messageSource, fileChooser);
		}
		return expertTagPanel;
	}

	@Override
	public void fillDialogFromModel(Object model) throws SignalMLException {

		ArtifactExpertTagDescriptor descriptor = (ArtifactExpertTagDescriptor) model;

		getExpertTagPanel().setTagFile(descriptor.getExpertTagFile());

	}

	@Override
	public void fillModelFromDialog(Object model) throws SignalMLException {

		ArtifactExpertTagDescriptor descriptor = (ArtifactExpertTagDescriptor) model;

		descriptor.setExpertTagFile(getExpertTagPanel().getTagFile());

	}

	@Override
	public void validateDialog(Object model, Errors errors) throws SignalMLException {
		super.validateDialog(model, errors);

		getExpertTagPanel().validatePanel(errors);

	}

	@Override
	public boolean supportsModelClass(Class<?> clazz) {
		return ArtifactExpertTagDescriptor.class.isAssignableFrom(clazz);
	}

	public ViewerFileChooser getFileChooser() {
		return fileChooser;
	}

	public void setFileChooser(ViewerFileChooser fileChooser) {
		this.fileChooser = fileChooser;
	}

}
