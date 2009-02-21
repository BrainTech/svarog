/* MP5ExecutorPanel.java created 2008-02-08
 * 
 */
package org.signalml.app.method.mp5;

import java.awt.BorderLayout;
import java.awt.Dimension;

import javax.swing.JPanel;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;

import org.signalml.app.view.element.ResolvableComboBox;
import org.signalml.method.mp5.MP5Data;
import org.signalml.method.mp5.MP5Executor;
import org.springframework.context.support.MessageSourceAccessor;
import org.springframework.validation.Errors;

/** MP5ExecutorPanel
 *
 * 
 * @author Michal Dobaczewski &copy; 2007-2008 CC Otwarte Systemy Komputerowe Sp. z o.o.
 */
public class MP5ExecutorPanel extends JPanel {
	
	private static final long serialVersionUID = 1L;

	private MessageSourceAccessor messageSource;
	
	private MP5ExecutorManager executorManager;
	
	private ResolvableComboBox executorComboBox;
	
	public MP5ExecutorPanel(MessageSourceAccessor messageSource, MP5ExecutorManager executorManager) {
		super();
		this.messageSource = messageSource;
		this.executorManager = executorManager;
		initialize();
	}

	private void initialize() {

		setLayout( new BorderLayout() );
		
		CompoundBorder border = new CompoundBorder(
			new TitledBorder( messageSource.getMessage("mp5Method.dialog.chooseExecutorTitle") ),
			new EmptyBorder(3,3,3,3)
		);
		setBorder(border);
		
		add( getExecutorComboBox(), BorderLayout.CENTER );
				
	}
	
	public ResolvableComboBox getExecutorComboBox() {
		if( executorComboBox == null ) {
			executorComboBox = new ResolvableComboBox(messageSource);
			executorComboBox.setModel( new MP5ExecutorComboBoxModel(executorManager) );
			executorComboBox.setPreferredSize( new Dimension(300,25) );
		}
		return executorComboBox;
	}
	
	public void fillPanelFromModel(MP5Data data) {

		String executorUID = data.getExecutorUID();
		MP5Executor executor = null;
		if( executorUID != null ) {
			executor = executorManager.findExecutor( executorUID );
		}
		if( executor == null ) {
			executor = executorManager.getDefaultExecutor();
		}
		
		getExecutorComboBox().setSelectedItem( executor );
		
	}
	
	public void fillModelFromPanel(MP5Data data) {
		
		MP5Executor executor = ((MP5Executor) getExecutorComboBox().getSelectedItem());
		if( executor == null ) {
			data.setExecutorUID(null);
		} else {
			data.setExecutorUID( executor.getUID() );
		}
		
	}
	
	public void validatePanel( Errors errors ) {
				
		if( getExecutorComboBox().getSelectedItem() == null ) {
			errors.rejectValue("executorUID", "error.mp5.noExecutor");		
		}
				
	}
		
}
