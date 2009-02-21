/* MP5RawConfigPanel.java created 2008-01-31
 * 
 */
package org.signalml.app.method.mp5;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;

import javax.swing.JPanel;
import javax.swing.border.TitledBorder;
import javax.swing.event.DocumentEvent;

import org.signalml.app.util.SwingUtils;
import org.signalml.app.view.dialog.AbstractDialog;
import org.signalml.app.view.element.AnyChangeDocumentAdapter;
import org.signalml.app.view.element.CompactButton;
import org.signalml.app.view.element.TextPanePanel;
import org.signalml.method.mp5.MP5Parameters;
import org.springframework.context.support.MessageSourceAccessor;
import org.springframework.validation.Errors;

/** MP5RawConfigPanel
 *
 * 
 * @author Michal Dobaczewski &copy; 2007-2008 CC Otwarte Systemy Komputerowe Sp. z o.o.
 */
public class MP5RawConfigPanel extends JPanel {
	
	private static final long serialVersionUID = 1L;

	private MessageSourceAccessor messageSource;
	private MP5ExecutorManager executorManager;
	private AbstractDialog owner;
	
	private TextPanePanel rawConfigTextPane;
	private MP5ExecutorPanel executorPanel;
	
	private boolean configChanged;
	
	public MP5RawConfigPanel(MessageSourceAccessor messageSource, MP5ExecutorManager executorManager, AbstractDialog owner) {
		super();
		this.messageSource = messageSource;
		this.executorManager = executorManager;
		this.owner = owner;
		initialize();
	}

	private void initialize() {
		
		setLayout( new BorderLayout() );
		
		JPanel rawConfigPanel = new JPanel( new BorderLayout(3,3) );
		rawConfigPanel.setBorder( new TitledBorder( messageSource.getMessage("mp5Method.dialog.rawConfig") ) );

		CompactButton rawConfigHelpButton = SwingUtils.createFieldHelpButton(messageSource, owner, MP5MethodDialog.HELP_RAW_CONFIG);
		
		JPanel rawConfigHelpPanel = new JPanel( new FlowLayout( FlowLayout.TRAILING ) );
		rawConfigHelpPanel.add( rawConfigHelpButton );
		
		rawConfigPanel.add( getRawConfigTextPane(), BorderLayout.CENTER );
		rawConfigPanel.add( rawConfigHelpPanel, BorderLayout.SOUTH );
		
		add( rawConfigPanel, BorderLayout.CENTER );
		add( getExecutorPanel(), BorderLayout.SOUTH );
		
	}
	
	public TextPanePanel getRawConfigTextPane() {
		if( rawConfigTextPane == null ) {
			rawConfigTextPane = new TextPanePanel(null);
			rawConfigTextPane.setPreferredSize( new Dimension(200,150) );
			
			rawConfigTextPane.getTextPane().getDocument().addDocumentListener( new AnyChangeDocumentAdapter() {

				@Override
				public void anyUpdate(DocumentEvent e) {
					configChanged = true;
				}
				
			});
		}
		return rawConfigTextPane;
	}
	
	public MP5ExecutorPanel getExecutorPanel() {
		if( executorPanel == null ) {
			executorPanel = new MP5ExecutorPanel(messageSource, executorManager);
		}
		return executorPanel;
	}

	public void fillPanelFromParameters(MP5Parameters parameters) {
		
		getRawConfigTextPane().getTextPane().setText( parameters.getRawConfigText() );
				
	}
	
	public void fillParametersFromPanel(MP5Parameters parameters) {
		
		parameters.setRawConfigText( getRawConfigTextPane().getTextPane().getText().trim() );
		
	}
	
	public void validatePanel( Errors errors ) {
				
		// nothing to do
		
	}

	public boolean isConfigChanged() {
		return configChanged;
	}
	
	public void setConfigChanged(boolean configChanged) {
		this.configChanged = configChanged;
	}
	
}
