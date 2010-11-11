package org.signalml.app.action;

import java.awt.Window;
import java.awt.event.ActionEvent;
import java.io.IOException;

import multiplexer.jmx.client.ConnectException;

import org.apache.log4j.Logger;
import org.signalml.app.document.ManagedDocumentType;
import org.signalml.app.model.OpenDocumentDescriptor;
import org.signalml.app.model.OpenMonitorDescriptor;
import org.signalml.app.view.ViewerElementManager;
import org.signalml.app.view.dialog.ErrorsDialog;
import org.signalml.app.view.dialog.OpenMonitorDialog;
import org.signalml.plugin.export.SignalMLException;
import org.signalml.plugin.export.view.AbstractSignalMLAction;

/** 
 * 
 */
public class OpenMonitorAction extends AbstractSignalMLAction {

	private static final long serialVersionUID = 1L;

	protected static final Logger logger = Logger.getLogger(OpenMonitorAction.class);

	private OpenMonitorDialog openMonitorDialog;
	private ViewerElementManager viewerElementManager;

	public OpenMonitorAction( ViewerElementManager viewerElementManager) {
		super(viewerElementManager.getMessageSource());
		this.viewerElementManager = viewerElementManager;
		setText( "action.openMonitorLabel");
//		setIconPath("org/signalml/app/icon/connect.png");
		setToolTip( "action.openMonitorToolTip");
	}

	@Override
	public void actionPerformed(ActionEvent ev) {
	
		logger.debug("Open monitor");

		OpenDocumentDescriptor ofd = new OpenDocumentDescriptor();

		ofd.setType( ManagedDocumentType.MONITOR);
		ofd.setMakeActive(true);

		OpenMonitorDescriptor model = ofd.getMonitorOptions();
		
		openMonitorDialog.cancelConnection();
		boolean ok = openMonitorDialog.showDialog(model, true);
		if( !ok ) {
			return;
		}

		try {
			viewerElementManager.getDocumentFlowIntegrator().openDocument( ofd);
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
