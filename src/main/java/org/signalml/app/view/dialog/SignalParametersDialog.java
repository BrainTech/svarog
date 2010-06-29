/* SignalParametersDialog.java created 2007-09-11
 *
 */
package org.signalml.app.view.dialog;

import java.awt.Window;
import java.io.IOException;
import java.net.URL;

import javax.swing.JComponent;

import org.signalml.app.model.SignalParameterDescriptor;
import org.signalml.app.util.IconUtils;
import org.signalml.app.view.element.SignalParametersPanel;
import org.signalml.exception.SignalMLException;
import org.springframework.context.support.MessageSourceAccessor;
import org.springframework.core.io.ClassPathResource;
import org.springframework.validation.Errors;

/** SignalParametersDialog
 *
 *
 * @author Michal Dobaczewski &copy; 2007-2008 CC Otwarte Systemy Komputerowe Sp. z o.o.
 */
public class SignalParametersDialog extends AbstractDialog {

	private static final long serialVersionUID = 1L;

	private SignalParametersPanel panel;

	private URL contextHelpURL = null;

	public SignalParametersDialog(MessageSourceAccessor messageSource, Window f, boolean isModal) {
		super(messageSource, f, isModal);
	}

	@Override
	protected void initialize() {
		setTitle(messageSource.getMessage("signalParameters.title"));
		setIconImage(IconUtils.loadClassPathImage("org/signalml/app/icon/signalparameters.png"));
		setResizable(false);
		super.initialize();
	}

	@Override
	public JComponent createInterface() {

		panel = new SignalParametersPanel(messageSource);
		return panel;

	}

	@Override
	public void fillDialogFromModel(Object model) throws SignalMLException {
		SignalParameterDescriptor spd = (SignalParameterDescriptor) model;

		panel.getRequiredSignalParamersPanel().fillPanelFromModel(spd);
		panel.getPagingSignalParamersPanel().fillPanelFromModel(spd);

	}

	@Override
	public void fillModelFromDialog(Object model) throws SignalMLException {
		SignalParameterDescriptor spd = (SignalParameterDescriptor) model;
		panel.getRequiredSignalParamersPanel().fillModelFromPanel(spd);
		panel.getPagingSignalParamersPanel().fillModelFromPanel(spd);
	}

	@Override
	public void validateDialog(Object model, Errors errors) throws SignalMLException {
		SignalParameterDescriptor spd = (SignalParameterDescriptor) model;
		panel.getRequiredSignalParamersPanel().validatePanel(spd, errors);
		panel.getPagingSignalParamersPanel().validatePanel(errors);
	}

	@Override
	public boolean supportsModelClass(Class<?> clazz) {
		return SignalParameterDescriptor.class.isAssignableFrom(clazz);
	}

	@Override
	protected URL getContextHelpURL() {
		if (contextHelpURL == null) {
			try {
				contextHelpURL = (new ClassPathResource("org/signalml/help/contents.html")).getURL();
				contextHelpURL = new URL(contextHelpURL.toExternalForm() + "#sigparams");
			} catch (IOException ex) {
				logger.error("Failed to get help URL", ex);
			}
		}
		return contextHelpURL;
	}

}
