package org.signalml.app.action.document.monitor;

import static org.signalml.app.util.i18n.SvarogI18n._;

import java.awt.Window;
import java.awt.event.ActionEvent;
import java.io.IOException;
import java.util.logging.Level;

import multiplexer.jmx.client.ConnectException;

import org.apache.log4j.Logger;
import org.signalml.app.document.ManagedDocumentType;
import org.signalml.app.document.MonitorSignalDocument;
import org.signalml.app.model.document.OpenDocumentDescriptor;
import org.signalml.app.model.document.opensignal.OpenMonitorDescriptor;
import org.signalml.app.view.components.dialogs.ErrorsDialog;
import org.signalml.app.view.components.dialogs.OpenMonitorDialog;
import org.signalml.app.view.workspace.ViewerElementManager;
import org.signalml.plugin.export.SignalMLException;
import org.signalml.plugin.export.signal.Document;
import org.signalml.plugin.export.view.AbstractSignalMLAction;

/** 
 * 
 */
public class OpenMonitorAction extends AbstractSignalMLAction {

	private static final long serialVersionUID = 1L;

	protected static final Logger logger = Logger.getLogger(OpenMonitorAction.class);

	private OpenMonitorDialog openMonitorDialog;
	private ViewerElementManager viewerElementManager;

	public OpenMonitorAction(ViewerElementManager viewerElementManager) {
		super();
		this.viewerElementManager = viewerElementManager;
		setText(_("Open monitor..."));
//		setIconPath("org/signalml/app/icon/connect.png");
		setToolTip(_("Open monitor and connect to multiplexer"));
	}

	@Override
	public void actionPerformed(ActionEvent ev) {
	
		logger.debug("Open monitor");

		OpenDocumentDescriptor ofd = new OpenDocumentDescriptor();

		ofd.setType(ManagedDocumentType.MONITOR);
		ofd.setMakeActive(true);

		OpenMonitorDescriptor model = ofd.getOpenSignalDescriptor().getOpenMonitorDescriptor();

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
		if( !ok ) {
			return;
		}

		try {
			viewerElementManager.getDocumentFlowIntegrator().openDocument(ofd);
		} catch(SignalMLException ex) {
			logger.error("Failed to open document", ex);
			ErrorsDialog.showImmediateExceptionDialog((Window) null, ex);
			return;			
		} catch(IOException ex) {
			logger.error("Failed to open document - i/o exception", ex);
			ErrorsDialog.showImmediateExceptionDialog((Window) null, ex);
			return;			
		} catch (ConnectException ex) {
			logger.error("Failed to open document - connection exception", ex);
			ErrorsDialog.showImmediateExceptionDialog((Window) null, ex);
			return;		 
		}

	}
	
	@Override
	public void setEnabledAsNeeded() {
		setEnabled(true);
	}

	public OpenMonitorDialog getOpenMonitorDialog() {
		return openMonitorDialog;
	}

	public void setOpenMonitorDialog(OpenMonitorDialog openMonitorDialog) {
		if( openMonitorDialog == null ) {
			throw new NullPointerException();
		}
		this.openMonitorDialog = openMonitorDialog;
	}

}
