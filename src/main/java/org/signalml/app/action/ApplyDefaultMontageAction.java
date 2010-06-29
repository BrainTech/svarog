/* ApplyDefaultMontageAction.java created 2007-11-24
 *
 */

package org.signalml.app.action;

import java.awt.event.ActionEvent;

import org.apache.log4j.Logger;
import org.signalml.app.action.selector.MontageFocusSelector;
import org.signalml.app.document.SignalDocument;
import org.signalml.app.view.dialog.OptionPane;
import org.signalml.domain.montage.Montage;
import org.springframework.context.support.MessageSourceAccessor;

/** ApplyDefaultMontageAction
 *
 *
 * @author Michal Dobaczewski &copy; 2007-2008 CC Otwarte Systemy Komputerowe Sp. z o.o.
 */
public class ApplyDefaultMontageAction extends AbstractFocusableSignalMLAction<MontageFocusSelector> {

	private static final long serialVersionUID = 1L;

	protected static final Logger logger = Logger.getLogger(ApplyDefaultMontageAction.class);

	public ApplyDefaultMontageAction(MessageSourceAccessor messageSource, MontageFocusSelector montageFocusSelector) {
		super(messageSource, montageFocusSelector);
		setText("action.applyDefaultMontage");
		setIconPath("org/signalml/app/icon/defaultmontage.png");
		setToolTip("action.applyDefaultMontageToolTip");
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
