/* OpenSignalAction.java created 2011-03-06
 *
 */
package org.signalml.app.action;

import java.awt.Window;
import java.awt.event.ActionEvent;
import java.io.IOException;
import java.util.logging.Level;

import multiplexer.jmx.client.ConnectException;

import org.apache.log4j.Logger;
import org.signalml.app.document.DocumentFlowIntegrator;
import org.signalml.app.document.ManagedDocumentType;
import org.signalml.app.document.MonitorSignalDocument;
import org.signalml.app.model.OpenDocumentDescriptor;
import org.signalml.app.model.OpenFileSignalDescriptor;
import org.signalml.app.model.OpenMonitorDescriptor;
import org.signalml.app.model.OpenSignalDescriptor;
import org.signalml.app.view.ViewerElementManager;
import org.signalml.app.view.dialog.ErrorsDialog;
import org.signalml.app.view.dialog.OpenMonitorDialog;
import org.signalml.app.view.opensignal.FileOpenSignalMethod;
import org.signalml.app.view.opensignal.OpenSignalAndSetMontageDialog;
import org.signalml.app.view.opensignal.SignalSource;
import org.signalml.domain.montage.Montage;
import org.signalml.domain.signal.SignalType;
import org.signalml.domain.signal.raw.RawSignalDescriptor;
import org.signalml.plugin.export.SignalMLException;
import org.signalml.plugin.export.signal.Document;
import org.signalml.plugin.export.view.AbstractSignalMLAction;

/**
 *
 * @author Piotr Szachewicz
 */
public class OpenSignalAndSetMontageAction extends AbstractSignalMLAction {

	private static final long serialVersionUID = 1L;
	protected static final Logger logger = Logger.getLogger(OpenSignalAndSetMontageAction.class);
	//private OpenMonitorDialog openMonitorDialog;
	private ViewerElementManager viewerElementManager;
	private OpenSignalAndSetMontageDialog openSignalAndSetMontageDialog;

	public OpenSignalAndSetMontageAction(ViewerElementManager viewerElementManager) {
		super(viewerElementManager.getMessageSource());
		this.viewerElementManager = viewerElementManager;
		setText("action.openSignalAndSetMontageLabel");
//		setIconPath("org/signalml/app/icon/connect.png");
		setToolTip("action.openSignalAndSetMontageToolTip");
	}

	@Override
	public void actionPerformed(ActionEvent ev) {

		logger.debug("OpenSignalAndSetMontageAction");

		OpenDocumentDescriptor ofd = new OpenDocumentDescriptor();
		ofd.setMakeActive(true);
		ofd.setType(ManagedDocumentType.SIGNAL);
		/*OpenSignalDescriptor descriptor = new OpenSignalDescriptor();
		descriptor.setSignalSource(SignalSource.FILE);
		OpenFileSignalDescriptor fileDescriptor = new OpenFileSignalDescriptor();
		fileDescriptor.setMethod(FileOpenSignalMethod.RAW);
		descriptor.setOpenFileSignalDescriptor(fileDescriptor);*/ /*Montage montage = SignalType.EEG_10_20.getConfigurer().createMontage(fileDescriptor.getRawSignalDescriptor().getChannelCount());
		descriptor.setMontage(montage);*/ 
		
		OpenSignalDescriptor openSignalDescriptor = ofd.getOpenSignalDescriptor();

		boolean ok = openSignalAndSetMontageDialog.showDialog(openSignalDescriptor, true);
		if (!ok) {
			return;
		}
		
		DocumentFlowIntegrator documentFlowIntegrator = viewerElementManager.getDocumentFlowIntegrator();
		documentFlowIntegrator.maybeOpenDocument(ofd);

		/*OpenDocumentDescriptor ofd = new OpenDocumentDescriptor();

		ofd.setType(ManagedDocumentType.MONITOR);
		ofd.setMakeActive(true);

		OpenMonitorDescriptor model = ofd.getMonitorOptions();

		//stop previous signal recording
		Document document = viewerElementManager.getActionFocusManager().getActiveDocument();
		if (document instanceof MonitorSignalDocument) {
			try {
				((MonitorSignalDocument) document).stopMonitorRecording();
			} catch (IOException ex) {
				java.util.logging.Logger.getLogger(OpenMonitorAction.class.getName()).log(Level.SEVERE, null, ex);
			}
		}

		openMonitorDialog.cancelConnection();
		boolean ok = openMonitorDialog.showDialog(model, true);
		if (!ok) {
			return;
		}

		try {
			viewerElementManager.getDocumentFlowIntegrator().openDocument(ofd);
		} catch (SignalMLException ex) {
			logger.error("Failed to open document", ex);
			ErrorsDialog.showImmediateExceptionDialog((Window) null, ex);
			return;
		} catch (IOException ex) {
			logger.error("Failed to open document - i/o exception", ex);
			ErrorsDialog.showImmediateExceptionDialog((Window) null, ex);
			return;
		} catch (ConnectException ex) {
			logger.error("Failed to open document - connection exception", ex);
			ErrorsDialog.showImmediateExceptionDialog((Window) null, ex);
			return;
		}*/

	}

	@Override
	public void setEnabledAsNeeded() {
		setEnabled(true);
	}

	public void setOpenSignalAndSetMontageDialog(OpenSignalAndSetMontageDialog openSignalAndSetMontageDialog) {
		if (openSignalAndSetMontageDialog == null)
			throw new NullPointerException();
		this.openSignalAndSetMontageDialog = openSignalAndSetMontageDialog;
	}
}
