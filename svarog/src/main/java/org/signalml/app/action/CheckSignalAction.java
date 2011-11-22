/* CheckSignalAction.java created 2010-10-24
 *
 */

package org.signalml.app.action;

import static org.signalml.app.SvarogI18n._;
import org.signalml.app.view.monitor.signalchecking.CheckSignalDialog;
import java.awt.event.ActionEvent;
import org.apache.log4j.Logger;
import org.signalml.app.action.selector.SignalDocumentFocusSelector;
import org.signalml.app.document.MonitorSignalDocument;
import org.signalml.app.document.SignalDocument;
import org.signalml.app.model.MontageDescriptor;

/** 
 * Opens a {@link CheckSignalDialog}.
 *
 * @author Tomasz Sawicki
 */
public class CheckSignalAction extends AbstractFocusableSignalMLAction<SignalDocumentFocusSelector> {
       
	/**
	 * Logger to save history of execution at.
	 */
        protected static final Logger logger = Logger.getLogger(CheckSignalAction.class);

        /**
	 * The dialog which is shown after evoking this action.
	 */
        private CheckSignalDialog checkSignalDialog;

	/**
	 * Constructor.
	 *
	 * @param signalDocumentFocusSelector a {@link SignalDocumentFocusSelector} used to detect
	 * which document is active.
	 */
	public CheckSignalAction(SignalDocumentFocusSelector signalDocumentFocusSelector) {

                super(signalDocumentFocusSelector);
		setText(_("Check signal..."));
		setToolTip(_("Check if signal is OK"));
	}

	/**
	 * Shows the dialog and sets it's montage.
	 *
	 * @param ev an event describing a change
	 */
	@Override
	public void actionPerformed(ActionEvent ev) {

		logger.debug("Check signal");
                
                SignalDocument signalDocument = getActionFocusSelector().getActiveSignalDocument();
		if (signalDocument == null) {
			logger.warn("Target document doesn't exist or is not a signal");
			return;
		}

		MontageDescriptor descriptor = new MontageDescriptor(signalDocument.getMontage(), signalDocument);

		boolean ok = checkSignalDialog.showDialog(descriptor, true);
		if (!ok) {
			return;
		}

		signalDocument.setMontage(descriptor.getMontage());
	}

        /**
         * Action is enabled only if the monitor is open.
         */
	@Override
	public void setEnabledAsNeeded() {

                SignalDocument signalDocument = getActionFocusSelector().getActiveSignalDocument();
		setEnabled((signalDocument != null) && (signalDocument instanceof MonitorSignalDocument));
	}

        /**
         * Gets the {@link #checkSignalDialog}.
         *
         * @return {@link #checkSignalDialog}
         */
	public CheckSignalDialog getCheckSignalDialog() {

                return checkSignalDialog;
	}

        /**
         * Sets the {@link #checkSignalDialog}.
         *
         * @param checkSignalDialog a {@link CheckSignalDialog} object
         */
	public void setCheckSignalDialog(CheckSignalDialog checkSignalDialog) {
           
                if( checkSignalDialog == null ) {
			throw new NullPointerException();
		}
		this.checkSignalDialog = checkSignalDialog;
	}
}