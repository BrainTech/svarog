/* MP5MethodConsumer.java created 2007-10-28
 * 
 */

package org.signalml.app.method.mp5;

import java.awt.Window;
import java.io.File;
import java.io.IOException;

import multiplexer.jmx.client.ConnectException;

import org.apache.log4j.Logger;
import org.signalml.app.document.DocumentFlowIntegrator;
import org.signalml.app.document.ManagedDocumentType;
import org.signalml.app.method.ApplicationMethodManager;
import org.signalml.app.method.InitializingMethodResultConsumer;
import org.signalml.app.model.OpenDocumentDescriptor;
import org.signalml.app.view.dialog.ErrorsDialog;
import org.signalml.exception.SignalMLException;
import org.signalml.method.Method;
import org.signalml.method.mp5.MP5Data;
import org.signalml.method.mp5.MP5Result;
import org.signalml.util.FileUtils;

/** MP5MethodConsumer
 *
 * 
 * @author Michal Dobaczewski &copy; 2007-2008 CC Otwarte Systemy Komputerowe Sp. z o.o.
 */
public class MP5MethodConsumer implements InitializingMethodResultConsumer {

	protected static final Logger logger = Logger.getLogger(MP5MethodConsumer.class);

	private MP5ResultDialog resultDialog;
	
	private DocumentFlowIntegrator documentFlowIntegrator;
	private Window dialogParent;
		
	@Override
	public void initialize(ApplicationMethodManager manager) {

		dialogParent = manager.getDialogParent();
		documentFlowIntegrator = manager.getDocumentFlowIntegrator();	
		
		resultDialog = new MP5ResultDialog(manager.getMessageSource(), dialogParent, true);
		resultDialog.setFileChooser(manager.getFileChooser());
		
	}

	@Override
	public boolean consumeResult(Method method, Object methodData, Object methodResult) throws SignalMLException {

		String bookFilePath = null;
		
		MP5Result result = (MP5Result) methodResult;
		if( result != null ) {
			bookFilePath = result.getBookFilePath();
		}
		
		if( bookFilePath == null ) {
			// for deserialized tasks
			MP5Data data = (MP5Data) methodData;
			if( data != null ) {
				bookFilePath = data.getBookFilePath();
			}
		}
		
		if( bookFilePath == null ) {
			throw new SignalMLException("No result book");
		}
		
		MP5ResultTargetDescriptor descriptor = new MP5ResultTargetDescriptor();
		descriptor.setOpenInWindow(true);
		descriptor.setSaveToFile(true);
		
		boolean dialogOk = resultDialog.showDialog(descriptor, true);
		if( !dialogOk ) {
			return false;
		}
		
		if( descriptor.isSaveToFile() ) {
			
			try {
				FileUtils.copyFile(new File(bookFilePath), descriptor.getBookFile());
			} catch (IOException ex) {
				logger.error( "Failed to copy file [" + bookFilePath + " to " + descriptor.getBookFile() + "] - i/o exception", ex );
				throw new SignalMLException(ex);
			}
			
		}
		
		if( descriptor.isOpenInWindow() ) {

			OpenDocumentDescriptor odd = new OpenDocumentDescriptor();
			odd.setMakeActive(true);
			odd.setFile(new File(bookFilePath));
			odd.setMakeActive(true);
			odd.setType(ManagedDocumentType.BOOK);
			
			try {
				documentFlowIntegrator.openDocument(odd);
			} catch(SignalMLException ex) {
				logger.error("Failed to open document", ex);
				ErrorsDialog.showImmediateExceptionDialog((Window) null, ex);
				return false;			
			} catch(IOException ex) {
				logger.error("Failed to open document - i/o exception", ex);
				ErrorsDialog.showImmediateExceptionDialog((Window) null, ex);
				return false;			
			} catch (ConnectException ex) {
				logger.error("Failed to open document - connection exception", ex);
				ErrorsDialog.showImmediateExceptionDialog(dialogParent, ex);
				return false;		   
			}
			
		}
		
		return true;
				
	}

}
