/* ChooseActiveTagAction.java created 2010-12-10
 *
 */

package org.signalml.app.action;

import java.awt.event.ActionEvent;
import org.signalml.app.action.selector.SignalDocumentFocusSelector;
import org.signalml.app.document.SignalDocument;
import org.signalml.app.view.signal.SignalView;
import org.signalml.app.view.signal.popup.CompareTagsPopupDialog;
import org.signalml.app.view.tag.comparison.TagComparisonDialog;
import org.signalml.plugin.export.view.DocumentView;
import org.springframework.context.support.MessageSourceAccessor;

/**
 * This class is responsible for action evoked when the user wants to compare
 * two tags (for example: the user selects an appropriate menu item in
 * the main menu).
 *
 * @author Piotr Szachewicz
 */
public class CompareTagsAction extends AbstractFocusableSignalMLAction<SignalDocumentFocusSelector> {

	/**
	 * A dialog for choosing which tags to compare.
	 */
	private CompareTagsPopupDialog compareTagsPopupDialog;

	/**
	 * A dialog for showing the results of comparison.
	 */
	private TagComparisonDialog tagComparisonDialog;

	/**
	 * Constructor.
	 * @param messageSource the message source accessor capable of resolving
	 * localized message codes
	 * @param signalDocumentFocusSelector a {@link SignalDocumentFocusSelector}
	 * used to get the active document.
	 */
	public CompareTagsAction(MessageSourceAccessor messageSource, SignalDocumentFocusSelector signalDocumentFocusSelector) {
		super(messageSource, signalDocumentFocusSelector);

		setText("action.compareTags");
		setIconPath("org/signalml/app/icon/comparetags.png");
		setToolTip("action.compareTagsToolTip");

	}

	@Override
	public void actionPerformed(ActionEvent e) {

		DocumentView documentView = getActionFocusSelector().getActiveSignalDocument().getDocumentView();
		SignalView signalView = null;
		if (documentView instanceof SignalView)
			signalView = (SignalView) documentView;

		CompareTagsPopupDialog dialog = getCompareTagsPopupDialog();
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

	/**
	 * Returns a dialog for choosing which tags to compare.
	 * @return a dialog for choosing which tags to compare
	 */
	protected CompareTagsPopupDialog getCompareTagsPopupDialog() {
		if (compareTagsPopupDialog == null) {
			compareTagsPopupDialog = new CompareTagsPopupDialog(messageSource, null, true);
			compareTagsPopupDialog.setTagComparisonDialog(tagComparisonDialog);
		}
		return compareTagsPopupDialog;
	}

}