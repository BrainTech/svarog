/* MP5ExpertConfigPanel.java created 2008-01-30
 * 
 */
package org.signalml.app.method.mp5;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;

import javax.swing.JPanel;
import javax.swing.border.TitledBorder;

import org.signalml.app.util.SwingUtils;
import org.signalml.app.view.dialog.AbstractDialog;
import org.signalml.app.view.element.CompactButton;
import org.signalml.app.view.element.TextPanePanel;
import org.signalml.method.mp5.MP5Parameters;
import org.springframework.context.support.MessageSourceAccessor;
import org.springframework.validation.Errors;

/** MP5ExpertConfigPanel
 *
 * 
 * @author Michal Dobaczewski &copy; 2007-2008 CC Otwarte Systemy Komputerowe Sp. z o.o.
 */
public class MP5ExpertConfigPanel extends JPanel {
	
	private static final long serialVersionUID = 1L;

	private MessageSourceAccessor messageSource;
	private AbstractDialog owner;
	
	private TextPanePanel additionalConfigTextPane;
			
	public MP5ExpertConfigPanel(MessageSourceAccessor messageSource, AbstractDialog owner) {
		super();
		this.messageSource = messageSource;
		this.owner = owner;
		initialize();
	}

	private void initialize() {
		
		setLayout( new BorderLayout() );
		
		JPanel additionalConfigPanel = new JPanel( new BorderLayout(3,3) );
		additionalConfigPanel.setBorder( new TitledBorder( messageSource.getMessage("mp5Method.dialog.additionalConfig") ) );

		CompactButton additionalConfigHelpButton = SwingUtils.createFieldHelpButton(messageSource, owner, MP5MethodDialog.HELP_ADDITIONAL_CONFIG);
		
		JPanel additionalConfigHelpPanel = new JPanel( new FlowLayout( FlowLayout.TRAILING ) );
		additionalConfigHelpPanel.add( additionalConfigHelpButton );
		
		additionalConfigPanel.add( getAdditionalConfigTextPane(), BorderLayout.CENTER );
		additionalConfigPanel.add( additionalConfigHelpPanel, BorderLayout.SOUTH );
		
		add( additionalConfigPanel, BorderLayout.CENTER );
								
	}
	
	public TextPanePanel getAdditionalConfigTextPane() {
		if( additionalConfigTextPane == null ) {
			additionalConfigTextPane = new TextPanePanel(null);
			additionalConfigTextPane.setPreferredSize( new Dimension(200,150) );
		}
		return additionalConfigTextPane;
	}

	public void fillPanelFromParameters(MP5Parameters parameters) {
		
		getAdditionalConfigTextPane().getTextPane().setText( parameters.getCustomConfigText() );
		
	}
	
	public void fillParametersFromPanel(MP5Parameters parameters) {
		
		parameters.setCustomConfigText( getAdditionalConfigTextPane().getTextPane().getText().trim() );		
		
	}
	
	public void validatePanel( Errors errors ) {
				
		// additional config panel is ok
		
	}
	
}
