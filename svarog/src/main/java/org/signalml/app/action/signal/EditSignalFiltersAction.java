package org.signalml.app.action.signal;

import java.awt.event.ActionEvent;
import org.apache.log4j.Logger;
import org.signalml.app.action.AbstractFocusableSignalMLAction;
import org.signalml.app.action.selector.SignalDocumentFocusSelector;
import org.signalml.app.document.signal.SignalDocument;
import org.signalml.app.model.montage.MontageDescriptor;
import static org.signalml.app.util.i18n.SvarogI18n._;
import org.signalml.app.view.montage.SignalMontageDialog;

/**
 * EditSignalFiltersAction (based on EditSignalMontageAction).
 *
 * @author ptr@mimuw.edu.pl
 */
public class EditSignalFiltersAction extends AbstractFocusableSignalMLAction<SignalDocumentFocusSelector> {

	private static final long serialVersionUID = 1L;

	protected static final Logger logger = Logger.getLogger(EditSignalFiltersAction.class);

	private SignalMontageDialog signalMontageDialog;

	public EditSignalFiltersAction(SignalDocumentFocusSelector signalDocumentFocusSelector) {
		super(signalDocumentFocusSelector);
		setText(_("Edit filters"));
		setIconPath("org/signalml/app/icon/editfilter.png");
		setToolTip(_("Change signal filtering"));
	}

	@Override
	public void actionPerformed(ActionEvent e) {

		SignalDocument signalDocument = getActionFocusSelector().getActiveSignalDocument();
		if (signalDocument == null) {
			logger.warn("Target document doesn't exist or is not a signal");
			return;
		}

		MontageDescriptor descriptor = new MontageDescriptor(signalDocument.getMontage(), signalDocument);

		signalMontageDialog.getSignalMontagePanel().activateFiltersPane();
		boolean ok = signalMontageDialog.showDialog(descriptor, true);
		if (!ok) {
			return;
		}

		signalDocument.setMontage(descriptor.getMontage());

	}

	@Override
	public void setEnabledAsNeeded() {
		setEnabled(getActionFocusSelector().getActiveSignalDocument() != null);
	}

	public SignalMontageDialog getSignalMontageDialog() {
		return signalMontageDialog;
	}

	public void setSignalMontageDialog(SignalMontageDialog signalMontageDialog) {
		this.signalMontageDialog = signalMontageDialog;
	}

}
