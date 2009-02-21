/* ArtifactMethodIterationConsumer.java created 2007-12-06
 * 
 */

package org.signalml.app.method.artifact;

import java.awt.Window;
import java.io.IOException;
import java.util.concurrent.ExecutionException;

import org.apache.log4j.Logger;
import org.signalml.app.document.TagDocument;
import org.signalml.app.method.ApplicationMethodManager;
import org.signalml.app.method.MethodIterationResultConsumer;
import org.signalml.app.view.dialog.ErrorsDialog;
import org.signalml.app.view.dialog.PleaseWaitDialog;
import org.signalml.app.view.roc.RocDialog;
import org.signalml.domain.roc.RocData;
import org.signalml.domain.tag.LegacyTagImporter;
import org.signalml.exception.SignalMLException;
import org.signalml.method.iterator.IterableMethod;
import org.signalml.method.iterator.MethodIteratorData;
import org.signalml.method.iterator.MethodIteratorResult;
import org.springframework.context.support.MessageSourceAccessor;

/** ArtifactMethodIterationConsumer
 *
 * 
 * @author Michal Dobaczewski &copy; 2007-2008 CC Otwarte Systemy Komputerowe Sp. z o.o.
 */
public class ArtifactMethodIterationConsumer implements MethodIterationResultConsumer {

	protected static final Logger logger = Logger.getLogger(ArtifactMethodIterationConsumer.class);
	
	private ApplicationMethodManager methodManager;
	
	private MessageSourceAccessor messageSource;
	
	private ArtifactExpertTagDialog expertTagDialog;
	private RocDialog rocDialog;
	private PleaseWaitDialog pleaseWaitDialog;
	
	private LegacyTagImporter legacyTagImporter;
	
	public ArtifactMethodIterationConsumer() {
		legacyTagImporter = new LegacyTagImporter();
	}
	
	@Override
	public void consumeIterationResult(IterableMethod method, MethodIteratorData data, MethodIteratorResult result) {
		
		ArtifactExpertTagDescriptor expertTagDescriptor = new ArtifactExpertTagDescriptor();
		
		boolean expertTagOk = getExpertTagDialog().showDialog(expertTagDescriptor, true);
		if( !expertTagOk ) {
			return;
		}
		
		TagDocument expertTag;
		try {
			expertTag = new TagDocument(expertTagDescriptor.getExpertTagFile());
		} catch (SignalMLException ex) {
			logger.error("Failed to open expertTag", ex);
			ErrorsDialog.showImmediateExceptionDialog((Window) null, ex);
			return;			
		} catch (IOException ex) {
			logger.error("Failed to open expertTag - i/o exception", ex);
			ErrorsDialog.showImmediateExceptionDialog((Window) null, ex);
			return;			
		}
				
		PleaseWaitDialog waitDialog = getPleaseWaitDialog();
		
		ArtifactIterationResultWorker resultWorker = new ArtifactIterationResultWorker(legacyTagImporter, data, result, expertTag, waitDialog);
		
		resultWorker.execute();
		
		waitDialog.setActivity(messageSource.getMessage("activity.analyzing"));			
		waitDialog.configureForDeterminate(0, data.getCompletedIterations(), 0);
		waitDialog.waitAndShowDialogIn(methodManager.getDialogParent(), 500, resultWorker);

		RocData rocData = null;		
		
		try {
			rocData = resultWorker.get();
		} catch (InterruptedException ex) {
			logger.error( "Worker failed to complete", ex );
			ErrorsDialog.showImmediateExceptionDialog((Window) null, ex);
			return;			
		} catch (ExecutionException ex) {
			logger.error( "Worker failed to complete", ex.getCause() );
			ErrorsDialog.showImmediateExceptionDialog((Window) null, ex.getCause());
			return;			
		}
										
		getRocDialog().showDialog(rocData, true);
		
	}

	public void initialize(ApplicationMethodManager methodManager) {
		this.methodManager = methodManager;
		setMessageSource(methodManager.getMessageSource());
	}

	public MessageSourceAccessor getMessageSource() {
		return messageSource;
	}

	public void setMessageSource(MessageSourceAccessor messageSource) {
		this.messageSource = messageSource;
	}

	public ArtifactExpertTagDialog getExpertTagDialog() {
		if( expertTagDialog == null ) {
			expertTagDialog = new ArtifactExpertTagDialog(methodManager.getMessageSource(), methodManager.getDialogParent(), true);
			expertTagDialog.setFileChooser(methodManager.getFileChooser());			
		}
		return expertTagDialog;
	}
	
	public RocDialog getRocDialog() {
		if( rocDialog == null ) {
			rocDialog = new RocDialog(methodManager.getMessageSource(), methodManager.getDialogParent(),true);
			rocDialog.setFileChooser(methodManager.getFileChooser());
			rocDialog.setTableToTextExporter(methodManager.getTableToTextExporter());			
		}
		return rocDialog;
	}
	
	public PleaseWaitDialog getPleaseWaitDialog() {
		if( pleaseWaitDialog == null ) {
			pleaseWaitDialog = new PleaseWaitDialog(methodManager.getMessageSource(), methodManager.getDialogParent());
			pleaseWaitDialog.initializeNow();
		}
		return pleaseWaitDialog;
	}
	
}
 