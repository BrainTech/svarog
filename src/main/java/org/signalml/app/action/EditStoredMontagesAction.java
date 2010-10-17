/* EditSignalMontageAction.java created 2007-09-28
 *
 */

package org.signalml.app.action;

import java.awt.event.ActionEvent;

import org.apache.log4j.Logger;
import org.signalml.app.model.MontageDescriptor;
import org.signalml.app.view.montage.SignalMontageDialog;
import org.signalml.plugin.export.view.AbstractSignalMLAction;
import org.springframework.context.support.MessageSourceAccessor;

/** EditSignalMontageAction
 *
 *
 * @author Michal Dobaczewski &copy; 2007-2008 CC Otwarte Systemy Komputerowe Sp. z o.o.
 */
public class EditStoredMontagesAction extends AbstractSignalMLAction {

	private static final long serialVersionUID = 1L;

	protected static final Logger logger = Logger.getLogger(EditStoredMontagesAction.class);

	private SignalMontageDialog signalMontageDialog;

	public EditStoredMontagesAction(MessageSourceAccessor messageSource) {
		super(messageSource);
		setText("action.storedMontages");
		setIconPath("org/signalml/app/icon/storedmontages.png");
		setToolTip("action.storedMontagesToolTip");
	}

	@Override
	public void actionPerformed(ActionEvent e) {

		logger.debug("Stored montages");

		MontageDescriptor descriptor = new MontageDescriptor(null,null);

		boolean ok = signalMontageDialog.showDialog(descriptor, true);
		if (!ok) {
			return;
		}

	}


	@Override
	public void setEnabledAsNeeded() {
		setEnabled(true);
	}

	public SignalMontageDialog getSignalMontageDialog() {
		return signalMontageDialog;
	}

	public void setSignalMontageDialog(SignalMontageDialog signalMontageDialog) {
		this.signalMontageDialog = signalMontageDialog;
	}

}
