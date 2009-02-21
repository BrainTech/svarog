/* ArtifactToolConfigDialog.java created 2008-02-08
 * 
 */

package org.signalml.app.method.artifact;

import java.awt.Window;
import java.io.File;

import javax.swing.JComponent;

import org.signalml.app.util.IconUtils;
import org.signalml.app.view.ViewerFileChooser;
import org.signalml.app.view.dialog.AbstractDialog;
import org.signalml.exception.SignalMLException;
import org.springframework.context.support.MessageSourceAccessor;
import org.springframework.validation.Errors;

/** ArtifactToolConfigDialog
 *
 * 
 * @author Michal Dobaczewski &copy; 2007-2008 CC Otwarte Systemy Komputerowe Sp. z o.o.
 */
public class ArtifactToolConfigDialog extends AbstractDialog {

	private static final long serialVersionUID = 1L;
	
	private ViewerFileChooser fileChooser;
	
	private ArtifactToolConfigPanel configPanel;
	
	public ArtifactToolConfigDialog(MessageSourceAccessor messageSource) {
		super(messageSource);
	}

	public ArtifactToolConfigDialog(MessageSourceAccessor messageSource, Window w, boolean isModal) {
		super(messageSource, w, isModal);
	}

	@Override
	protected void initialize() {
		setTitle( messageSource.getMessage( "artifactMethod.config.title" ) );
		setIconImage( IconUtils.loadClassPathImage( ArtifactMethodDescriptor.ICON_PATH ) );
		setResizable(false);
		super.initialize();
	}
	
	@Override
	public JComponent createInterface() {
		return getConfigPanel();
	}
	
	public ArtifactToolConfigPanel getConfigPanel() {
		if( configPanel == null ) {
			configPanel = new ArtifactToolConfigPanel(messageSource,fileChooser);
		}
		return configPanel;
	}
	
	@Override
	public void fillDialogFromModel(Object model) throws SignalMLException {
		
		getConfigPanel().fillPanelFromModel((ArtifactConfiguration) model);
	
	}

	@Override
	public void fillModelFromDialog(Object model) throws SignalMLException {
		
		getConfigPanel().fillModelFromPanel((ArtifactConfiguration) model);
		
	}
	
	@Override
	public void validateDialog(Object model, Errors errors) throws SignalMLException {
		super.validateDialog(model, errors);
		
		getConfigPanel().validatePanel(errors);
		
		if( !errors.hasErrors() ) {
			File file = getConfigPanel().getWorkingDirectoryPanel().getDirectory();
			if( file == null || !file.exists() || !file.canWrite() ) {
				errors.rejectValue( "workingDirectoryPath", "error.artifact.noWorkingDirectory");
			}
		}
		
	}

	@Override
	public boolean supportsModelClass(Class<?> clazz) {
		return ArtifactConfiguration.class.isAssignableFrom(clazz);
	}

	public ViewerFileChooser getFileChooser() {
		return fileChooser;
	}

	public void setFileChooser(ViewerFileChooser fileChooser) {
		this.fileChooser = fileChooser;
	}
	
}
