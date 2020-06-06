package org.signalml.app.action.signal;

import static org.signalml.app.util.i18n.SvarogI18n._;

import java.awt.event.ActionEvent;
import javax.swing.JToggleButton;

import org.apache.log4j.Logger;
import org.signalml.app.action.AbstractFocusableSignalMLAction;
import org.signalml.app.action.selector.SignalDocumentFocusSelector;
import org.signalml.app.document.signal.SignalDocument;
import org.signalml.domain.montage.Montage;

/**
 * SignalFilterSwitchAction
 *
 * @author Michal Dobaczewski &copy; 2007-2008 CC Otwarte Systemy Komputerowe Sp. z o.o.
 */
public class SignalFilterSwitchAction extends AbstractFocusableSignalMLAction<SignalDocumentFocusSelector> {

	private static final long serialVersionUID = 1L;

	protected static final Logger logger = Logger.getLogger(SignalFilterSwitchAction.class);

	public SignalFilterSwitchAction(SignalDocumentFocusSelector signalDocumentFocusSelector) {
		super(signalDocumentFocusSelector);
		setText(_("Toggle filtering"));
		setIconPath("org/signalml/app/icon/filter.png");
		setToolTip(_("Switch filtering on/off"));
	}

	@Override
	public void actionPerformed(ActionEvent ev) {

		logger.debug("Filter switch");

		SignalDocument signalDocument = getActionFocusSelector().getActiveSignalDocument();
		if (signalDocument == null) {
			logger.warn("Target document doesn't exist or is not a signal");
			return;
		}
		
		JToggleButton button = signalDocument.getSignalView().getFilterSwitchButton();
		boolean selected = button.isSelected();
		if (ev.getSource() != button) {
			// toggled by top menu item
			selected = !selected;
		}

		Montage montage = signalDocument.getMontage();
		if (selected != montage.isFilteringEnabled()) {
			montage = new Montage(montage);
			montage.setFilteringEnabled(selected);
			signalDocument.setMontage(montage);
		}

	}

	@Override
	public void setEnabledAsNeeded() {
		setEnabled(getActionFocusSelector().getActiveSignalDocument() != null);
	}

}
