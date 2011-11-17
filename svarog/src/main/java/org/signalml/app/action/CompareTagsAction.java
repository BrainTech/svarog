/* ChooseActiveTagAction.java created 2010-12-10
 *
 */

package org.signalml.app.action;

import static org.signalml.app.SvarogI18n._;
import java.awt.event.ActionEvent;
import org.signalml.app.action.selector.SignalDocumentFocusSelector;
import org.signalml.app.document.SignalDocument;
import org.signalml.app.view.signal.SignalView;
import org.signalml.app.view.signal.popup.CompareTagsPopupDialog;
import org.signalml.app.view.tag.comparison.TagComparisonDialog;
import org.signalml.plugin.export.view.DocumentView;

/**
 * This class is responsible for action evoked when the user wants to compare
 * two tags (for example: the user selects an appropriate menu item in
 * the main menu).
 *
 * @author Piotr Szachewicz
 */
public class CompareTagsAction extends AbstractFocusableSignalMLAction<SignalDocumentFocusSelector> {

	/**
	 * A dialog for showing the results of comparison.
	 */
	private TagComparisonDialog tagComparisonDialog;

	/**
	 * Constructor.
	 * localized message codes
	 * @param signalDocumentFocusSelector a {@link SignalDocumentFocusSelector}
	 * used to get the active document.
	 */
	public CompareTagsAction(SignalDocumentFocusSelector signalDocumentFocusSelector) {
		super(signalDocumentFocusSelector);

		setText(_("Compare tags"));
		setIconPath("org/signalml/app/icon/comparetags.png");
		setToolTip(_("Compare tags (2 or more must be open)"));

	}

	@Override
	public void actionPerformed(ActionEvent e) {

		DocumentView documentView = getActionFocusSelector().getActiveSignalDocument().getDocumentView();
		SignalView signalView = null;
		if (documentView instanceof SignalView)
			signalView = (SignalView) documentView;

		CompareTagsPopupDialog dialog = new CompareTagsPopupDialog(null, true);
		dialog.setTagComparisonDialog(tagComparisonDialog);
		dialog.setSignalView(signalView);
		dialog.showDialog(null, true);

	}

	@Override
	public void setEnabledAsNeeded() {

		SignalDocument signalDocument = getActionFocusSelector().getActiveSignalDocument();

		if (signalDocument != null && signalDocument.getTagDocuments().size() >= 2)
			setEnabled(true);
		else
			setEnabled(false);

	}

	/**
	 * Returns the {@link TagComparisonDialog} used by this action.
	 * @return the {@link TagComparisonDialog} used by this action
	 */
	public TagComparisonDialog getTagComparisonDialog() {
		return tagComparisonDialog;
	}

	/**
	 * Sets the {@link TagComparisonDialog} to be used when evoking this
	 * action.
	 * @param tagComparisonDialog a {@link TagComparisonDialog} to be used
	 * by this action
	 */
	public void setTagComparisonDialog(TagComparisonDialog tagComparisonDialog) {
		this.tagComparisonDialog = tagComparisonDialog;
	}

}
