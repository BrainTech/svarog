/* MP5ToolConfigPanel.java created 2008-02-08
 * 
 */
package org.signalml.app.method.mp5;

import java.awt.BorderLayout;

import javax.swing.JPanel;

import org.springframework.context.support.MessageSourceAccessor;

/** MP5ToolConfigPanel
 *
 * 
 * @author Michal Dobaczewski &copy; 2007-2008 CC Otwarte Systemy Komputerowe Sp. z o.o.
 */
public class MP5ToolConfigPanel extends JPanel {
	
	private static final long serialVersionUID = 1L;

	private MessageSourceAccessor messageSource;
	private MP5ExecutorManager mp5ExecutorManager;
	private MP5LocalExecutorDialog localExecutorDialog;
	private MP5RemoteExecutorDialog remoteExecutorDialog;
	
	private MP5ToolExecutorConfigPanel executorConfigPanel;
	
	public MP5ToolConfigPanel(MessageSourceAccessor messageSource, MP5ExecutorManager mp5ExecutorManager) {
		super();
		this.messageSource = messageSource;
		this.mp5ExecutorManager = mp5ExecutorManager;
		initialize();
	}

	private void initialize() {
		
		setLayout(new BorderLayout());

		add( getExecutorConfigPanel(), BorderLayout.CENTER );
		
	}
	
	public MP5ToolExecutorConfigPanel getExecutorConfigPanel() {
		if( executorConfigPanel == null ) {
			executorConfigPanel = new MP5ToolExecutorConfigPanel(messageSource,mp5ExecutorManager);
			executorConfigPanel.setLocalExecutorDialog(localExecutorDialog);
			executorConfigPanel.setRemoteExecutorDialog(remoteExecutorDialog);
		}
		return executorConfigPanel;
	}

	public MP5LocalExecutorDialog getLocalExecutorDialog() {
		return localExecutorDialog;
	}

	public void setLocalExecutorDialog(MP5LocalExecutorDialog localExecutorDialog) {
		this.localExecutorDialog = localExecutorDialog;
		getExecutorConfigPanel().setLocalExecutorDialog(localExecutorDialog);
	}

	public MP5RemoteExecutorDialog getRemoteExecutorDialog() {
		return remoteExecutorDialog;
	}

	public void setRemoteExecutorDialog(MP5RemoteExecutorDialog remoteExecutorDialog) {
		this.remoteExecutorDialog = remoteExecutorDialog;
		getExecutorConfigPanel().setRemoteExecutorDialog(remoteExecutorDialog);
	}
			
}
