/* RegisterCodecDialog.java created 2007-09-18
 * 
 */

package org.signalml.app.view.dialog;

import java.awt.Window;
import java.io.File;
import java.io.IOException;

import javax.swing.JComponent;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.BadLocationException;

import org.signalml.app.model.RegisterCodecDescriptor;
import org.signalml.app.util.IconUtils;
import org.signalml.app.view.element.EmbeddedFileChooser;
import org.signalml.app.view.element.RegisterCodecStepOnePanel;
import org.signalml.app.view.element.RegisterCodecStepTwoPanel;
import org.signalml.codec.SignalMLCodecManager;
import org.signalml.codec.XMLSignalMLCodec;
import org.signalml.codec.generator.xml.XMLCodecException;
import org.signalml.exception.SignalMLException;
import org.signalml.util.Util;
import org.springframework.context.support.MessageSourceAccessor;
import org.springframework.validation.Errors;

/** RegisterCodecDialog
 *
 * 
 * @author Michal Dobaczewski &copy; 2007-2008 CC Otwarte Systemy Komputerowe Sp. z o.o.
 */
public class RegisterCodecDialog extends AbstractWizardDialog {

	private static final long serialVersionUID = 1L;
	
	private SignalMLCodecManager codecManager;
	private XMLSignalMLCodec currentCodec;
	
	private File profileDir;
	
	public RegisterCodecDialog(MessageSourceAccessor messageSource) {
		super(messageSource);
	}

	public RegisterCodecDialog(MessageSourceAccessor messageSource, Window f, boolean isModal) {
		super(messageSource, f, isModal);
	}

	@Override
	protected void initialize() {
		
		setTitle(messageSource.getMessage("registerCodec.title"));
		setIconImage( IconUtils.loadClassPathImage("org/signalml/app/icon/fileopen.png") );
		
		super.initialize();
		
		getStepTwoPanel().getNameField().getDocument().addDocumentListener(new DocumentListener() {

			@Override
			public void changedUpdate(DocumentEvent e) {
				react(e);
			}

			@Override
			public void insertUpdate(DocumentEvent e) {
				react(e);
			}

			@Override
			public void removeUpdate(DocumentEvent e) {
				react(e);
			}
			
			private void react(DocumentEvent e) {
				try {
					checkNameExists(e.getDocument().getText(0, e.getDocument().getLength()));
				} catch (BadLocationException ex) {
					logger.error("Sanity exception", ex);
				}
			}
			
		});
		
	}
	
	@Override
	public int getStepCount() {
		return 2;
	}
	
	@Override
	protected boolean onStepChange(int toStep, int fromStep, Object model) {
		if( fromStep == 0 ) {
			String defaultFormatName = currentCodec.getFormatName();
			getStepTwoPanel().getNameField().setText(defaultFormatName);
			checkNameExists(defaultFormatName);
		}
		return true;
	}
	
	@Override
	public void validateDialogStep(int step, Object model, Errors errors) throws SignalMLException {
		if( step == 0 ) {
			
			EmbeddedFileChooser fileChooser = getStepOnePanel().getFileChooser();
			
			fileChooser.forceApproveSelection();
			fileChooser.validateFile(errors, "sourceFile", false, false, false, false, true);
			
			if( !errors.hasErrors() ) {
				File file = fileChooser.getSelectedFile();
				try {
					currentCodec = new XMLSignalMLCodec(file, profileDir);
				} catch(IOException ex) {
					logger.debug("Failed to read codec file", ex);
					errors.rejectValue("sourceFile", "error.failedToReadSignalMLFile");
				} catch(XMLCodecException ex) {
					logger.debug("Failed to compile codec file", ex);
					errors.rejectValue("sourceFile", "error.codecCompilationFailed");					
				}
			}
		} else if( step == 1 ) {
			String name = getStepTwoPanel().getNameField().getText();
			if( name == null || name.length() == 0 ) {
				errors.rejectValue("formatName", "error.formatNameMustBeSet");
			}
			if( !Util.validateString(name) ) {
				errors.rejectValue("formatName", "error.badCharactersInFormatName");
			}
		}
	}
			
	private void checkNameExists(String formatName) {
		if( codecManager.getCodecForFormat(formatName) != null ) {
			getStepTwoPanel().getWarningLabel().setVisible(true);
		} else {
			getStepTwoPanel().getWarningLabel().setVisible(false);
		}		
	}
	
	@Override
	protected void resetDialog() {
		super.resetDialog();
		getStepTwoPanel().getWarningLabel().setVisible(false);
	}

	@Override
	public JComponent createInterfaceForStep(int step) {
		switch( step ) {
			case 0 :
				return new RegisterCodecStepOnePanel(messageSource);
			case 1 :
				return new RegisterCodecStepTwoPanel(messageSource);
			default :
				throw new IndexOutOfBoundsException();
		}
	}
	
	@Override
	public boolean isFinishAllowedOnStep(int step) {
		return (step == (getStepCount() - 1));
	}

	public RegisterCodecStepOnePanel getStepOnePanel() {
		return (RegisterCodecStepOnePanel) getInterfaceForStep(0);
	}
	
	public RegisterCodecStepTwoPanel getStepTwoPanel() {
		return (RegisterCodecStepTwoPanel) getInterfaceForStep(1);
	}

	@Override
	public void fillDialogFromModel(Object model) throws SignalMLException {
		RegisterCodecDescriptor rcd = (RegisterCodecDescriptor) model;
		File f = rcd.getSourceFile();
		if( f == null ) {
			f = new File("");
		}
		getStepOnePanel().getFileChooser().setSelectedFile(f);
	}

	@Override
	public void fillModelFromDialog(Object model) throws SignalMLException {
		RegisterCodecDescriptor rcd = (RegisterCodecDescriptor) model;
		rcd.setSourceFile(getStepOnePanel().getFileChooser().getSelectedFile());
		rcd.setCodec(currentCodec);
		rcd.setFormatName(getStepTwoPanel().getNameField().getText());
	}

	@Override
	public boolean supportsModelClass(Class<?> clazz) {
		return RegisterCodecDescriptor.class.isAssignableFrom(clazz);
	}

	public SignalMLCodecManager getCodecManager() {
		return codecManager;
	}

	public void setCodecManager(SignalMLCodecManager codecManager) {
		this.codecManager = codecManager;
	}

	public File getProfileDir() {
		return profileDir;
	}

	public void setProfileDir(File profileDir) {
		this.profileDir = profileDir;
	}	
	
}
