/* EditSignalMontageAction.java created 2007-09-28
 *
 */

package org.signalml.app.action.montage;

import static org.signalml.app.util.i18n.SvarogI18n._;

import java.awt.event.ActionEvent;

import org.apache.log4j.Logger;
import org.signalml.app.action.AbstractFocusableSignalMLAction;
import org.signalml.app.action.selector.SignalDocumentFocusSelector;
import org.signalml.app.document.SignalDocument;
import org.signalml.app.model.montage.MontageDescriptor;
import org.signalml.app.view.montage.SignalMontageDialog;

/** EditSignalMontageAction
 *
 *
 * @author Michal Dobaczewski &copy; 2007-2008 CC Otwarte Systemy Komputerowe Sp. z o.o.
 */
public class EditSignalMontageAction extends AbstractFocusableSignalMLAction<SignalDocumentFocusSelector> {

	private static final long serialVersionUID = 1L;

	protected static final Logger logger = Logger.getLogger(EditSignalMontageAction.class);

	private SignalMontageDialog signalMontageDialog;

	public EditSignalMontageAction(SignalDocumentFocusSelector signalDocumentFocusSelector) {
		super(signalDocumentFocusSelector);
		setText(_("Signal montage"));
		setIconPath("org/signalml/app/icon/montage.png");
		setToolTip(_("Change signal montage"));
	}

	@Override
	public void actionPerformed(ActionEvent e) {

		logger.debug("Signal montage");

		SignalDocument signalDocument = getActionFocusSelector().getActiveSignalDocument();
		if (signalDocument == null) {
			logger.warn("Target document doesn't exist or is not a signal");
			return;
		}

		MontageDescriptor descriptor = new MontageDescriptor(signalDocument.getMontage(), signalDocument);

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
