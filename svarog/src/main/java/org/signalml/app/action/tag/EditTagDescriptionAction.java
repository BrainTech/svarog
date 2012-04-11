/* EditTagDescriptionAction.java created 2007-11-22
 *
 */

package org.signalml.app.action.tag;

import static org.signalml.app.util.i18n.SvarogI18n._;

import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;

import org.apache.log4j.Logger;
import org.signalml.app.action.AbstractFocusableSignalMLAction;
import org.signalml.app.action.selector.TagDocumentFocusSelector;
import org.signalml.app.document.TagDocument;
import org.signalml.app.view.components.dialogs.EditTagDescriptionDialog;

/** EditTagDescriptionAction
 *
 *
 * @author Michal Dobaczewski &copy; 2007-2008 CC Otwarte Systemy Komputerowe Sp. z o.o.
 */
public class EditTagDescriptionAction extends AbstractFocusableSignalMLAction<TagDocumentFocusSelector> {

	private static final long serialVersionUID = 1L;

	protected static final Logger logger = Logger.getLogger(EditTagDescriptionAction.class);

	private EditTagDescriptionDialog editTagDescriptionDialog;

	public EditTagDescriptionAction(TagDocumentFocusSelector tagDocumentFocusSelector) {
		super(tagDocumentFocusSelector);
		setText(_("Edit tag description"));
		setToolTip(_("Edit tag description"));
		setIconPath("org/signalml/app/icon/edittagdescription.png");
		setMnemonic(KeyEvent.VK_D);
	}

	@Override
	public void actionPerformed(ActionEvent e) {

		TagDocument tagDocument = getActionFocusSelector().getActiveTagDocument();
		if (tagDocument == null) {
			return;
		}

		boolean ok = editTagDescriptionDialog.showDialog(tagDocument, true);
		if (!ok) {
			return;
		}

		tagDocument.invalidate();

	}

	@Override
	public void setEnabledAsNeeded() {
		TagDocument tagDocument = getActionFocusSelector().getActiveTagDocument();
		setEnabled(tagDocument != null && !isTagDocumentAMonitorTagDocument(tagDocument));
	}

	public EditTagDescriptionDialog getEditTagDescriptionDialog() {
		return editTagDescriptionDialog;
	}

	public void setEditTagDescriptionDialog(EditTagDescriptionDialog editTagDescriptionDialog) {
		this.editTagDescriptionDialog = editTagDescriptionDialog;
	}

}
