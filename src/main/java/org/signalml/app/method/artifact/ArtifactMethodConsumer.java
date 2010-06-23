/* ArtifactMethodConsumer.java created 2007-11-02
 * 
 */

package org.signalml.app.method.artifact;

import java.awt.Window;
import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.util.ArrayList;

import multiplexer.jmx.client.ConnectException;

import org.apache.log4j.Logger;
import org.signalml.app.document.DocumentFlowIntegrator;
import org.signalml.app.document.ManagedDocumentType;
import org.signalml.app.document.SignalDocument;
import org.signalml.app.document.TagDocument;
import org.signalml.app.method.ApplicationMethodManager;
import org.signalml.app.method.InitializingMethodResultConsumer;
import org.signalml.app.model.OpenDocumentDescriptor;
import org.signalml.app.view.ViewerFileChooser;
import org.signalml.app.view.dialog.ErrorsDialog;
import org.signalml.app.view.dialog.OptionPane;
import org.signalml.domain.tag.LegacyTagImporter;
import org.signalml.domain.tag.StyledTagSet;
import org.signalml.exception.SignalMLException;
import org.signalml.method.Method;
import org.signalml.method.artifact.ArtifactResult;
import org.signalml.util.Util;

/** ArtifactMethodConsumer
 *
 * 
 * @author Michal Dobaczewski &copy; 2007-2008 CC Otwarte Systemy Komputerowe Sp. z o.o.
 */
public class ArtifactMethodConsumer implements InitializingMethodResultConsumer {

	protected static final Logger logger = Logger.getLogger(ArtifactMethodConsumer.class);
	
	private DocumentFlowIntegrator documentFlowIntegrator;
	private Window dialogParent;
	private ViewerFileChooser fileChooser;
		
	private ArtifactResultDialog resultDialog;
	
	private LegacyTagImporter legacyTagImporter;
		
	@Override
	public void initialize(ApplicationMethodManager manager) {

		documentFlowIntegrator = manager.getDocumentFlowIntegrator();
		dialogParent = manager.getDialogParent();
		fileChooser = manager.getFileChooser();

		resultDialog = new ArtifactResultDialog(manager.getMessageSource(), dialogParent, true);
		resultDialog.setFileChooser(fileChooser);
		
		legacyTagImporter = new LegacyTagImporter();		
		
	}

	@Override
	public boolean consumeResult(Method method, Object methodData, Object methodResult) throws SignalMLException {
		
		if( !(methodData instanceof ArtifactApplicationData) ) {
			logger.error( "Invalid artifact data" );
			return false;
		}
		
		ArtifactApplicationData data = (ArtifactApplicationData) methodData;
		ArtifactResult result = (ArtifactResult) methodResult;
		
		ArtifactResultTargetDescriptor descriptor = new ArtifactResultTargetDescriptor();
		
		SignalDocument signalDocument = data.getSignalDocument();
		boolean signalAvailable;
		if( signalDocument == null || signalDocument.isClosed() ) {
			logger.warn( "Document unavailable or has been closed" );
			signalAvailable = false;
		} else {
			signalAvailable = true;
		}
		descriptor.setSignalAvailable(signalAvailable);

		final File primaryTagFile = result.getTagFile();
		// TODO temove test
//		final File primaryTagFile = new File( "D:/Install/signalml-downloads/dane/ZCB02A.TAG" );
		if( primaryTagFile == null || !primaryTagFile.exists() ) {
			throw new SignalMLException( "No result tag" );
		}
		
		StyledTagSet primaryTagSet = null;
		try {
			primaryTagSet = legacyTagImporter.importLegacyTags(primaryTagFile, data.getSampleSource().getSamplingFrequency());
		} catch( SignalMLException ex ) {
			logger.error("Failed to read primary result tag", ex);
			ErrorsDialog.showImmediateExceptionDialog(dialogParent, ex);
			return false;										
		}
		TagDocument primaryTag = new TagDocument(primaryTagSet);
		
		descriptor.setPrimaryTag(primaryTag);
		
		File workingDirectory = new File( data.getProjectPath(), data.getPatientName() );
		File[] additionalTagFiles = workingDirectory.listFiles( new FileFilter() {

			@Override
			public boolean accept(File pathname) {
				if( pathname.equals(primaryTagFile) ) {
					return false;
				}
				String fileExtension = Util.getFileExtension(pathname, false);
				return ( fileExtension != null && "tag".equalsIgnoreCase(fileExtension) );
			}
			
		});
		ArrayList<File> additionalTags = new ArrayList<File>();
		for( File f : additionalTagFiles ) {
			additionalTags.add( f );
		}
		
		descriptor.setAdditionalTags(additionalTags);
		descriptor.setChosenAdditionalTags(new ArrayList<File>());
		
		descriptor.setPrimaryOpenInWindow(true);
		descriptor.setPrimarySaveToFile(true);
		
		descriptor.setAdditionalOpenInWindow(false);
		descriptor.setAdditionalSaveToFile(false);
		
		boolean dialogOk = resultDialog.showDialog(descriptor, true);
		if( !dialogOk ) {
			return false;
		}
		
		if( descriptor.isPrimarySaveToFile() ) {
			primaryTag.setBackingFile( descriptor.getPrimaryTagFile() );
			try {
				primaryTag.saveDocument();
			} catch(SignalMLException ex ) {
				logger.error("Failed to save document", ex);
				ErrorsDialog.showImmediateExceptionDialog(dialogParent, ex);
				return false;							
			} catch(IOException ex) {
				logger.error("Failed to save document - i/o exception", ex);
				ErrorsDialog.showImmediateExceptionDialog(dialogParent, ex);
				return false;			
			}
		}
		
		if( signalAvailable && descriptor.isPrimaryOpenInWindow() ) {
					
			OpenDocumentDescriptor odd = new OpenDocumentDescriptor();
			odd.setFile( primaryTag.getBackingFile() );
			odd.setMakeActive(true);
			odd.setType(ManagedDocumentType.TAG);
			odd.getTagOptions().setParent(signalDocument);
			odd.getTagOptions().setExistingDocument(primaryTag);

			try {
				documentFlowIntegrator.openDocument(odd);
			} catch(SignalMLException ex) {
				logger.error("Failed to open document", ex);
				ErrorsDialog.showImmediateExceptionDialog(dialogParent, ex);
				return false;			
			} catch(IOException ex) {
				logger.error("Failed to open document - i/o exception", ex);
				ErrorsDialog.showImmediateExceptionDialog(dialogParent, ex);
				return false;			
			} catch (ConnectException ex) {
				logger.error("Failed to open document - connection exception", ex);
				ErrorsDialog.showImmediateExceptionDialog(dialogParent, ex);
				return false;		   
			}
			
		}
		
		ArrayList<File> chosenAdditionalTags = descriptor.getChosenAdditionalTags();
		if( !chosenAdditionalTags.isEmpty() ) {
			
			boolean additionalOpenInWindow = descriptor.isAdditionalOpenInWindow();
			boolean additionalSaveToFile = descriptor.isAdditionalSaveToFile();
			
			if( additionalOpenInWindow || additionalSaveToFile ) {

				StyledTagSet additionalTagSet = null;
				TagDocument additionalTag = null;
				File saveFile;
				boolean hasFile = false;
				
				for( File file : chosenAdditionalTags ) {
					
					try {
						additionalTagSet = legacyTagImporter.importLegacyTags(file, data.getSampleSource().getSamplingFrequency());
					} catch( SignalMLException ex ) {
						logger.error("Failed to read additional result tag", ex);
						ErrorsDialog.showImmediateExceptionDialog(dialogParent, ex);
						return false;										
					}
					additionalTag = new TagDocument(additionalTagSet);
					
					if( additionalSaveToFile ) {
						
						hasFile = false;
						
						do {
							
							saveFile = fileChooser.chooseSaveTag(dialogParent);
							if( saveFile == null ) {
								// file choice canceled
								break;
							}
							
							hasFile = true;
							
							// file exists warning
							if( saveFile.exists() ) {
								int res = OptionPane.showFileAlreadyExists(dialogParent);
								if( res != OptionPane.OK_OPTION ) {
									hasFile = false;
								}								
							}
							
						} while( !hasFile );
						
						if( hasFile ) {
							
							additionalTag.setBackingFile( saveFile );
							try {
								additionalTag.saveDocument();
							} catch(SignalMLException ex ) {
								logger.error("Failed to save document", ex);
								ErrorsDialog.showImmediateExceptionDialog(dialogParent, ex);
								return false;							
							} catch(IOException ex) {
								logger.error("Failed to save document - i/o exception", ex);
								ErrorsDialog.showImmediateExceptionDialog(dialogParent, ex);
								return false;			
							}
														
						}
												
					}
					
					if( additionalOpenInWindow ) {
						
						OpenDocumentDescriptor odd = new OpenDocumentDescriptor();
						odd.setFile( additionalTag.getBackingFile() );
						odd.setMakeActive(false);
						odd.setType(ManagedDocumentType.TAG);
						odd.getTagOptions().setParent(signalDocument);
						odd.getTagOptions().setExistingDocument(additionalTag);

						try {
							documentFlowIntegrator.openDocument(odd);
						} catch(SignalMLException ex) {
							logger.error("Failed to open document", ex);
							ErrorsDialog.showImmediateExceptionDialog(dialogParent, ex);
							return false;			
						} catch(IOException ex) {
							logger.error("Failed to open document - i/o exception", ex);
							ErrorsDialog.showImmediateExceptionDialog(dialogParent, ex);
							return false;			
						} catch (ConnectException ex) {
							logger.error("Failed to open document - connection exception", ex);
							ErrorsDialog.showImmediateExceptionDialog(dialogParent, ex);
							return false;		   
						}
												
					}
											
				}
								
			}
						
		}
	
		return true;
	}
	
}
