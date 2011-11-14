/* EditTagAnnotationAction.java created 2007-10-23
 *
 */

package org.signalml.app.action;

import static org.signalml.app.SvarogApplication._;
import java.awt.event.ActionEvent;

import org.apache.log4j.Logger;
import org.signalml.app.action.selector.TagFocusSelector;
import org.signalml.app.document.SignalDocument;
import org.signalml.app.document.TagDocument;
import org.signalml.app.view.dialog.EditTagAnnotationDialog;
import org.signalml.app.view.signal.PositionedTag;
import org.signalml.domain.tag.StyledTagSet;

/** EditTagAnnotationAction
 *
 *
 * @author Michal Dobaczewski &copy; 2007-2008 CC Otwarte Systemy Komputerowe Sp. z o.o.
 */
public class EditTagAnnotationAction extends AbstractFocusableSignalMLAction<TagFocusSelector> {

	private static final long serialVersionUID = 1L;

	protected static final Logger logger = Logger.getLogger(EditTagAnnotationAction.class);

	private EditTagAnnotationDialog editTagAnnotationDialog;

	public EditTagAnnotationAction(TagFocusSelector tagFocusSelector) {
		super(tagFocusSelector);
		setText(_("Annotate tag"));
		setToolTip(_("Edit tag annotation (Ctrl-A)"));
		setIconPath("org/signalml/app/icon/editannotation.png");
	}

	@Override
	public void actionPerformed(ActionEvent e) {

		TagFocusSelector tagFocusSelector = getActionFocusSelector();

		SignalDocument signalDocument = tagFocusSelector.getActiveSignalDocument();
		if (signalDocument == null) {
			return;
		}

		PositionedTag positionedTag = tagFocusSelector.getActiveTag();
		if (positionedTag == null) {
			logger.warn("Target tag doesn't exist");
			return;
		}

		TagDocument tagDocument = signalDocument.getTagDocuments().get(positionedTag.getTagPositionIndex());

		boolean ok = editTagAnnotationDialog.showDialog(positionedTag.getTag(), true);
		if (!ok) {
			return;
		}

		StyledTagSet tagSet = tagDocument.getTagSet();
		tagSet.editTag(positionedTag.getTag());
		tagDocument.invalidate();

	}

	@Override
	public void setEnabledAsNeeded() {
		setEnabled(getActionFocusSelector().getActiveTag() != null);
	}

	public EditTagAnnotationDialog getEditTagAnnotationDialog() {
		return editTagAnnotationDialog;
	}

	public void setEditTagAnnotationDialog(EditTagAnnotationDialog editTagAnnotationDialog) {
		this.editTagAnnotationDialog = editTagAnnotationDialog;
	}

}
