/* MP5RemoteExecutorDialog.java created 2008-02-14
 * 
 */

package org.signalml.app.method.mp5;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.concurrent.ExecutionException;

import javax.swing.AbstractAction;
import javax.swing.Box;
import javax.swing.DefaultComboBoxModel;
import javax.swing.GroupLayout;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import javax.swing.GroupLayout.Alignment;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;

import org.signalml.app.config.ConfigurationDefaults;
import org.signalml.app.util.IconUtils;
import org.signalml.app.view.dialog.AbstractDialog;
import org.signalml.app.view.dialog.ErrorsDialog;
import org.signalml.app.view.dialog.OptionPane;
import org.signalml.app.view.dialog.PleaseWaitDialog;
import org.signalml.app.view.element.ExternalLinkLabel;
import org.signalml.app.worker.TestConnectionWorker;
import org.signalml.exception.SanityCheckException;
import org.signalml.exception.SignalMLException;
import org.signalml.method.mp5.MP5RemotePasswordExecutor;
import org.signalml.method.mp5.remote.Credentials;
import org.signalml.method.mp5.remote.PasswordCredentials;
import org.signalml.method.mp5.remote.TestConnectionRequest;
import org.signalml.method.mp5.remote.TestConnectionResponse;
import org.signalml.util.Util;
import org.springframework.context.support.MessageSourceAccessor;
import org.springframework.core.io.ClassPathResource;
import org.springframework.validation.BindException;
import org.springframework.validation.Errors;

/** MP5RemoteExecutorDialog
 *
 * 
 * @author Michal Dobaczewski &copy; 2007-2008 CC Otwarte Systemy Komputerowe Sp. z o.o.
 */
public class MP5RemoteExecutorDialog extends AbstractDialog {

	private static final long serialVersionUID = 1L;
	
	private PleaseWaitDialog pleaseWaitDialog;
	
	private URL contextHelpURL = null;
	
	private JTextField nameTextField;
	
	private JComboBox typeComboBox;
	
	private JTextField urlTextField;
	
	private JTextField userNameTextField;
	private JPasswordField passwordField;
	private JPasswordField passwordAgainField;
	
	private JCheckBox savePasswordCheckBox;

	private ExternalLinkLabel linkLabel;
	
	private boolean passwordOnly;
		
	public MP5RemoteExecutorDialog(MessageSourceAccessor messageSource) {
		super(messageSource);
	}

	public MP5RemoteExecutorDialog(MessageSourceAccessor messageSource, Window w, boolean isModal) {
		super(messageSource, w, isModal);
	}

	@Override
	protected JPanel createControlPane() {
		JPanel controlPane = super.createControlPane();
		controlPane.add( Box.createHorizontalStrut(10), 1 );
		controlPane.add( new JButton( new TestConnectionAction() ), 1 );
		return controlPane;
	}
	
	@Override
	protected void initialize() {
		setTitle( messageSource.getMessage( "mp5Method.config.remote.title" ) );
		setIconImage( IconUtils.loadClassPathImage( "org/signalml/app/icon/configure.png" ) );
		setResizable(false);
		super.initialize();
	}

	@Override
	protected URL getContextHelpURL() {
		if( contextHelpURL == null ) {
			 try {
				 contextHelpURL = (new ClassPathResource("org/signalml/help/mp5Remote.html")).getURL();
			} catch (IOException ex) {
				logger.error("Failed to get help URL", ex);
			}				
		}
		return contextHelpURL;
	}
	
	@Override
	public JComponent createInterface() {

		JPanel interfacePanel = new JPanel( new BorderLayout() );
		
		JPanel namePanel = new JPanel( new BorderLayout() );
		
		CompoundBorder border = new CompoundBorder(
				new TitledBorder( messageSource.getMessage("mp5Method.config.remote.nameTitle") ),
				new EmptyBorder(3,3,3,3)
		);
		namePanel.setBorder(border);
		
		namePanel.add( getNameTextField(), BorderLayout.CENTER );
		
		JPanel settingsPanel = new JPanel();
		
		border = new CompoundBorder(
				new TitledBorder( messageSource.getMessage("mp5Method.config.remote.settingsTitle") ),
				new EmptyBorder(3,3,3,3)
		);
		settingsPanel.setBorder(border);
		
		GroupLayout layout = new GroupLayout(settingsPanel);
		settingsPanel.setLayout(layout);
		layout.setAutoCreateContainerGaps(false);
		layout.setAutoCreateGaps(true);
		layout.setHonorsVisibility(false);
		
		JLabel typeLabel = new JLabel(messageSource.getMessage("mp5Method.config.remote.type"));
		JLabel urlLabel = new JLabel(messageSource.getMessage("mp5Method.config.remote.url"));
		JLabel userNameLabel = new JLabel(messageSource.getMessage("mp5Method.config.remote.userName"));
		JLabel passwordLabel = new JLabel(messageSource.getMessage("mp5Method.config.remote.password"));
		JLabel passwordAgainLabel = new JLabel(messageSource.getMessage("mp5Method.config.remote.passwordAgain"));
		JLabel savePasswordLabel = new JLabel(messageSource.getMessage("mp5Method.config.remote.savePassword"));
		savePasswordLabel.setMinimumSize( new Dimension(35,35) );
		
		Component glue1 = Box.createHorizontalGlue();
		Component glue2 = Box.createHorizontalGlue();
		Component glue3 = Box.createHorizontalGlue();
		Component glue4 = Box.createHorizontalGlue();
		Component glue5 = Box.createHorizontalGlue();
		Component glue6 = Box.createHorizontalGlue();
		Component glue7 = Box.createHorizontalGlue();
				
		Component filler1 = Box.createRigidArea( new Dimension(1,1) );
		
		GroupLayout.SequentialGroup hGroup = layout.createSequentialGroup();
		
		hGroup.addGroup(
				layout.createParallelGroup()
				.addComponent(typeLabel)
				.addComponent(urlLabel)
				.addComponent(userNameLabel)
				.addComponent(passwordLabel)
				.addComponent(passwordAgainLabel)
				.addComponent(savePasswordLabel)
				.addComponent(filler1)
			);

		hGroup.addGroup(
				layout.createParallelGroup()
				.addComponent(glue1)
				.addComponent(glue2)
				.addComponent(glue3)
				.addComponent(glue4)
				.addComponent(glue5)
				.addComponent(glue6)
				.addComponent(glue7)
			);
		
		hGroup.addGroup(
				layout.createParallelGroup(Alignment.TRAILING)
				.addComponent(getTypeComboBox())
				.addComponent(getUrlTextField())
				.addComponent(getUserNameTextField())
				.addComponent(getPasswordField())
				.addComponent(getPasswordAgainField())
				.addComponent(getSavePasswordCheckBox())
				.addComponent(getLinkLabel())
			);
		
		layout.setHorizontalGroup(hGroup);
		
		GroupLayout.SequentialGroup vGroup = layout.createSequentialGroup();

		vGroup.addGroup(
				layout.createParallelGroup(Alignment.CENTER)
	            .addComponent(typeLabel)
	            .addComponent(glue1)
	            .addComponent(getTypeComboBox())
			);

		vGroup.addGroup(
				layout.createParallelGroup(Alignment.CENTER)
	            .addComponent(urlLabel)
	            .addComponent(glue2)
	            .addComponent(getUrlTextField())
			);

		vGroup.addGroup(
				layout.createParallelGroup(Alignment.CENTER)
	            .addComponent(userNameLabel)
	            .addComponent(glue3)
	            .addComponent(getUserNameTextField())
			);

		vGroup.addGroup(
				layout.createParallelGroup(Alignment.CENTER)
	            .addComponent(passwordLabel)
	            .addComponent(glue4)
	            .addComponent(getPasswordField())
			);

		vGroup.addGroup(
				layout.createParallelGroup(Alignment.CENTER)
	            .addComponent(passwordAgainLabel)
	            .addComponent(glue5)
	            .addComponent(getPasswordAgainField())
			);

		vGroup.addGroup(
				layout.createParallelGroup(Alignment.CENTER)
	            .addComponent(savePasswordLabel)
	            .addComponent(glue6)
	            .addComponent(getSavePasswordCheckBox())
			);
		
		vGroup.addGroup(
				layout.createParallelGroup(Alignment.BASELINE)
	            .addComponent(filler1)
	            .addComponent(glue7)
	            .addComponent(getLinkLabel())
			);
		
		layout.setVerticalGroup(vGroup);						
		
		interfacePanel.add( namePanel, BorderLayout.NORTH );
		interfacePanel.add( settingsPanel, BorderLayout.CENTER );
		
		return interfacePanel;
		
	}
	
	public JTextField getNameTextField() {
		if( nameTextField == null ) {
			nameTextField = new JTextField();
			nameTextField.setPreferredSize(new Dimension(300,25) );
		}
		return nameTextField;
	}
	
	
	
	public JComboBox getTypeComboBox() {
		if( typeComboBox == null ) {
			DefaultComboBoxModel model = new DefaultComboBoxModel( new Object[] {
					messageSource.getMessage("mp5Method.config.remote.typeEegPl"),
					messageSource.getMessage("mp5Method.config.remote.typeOther")
			});
			
			typeComboBox = new JComboBox(model);
			typeComboBox.setPreferredSize( new Dimension(200,25) );
			
			configureForEegPl();
			
			typeComboBox.addItemListener( new ItemListener() {

				@Override
				public void itemStateChanged(ItemEvent e) {
					if( e.getStateChange() == ItemEvent.SELECTED ) {
						
						int index = typeComboBox.getSelectedIndex();
						if( index == 0 ) {

							configureForEegPl();
														
						}
						else if( index == 1 ) {

							configureForOther();
							
						}
						
					}					
				}
					
			});
			
		}
		return typeComboBox;
	}

	private void configureForEegPl() {

		JTextField field = getUrlTextField();
		field.setText(ConfigurationDefaults.getDefaultEegPlSignalmlWsURL());
		field.setEditable(false);
		
		getLinkLabel().setVisible(true);
		
	}

	private void configureForOther() {

		JTextField field = getUrlTextField();
		field.setText("");
		field.setEditable(true);
		
		getLinkLabel().setVisible(false);							
		
	}
	
	public JTextField getUrlTextField() {
		if( urlTextField == null ) {
			urlTextField = new JTextField();
			urlTextField.setPreferredSize( new Dimension(200,25) );
		}
		return urlTextField;
	}
	
	public JTextField getUserNameTextField() {
		if( userNameTextField == null ) {
			userNameTextField = new JTextField();
			userNameTextField.setPreferredSize( new Dimension(200,25) );
		}
		return userNameTextField;
	}
	
	public JPasswordField getPasswordField() {
		if( passwordField == null ) {
			passwordField = new JPasswordField();
			passwordField.setPreferredSize( new Dimension(200,25) );
		}
		return passwordField;
	}
	
	public JPasswordField getPasswordAgainField() {
		if( passwordAgainField == null ) {
			passwordAgainField = new JPasswordField();
			passwordAgainField.setPreferredSize( new Dimension(200,25) );
		}
		return passwordAgainField;
	}
	
	public JCheckBox getSavePasswordCheckBox() {
		if( savePasswordCheckBox == null ) {
			savePasswordCheckBox = new JCheckBox();
		}
		return savePasswordCheckBox;
	}
	
	public ExternalLinkLabel getLinkLabel() {
		if( linkLabel == null ) {
			URI eegPlURI;
			try {
				eegPlURI = new URI(ConfigurationDefaults.getDefaultEegPlRegisterURL());
			} catch (URISyntaxException ex) {
				throw new SanityCheckException(ex);
			}
			
			linkLabel = new ExternalLinkLabel( messageSource.getMessage("mp5Method.config.remote.makeEegPlAccount"), eegPlURI );
		}
		return linkLabel;
	}
	
	@Override
	public void fillDialogFromModel(Object model) throws SignalMLException {
		
		MP5RemotePasswordExecutor executor = (MP5RemotePasswordExecutor) model;

		String name = executor.getName();
		if( name == null ) {
			name = messageSource.getMessage( "mp5Method.config.remote.newNameSuggestion" );
		}
		
		JTextField nameField = getNameTextField();
		nameField.setText( name );
		
		String url = executor.getUrl();
		boolean urlFieldEditable;
		if( url == null || url.equals(ConfigurationDefaults.getDefaultEegPlSignalmlWsURL()) ) {
			getTypeComboBox().setSelectedIndex(0);
			urlFieldEditable = false;
		} else {
			getTypeComboBox().setSelectedIndex(1);
			getUrlTextField().setText(url);			
			urlFieldEditable = true;
		}
		getTypeComboBox().repaint();
		
		getUserNameTextField().setText( executor.getUserName() );
		getPasswordField().setText( executor.getPassword() );
		getPasswordAgainField().setText( executor.getPassword() );
		
		getSavePasswordCheckBox().setSelected( executor.isSavePassword() );
		
		if( passwordOnly ) {
			
			nameField.setEditable(false);
			getTypeComboBox().setEnabled(false);
			getUrlTextField().setEditable(false);
			getUserNameTextField().setEditable(false);
			getSavePasswordCheckBox().setEnabled(false);
			
			getPasswordField().requestFocusInWindow();

		} else {
		
			nameField.setEditable(true);
			getTypeComboBox().setEnabled(true);
			getUrlTextField().setEditable(urlFieldEditable);
			getUserNameTextField().setEditable(true);			
			getSavePasswordCheckBox().setEnabled(true);
			
			nameField.selectAll();		
			nameField.requestFocusInWindow();
			
		}
		
	}

	@Override
	public void fillModelFromDialog(Object model) throws SignalMLException {

		MP5RemotePasswordExecutor executor = (MP5RemotePasswordExecutor) model;
		executor.setName( getNameTextField().getText() );
		
		executor.setUrl( getUrlTextField().getText() );
		executor.setUserName( getUserNameTextField().getText() );
		executor.setPassword( new String( getPasswordField().getPassword() ) );
		executor.setSavePassword( getSavePasswordCheckBox().isSelected() );
		
	}
	
	@Override
	public void validateDialog(Object model, Errors errors) throws SignalMLException {
		super.validateDialog(model, errors);
		
		if( !Util.validateString( getNameTextField().getText() ) ) {
			errors.rejectValue("name", "error.nameBadCharacters");
		}

		try {
			new URI( getUrlTextField().getText() );
		} catch (URISyntaxException ex) {
			errors.rejectValue("url", "error.badUrl");
		}
		
		if( !Util.validateString( getUserNameTextField().getText() ) ) {
			errors.rejectValue("userName", "error.userNameBadCharacters");
		}
		
		String password = new String( getPasswordField().getPassword() );
		if( !Util.validateString(password) ) {
			errors.rejectValue("password", "error.passwordBadCharacters");
		}
		if( passwordOnly && password.isEmpty() ) {
			errors.rejectValue("password", "error.passwordRequired");
		}
		
		if( !password.equals( new String( getPasswordAgainField().getPassword() ) ) ) {
			errors.rejectValue("password", "error.passwordsDifferent");
			getPasswordField().setText("");
			getPasswordAgainField().setText("");
		}		
		
	}
	
	@Override
	public boolean supportsModelClass(Class<?> clazz) {
		return MP5RemotePasswordExecutor.class.isAssignableFrom(clazz);
	}
	
	public boolean isPasswordOnly() {
		return passwordOnly;
	}

	public void setPasswordOnly(boolean passwordOnly) {
		this.passwordOnly = passwordOnly;
	}

	protected PleaseWaitDialog getPleaseWaitDialog() {
		if( pleaseWaitDialog == null ) {
			pleaseWaitDialog = new PleaseWaitDialog(messageSource, this);
			pleaseWaitDialog.initializeNow();			
		}
		return pleaseWaitDialog;
	}

	protected class TestConnectionAction extends AbstractAction {

		public static final String HELLO_STRING = "Hello server";
		private static final long serialVersionUID = 1L;

		public TestConnectionAction() {
			super(messageSource.getMessage("mp5Method.config.remote.testConnection"));
			putValue(AbstractAction.SMALL_ICON, IconUtils.loadClassPathIcon("org/signalml/app/icon/testconnection.png") );
			putValue(AbstractAction.SHORT_DESCRIPTION,messageSource.getMessage("mp5Method.config.remote.testConnectionToolTip"));
		}
		
		public void actionPerformed(ActionEvent ev) {			
			
			Object currentModel = getCurrentModel();
			if( currentModel != null ) {
				Errors errors = new BindException(currentModel, "data");
				try {
					validateDialog(currentModel,errors);
				} catch( SignalMLException ex ) {
					logger.error("Dialog validation threw an exception", ex );
					ErrorsDialog.showImmediateExceptionDialog(MP5RemoteExecutorDialog.this, ex);
					setCurrentModel(null);
					setClosedWithOk(false);
					setVisible(false);
					return;
				}
				
				if( errors.hasErrors() ) {
					showValidationErrors(errors);
					return;
				}
			}
			
			TestConnectionRequest request = new TestConnectionRequest();
			PasswordCredentials credentials = new PasswordCredentials();
			
			credentials.setUserName( getUserNameTextField().getText() );
			credentials.setPassword( new String( getPasswordField().getPassword() ) );
			
			Credentials creds = new Credentials();
			creds.setPasswordCredentials(credentials);
			
			request.setCredentials(creds);
			request.setHelloString(HELLO_STRING);
			
			PleaseWaitDialog waitDialog = getPleaseWaitDialog();
			TestConnectionWorker worker = new TestConnectionWorker(request, getUrlTextField().getText(), waitDialog);
			
			worker.execute();

			waitDialog.setActivity(messageSource.getMessage("activity.testingConnection"));			
			waitDialog.configureForIndeterminateSimulated();
			waitDialog.waitAndShowDialogIn(MP5RemoteExecutorDialog.this.getRootPane(), 500, worker);
			
			TestConnectionResponse response = null;
			try {
				response = worker.get();
			} catch( InterruptedException ex ) {
				// ignore
			} catch (ExecutionException ex) {
				logger.error( "Failed to connect to server", ex);
				ErrorsDialog.showImmediateExceptionDialog(MP5RemoteExecutorDialog.this, ex.getCause());
				return;
			}
						
			String responseString = response.getResponseString();
			if( responseString == null || !responseString.contains(HELLO_STRING) ) {
				logger.error( "Server returned garbage [" + responseString + "]" );
				ErrorsDialog.showImmediateExceptionDialog(MP5RemoteExecutorDialog.this, new SanityCheckException("Server returned garbage"));
				return;
			}
			
			OptionPane.showMessage(MP5RemoteExecutorDialog.this, "mp5Method.config.remote.testConnectionSuccess");
			
		}
		
	}
	
}
