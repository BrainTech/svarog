package org.signalml.app.action.signal;

import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import org.apache.log4j.Logger;
import org.signalml.app.action.AbstractFocusableSignalMLAction;
import org.signalml.app.action.selector.SignalDocumentFocusSelector;
import org.signalml.app.document.signal.SignalDocument;
import static org.signalml.app.util.i18n.SvarogI18n._;
import org.signalml.app.view.signal.SignalView;
import org.signalml.app.view.signal.popup.SignalPlotOptionsPopupDialog;

/**
 * Action for "Plot options" menu item.
 * 
 * @author ptr@mimuw.edu.pl
 */
public class EditPlotOptionsAction extends AbstractFocusableSignalMLAction<SignalDocumentFocusSelector> {

	private static final long serialVersionUID = 1L;

	protected static final Logger logger = Logger.getLogger(EditPlotOptionsAction.class);

	public EditPlotOptionsAction(SignalDocumentFocusSelector signalDocumentFocusSelector, boolean withText) {
		super(signalDocumentFocusSelector);
		if (withText) {
			setText(_("Plot options"));
		}
		setIconPath("org/signalml/app/icon/plotoptions.png");
		setToolTip(_("Change plot options"));
		setMnemonic(KeyEvent.VK_P);
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		SignalDocument signalDocument = getActionFocusSelector().getActiveSignalDocument();
		if (signalDocument != null) {
			showPlotOptionsDialog(signalDocument.getSignalView());
		}
	}

	@Override
	public void setEnabledAsNeeded() {
		setEnabled(getActionFocusSelector().getActiveSignalDocument() != null);
	}
	
	private void showPlotOptionsDialog(SignalView signalView) {
		SignalPlotOptionsPopupDialog dialog =
				new SignalPlotOptionsPopupDialog((Window) signalView.getTopLevelAncestor(), true);
		dialog.setSignalView(signalView);
		dialog.initializeNow();
		dialog.showDialog(null, true);
	}
}
