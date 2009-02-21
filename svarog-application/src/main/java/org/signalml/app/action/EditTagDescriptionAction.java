/* EditTagDescriptionAction.java created 2007-11-22
 * 
 */

package org.signalml.app.action;

import java.awt.event.ActionEvent;

import org.apache.log4j.Logger;
import org.signalml.app.action.selector.TagDocumentFocusSelector;
import org.signalml.app.document.TagDocument;
import org.signalml.app.view.dialog.EditTagDescriptionDialog;
import org.springframework.context.support.MessageSourceAccessor;

/** EditTagDescriptionAction
 *
 * 
 * @author Michal Dobaczewski &copy; 2007-2008 CC Otwarte Systemy Komputerowe Sp. z o.o.
 */
public class EditTagDescriptionAction extends AbstractFocusableSignalMLAction<TagDocumentFocusSelector> {

	private static final long serialVersionUID = 1L;
	
	protected static final Logger logger = Logger.getLogger(EditTagDescriptionAction.class);

	private EditTagDescriptionDialog editTagDescriptionDialog;
	
	public EditTagDescriptionAction(MessageSourceAccessor messageSource, TagDocumentFocusSelector tagDocumentFocusSelector) {
		super(messageSource, tagDocumentFocusSelector);
		setText("action.editTagDescription");
		setToolTip("action.editTagDescriptionToolTip");
		setIconPath("org/signalml/app/icon/edittagdescription.png");
	}
	
	@Override
	public void actionPerformed(ActionEvent e) {

		TagDocument tagDocument = getActionFocusSelector().getActiveTagDocument();
		if( tagDocument == null ) {
			return;
		}
				
		boolean ok = editTagDescriptionDialog.showDialog(tagDocument, true);
		if( !ok ) {
			return;
		}
		
		tagDocument.invalidate();
				
	}

	@Override
	public void setEnabledAsNeeded() {
		setEnabled( getActionFocusSelector().getActiveTagDocument() != null );
	}

	public EditTagDescriptionDialog getEditTagDescriptionDialog() {
		return editTagDescriptionDialog;
	}

	public void setEditTagDescriptionDialog(EditTagDescriptionDialog editTagDescriptionDialog) {
		this.editTagDescriptionDialog = editTagDescriptionDialog;
	}
	
}
