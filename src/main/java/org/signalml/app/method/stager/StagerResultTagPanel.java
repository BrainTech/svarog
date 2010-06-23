/* StagerResultTagPanel.java created 2008-02-20
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
import org.springframework.context.support.MessageSourceAccessor;
import org.springframework.validation.Errors;

/** StagerResultTagPanel
 *
 * 
 * @author Michal Dobaczewski &copy; 2007-2008 CC Otwarte Systemy Komputerowe Sp. z o.o.
 */
public class StagerResultTagPanel extends JPanel {
	
	private static final long serialVersionUID = 1L;

	private MessageSourceAccessor messageSource;
	
	private JTextField tagTextField;
	private JButton chooseTagButton;

	private ViewerFileChooser fileChooser;
	
	private File tagFile;
	
	public StagerResultTagPanel(MessageSourceAccessor messageSource, ViewerFileChooser fileChooser) {
		super();
		this.messageSource = messageSource;
		this.fileChooser = fileChooser;
		initialize();
	}

	private void initialize() {
		
		CompoundBorder border = new CompoundBorder(
			new TitledBorder( messageSource.getMessage("stagerMethod.dialog.result.choosePrimaryTagTitle") ),
			new EmptyBorder(3,3,3,3)
		);
		setBorder(border);

		GroupLayout layout = new GroupLayout(this);
		this.setLayout(layout);
		layout.setAutoCreateContainerGaps(false);
		layout.setAutoCreateGaps(true);

		JLabel tagFileLabel = new JLabel(messageSource.getMessage("stagerMethod.dialog.result.primaryTagFile"));
		
		GroupLayout.SequentialGroup hGroup = layout.createSequentialGroup();
		
		hGroup.addGroup(
				layout.createParallelGroup()
				.addComponent(tagFileLabel)
			);
		
		hGroup.addGroup(
				layout.createParallelGroup()
				.addComponent(getTagTextField())
			);

		hGroup.addGroup(
				layout.createParallelGroup()
				.addComponent(getChooseTagButton())
			);
		
		layout.setHorizontalGroup(hGroup);
		
		GroupLayout.SequentialGroup vGroup = layout.createSequentialGroup();

		vGroup.addGroup(
				layout.createParallelGroup(Alignment.BASELINE)
				.addComponent(tagFileLabel)
				.addComponent(getTagTextField())
				.addComponent(getChooseTagButton())
			);
				
		layout.setVerticalGroup(vGroup);		
						
	}
	
	public JTextField getTagTextField() {
		if( tagTextField == null ) {
			tagTextField = new JTextField();
			tagTextField.setPreferredSize( new Dimension( 300,25 ) );
			tagTextField.setEditable(false);
		}
		return tagTextField;
	}

	public JButton getChooseTagButton() {
		if( chooseTagButton == null ) {
			chooseTagButton = new JButton( new ChooseTagFileAction() );
		}
		return chooseTagButton;
	}
	
	public void fillPanelFromModel(StagerResultTargetDescriptor descriptor) {
		
		if( descriptor.isPrimarySaveToFile() ) {
			tagFile = descriptor.getPrimaryTagFile();
			if( tagFile != null ) {
				getTagTextField().setText( tagFile.getAbsolutePath() );
			} else {
				getTagTextField().setText( "" );
			}
		} else {
			tagFile = null;
			getTagTextField().setText( "" );
		}
		
	}
	
	public void fillModelFromPanel(StagerResultTargetDescriptor descriptor) {
		
		descriptor.setPrimaryTagFile(tagFile);
		
	}
	
	public void validatePanel( Errors errors ) {
				
		if( tagFile == null ) {
			errors.rejectValue("primaryTagFile", "error.stager.result.badTagFile");
		} else {
			File parent = tagFile.getParentFile();
			if( parent == null || !parent.exists() || !parent.canWrite() ) {
				errors.rejectValue("primaryTagFile", "error.stager.result.tagFileNotWritable");
			}
		}
		
	}
	
	@Override
	public void setEnabled(boolean enabled) {
		getTagTextField().setEnabled(enabled);
		getChooseTagButton().setEnabled(enabled);
		super.setEnabled(enabled);
	}
	
	protected class ChooseTagFileAction extends AbstractAction {

		private static final long serialVersionUID = 1L;

		public ChooseTagFileAction() {
			super(messageSource.getMessage("stagerMethod.dialog.result.choosePrimaryTagFile"));
			putValue(AbstractAction.SMALL_ICON, IconUtils.loadClassPathIcon("org/signalml/app/icon/find.png") );
			putValue(AbstractAction.SHORT_DESCRIPTION,messageSource.getMessage("stagerMethod.dialog.result.choosePrimaryTagFileToolTip"));
		}
		
		public void actionPerformed(ActionEvent ev) {			
			
			File file = fileChooser.chooseSaveTag(StagerResultTagPanel.this.getTopLevelAncestor());
			if( file == null ) {
				return;
			}
			
			tagFile = file;			

			getTagTextField().setText(tagFile.getAbsolutePath());
			
		}
		
	}	
	
}
