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
import org.signalml.exception.SignalMLException;
import org.signalml.util.SvarogConstants;
import org.springframework.context.support.MessageSourceAccessor;
import org.springframework.validation.BindException;
import org.springframework.validation.Errors;

/** AbstractDialog
 *
 * @author Michal Dobaczewski &copy; 2007-2008 CC Otwarte Systemy Komputerowe Sp. z o.o.
 */
public abstract class AbstractDialog extends JDialog {

	static final long serialVersionUID = 1L;
	
	protected transient final Logger logger = Logger.getLogger(getClass());
	protected final MessageSourceAccessor messageSource;
	
	private boolean initialized = false;
	private boolean hasParent = false;
	
	Object currentModel;
	boolean closedWithOk = false;
		
	OkAction okAction;
	private CancelAction cancelAction;
	
	private JButton okButton;
	private JButton cancelButton;
	
	private JPanel contentPane;
	private JPanel controlPane;
	
	private JComponent interfaceComponent;
	
	private ErrorsDialog errorsDialog;
	private HelpDialog helpDialog;
		
	public AbstractDialog(MessageSourceAccessor messageSource) {
		super();
		this.messageSource = messageSource;
	}
	
	public AbstractDialog(MessageSourceAccessor messageSource,Window w, boolean isModal) {
		super(w, (isModal) ? Dialog.ModalityType.APPLICATION_MODAL : Dialog.ModalityType.MODELESS );
		if( w != null ) {
			hasParent = true;
		}
		this.messageSource = messageSource;
	}

	public final void initializeNow() {
		if( !initialized ) {
			initialize();
			initialized = true;
		}
	}
	
	protected void initialize() {
		
		setDefaultCloseOperation(JDialog.DO_NOTHING_ON_CLOSE);
		setMaximumSize(SvarogConstants.MIN_ASSUMED_DESKTOP_SIZE);
		
		addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent arg0) {
				if( isCancellable() ) {
					getCancelAction().actionPerformed(new ActionEvent(this,0,"cancel"));
				} else {
					okAction.actionPerformed(new ActionEvent(this,0,"cancel"));
				}
			}
		});
		
		contentPane = new JPanel();
		contentPane.setLayout(new BorderLayout());
		
		if( isControlPanelEquipped() ) {
			initializeControlPane();
		}		
		initializeContentPane();
		
		getRootPane().setContentPane(contentPane);

		if( isCancelOnEscape() ) {
			KeyStroke escape = KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0, false);
			getRootPane().getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(escape, "ESCAPE");
			getRootPane().getActionMap().put("ESCAPE", getCancelAction());
		}
		
		pack();
		
		initialized = true;
		
	}
	
	protected final void initializeControlPane() {
		
		JPanel controlPane = getControlPane();
		contentPane.add(controlPane,BorderLayout.SOUTH);
	
		addContextHelp();
		
	}
	
	protected void addContextHelp() {
				
		JPanel controlPane = getControlPane();
		URL contextHelpURL = getContextHelpURL();
		if( contextHelpURL != null ) {
			
			controlPane.add( Box.createHorizontalStrut(5), 0 );
			ContextHelpAction helpAction = createContextHelpAction(contextHelpURL);
			KeyStroke f1 = KeyStroke.getKeyStroke(KeyEvent.VK_F1, 0, false);
			getRootPane().getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(f1, "HELP");
			getRootPane().getActionMap().put("HELP", helpAction);
			controlPane.add( new JButton( helpAction ), 0 );
			
		}		
		
	}
	
	public ContextHelpAction createContextHelpAction( URL helpURL ) {		
		return new ContextHelpAction( helpURL );
	}
	
	protected JPanel createControlPane() {
		
		JPanel controlPane = new JPanel();
		controlPane.setLayout(new BoxLayout(controlPane, BoxLayout.X_AXIS));
		controlPane.setBorder(new EmptyBorder(3,0,0,0));		
		controlPane.add( Box.createHorizontalGlue() );		
		
		getRootPane().setDefaultButton(getOkButton());
		controlPane.add(getOkButton());
		
		if( isCancellable() ) {
			controlPane.add(Box.createHorizontalStrut(3));
			controlPane.add(getCancelButton());
		}
		
		return controlPane;
		
	}
	
	protected final JPanel getControlPane() {
		if( controlPane == null ) {
			controlPane = createControlPane();
		}
		return controlPane;		
	}
		
	protected void initializeContentPane() {

		contentPane.setBorder(new EmptyBorder(3,3,3,3));
		
		contentPane.add(getInterface(),BorderLayout.CENTER);		

	}

	public final boolean isInitialized() {
		return initialized;
	}
	
	public boolean isCancellable() {
		return true;
	}
	
	public boolean isCancelOnEscape() {
		return true;
	}
	
	public boolean isControlPanelEquipped() {
		return true;
	}

	protected abstract JComponent createInterface();
	
	public JComponent getInterface() {
		if( interfaceComponent == null ) {
			interfaceComponent = createInterface();			
		}
		return interfaceComponent;
	}
	
	public abstract boolean supportsModelClass( Class<?> clazz );
	
	public abstract void fillDialogFromModel( Object model ) throws SignalMLException;
		
	public abstract void fillModelFromDialog( Object model ) throws SignalMLException;

	public final boolean showDialog( Object model, boolean centered ) {
		if( !initialized ) {
			initialize();
			initialized = true;
		}
		if( centered ) {
			setLocationRelativeTo(getParent());
		}
		return showDialog(model);
	}

	public final boolean showDialog( Object model, double xpos, double ypos ) {
		if( !initialized ) {
			initialize();
			initialized = true;
		}
		centerInParent(xpos, ypos);
		return showDialog(model);
	}
	
	public boolean showDialog( Object model ) {

		if( !initialized ) {
			initialize();
			initialized = true;
		}
		
		resetDialog();
		
		if( model != null ) {
			if( !supportsModelClass(model.getClass())) {
				throw new ClassCastException( "Model class [" + model.getClass().toString() + "] not supported by dialog [" + this.getClass().toString() + "]" );
			}
		} else {
			if( !supportsModelClass(null) ) {
				throw new ClassCastException( "Model required for dialog [" + this.getClass().toString() + "]" );
			}
		}

		try {
			fillDialogFromModel(model);
		} catch( SignalMLException ex ) {
			logger.error("Exception when filling the dialog from the model", ex );
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
			
	protected void resetDialog() {
		// do nothing		
	}
	
	protected boolean onCancel() {
		// do nothing		
		return true;
	}
	
	protected void onDialogClose() {
		// do nothing
	}

	public void validateDialog( Object model, Errors errors ) throws SignalMLException {
		/* do nothing */
	}
	
	protected URL getContextHelpURL() {
		return null;
	}
				
	protected void showValidationErrors( Errors errors ) {
		getErrorsDialog().showDialog(errors, true);			
	}
	
	public void centerInParent() {
		centerInParent(0.5, 0.5);		
	}

	public void centerInParent(double xpos, double ypos) {
		
		final double safeXpos = xpos > 1.0 ? 1.0 : (xpos < 0.0 ? 0.0 : xpos);
		final double safeYpos = ypos > 1.0 ? 1.0 : (ypos < 0.0 ? 0.0 : ypos);
		
		Dimension d = null;
		if( hasParent ) {
			d = getParent().getSize();
		} else {
			// this dialog is alone on the screen
			Toolkit tk = Toolkit.getDefaultToolkit();
			d = tk.getScreenSize();			
		}
		
		int x = (int) ((d.width - getWidth()) * safeXpos);
		int y = (int) ((d.height - getHeight()) * safeYpos);
		
		if( isUndecorated() && hasParent ) {
			Point parentLoc = getParent().getLocationOnScreen();
			x += parentLoc.x;
			y += parentLoc.y;
		}
		setLocation(x, y); 			
	}

	public void centerInComponent(Component top, double xpos, double ypos) {
		
		final double safeXpos = xpos > 1.0 ? 1.0 : (xpos < 0.0 ? 0.0 : xpos);
		final double safeYpos = ypos > 1.0 ? 1.0 : (ypos < 0.0 ? 0.0 : ypos);

		Dimension d;
		if( top != null ) {
			d = top.getSize();
		} else {
			Toolkit tk = Toolkit.getDefaultToolkit();
			d = tk.getScreenSize();						
		}
		
		int x = (int) ((d.width - getWidth()) * safeXpos);
		int y = (int) ((d.height - getHeight()) * safeYpos);
		
		Point parentLoc;
		if( top != null ) {
			parentLoc = top.getLocationOnScreen();
		} else {
			parentLoc = new Point(0,0);
		}
		x += parentLoc.x;
		y += parentLoc.y;
		
		setLocation(x, y);
		
	}
		
	protected ErrorsDialog getErrorsDialog() {
		if( errorsDialog == null ) {
			errorsDialog = new ErrorsDialog(messageSource,this,true);
		}
		return errorsDialog;
	}
	
	protected HelpDialog getHelpDialog() {
		if( helpDialog == null ) {
			helpDialog = new HelpDialog(messageSource,this,true);
		}
		return helpDialog;
	}
	
	protected OkAction getOkAction() {
		if( okAction == null ) {
			okAction = new OkAction();
		}
		return okAction;
	}

	protected CancelAction getCancelAction() {
		if( cancelAction == null ) {
			cancelAction = new CancelAction();
		}
		return cancelAction;
	}
	
	protected JButton getOkButton() {
		if( okButton == null ) {
			okButton = new JButton(getOkAction());			
		}
		return okButton;
	}
	
	protected JButton getCancelButton() {
		if( cancelButton == null ) {
			cancelButton = new JButton(getCancelAction());
		}
		return cancelButton;
	}
	
	protected Object getCurrentModel() {
		return currentModel;
	}

	protected void setCurrentModel(Object currentModel) {
		this.currentModel = currentModel;
	}
	
	public boolean isClosedWithOk() {
		return closedWithOk;
	}
	
	protected void setClosedWithOk(boolean closedWithOk) {
		this.closedWithOk = closedWithOk;
	}	

	protected class OkAction extends AbstractAction {

		private static final long serialVersionUID = 1L;

		public OkAction() {
			super(messageSource.getMessage("ok"));
			putValue(AbstractAction.SMALL_ICON, IconUtils.loadClassPathIcon("org/signalml/app/icon/ok.png") );
		}
		
		public void actionPerformed(ActionEvent ev) {
			
			if( currentModel != null ) {
				Errors errors = new BindException(currentModel, "data");
				try {
					validateDialog(currentModel,errors);
				} catch( SignalMLException ex ) {
					logger.error("Dialog validation threw an exception", ex );
					ErrorsDialog.showImmediateExceptionDialog(AbstractDialog.this, ex);
					currentModel = null;
					closedWithOk = false;
					setVisible(false);
					return;
				}
				
				if( errors.hasErrors() ) {
					showValidationErrors(errors);
					return;
				}
			}
						
			try {
				fillModelFromDialog(currentModel);
			} catch (SignalMLException ex) {
				logger.error("Exception when filling the model from the dialog", ex );
				ErrorsDialog.showImmediateExceptionDialog(AbstractDialog.this, ex);
				currentModel = null;
				closedWithOk = false;
				setVisible(false);
				return;
			}
			
			currentModel = null;
			closedWithOk = true;
			setVisible(false);
			
			onDialogClose();
			
		}
		
	}
	
	protected class CancelAction extends AbstractAction {

		private static final long serialVersionUID = 1L;

		public CancelAction() {
			super(messageSource.getMessage("cancel"));
			putValue(AbstractAction.SMALL_ICON, IconUtils.loadClassPathIcon("org/signalml/app/icon/cancel.png") );
		}
		
		public void actionPerformed(ActionEvent ev) {			
			
			boolean ok = onCancel();
			if( !ok ) {
				return;
			}
			
			currentModel = null;
			closedWithOk = false;
			setVisible(false);
			
			onDialogClose();
			
		}
		
	}
	
	protected class ContextHelpAction extends AbstractAction {

		private static final long serialVersionUID = 1L;

		private URL contextHelpURL;
		
		public ContextHelpAction(URL url) {
			super();
			putValue(AbstractAction.SMALL_ICON, IconUtils.loadClassPathIcon("org/signalml/app/icon/help.png") );
			putValue(AbstractAction.SHORT_DESCRIPTION,messageSource.getMessage("help.contextHelpToolTip"));
			contextHelpURL = url;
		}
		
		public void actionPerformed(ActionEvent ev) {						
			HelpDialog helpDialog = getHelpDialog();
			helpDialog.reset();
			helpDialog.showDialog(contextHelpURL, true);
		}
				
	}
	
}
