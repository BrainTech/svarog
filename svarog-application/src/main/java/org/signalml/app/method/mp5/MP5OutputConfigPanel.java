/* MP5OutputConfigPanel.java created 2008-01-31
 * 
 */
package org.signalml.app.method.mp5;

import java.awt.Component;

import javax.swing.Box;
import javax.swing.GroupLayout;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.GroupLayout.Alignment;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;

import org.signalml.app.util.SwingUtils;
import org.signalml.app.view.dialog.AbstractDialog;
import org.signalml.app.view.element.CompactButton;
import org.signalml.method.mp5.MP5Parameters;
import org.springframework.context.support.MessageSourceAccessor;
import org.springframework.validation.Errors;

/** MP5OutputConfigPanel
 *
 * 
 * @author Michal Dobaczewski &copy; 2007-2008 CC Otwarte Systemy Komputerowe Sp. z o.o.
 */
public class MP5OutputConfigPanel extends JPanel {
	
	private static final long serialVersionUID = 1L;

	private MessageSourceAccessor messageSource;
	private AbstractDialog owner;		
			
	private JCheckBox bookWithSignalCheckBox;
	
	public MP5OutputConfigPanel(MessageSourceAccessor messageSource, AbstractDialog owner) {
		super();
		this.messageSource = messageSource;
		this.owner = owner;
		initialize();
	}

	private void initialize() {
		
		CompoundBorder border = new CompoundBorder(
				new TitledBorder( messageSource.getMessage("mp5Method.dialog.outputTitle") ),
				new EmptyBorder(3,3,3,3)
			);
		
		setBorder(border);
		
		GroupLayout layout = new GroupLayout(this);
		this.setLayout(layout);
		layout.setAutoCreateContainerGaps(false);
		layout.setAutoCreateGaps(true);

		JLabel bookWithSignalLabel = new JLabel(messageSource.getMessage("mp5Method.dialog.bookWithSignal"));
		
		CompactButton bookWithSignalHelpButton = SwingUtils.createFieldHelpButton(messageSource, owner, MP5MethodDialog.HELP_BOOK_WITH_SIGNAL);

		Component glue1 = Box.createHorizontalGlue();
		
		GroupLayout.SequentialGroup hGroup = layout.createSequentialGroup();

		hGroup.addGroup(
				layout.createParallelGroup()
				.addComponent(bookWithSignalLabel)
			);

		hGroup.addGroup(
				layout.createParallelGroup()
				.addComponent(glue1)
			);
		
		hGroup.addGroup(
				layout.createParallelGroup()
				.addComponent(getBookWithSignalCheckBox())
			);

		hGroup.addGroup(
				layout.createParallelGroup()
				.addComponent(bookWithSignalHelpButton)
			);
		
		layout.setHorizontalGroup(hGroup);
		
		GroupLayout.SequentialGroup vGroup = layout.createSequentialGroup();

		vGroup.addGroup(
				layout.createParallelGroup(Alignment.CENTER)
	            .addComponent(bookWithSignalLabel)
	            .addComponent(glue1)
	            .addComponent(getBookWithSignalCheckBox())
	            .addComponent(bookWithSignalHelpButton)
			);
	
		layout.setVerticalGroup(vGroup);				
								
	}
	
	public JCheckBox getBookWithSignalCheckBox() {
		if( bookWithSignalCheckBox == null ) {
			bookWithSignalCheckBox = new JCheckBox();
		}
		return bookWithSignalCheckBox;
	}
	
	public void fillPanelFromParameters(MP5Parameters parameters) {
		
		getBookWithSignalCheckBox().setSelected( parameters.isBookWithSignal() );
		
	}
	
	public void fillParametersFromPanel(MP5Parameters parameters) {
		
		parameters.setBookWithSignal( getBookWithSignalCheckBox().isSelected() );
		
	}
	
	public void validatePanel( Errors errors ) {
				
		// nothing to do
		
	}
	
}
