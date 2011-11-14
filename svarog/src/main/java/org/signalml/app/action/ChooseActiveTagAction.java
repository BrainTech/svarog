/* ChooseActiveTagAction.java created 2010-12-10
 *
 */

package org.signalml.app.action;

import static org.signalml.app.SvarogApplication._;
import java.awt.event.ActionEvent;
import org.signalml.app.action.selector.SignalDocumentFocusSelector;
import org.signalml.app.document.SignalDocument;
import org.signalml.app.view.signal.SignalView;
import org.signalml.app.view.signal.popup.ActiveTagPopupDialog;
import org.signalml.plugin.export.view.DocumentView;

/**
 * This class is responsible for action evoked when the user wants to change
 * the active tag (for example: the user selects an appropriate menu item in
 * the main menu). It shows a dialog in which active tag can be chosen.
 *
 * @author Piotr Szachewicz
 */
public class ChooseActiveTagAction extends AbstractFocusableSignalMLAction<SignalDocumentFocusSelector> {

	/**
	 * Constructor.
	 * localized message codes
	 * @param signalDocumentFocusSelector a {@link SignalDocumentFocusSelector}
	 * used to get the active document.
	 */
	public ChooseActiveTagAction( SignalDocumentFocusSelector signalDocumentFocusSelector) {
		super( signalDocumentFocusSelector);

		setText(_("Choose active tag"));
		setIconPath("org/signalml/app/icon/activetag.png");
		setToolTip(_("Change active tag (the tag that is being edited)"));
	}

	@Override
	public void actionPerformed(ActionEvent e) {

		DocumentView documentView = getActionFocusSelector().getActiveSignalDocument().getDocumentView();
		SignalView signalView = null;
		if (documentView instanceof SignalView)
			signalView = (SignalView) documentView;

		ActiveTagPopupDialog dialog = new ActiveTagPopupDialog( null, true);
		dialog.setSignalView(signalView);
		dialog.showDialog(null, true);

	}

	@Override
	public void setEnabledAsNeeded() {

		SignalDocument signalDocument = getActionFocusSelector().getActiveSignalDocument();
		
		if (signalDocument != null && signalDocument.getActiveTag() != null)
			setEnabled(true);
		else
			setEnabled(false);

	}

}
