/* SignalParametersDialog.java created 2007-09-11
 *
 */
package org.signalml.app.view.components.dialogs;

import static org.signalml.app.util.i18n.SvarogI18n._;

import java.awt.Window;
import java.io.IOException;
import java.net.URL;

import javax.swing.JComponent;
import org.omg.CORBA.Request;

import org.signalml.app.model.components.validation.ValidationErrors;
import org.signalml.app.model.document.opensignal.SignalParameters;
import org.signalml.app.model.signal.PagingParameterDescriptor;
import org.signalml.app.util.IconUtils;
import org.signalml.app.view.components.PagingParametersPanel;
import org.signalml.app.view.components.RequiredSignalParametersPanel;
import org.signalml.app.view.components.SignalParametersPanel;
import org.signalml.plugin.export.SignalMLException;

import org.springframework.core.io.ClassPathResource;
import org.springframework.validation.Errors;

/**
 * Dialog which displays the parameters of the signal and, if such option is
 * set to edit them.
 * For details see - {@link SignalParametersPanel}.
 *
 * @author Michal Dobaczewski &copy; 2007-2008 CC Otwarte Systemy Komputerowe Sp. z o.o.
 */
public class SignalParametersDialog extends AbstractDialog  {

	private static final long serialVersionUID = 1L;

	/**
	 * the {@link SignalParametersPanel panel} which displays (and allows to
	 * change) the parameters of the signal
	 */
	private SignalParametersPanel panel;

	/**
	 * the URL to the help for this dialog
	 */
	private URL contextHelpURL = null;

	/**
	 * Constructor. Sets message source, parent window and if this dialog
	 * blocks top-level windows.
	 * @param f the parent window or null if there is no parent
	 * @param isModal true, dialog blocks top-level windows, false otherwise
	 */
	public SignalParametersDialog(Window f, boolean isModal) {
		super(f, isModal);
	}

	/**
	 * Sets the title, the icon and that this panel can not be resized and
	 * calls the {@link AbstractDialog#initialize() initialization} in parent.
	 */
	@Override
	protected void initialize() {
		setTitle(_("Signal parameters"));
		setIconImage(IconUtils.loadClassPathImage("org/signalml/app/icon/signalparameters.png"));
		setResizable(false);
		super.initialize();
	}

	/**
	 * Creates the interface for this dialog with only one panel - {@link
	 * SignalParametersPanel}.
	 */
	@Override
	public JComponent createInterface() {

		panel = new SignalParametersPanel();
		return panel;

	}

	/**
	 * Fills the sub-panels of this dialog using the given {@link SignalParameters model}
	 * ({@link RequiredSignalParametersPanel#fillPanelFromModel(SignalParameters) RequiredSignalParametersPanel}
	 * and {@link PagingParametersPanel#fillPanelFromModel(PagingParameterDescriptor) PagingParametersPanel}).
	 */
	@Override
	public void fillDialogFromModel(Object model) throws SignalMLException {
		SignalParameters spd = (SignalParameters) model;

		panel.getRequiredSignalParamersPanel().fillPanelFromModel(spd);
		panel.getPagingSignalParamersPanel().fillPanelFromModel(spd);

	}

	/**
	 * Fills the the given {@link SignalParameters model} from
	 * sub-panels
	 * ({@link RequiredSignalParametersPanel#fillPanelFromModel(SignalParameters)
	 * RequiredSignalParametersPanel} and
	 * {@link PagingParametersPanel#fillPanelFromModel(PagingParameterDescriptor) PagingParametersPanel}).
	 */
	@Override
	public void fillModelFromDialog(Object model) throws SignalMLException {
		SignalParameters spd = (SignalParameters) model;
		panel.getRequiredSignalParamersPanel().fillModelFromPanel(spd);
		panel.getPagingSignalParamersPanel().fillModelFromPanel(spd);
	}

	/**
	 * Validates this dialog. This dialog is valid if sub-panels are valid
	 * ({@link RequiredSignalParametersPanel#validatePanel(SignalParameters, Errors)
	 * RequiredSignalParametersPanel}
	 * and {@link PagingParametersPanel#validatePanel(Errors)
	 * PagingParametersPanel}).
	 */
	@Override
	public void validateDialog(Object model, ValidationErrors errors) throws SignalMLException {
		SignalParameters spd = (SignalParameters) model;
		panel.getRequiredSignalParamersPanel().validatePanel(spd, errors);
		panel.getPagingSignalParamersPanel().validatePanel(errors);
	}

	/**
	 * The model for this dialog must be of type {@link SignalParameters}.
	 */
	@Override
	public boolean supportsModelClass(Class<?> clazz) {
		return SignalParameters.class.isAssignableFrom(clazz);
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
