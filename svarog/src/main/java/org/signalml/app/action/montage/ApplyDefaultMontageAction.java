/* ApplyDefaultMontageAction.java created 2007-11-24
 *
 */

package org.signalml.app.action.montage;

import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import org.apache.log4j.Logger;
import org.signalml.app.action.AbstractFocusableSignalMLAction;
import org.signalml.app.action.selector.MontageFocusSelector;
import org.signalml.app.document.signal.SignalDocument;
import static org.signalml.app.util.i18n.SvarogI18n._;
import org.signalml.app.view.common.dialogs.OptionPane;
import org.signalml.domain.montage.Montage;

/** ApplyDefaultMontageAction
 *
 *
 * @author Michal Dobaczewski &copy; 2007-2008 CC Otwarte Systemy Komputerowe Sp. z o.o.
 */
public class ApplyDefaultMontageAction extends AbstractFocusableSignalMLAction<MontageFocusSelector> {

	private static final long serialVersionUID = 1L;

	protected static final Logger logger = Logger.getLogger(ApplyDefaultMontageAction.class);

	public ApplyDefaultMontageAction(MontageFocusSelector montageFocusSelector) {
		super(montageFocusSelector);
		setText(_("Default montage"));
		setIconPath("org/signalml/app/icon/defaultmontage.png");
		setToolTip(_("Apply default montage"));
		setMnemonic(KeyEvent.VK_D);
	}

	@Override
	public void actionPerformed(ActionEvent e) {

		logger.debug("Apply default montage");

		MontageFocusSelector montageSelector = getActionFocusSelector();

		SignalDocument signalDocument = montageSelector.getActiveSignalDocument();
		if (signalDocument == null) {
			logger.warn("Target document doesn't exist or is not a signal");
			return;
		}
		Montage montage = montageSelector.getActiveMontage();
		if (montage == null) {
			return;
		}

		if (montage.isCompatible(signalDocument)) {
			signalDocument.setMontage(montage);
		} else {
			OptionPane.showDefaultMontageNotCompatible(null);
		}

	}

	@Override
	public void setEnabledAsNeeded() {
		MontageFocusSelector montageSelector = getActionFocusSelector();
		setEnabled(montageSelector.getActiveSignalDocument() != null && montageSelector.getActiveMontage() != null);
	}

}
