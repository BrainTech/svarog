/* SignalFFTSettingsPopupDialog.java created 2007-12-17
 * 
 */

package org.signalml.app.view.signal.popup;

import java.awt.Window;

import javax.swing.JComponent;

import org.signalml.app.view.dialog.AbstractPopupDialog;
import org.signalml.app.view.element.SignalFFTSettingsPanel;
import org.signalml.app.view.signal.SignalFFTTool;
import org.signalml.exception.SignalMLException;
import org.springframework.context.support.MessageSourceAccessor;
import org.springframework.validation.Errors;

/** SignalFFTSettingsPopupDialog
 *
 * 
 * @author Michal Dobaczewski &copy; 2007-2008 CC Otwarte Systemy Komputerowe Sp. z o.o.
 */
public class SignalFFTSettingsPopupDialog extends AbstractPopupDialog {

	private static final long serialVersionUID = 1L;
			
	private SignalFFTSettingsPanel signalFFTSettingsPanel;
	
	public SignalFFTSettingsPopupDialog(MessageSourceAccessor messageSource, Window w, boolean isModal) {
		super(messageSource, w, isModal);
	}
	
	@Override
	public JComponent createInterface() {
		
		signalFFTSettingsPanel = new SignalFFTSettingsPanel(messageSource, true);
		
		return signalFFTSettingsPanel;
				
	}
	
	@Override
	public void fillDialogFromModel(Object model) throws SignalMLException {
		SignalFFTTool tool = (SignalFFTTool) model;
		
		signalFFTSettingsPanel.fillPanelFromModel( tool.getSettings() );
		
	}

	@Override
	public void fillModelFromDialog(Object model) throws SignalMLException {
		SignalFFTTool tool = (SignalFFTTool) model;
		
		signalFFTSettingsPanel.fillModelFromPanel( tool.getSettings() );
	}
	
	@Override
	public void validateDialog(Object model, Errors errors) throws SignalMLException {
		super.validateDialog(model, errors);
		
		errors.pushNestedPath("settings");
		signalFFTSettingsPanel.validatePanel(errors);
		errors.popNestedPath();
		
	}
	
	@Override
	public boolean supportsModelClass(Class<?> clazz) {
		return SignalFFTTool.class.isAssignableFrom(clazz);
	}
	
	@Override
	public boolean isControlPanelEquipped() {
		return false;
	}		

	@Override
	public boolean isCancellable() {
		return false;
	}
	
	@Override
	public boolean isFormClickApproving() {
		return true;
	}
	
}
