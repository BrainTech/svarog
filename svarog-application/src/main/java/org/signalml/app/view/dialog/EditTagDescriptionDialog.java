/* EditTagDescriptionDialog.java created 2007-11-22
 * 
 */

package org.signalml.app.view.dialog;

import java.awt.Dimension;
import java.awt.Window;

import javax.swing.BoxLayout;
import javax.swing.JComponent;
import javax.swing.JPanel;

import org.signalml.app.document.TagDocument;
import org.signalml.app.util.IconUtils;
import org.signalml.app.view.element.TextPanePanel;
import org.signalml.domain.tag.StyledTagSet;
import org.signalml.exception.SignalMLException;
import org.signalml.util.Util;
import org.springframework.context.support.MessageSourceAccessor;
import org.springframework.validation.Errors;

/** EditTagDescriptionDialog
 *
 * 
 * @author Michal Dobaczewski &copy; 2007-2008 CC Otwarte Systemy Komputerowe Sp. z o.o.
 */
public class EditTagDescriptionDialog extends AbstractDialog {

	private static final long serialVersionUID = 1L;
		
	private TextPanePanel textInfoPanel;
	private TextPanePanel montageInfoPanel;
		
	public EditTagDescriptionDialog(MessageSourceAccessor messageSource) {
		super(messageSource);
	}

	public EditTagDescriptionDialog(MessageSourceAccessor messageSource, Window w, boolean isModal) {
		super(messageSource, w, isModal);
	}

	@Override
	public boolean supportsModelClass(Class<?> clazz) {
		return TagDocument.class.isAssignableFrom(clazz);
	}
	
	@Override
	public void fillDialogFromModel(Object model) throws SignalMLException {
		TagDocument tagDocument = (TagDocument) model;
		StyledTagSet tagSet = tagDocument.getTagSet();
		String description = tagSet.getInfo();
		textInfoPanel.getTextPane().setText( description != null ? description : "" );
		description = tagSet.getMontageInfo();
		montageInfoPanel.getTextPane().setText( description != null ? description : "" );
	}

	@Override
	public void fillModelFromDialog(Object model) throws SignalMLException {
		TagDocument tagDocument = (TagDocument) model;
		String description = textInfoPanel.getTextPane().getText();
		if( description.isEmpty() ) {
			description = null;
		}
		tagDocument.getTagSet().setInfo(description);
	}

	@Override
	public void validateDialog(Object model, Errors errors) throws SignalMLException {
		super.validateDialog(model, errors);
		
		String description = textInfoPanel.getTextPane().getText();
		if( description != null && !description.isEmpty() ) {
			if( !Util.validateString(description) ) {
				errors.rejectValue( "description", "error.descriptionnBadChars" );
			}
		}		
		
	}
	
	@Override
	protected void initialize() {
		setTitle( messageSource.getMessage("tagDescription.title") );
		setIconImage( IconUtils.loadClassPathImage("org/signalml/app/icon/edittagdescription.png"));
		super.initialize();
	}
	
	@Override
	public JComponent createInterface() {

		textInfoPanel = new TextPanePanel( messageSource.getMessage( "tagDescription.info" ) );
		textInfoPanel.setPreferredSize( new Dimension( 400,200 ) );
		
		montageInfoPanel = new TextPanePanel( messageSource.getMessage("tagDescription.montageInfo") );
		montageInfoPanel.setPreferredSize( new Dimension( 400,200 ) );
		montageInfoPanel.getTextPane().setEditable(false);
		
		JPanel interfacePanel = new JPanel();
		interfacePanel.setLayout( new BoxLayout( interfacePanel, BoxLayout.Y_AXIS ) );
		interfacePanel.add( textInfoPanel );
		interfacePanel.add( montageInfoPanel );
		
		return interfacePanel;
		
	}

}
