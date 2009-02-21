/* ToolsConfigPanel.java created 2007-12-14
 * 
 */
package org.signalml.app.view.element;

import java.awt.BorderLayout;

import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

import org.signalml.app.config.ApplicationConfiguration;
import org.signalml.app.method.artifact.ArtifactToolConfigPanel;
import org.signalml.app.method.mp5.MP5ExecutorManager;
import org.signalml.app.method.mp5.MP5LocalExecutorDialog;
import org.signalml.app.method.mp5.MP5RemoteExecutorDialog;
import org.signalml.app.method.mp5.MP5ToolConfigPanel;
import org.signalml.app.method.stager.StagerToolConfigPanel;
import org.signalml.app.view.ViewerFileChooser;
import org.springframework.context.support.MessageSourceAccessor;
import org.springframework.validation.Errors;

/** ToolsConfigPanel
 *
 * 
 * @author Michal Dobaczewski &copy; 2007-2008 CC Otwarte Systemy Komputerowe Sp. z o.o.
 */
public class ToolsConfigPanel extends JPanel {
	
	private static final long serialVersionUID = 1L;

	private MessageSourceAccessor messageSource;
	private ViewerFileChooser fileChooser;
	private MP5ExecutorManager mp5ExecutorManager;
	private MP5LocalExecutorDialog mp5LocalExecutorDialog;
	private MP5RemoteExecutorDialog mp5RemoteExecutorDialog;
	
	private MP5ToolConfigPanel mp5Panel;
	private ArtifactToolConfigPanel artifactPanel;
	private StagerToolConfigPanel stagerPanel;
	
	public ToolsConfigPanel(MessageSourceAccessor messageSource, ViewerFileChooser fileChooser, MP5ExecutorManager mp5ExecutorManager) {
		super();
		this.messageSource = messageSource;
		this.fileChooser = fileChooser;
		this.mp5ExecutorManager = mp5ExecutorManager;
		initialize();
	}

	private void initialize() {
		
		setLayout(new BorderLayout());		
		setBorder(new EmptyBorder(3,3,3,3));

		add( getMp5Panel(), BorderLayout.NORTH );
		add( getArtifactPanel(), BorderLayout.CENTER );
		add( getStagerPanel(), BorderLayout.SOUTH );
		
	}

	public MP5ToolConfigPanel getMp5Panel() {
		if( mp5Panel == null ) {
			mp5Panel = new MP5ToolConfigPanel(messageSource, mp5ExecutorManager);
			mp5Panel.setLocalExecutorDialog(mp5LocalExecutorDialog);
			mp5Panel.setRemoteExecutorDialog(mp5RemoteExecutorDialog);
		}
		return mp5Panel;
	}
	
	public ArtifactToolConfigPanel getArtifactPanel() {
		if( artifactPanel == null ) {
			artifactPanel = new ArtifactToolConfigPanel(messageSource,fileChooser);
		}
		return artifactPanel;
	}
	
	public StagerToolConfigPanel getStagerPanel() {
		if( stagerPanel == null ) {
			stagerPanel = new StagerToolConfigPanel(messageSource,fileChooser);
		}
		return stagerPanel;
	}

	public void fillPanelFromModel( ApplicationConfiguration applicationConfig ) {
		
		getArtifactPanel().fillPanelFromModel(applicationConfig.getArtifactConfig());
		getStagerPanel().fillPanelFromModel(applicationConfig.getStagerConfig());
		
	}
	
	public void fillModelFromPanel( ApplicationConfiguration applicationConfig ) {
		
		getArtifactPanel().fillModelFromPanel(applicationConfig.getArtifactConfig());
		getStagerPanel().fillModelFromPanel(applicationConfig.getStagerConfig());
				
	}
	
	public void validatePanel(Errors errors) {

		errors.pushNestedPath("artifactConfig");
		getArtifactPanel().validatePanel(errors);
		errors.popNestedPath();

		errors.pushNestedPath("stagerConfig");
		getStagerPanel().validatePanel(errors);
		errors.popNestedPath();
		
	}

	public MP5LocalExecutorDialog getMp5LocalExecutorDialog() {
		return mp5LocalExecutorDialog;
	}

	public void setMp5LocalExecutorDialog(MP5LocalExecutorDialog mp5LocalExecutorDialog) {
		this.mp5LocalExecutorDialog = mp5LocalExecutorDialog;
		getMp5Panel().setLocalExecutorDialog(mp5LocalExecutorDialog);
	}

	public MP5RemoteExecutorDialog getMp5RemoteExecutorDialog() {
		return mp5RemoteExecutorDialog;
	}

	public void setMp5RemoteExecutorDialog(MP5RemoteExecutorDialog mp5RemoteExecutorDialog) {
		this.mp5RemoteExecutorDialog = mp5RemoteExecutorDialog;
		getMp5Panel().setRemoteExecutorDialog(mp5RemoteExecutorDialog);
	}
	
	
		
}
