/* StagerBookPanel.java created 2008-02-14
 * 
 */
package org.signalml.app.method.stager;

import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.io.File;

import javax.swing.AbstractAction;
import javax.swing.GroupLayout;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.GroupLayout.Alignment;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;

import org.signalml.app.util.IconUtils;
import org.signalml.app.view.ViewerFileChooser;
import org.signalml.method.stager.StagerParameters;
import org.springframework.context.support.MessageSourceAccessor;
import org.springframework.validation.Errors;

/** StagerBookPanel
 *
 * 
 * @author Michal Dobaczewski &copy; 2007-2008 CC Otwarte Systemy Komputerowe Sp. z o.o.
 */
public class StagerBookPanel extends JPanel {
	
	private static final long serialVersionUID = 1L;

	private MessageSourceAccessor messageSource;
	
	private JTextField bookTextField;
	private JButton chooseBookButton;

	private ViewerFileChooser fileChooser;
	
	private File bookFile;
	
	public StagerBookPanel(MessageSourceAccessor messageSource, ViewerFileChooser fileChooser) {
		super();
		this.messageSource = messageSource;
		this.fileChooser = fileChooser;
		initialize();
	}

	private void initialize() {
		
		CompoundBorder border = new CompoundBorder(
			new TitledBorder( messageSource.getMessage("stagerMethod.dialog.chooseBookTitle") ),
			new EmptyBorder(3,3,3,3)
		);
		setBorder(border);

		GroupLayout layout = new GroupLayout(this);
		this.setLayout(layout);
		layout.setAutoCreateContainerGaps(false);
		layout.setAutoCreateGaps(true);

		JLabel bookFileLabel = new JLabel(messageSource.getMessage("stagerMethod.dialog.bookFile"));
		
		GroupLayout.SequentialGroup hGroup = layout.createSequentialGroup();
		
		hGroup.addGroup(
				layout.createParallelGroup()
				.addComponent(bookFileLabel)
			);
		
		hGroup.addGroup(
				layout.createParallelGroup()
				.addComponent(getBookTextField())
			);

		hGroup.addGroup(
				layout.createParallelGroup()
				.addComponent(getChooseBookButton())
			);
		
		layout.setHorizontalGroup(hGroup);
		
		GroupLayout.SequentialGroup vGroup = layout.createSequentialGroup();

		vGroup.addGroup(
				layout.createParallelGroup(Alignment.BASELINE)
	            .addComponent(bookFileLabel)
	            .addComponent(getBookTextField())
	            .addComponent(getChooseBookButton())
			);
				
		layout.setVerticalGroup(vGroup);		
						
	}
	
	public JTextField getBookTextField() {
		if( bookTextField == null ) {
			bookTextField = new JTextField();
			bookTextField.setPreferredSize( new Dimension( 300,25 ) );
			bookTextField.setEditable(false);
		}
		return bookTextField;
	}

	public JButton getChooseBookButton() {
		if( chooseBookButton == null ) {
			chooseBookButton = new JButton( new ChooseBookFileAction() );
		}
		return chooseBookButton;
	}
	
	public void fillPanelFromModel(StagerParameters parameters) {
		
		String path = parameters.getBookFilePath();
		if( path != null ) {
			bookFile = new File( path );
			getBookTextField().setText( path );
		} else {
			bookFile = null;
			getBookTextField().setText( "" );
		}
		
	}
	
	public void fillModelFromPanel(StagerParameters parameters) {
		
		if( bookFile == null ) {
			parameters.setBookFilePath(null);
		} else {
			parameters.setBookFilePath(bookFile.getAbsolutePath());
		}
		
	}
	
	public void validatePanel( Errors errors ) {
				
		if( bookFile == null || !bookFile.exists() || !bookFile.canRead() ) {
			errors.rejectValue("bookFilePath", "error.stager.badBookFilePath");
		}
		
	}
	
	protected class ChooseBookFileAction extends AbstractAction {

		private static final long serialVersionUID = 1L;

		public ChooseBookFileAction() {
			super(messageSource.getMessage("stagerMethod.dialog.chooseBookFile"));
			putValue(AbstractAction.SMALL_ICON, IconUtils.loadClassPathIcon("org/signalml/app/icon/find.png") );
			putValue(AbstractAction.SHORT_DESCRIPTION,messageSource.getMessage("stagerMethod.dialog.chooseBookFileToolTip"));
		}
		
		public void actionPerformed(ActionEvent ev) {			
			
			File file = fileChooser.chooseBookFile(StagerBookPanel.this.getTopLevelAncestor());
			if( file == null ) {
				return;
			}
			
			bookFile = file;			

			getBookTextField().setText(bookFile.getAbsolutePath());
			
		}
		
	}	
	
}
