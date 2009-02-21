/* ExportBookAction.java created 2008-01-27
 * 
 */
package org.signalml.app.action;

import java.awt.Component;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.io.File;
import java.util.concurrent.ExecutionException;

import org.apache.log4j.Logger;
import org.signalml.app.action.selector.BookDocumentFocusSelector;
import org.signalml.app.document.BookDocument;
import org.signalml.app.document.FileBackedDocument;
import org.signalml.app.view.ViewerFileChooser;
import org.signalml.app.view.dialog.ErrorsDialog;
import org.signalml.app.view.dialog.OptionPane;
import org.signalml.app.view.dialog.PleaseWaitDialog;
import org.signalml.app.worker.ExportBookWorker;
import org.signalml.domain.book.StandardBook;
import org.signalml.util.Util;
import org.springframework.context.support.MessageSourceAccessor;

/** ExportBookAction
 *
 * 
 * @author Michal Dobaczewski &copy; 2007-2008 CC Otwarte Systemy Komputerowe Sp. z o.o.
 */
public class ExportBookAction extends AbstractFocusableSignalMLAction<BookDocumentFocusSelector> {

	private static final long serialVersionUID = 1L;
	
	protected static final Logger logger = Logger.getLogger(ExportBookAction.class);
		
	private PleaseWaitDialog pleaseWaitDialog;
	private ViewerFileChooser fileChooser;
	private Component optionPaneParent;
		
	public ExportBookAction(MessageSourceAccessor messageSource, BookDocumentFocusSelector bookDocumentFocusSelector) {
		super(messageSource, bookDocumentFocusSelector);
		setText("action.exportBook");
		setToolTip("action.exportBookToolTip");
	}
		
	@Override
	public void actionPerformed(ActionEvent ev) {
		
		logger.debug("Export book");
		
		BookDocument bookDocument = getActionFocusSelector().getActiveBookDocument();
		if( bookDocument == null ) {
			logger.warn("Target document doesn't exist or is not a book");
			return;
		}
				
		File fileSuggestion = null;
		if( bookDocument instanceof FileBackedDocument ) {
			File originalFile = ((FileBackedDocument) bookDocument).getBackingFile();
			if( originalFile != null ) {
				fileSuggestion = new File( "export_" + originalFile.getName() );
			}
		}
		
		File file;
		boolean hasFile = false;
		do {
			
			file = fileChooser.chooseExportBookFile(optionPaneParent, fileSuggestion);
			if( file == null ) {
				return;
			}
			String ext = Util.getFileExtension(file,false);
			if( ext == null ) {
				file = new File( file.getAbsolutePath() + ".b" );
			}
			
			hasFile = true;
			
			if( file.exists() ) {
				int res = OptionPane.showFileAlreadyExists(optionPaneParent, file.getName());
				if( res != OptionPane.OK_OPTION ) {
					hasFile = false;
				}								
			}
						
		} while( !hasFile );
						
		
		StandardBook book = bookDocument.getBook();
				
		ExportBookWorker worker = new ExportBookWorker(book, file, pleaseWaitDialog);
		
		worker.execute();

		pleaseWaitDialog.setActivity(messageSource.getMessage("activity.exportingBook"));			
		pleaseWaitDialog.configureForDeterminate(0, book.getSegmentCount(), 0);
		pleaseWaitDialog.waitAndShowDialogIn(optionPaneParent, 500, worker);
		
		try {
			worker.get();
		} catch (InterruptedException ex) {
			// ignore
		} catch (ExecutionException ex) {
			logger.error( "Worker failed to save", ex.getCause() );
			ErrorsDialog.showImmediateExceptionDialog((Window) null, ex.getCause());
			return;			
		}
						
	}

	@Override
	public void setEnabledAsNeeded() {
		setEnabled( getActionFocusSelector().getActiveBookDocument() != null ); 
	}

	public PleaseWaitDialog getPleaseWaitDialog() {
		return pleaseWaitDialog;
	}

	public void setPleaseWaitDialog(PleaseWaitDialog pleaseWaitDialog) {
		this.pleaseWaitDialog = pleaseWaitDialog;
	}

	public ViewerFileChooser getFileChooser() {
		return fileChooser;
	}

	public void setFileChooser(ViewerFileChooser fileChooser) {
		this.fileChooser = fileChooser;
	}

	public Component getOptionPaneParent() {
		return optionPaneParent;
	}

	public void setOptionPaneParent(Component optionPaneParent) {
		this.optionPaneParent = optionPaneParent;
	}
	
}
