package org.signalml.app.action.document;

import static org.signalml.app.util.i18n.SvarogI18n._;

import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.IOException;

import org.signalml.app.document.DocumentFlowIntegrator;
import org.signalml.app.document.MonitorSignalDocument;
import org.signalml.app.document.signal.SignalDocument;
import org.signalml.app.model.document.OpenDocumentDescriptor;
import org.signalml.app.view.document.opensignal.OpenSignalWizardDialog;
import org.signalml.app.worker.monitor.MonitorWorker;
import org.signalml.plugin.export.SignalMLException;
import org.signalml.plugin.export.view.AbstractSignalMLAction;

public class OpenSignalWizardAction extends AbstractSignalMLAction implements PropertyChangeListener {

	private DocumentFlowIntegrator documentFlowIntegrator;
	private OpenSignalWizardDialog openSignalWizardDialog;
	private OpenDocumentDescriptor openDocumentDescriptor;

	private SignalDocument signalDocument;

	/**
	 * Constructor.
	 * @param viewerElementManager ViewerElementManager to be used by
	 * this action.
	 */
	public OpenSignalWizardAction(DocumentFlowIntegrator documentFlowIntegrator) {
		super();
		this.documentFlowIntegrator = documentFlowIntegrator;
		setText(_("Open signal"));
		setIconPath("org/signalml/app/icon/fileopen.png");
		setToolTip(_("Open signal and set montage for it"));
		setMnemonic(KeyEvent.VK_O);
	}

	public void setOpenSignalWizardDialog(OpenSignalWizardDialog openSignalWizardDialog) {
		this.openSignalWizardDialog = openSignalWizardDialog;
	}

	@Override
	public void actionPerformed(ActionEvent arg0) {
		openDocumentDescriptor = new OpenDocumentDescriptor();

		boolean ok = openSignalWizardDialog.showDialog(openDocumentDescriptor, true);
		if (!ok) {
			return;
		}
		openDocumentDescriptor.getOpenSignalDescriptor();
		signalDocument = (SignalDocument) documentFlowIntegrator.maybeOpenDocument(openDocumentDescriptor);

		if (signalDocument instanceof MonitorSignalDocument) {
			MonitorSignalDocument monitorSignalDocument = (MonitorSignalDocument) signalDocument;
			MonitorWorker monitorWorker = monitorSignalDocument.getMonitorWorker();
			monitorWorker.addPropertyChangeListener(this);
		}

	}

	@Override
	public void propertyChange(PropertyChangeEvent evt) {
		if (signalDocument != null && MonitorWorker.OPENING_MONITOR_CANCELLED.equals(evt.getPropertyName())) {
			try {
				documentFlowIntegrator.closeDocument(signalDocument, true, true);
			} catch (IOException e) {
				e.printStackTrace();
			} catch (SignalMLException e) {
				e.printStackTrace();
			}
		}
	}

}
