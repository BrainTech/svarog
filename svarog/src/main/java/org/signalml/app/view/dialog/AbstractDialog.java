/* AbstractDialog.java created 2007-09-11
 *
 */
package org.signalml.app.view.dialog;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dialog;
import java.awt.Dimension;
import java.awt.Point;
import java.awt.Toolkit;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.net.URL;

import javax.swing.AbstractAction;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.KeyStroke;
import javax.swing.border.EmptyBorder;

import org.apache.log4j.Logger;
import org.signalml.app.util.IconUtils;
import org.signalml.plugin.export.SignalMLException;
import org.signalml.plugin.impl.PluginAccessClass;
import org.signalml.util.SvarogConstants;
import static org.signalml.app.SvarogI18n._;

import org.springframework.validation.BindException;
import org.springframework.validation.Errors;

/**
 * The abstract dialog, from which every dialog in Svarog should inherit.
 * Contains the control pane (with OK and CANCEL button).
 * Performs the operations necessary to create and close (either with OK or
 * CANCEL) this dialog, so that the children of this class must only implement:
 * <ul>
 * <li>{@link #fillDialogFromModel(Object)}</li>
 * <li>{@link #fillModelFromDialog(Object)}</li>
 * <li>{@link #validateDialog(Object, Errors)}</li>
 * <li>{@link #supportsModelClass(Class)}</li>
 * <li>{@link #createInterface()}</li>
 * </ul>
 * 
 * 
 * @author Michal Dobaczewski &copy; 2007-2008 CC Otwarte Systemy Komputerowe Sp. z o.o.
 */
public abstract class AbstractDialog extends JDialog {

	static final long serialVersionUID = 1L;

	protected transient final Logger logger = Logger.getLogger(getClass());

	/**
	 * true if the initialization has been performed, false otherwise
	 */
	private boolean initialized = false;
	/**
	 * true if there is a parent window for this dialog, false otherwise
	 */
	private boolean hasParent = false;

	/**
	 * the model with data from which this dialog is filled and in which
	 * the results of this dialog are stored
	 */
	protected Object currentModel;
	
	/**
	 * true if this dialog was closed with the OK button
	 * (or in other way that means the success) and there were no errors
	 * during validation and filling the model,
	 * false otherwise.
	 */
	boolean closedWithOk = false;

	/**
	 * the action that is performed when the dialog is closed with OK button
	 */
	OkAction okAction;
	
	/**
	 * the action that is performed when the dialog is closed with CANCEL button
	 */
	private CancelAction cancelAction;

	/**
	 * the button for the {@link OkAction}
	 */
	private JButton okButton;
	/**
	 * the button for the {@link CancelAction}
	 */
	private JButton cancelButton;

	/**
	 * the container that holds the components parented by the root pane
	 */
	private JPanel contentPane;
	/**
	 * the pane with the {@link #okButton OK} and {@link #cancelButton CANCEL}
	 * button
	 */
	private JPanel controlPane;

	/**
	 * the actual interface of this dialog that is located in the
	 * {@link #contentPane content pane}
	 */
	private JComponent interfaceComponent;

	/**
	 * the dialog that shows errors gathered during the
	 * {@link #validateDialog(Object, Errors) validation} of the dialog
	 */
	private ErrorsDialog errorsDialog;
	/**
	 * the dialog in which the help for this dialog is displayed
	 */
	private HelpDialog helpDialog;
	
	/**
	 * Constructor. Sets the default source of messages (obtained from
	 * {@link PluginAccessClass}).
	 */
	public AbstractDialog() {
		super();
	}

	/**
	 * Constructor. Sets parent window and if this dialog
	 * blocks top-level windows.
	 * @param w the parent window or null if there is no parent
	 * @param isModal true, dialog blocks top-level windows, false otherwise
	 */
	public AbstractDialog(Window w, boolean isModal) {
		super(w, (isModal) ? Dialog.ModalityType.APPLICATION_MODAL : Dialog.ModalityType.MODELESS);
		if (w != null) {
			hasParent = true;
		}
	}

	/**
	 * If dialog is not initialized yet the initialization is called.
	 */
	public final void initializeNow() {
		if (!initialized) {
			initialize();
			initialized = true;
		}
	}

	/**
	 * Initializes this dialog.
	 * Sets the action which is called when window is being closed.
	 * Initializes the {@link #initializeControlPane() control} and
	 * {@link #initializeContentPane() content} pane.
	 * If the dialog should be canceled on escape sets actions necessary to
	 * do it.
	 */
	protected void initialize() {

		setDefaultCloseOperation(JDialog.DO_NOTHING_ON_CLOSE);
		setMaximumSize(SvarogConstants.MIN_ASSUMED_DESKTOP_SIZE);

		addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent arg0) {
				if (isCancellable()) {
					getCancelAction().actionPerformed(new ActionEvent(this,0,"cancel"));
				} else {
					getOkAction().actionPerformed(new ActionEvent(this,0,"cancel"));
				}
			}
		});

		contentPane = new JPanel();
		contentPane.setLayout(new BorderLayout());

		if (isControlPanelEquipped()) {
			initializeControlPane();
		}
		initializeContentPane();

		getRootPane().setContentPane(contentPane);

		if (isCancelOnEscape()) {
			KeyStroke escape = KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0, false);
			getRootPane().getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(escape, "ESCAPE");
			getRootPane().getActionMap().put("ESCAPE", getCancelAction());
		}

		pack();

		initialized = true;

	}

	/**
	 * Adds a {@link #getControlPane() control pane} to the content pane
	 * and adds the {@link #addContextHelp() context help}.
	 */
	protected final void initializeControlPane() {

		JPanel controlPane = getControlPane();
		contentPane.add(controlPane,BorderLayout.SOUTH);

		addContextHelp();

	}
	
	/**
	 * If the {@link #getContextHelpURL() context help URL} is not null the
	 * {@link ContextHelpAction action} that shows help is created, it is added
	 * as a button to the control pane and the key {@code F1} is associated with it.
	 */
	protected void addContextHelp() {

		JPanel controlPane = getControlPane();
		URL contextHelpURL = getContextHelpURL();
		if (contextHelpURL != null) {

			controlPane.add(Box.createHorizontalStrut(5), 0);
			ContextHelpAction helpAction = createContextHelpAction(contextHelpURL);
			KeyStroke f1 = KeyStroke.getKeyStroke(KeyEvent.VK_F1, 0, false);
			getRootPane().getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(f1, "HELP");
			getRootPane().getActionMap().put("HELP", helpAction);
			controlPane.add(new JButton(helpAction), 0);

		}

	}

	/**
	 * Creates the action that shows help with the given URL to help.
	 * @param helpURL the URL to help
	 * @return created action
	 */
	public ContextHelpAction createContextHelpAction(URL helpURL) {
		return new ContextHelpAction(helpURL);
	}

	/**
	 * Creates the control pane.
	 * Adds OK button and, if dialog can be canceled, the CANCEL button.
	 * @return the created pane
	 */
	protected JPanel createControlPane() {

		JPanel controlPane = new JPanel();
		controlPane.setLayout(new BoxLayout(controlPane, BoxLayout.X_AXIS));
		controlPane.setBorder(new EmptyBorder(3,0,0,0));
		controlPane.add(Box.createHorizontalGlue());

		getRootPane().setDefaultButton(getOkButton());
		controlPane.add(getOkButton());

		if (isCancellable()) {
			controlPane.add(Box.createHorizontalStrut(3));
			controlPane.add(getCancelButton());
		}

		return controlPane;

	}

	/**
	 * Returns the control pane.
	 * If it doesn't exist, {@link #createControlPane() creates is}.
	 * @return the control pane
	 */
	protected final JPanel getControlPane() {
		if (controlPane == null) {
			controlPane = createControlPane();
		}
		return controlPane;
	}

	/**
	 * Initializes the content pane.
	 * Adds the {@link #getInterface() interface} to it.
	 */
	protected void initializeContentPane() {

		contentPane.setBorder(new EmptyBorder(3,3,3,3));

		contentPane.add(getInterface(),BorderLayout.CENTER);

	}

	/**
	 * Returns if this dialog is initialized.
	 * @return true if this dialog is initialized, false otherwise
	 */
	public final boolean isInitialized() {
		return initialized;
	}

	/**
	 * Returns if this dialog can be canceled.
	 * @return true if this dialog can be canceled, false otherwise
	 */
	public boolean isCancellable() {
		return true;
	}

	/**
	 * Returns if this dialog can be canceled with escape button.
	 * @return true if this dialog can be canceled with escape button,
	 * false otherwise
	 */
	public boolean isCancelOnEscape() {
		return true;
	}

	/**
	 * Returns if the control pane should be used.
	 * @return true if the control pane should be used,
	 * false otherwise
	 */
	public boolean isControlPanelEquipped() {
		return true;
	}

	/**
	 * Creates the interface of this dialog.
	 * Contents of this interface depends on the implementation.
	 * @return the interface of this dialog
	 */
	protected abstract JComponent createInterface();

	/**
	 * Returns the interface of this dialog.
	 * If it doesn't exist it is created
	 * @return the interface of this dialog
	 */
	public JComponent getInterface() {
		if (interfaceComponent == null) {
			interfaceComponent = createInterface();
		}
		return interfaceComponent;
	}

	/**
	 * Returns if the model can be of the given type.
	 * @param clazz the type of the model
	 * @return true the model can be of the given type, false otherwise
	 */
	public abstract boolean supportsModelClass(Class<?> clazz);

	/**
	 * Fills the fields of this dialog from the given model.
	 * @param model the model from which this dialog will be filled.
	 * @throws SignalMLException TODO when it is thrown
	 */
	protected abstract void fillDialogFromModel(Object model) throws SignalMLException;

	/**
	 * Fills the model with the data from this dialog (user input).
	 * @param model the model to be filled
	 * @throws SignalMLException TODO when it is thrown
	 */
	public abstract void fillModelFromDialog(Object model) throws SignalMLException;

	/**
	 * Shows this dialog.
	 * If it is not initialized, the initialization is done.
	 * Calls {@link #showDialog(Object)}.
	 * @param model the model from which this dialog will be filled and
	 * to which the results will be written
	 * @param centered boolean the location of this dialog should be set
	 * relative to the parent component
	 * @return if the dialog was closed with OK
	 */
	public final boolean showDialog(Object model, boolean centered) {
		if (!initialized) {
			initialize();
			initialized = true;
		}
		if (centered) {
			setLocationRelativeTo(getParent());
		}
		return showDialog(model);
	}

	/**
	 * Shows this dialog.
	 * If it is not initialized, the initialization is done.
	 * Calls {@link #showDialog(Object)}.
	 * The location of this dialog is set using
	 * {@link #centerInParent(double, double)}.
	 * @param model the model from which this dialog will be filled and
	 * to which the results will be written
	 * @param xpos the x proportion of the parent window at which this dialog
	 * should be located
	 * @param ypos the y proportion of the parent window at which this dialog
	 * should be located
	 * @return true if the dialog was closed with OK, false otherwise
	 */
	public final boolean showDialog(Object model, double xpos, double ypos) {
		if (!initialized) {
			initialize();
			initialized = true;
		}
		centerInParent(xpos, ypos);
		return showDialog(model);
	}

	/**
	 * Initializes this dialog, fills it from model and makes it visible.
	 * @param model the model from which this dialog will be filled and
	 * to which the results will be written
	 * @return true if the dialog was closed with OK, false otherwise
	 * @throws ClassCastException if the model is not of the type supported
	 * by this dialog
	 */
	public boolean showDialog(Object model) {

		if (!initialized) {
			initialize();
			initialized = true;
		}

		resetDialog();

		if (model != null) {
			if (!supportsModelClass(model.getClass())) {
				throw new ClassCastException("Model class [" + model.getClass().toString() + "] not supported by dialog [" + this.getClass().toString() + "]");
			}
		} else {
			if (!supportsModelClass(null)) {
				throw new ClassCastException("Model required for dialog [" + this.getClass().toString() + "]");
			}
		}

		try {
			fillDialogFromModel(model);
		} catch (SignalMLException ex) {
			logger.error("Exception when filling the dialog from the model", ex);
			ErrorsDialog.showImmediateExceptionDialog(this, ex);
			return false;
		}
		currentModel = model;
		closedWithOk = false;

		setVisible(true);

		// if the dialog was modal then closedWithOk may have been changed
		// otherwise, the method always returns false
		return closedWithOk;
	}

	/**
	 * Resets the fields of the dialog.
	 */
	protected void resetDialog() {
		// do nothing
	}

	/**
	 * Called when the dialog was canceled.
	 * @return true if the operation was successful, false otherwise
	 */
	protected boolean onCancel() {
		// do nothing
		return true;
	}

	/**
	 * Called when the dialog is closing.
	 */
	protected void onDialogClose() {
		// do nothing
	}

	/**
	 * Called when the dialog is closing with OK.
	 */
	protected void onDialogCloseWithOK() {
		// do nothing
	}

	public boolean validateDialog() {

		if (currentModel != null) {
			Errors errors = new BindException(currentModel, "data");
			try {
				validateDialog(currentModel, errors);
			} catch (SignalMLException ex) {
				logger.error("Dialog validation threw an exception", ex);
				ErrorsDialog.showImmediateExceptionDialog(AbstractDialog.this, ex);
				currentModel = null;
				closedWithOk = false;
				setVisible(false);
				return false;
			}

			if (errors.hasErrors()) {
				showValidationErrors(errors);
				return false;
			}
		}
		return true;

	}

	/**
	 * Checks if this dialog is properly filled.
	 * @param model the model for this dialog
	 * @param errors the object in which errors are stored
	 * @throws SignalMLException TODO when it is thrown
	 */
	public void validateDialog(Object model, Errors errors) throws SignalMLException {
		/* do nothing */
	}

	/**
	 * Returns the URL to context help.
	 * @return the URL to context help
	 */
	protected URL getContextHelpURL() {
		return null;
	}

	/**
	 * Shows the {@link ErrorsDialog dialog} with given validation errors.
	 * @param errors the errors to be displayed
	 */
	protected void showValidationErrors(Errors errors) {
		getErrorsDialog().showDialog(errors, true);
	}

	/**
	 * Sets the location of this dialog to be in the centre of parent window.
	 */
	public void centerInParent() {
		centerInParent(0.5, 0.5);
	}

	/**
	 * Sets the location of this dialog so that it is located
	 * {@code xpos*parentWidth} from the left border of the parent window and
	 * {@code ypos*parentHeight} from the top border of the parent window.
	 * If there is no parent window, the whole screen is considered.
	 * @param xpos the x proportion of the parent window at which this dialog
	 * should be located 
	 * @param ypos the y proportion of the parent window at which this dialog
	 * should be located 
	 */
	public void centerInParent(double xpos, double ypos) {

		final double safeXpos = xpos > 1.0 ? 1.0 : (xpos < 0.0 ? 0.0 : xpos);
		final double safeYpos = ypos > 1.0 ? 1.0 : (ypos < 0.0 ? 0.0 : ypos);

		Dimension d = null;
		if (hasParent) {
			d = getParent().getSize();
		} else {
			// this dialog is alone on the screen
			Toolkit tk = Toolkit.getDefaultToolkit();
			d = tk.getScreenSize();
		}

		int x = (int)((d.width - getWidth()) * safeXpos);
		int y = (int)((d.height - getHeight()) * safeYpos);

		if (isUndecorated() && hasParent) {
			Point parentLoc = getParent().getLocationOnScreen();
			x += parentLoc.x;
			y += parentLoc.y;
		}
		setLocation(x, y);
	}

	/**
	 * Sets the location of this dialog so that it is located
	 * {@code xpos*parentWidth} from the left border of the given component and
	 * {@code ypos*parentHeight} from the top border of the given component.
	 * If there is no parent window, the whole screen is considered.
	 * @param top the component in which this dialog is to centered
	 * @param xpos the x proportion of the given component at which this dialog
	 * should be located 
	 * @param ypos the y proportion of the given component at which this dialog
	 * should be located 
	 */
	public void centerInComponent(Component top, double xpos, double ypos) {

		final double safeXpos = xpos > 1.0 ? 1.0 : (xpos < 0.0 ? 0.0 : xpos);
		final double safeYpos = ypos > 1.0 ? 1.0 : (ypos < 0.0 ? 0.0 : ypos);

		Dimension d;
		if (top != null) {
			d = top.getSize();
		} else {
			Toolkit tk = Toolkit.getDefaultToolkit();
			d = tk.getScreenSize();
		}

		int x = (int)((d.width - getWidth()) * safeXpos);
		int y = (int)((d.height - getHeight()) * safeYpos);

		Point parentLoc;
		if (top != null) {
			parentLoc = top.getLocationOnScreen();
		} else {
			parentLoc = new Point(0,0);
		}
		x += parentLoc.x;
		y += parentLoc.y;

		setLocation(x, y);

	}

	/**
	 * Returns the {@link ErrorsDialog errors dialog}.
	 * If it doesn't exist it is created.
	 * @return the errors dialog
	 */
	protected synchronized ErrorsDialog getErrorsDialog() {
		if (errorsDialog == null) {
			errorsDialog = new ErrorsDialog(this, true);
		}
		return errorsDialog;
	}

	/**
	 * Returns the {@link HelpDialog help dialog}.
	 * If it doesn't exist it is created.
	 * @return the help dialog
	 */
	protected synchronized HelpDialog getHelpDialog() {
		if (helpDialog == null) {
			helpDialog = new HelpDialog(this, true);
		}
		return helpDialog;
	}

	/**
	 * Returns the {@link OkAction}.
	 * If it doesn't exist it is created.
	 * @return the OK action
	 */
	protected synchronized OkAction getOkAction() {
		if (okAction == null) {
			okAction = new OkAction();
		}
		return okAction;
	}

	/**
	 * Returns the {@link CancelAction}.
	 * If it doesn't exist it is created.
	 * @return the CANCEL action
	 */
	protected synchronized CancelAction getCancelAction() {
		if (cancelAction == null) {
			cancelAction = new CancelAction();
		}
		return cancelAction;
	}

	/**
	 * Returns the button for the {@link OkAction}.
	 * If it doesn't exist it is created.
	 * @return the button for OK action
	 */
	protected synchronized JButton getOkButton() {
		if (okButton == null) {
			okButton = new JButton(getOkAction());
		}
		return okButton;
	}

	/**
	 * Returns the button for the {@link CancelAction}.
	 * If it doesn't exist it is created.
	 * @return the button for CANCEL action
	 */
	protected synchronized JButton getCancelButton() {
		if (cancelButton == null) {
			cancelButton = new JButton(getCancelAction());
		}
		return cancelButton;
	}

	/**
	 * Returns the model for this dialog.
	 * @return the model for this dialog
	 */
	protected Object getCurrentModel() {
		return currentModel;
	}

	/**
	 * Sets the model for this dialog.
	 * @param currentModel the model for this dialog
	 */
	protected void setCurrentModel(Object currentModel) {
		this.currentModel = currentModel;
	}

	/**
	 * Returns true if this dialog was closed with the OK button
	 * (or in other way that means the success) and there were no errors
	 * during validation and filling the model,
	 * false otherwise.
	 * @return true if this dialog was closed with the OK button
	 * (or in other way that means the success) and there were no errors
	 * during validation and filling the model,
	 * false otherwise.
	 */
	public boolean isClosedWithOk() {
		return closedWithOk;
	}

	/**
	 * Sets if this dialog was closed with the OK button
	 * (or in other way that means the success) and there were no errors
	 * during validation and filling the model.
	 * @param closedWithOk the value to set
	 */
	protected void setClosedWithOk(boolean closedWithOk) {
		this.closedWithOk = closedWithOk;
	}

	/**
	 * The OK action.
	 * Contains the icon which is used to create the button.
	 * When this action is performed the dialog is
	 * {@link AbstractDialog#validateDialog(Object, Errors) validated},
	 * the model is {@link AbstractDialog#fillModelFromDialog(Object) filled}
	 * from the dialog and the dialog is set to be invisible.
	 */
	protected class OkAction extends AbstractAction {

		private static final long serialVersionUID = 1L;

		/**
		 * Constructor. Sets the description and the icon.
		 */
		public OkAction() {
			super(_("Ok"));
			putValue(AbstractAction.SMALL_ICON, IconUtils.loadClassPathIcon("org/signalml/app/icon/ok.png"));
		}

		/**
		 * Called when this action is performed.
		 * {@link AbstractDialog#validateDialog(Object, Errors) Validates}
		 * the dialog, {@link AbstractDialog#fillModelFromDialog(Object) fills}
		 * the model from it and sets the dialog to be invisible.
		 */
		public void actionPerformed(ActionEvent ev) {

			if (validateDialog() == false)
				return;

			try {
				fillModelFromDialog(currentModel);
			} catch (SignalMLException ex) {
				logger.error("Exception when filling the model from the dialog", ex);
				ErrorsDialog.showImmediateExceptionDialog(AbstractDialog.this, ex);
				currentModel = null;
				closedWithOk = false;
				setVisible(false);
				return;
			}

			currentModel = null;
			closedWithOk = true;
			setVisible(false);

			onDialogCloseWithOK();
			onDialogClose();

		}

	}

	/**
	 * The CANCEL action.
	 * Contains the icon which is used to create the button.
	 * When this action is performed {@link AbstractDialog#closedWithOk}
	 * is set to be false and the dialog is set to be invisible.
	 */
	protected class CancelAction extends AbstractAction {

		private static final long serialVersionUID = 1L;

		/**
		 * Constructor. Sets the description and the icon.
		 */
		public CancelAction() {
			super(_("Cancel"));
			putValue(AbstractAction.SMALL_ICON, IconUtils.loadClassPathIcon("org/signalml/app/icon/cancel.png"));
		}

		/**
		 * Called when this action is performed.
		 * Sets {@link AbstractDialog#closedWithOk} to false,
		 * sets the dialog to be invisible and performs
		 * {@link AbstractDialog#onDialogClose() closing operations}.
		 */
		public void actionPerformed(ActionEvent ev) {

			boolean ok = onCancel();
			if (!ok) {
				return;
			}

			currentModel = null;
			closedWithOk = false;
			setVisible(false);

			onDialogClose();

		}

	}

	/**
	 * The action that displays the context help.
	 * When this action is performed {@link HelpDialog help dialog}
	 * is created.
	 */
	protected class ContextHelpAction extends AbstractAction {

		private static final long serialVersionUID = 1L;

		/**
		 * the URL to help
		 */
		private URL contextHelpURL;

		/**
		 * Constructor. Sets the description, icon, tooltip and the URL to help.
		 * @param url the address where the help is stored
		 */
		public ContextHelpAction(URL url) {
			super();
			putValue(AbstractAction.SMALL_ICON, IconUtils.loadClassPathIcon("org/signalml/app/icon/help.png"));
			putValue(AbstractAction.SHORT_DESCRIPTION,_("Display context help for this dialog"));
			contextHelpURL = url;
		}

		/**
		 * Creates the {@link HelpDialog help dialog} with the given URL to
		 * the help.
		 */
		public void actionPerformed(ActionEvent ev) {
			HelpDialog helpDialog = getHelpDialog();
			helpDialog.reset();
			helpDialog.showDialog(contextHelpURL, true);
		}

	}

}
