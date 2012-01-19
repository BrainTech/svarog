/* EEGLabExportAction.java created 2010-11-29
 *
 */

package org.signalml.app.action.document;

import static org.signalml.app.util.i18n.SvarogI18n._;

import java.awt.event.ActionEvent;

import org.signalml.app.action.AbstractFocusableSignalMLAction;
import org.signalml.app.action.document.monitor.CheckSignalAction;
import org.signalml.app.action.selector.SignalDocumentFocusSelector;
import org.signalml.app.document.MonitorSignalDocument;
import org.signalml.app.document.SignalDocument;
import org.signalml.app.model.montage.MontageDescriptor;
import org.signalml.app.view.components.dialogs.EEGLabExportDialog;
import org.apache.log4j.Logger;

/**
 * Opens a {@link EEGLabExportDialog}.
 *
 * @author Tomasz Sawicki
 */
public class EEGLabExportAction extends AbstractFocusableSignalMLAction<SignalDocumentFocusSelector> {

	/**
	 * Logger to save history of execution at.
	 */
        protected static final Logger logger = Logger.getLogger(CheckSignalAction.class);

        /**
	 * The dialog which is shown after evoking this action.
	 */
        private EEGLabExportDialog eeglabExportDialog;

	/**
	 * Constructor.
	 *
	 * @param signalDocumentFocusSelector a {@link SignalDocumentFocusSelector} used to detect
	 * which document is active.
	 */
        public EEGLabExportAction(SignalDocumentFocusSelector signalDocumentFocusSelector) {

                super(signalDocumentFocusSelector);
		setText(_("Export to EEGLab..."));
		setToolTip(_("Export signal in EEGLab format"));
	}

	/**
	 * Shows the dialog and sets it's montage.
	 *
	 * @param ev an event describing a change
	 */
        @Override
        public void actionPerformed(ActionEvent e) {

                logger.debug("EEGLab Export");

                SignalDocument signalDocument = getActionFocusSelector().getActiveSignalDocument();
		if (signalDocument == null) {
			logger.warn("Target document doesn't exist or is not a signal");
			return;
		}

		MontageDescriptor descriptor = new MontageDescriptor(signalDocument.getMontage(), signalDocument);

                boolean ok = eeglabExportDialog.showDialog(descriptor, true);
                if (!ok) {
                        return;
                }

        }

        /**
         * Action is enabled only if an offline document is open.
         */
	@Override
	public void setEnabledAsNeeded() {
		SignalDocumentFocusSelector x = getActionFocusSelector();
		if (null != x) {
			SignalDocument signalDocument = x.getActiveSignalDocument();
			setEnabled((signalDocument != null) && !(signalDocument instanceof MonitorSignalDocument));
		}
	}

        /**
         * Gets the {@link #eeglabExportDialog}.
         *
         * @return the {@link #eeglabExportDialog}
         */
        public EEGLabExportDialog getEEGLabExportDialog() {

                return eeglabExportDialog;
        }

        /**
         * Sets the {@link #eeglabExportDialog}
         *
         * @param eeglabExportDialog an {@link EEGLabExportDialog} object
         */
        public void setEEGLabExportDialog(EEGLabExportDialog eeglabExportDialog) {

                if(eeglabExportDialog == null) {
			throw new NullPointerException();
		}
                this.eeglabExportDialog = eeglabExportDialog;
        }
}
