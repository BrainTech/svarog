/* OpenDocumentAction.java created 2007-09-10
 * 
 */
package org.signalml.app.action;

import java.awt.Window;
import java.awt.event.ActionEvent;
import java.io.IOException;

import multiplexer.jmx.client.ConnectException;

import org.apache.log4j.Logger;
import org.signalml.app.document.DocumentFlowIntegrator;
import org.signalml.app.model.OpenDocumentDescriptor;
import org.signalml.app.view.dialog.ErrorsDialog;
import org.signalml.app.view.dialog.OpenDocumentDialog;
import org.signalml.exception.SignalMLException;
import org.springframework.context.support.MessageSourceAccessor;

/** OpenDocumentAction
 *
 * 
 * @author Michal Dobaczewski &copy; 2007-2008 CC Otwarte Systemy Komputerowe Sp. z o.o.
 */
public class OpenDocumentAction extends AbstractSignalMLAction {

	private static final long serialVersionUID = 1L;
	
	protected static final Logger logger = Logger.getLogger(OpenDocumentAction.class);
			
	private OpenDocumentDialog openDocumentDialog;
	private DocumentFlowIntegrator documentFlowIntegrator;
	
	public OpenDocumentAction(MessageSourceAccessor messageSource) {
		super(messageSource);
		setText("action.openDocument");
		setIconPath("org/signalml/app/icon/fileopen.png");
		setToolTip("action.openDocumentToolTip");
	}

	@Override
	public void actionPerformed(ActionEvent ev) {
		
		logger.debug("Open document");

		OpenDocumentDescriptor ofd = new OpenDocumentDescriptor();
		ofd.setMakeActive(true);
		
		boolean ok = openDocumentDialog.showDialog(ofd, true);		
		if( !ok ) {
			return;
		}

		try {
			documentFlowIntegrator.openDocument(ofd);
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

	public OpenDocumentDialog getOpenDocumentDialog() {
		return openDocumentDialog;
	}

	public void setOpenDocumentDialog(OpenDocumentDialog openDocumentDialog) {
		if( openDocumentDialog == null ) {
			throw new NullPointerException();
		}
		this.openDocumentDialog = openDocumentDialog;
	}

	public DocumentFlowIntegrator getDocumentFlowIntegrator() {
		return documentFlowIntegrator;
	}

	public void setDocumentFlowIntegrator(DocumentFlowIntegrator documentFlowIntegrator) {
		if( documentFlowIntegrator == null ) {
			throw new NullPointerException();
		}
		this.documentFlowIntegrator = documentFlowIntegrator;
	}
		
}
