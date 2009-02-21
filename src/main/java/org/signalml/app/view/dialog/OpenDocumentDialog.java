/* OpenDocumentDialog.java created 2007-09-17
 * 
 */

package org.signalml.app.view.dialog;

import java.awt.Window;
import java.awt.event.ActionEvent;
import java.io.File;
import java.io.IOException;
import java.net.URL;

import javax.swing.AbstractAction;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JFileChooser;

import org.signalml.app.action.RegisterCodecAction;
import org.signalml.app.config.ApplicationConfiguration;
import org.signalml.app.document.DocumentDetector;
import org.signalml.app.document.DocumentManager;
import org.signalml.app.document.ManagedDocumentType;
import org.signalml.app.document.SignalDocument;
import org.signalml.app.document.TagDocument;
import org.signalml.app.model.OpenDocumentDescriptor;
import org.signalml.app.model.OpenSignalDescriptor;
import org.signalml.app.model.OpenTagDescriptor;
import org.signalml.app.model.SignalMLCodecListModel;
import org.signalml.app.model.SignalParameterDescriptor;
import org.signalml.app.model.OpenSignalDescriptor.OpenSignalMethod;
import org.signalml.app.util.IconUtils;
import org.signalml.app.view.ViewerFileChooser;
import org.signalml.app.view.element.EmbeddedFileChooser;
import org.signalml.app.view.element.OpenDocumentStepOnePanel;
import org.signalml.app.view.element.OpenDocumentStepTwoPanel;
import org.signalml.app.view.element.OpenSignalOptionsPanel;
import org.signalml.app.view.element.PagingParametersPanel;
import org.signalml.app.view.element.SignalMLOptionsPanel;
import org.signalml.codec.SignalMLCodec;
import org.signalml.codec.SignalMLCodecManager;
import org.signalml.codec.SignalMLCodecSelector;
import org.signalml.domain.signal.raw.RawSignalDescriptor;
import org.signalml.domain.signal.raw.RawSignalDescriptorReader;
import org.signalml.domain.tag.LegacyTagImporter;
import org.signalml.domain.tag.StyledTagSet;
import org.signalml.exception.SignalMLException;
import org.signalml.util.Util;
import org.springframework.context.MessageSourceResolvable;
import org.springframework.context.support.MessageSourceAccessor;
import org.springframework.core.io.ClassPathResource;
import org.springframework.validation.Errors;

/** OpenDocumentDialog
 *
 * 
 * @author Michal Dobaczewski &copy; 2007-2008 CC Otwarte Systemy Komputerowe Sp. z o.o.
 */
public class OpenDocumentDialog extends AbstractWizardDialog {

	private static final long serialVersionUID = 1L;
	
	private File profileDir;
	private RegisterCodecDialog registerCodecDialog;
	private SignalMLCodecManager codecManager;
	private DocumentDetector documentDetector;
	private DocumentManager documentManager;
	private ApplicationConfiguration applicationConfig;
	private PleaseWaitDialog pleaseWaitDialog;
	ViewerFileChooser fileChooser;
	
	private ManagedDocumentType targetType = null;
	
	private URL contextHelpURL = null;
	
	RawSignalDescriptor currentRawSignalDescriptor;
	
	public OpenDocumentDialog(MessageSourceAccessor messageSource) {
		super(messageSource);
	}

	public OpenDocumentDialog(MessageSourceAccessor messageSource, Window f, boolean isModal) {
		super(messageSource, f, isModal);
	}

	@Override
	protected void initialize() {
		
		setTitle(messageSource.getMessage("openDocument.title"));
		setIconImage( IconUtils.loadClassPathImage("org/signalml/app/icon/fileopen.png") );
		
		super.initialize();

		SignalMLCodecListModel codecListModel = new SignalMLCodecListModel();
		codecListModel.setCodecManager(codecManager);
		
		RegisterCodecAction registerCodecAction = new RegisterCodecAction(messageSource);
		registerCodecAction.setCodecManager(codecManager);
		registerCodecAction.setRegisterCodecDialog(getRegisterCodecDialog());
		registerCodecAction.setPleaseWaitDialog(getPleaseWaitDialog());
		registerCodecAction.setApplicationConfig(applicationConfig);
		registerCodecAction.initializeAll();

		final SignalMLOptionsPanel signalMLOptionsPanel = getStepTwoPanel().getSignalOptionsPanel().getSignalMLOptionsPanel();
		
		registerCodecAction.setSelector(new SignalMLCodecSelector() {

			@Override
			public SignalMLCodec getSelectedCodec() {
				return (SignalMLCodec) signalMLOptionsPanel.getSignalMLDriverComboBox().getSelectedItem();
			}

			@Override
			public void setSelectedCodec(SignalMLCodec codec) {				
				signalMLOptionsPanel.getSignalMLDriverComboBox().setSelectedItem(codec);
			}
			
		});
		
		signalMLOptionsPanel.getSignalMLDriverComboBox().setModel(codecListModel);
		signalMLOptionsPanel.getRegisterCodecButton().setAction(registerCodecAction);
		
		ReadXMLManifestAction readXMLManifestAction = new ReadXMLManifestAction();
		getStepTwoPanel().getSignalOptionsPanel().getRawSignalOptionsPanel().getReadXMLManifestButton().setAction(readXMLManifestAction);
		
	}
	
	@Override
	public int getStepCount() {
		return 2;
	}
	
	@Override
	protected boolean onStepChange(int toStep, int fromStep, Object model) {
		if( fromStep == 0 ) {
			JFileChooser fileChooser = getStepOnePanel().getFileChooser();
						
			boolean autodetect = getStepOnePanel().getAutodetectRadio().isSelected();
			if( autodetect ) {
				File file = fileChooser.getSelectedFile();
				try {
					targetType = documentDetector.detectDocumentType(file);
				} catch( IOException ex ) {
					logger.error("Exception at detection", ex);
					// assume signal
					targetType = ManagedDocumentType.SIGNAL;
				}
			} else {
				switch( getStepOnePanel().getFileTypeCombo().getSelectedIndex() ) {
				
					case 0 :
						targetType = ManagedDocumentType.SIGNAL;
						break;
					case 1 :
						targetType = ManagedDocumentType.BOOK;;
						break;
					case 2 :
						targetType = ManagedDocumentType.TAG;
						break;
					default :
						throw new IndexOutOfBoundsException();
					
				}
			}
			
			if( targetType == ManagedDocumentType.SIGNAL ) {
				
				SignalMLOptionsPanel signalMLOptionsPanel = getStepTwoPanel().getSignalOptionsPanel().getSignalMLOptionsPanel();
				
				JComboBox signalMLDriverComboBox = signalMLOptionsPanel.getSignalMLDriverComboBox();
				if( signalMLDriverComboBox.getItemCount() > 0 ) {
					if( signalMLDriverComboBox.getSelectedIndex() < 0 ) {
						signalMLDriverComboBox.setSelectedIndex(0);
					}
				} else {
					signalMLDriverComboBox.setSelectedIndex(-1);
				}
				PagingParametersPanel pagingPanel = getStepTwoPanel().getSignalOptionsPanel().getPagingSignalParamersPanel();
				pagingPanel.getPageSizeField().setText( Float.toString( applicationConfig.getPageSize() ) );
				pagingPanel.getBlocksPerPageField().setText( Integer.toString( applicationConfig.getBlocksPerPage() ) );
				
			} 
			else if( targetType == ManagedDocumentType.TAG ) {
				
				int cnt = documentManager.getDocumentCount(ManagedDocumentType.SIGNAL);
				String[] labels = new String[cnt];
				for( int i=0; i<cnt; i++ ) {
					labels[i] = messageSource.getMessage((MessageSourceResolvable) documentManager.getDocumentAt(ManagedDocumentType.SIGNAL, i)); 
				}
							
				DefaultComboBoxModel documentListModel = new DefaultComboBoxModel(labels);
				
				JComboBox signalDocumentComboBox = getStepTwoPanel().getTagOptionsPanel().getSignalDocumentComboBox();
				signalDocumentComboBox.setModel(documentListModel);
				if( documentListModel.getSize() > 0 ) {
					if( signalDocumentComboBox.getSelectedIndex() < 0 ) {
						signalDocumentComboBox.setSelectedIndex(0);
					}
				} else {
					signalDocumentComboBox.setSelectedIndex(-1);
				}
				
			}
			
			getStepTwoPanel().setExpectedType(targetType, autodetect);
			
		}
		return true;
	}
	
	@Override
	public JComponent createInterfaceForStep(int step) {
		switch( step ) {
			case 0 :
				return new OpenDocumentStepOnePanel(messageSource);
			case 1 :
				return new OpenDocumentStepTwoPanel(messageSource);
			default :
				throw new IndexOutOfBoundsException();
		}
	}
	
	@Override
	public void validateDialogStep(int step, Object model, Errors errors) throws SignalMLException {
		if( step == 0 ) {
			
			EmbeddedFileChooser fileChooser = getStepOnePanel().getFileChooser();
			
			fileChooser.forceApproveSelection();
			fileChooser.validateFile(errors, "file", false, false, false, true, true);
			
		} else if( step == 1 ) {
			if( targetType == null ) {
				logger.error("Target type null");
				throw new SignalMLException("error.invalidValue");
			}
			if( targetType.equals(ManagedDocumentType.SIGNAL) ) {
				validateSignalOptions(model, errors);
			} else if( targetType.equals(ManagedDocumentType.BOOK) ) {
				validateBookOptions(model,errors);
			} else if( targetType.equals(ManagedDocumentType.TAG) ) {
				validateTagOptions(model,errors);
			} else {
				logger.error("Unsupported type [" + targetType + "]");
				throw new SignalMLException("error.invalidValue");
			}
		} 
	}
	
	private void validateTagOptions(Object model, Errors errors) {
		int selectedIndex = getStepTwoPanel().getTagOptionsPanel().getSignalDocumentComboBox().getSelectedIndex();
		if( selectedIndex < 0 ) {
			errors.rejectValue("tagOptions.signalDocument", "error.signalDocumentMustBeSet");
		}		
	}

	private void validateBookOptions(Object model, Errors errors) {
		// nothing so far		
	}

	private void validateSignalOptions(Object model, Errors errors) throws SignalMLException {
		
		OpenSignalOptionsPanel optionsPanel = getStepTwoPanel().getSignalOptionsPanel();
		int method = optionsPanel.getMethodComboBox().getSelectedIndex();
		if( method == 0 ) {
			SignalMLCodec codec = (SignalMLCodec) optionsPanel.getSignalMLOptionsPanel().getSignalMLDriverComboBox().getSelectedItem();
			if( codec == null ) {
				errors.rejectValue("signalOptions.codec", "error.codecMustBeSet");
			}
		} else if( method == 1 ) {
			errors.pushNestedPath("signalOptions.rawSignalDescriptor");
			optionsPanel.getRawSignalOptionsPanel().validatePanel(errors);
			errors.popNestedPath();
		} else {
			throw new SignalMLException("error.invalidValue");			
		}
				
		getStepTwoPanel().getSignalOptionsPanel().getPagingSignalParamersPanel().validatePanel(errors);

	}

	@Override
	public boolean isFinishAllowedOnStep(int step) {
		return (step >= 1);
	}

	public OpenDocumentStepOnePanel getStepOnePanel() {
		return (OpenDocumentStepOnePanel) getInterfaceForStep(0);
	}
	
	public OpenDocumentStepTwoPanel getStepTwoPanel() {
		return (OpenDocumentStepTwoPanel) getInterfaceForStep(1);
	}
		
	@Override
	public void fillDialogFromModel(Object model) {
		
		String path = applicationConfig.getLastOpenDocumentPath();
		if( path != null ) {
			getStepOnePanel().getFileChooser().setCurrentDirectory(new File(path));
		}		
				
		OpenDocumentDescriptor ofd = (OpenDocumentDescriptor) model;
		File f = ofd.getFile();
		if( f == null ) {
			f = new File("");
		}
		getStepOnePanel().getFileChooser().setSelectedFile(f);
		
		OpenSignalDescriptor osd = ofd.getSignalOptions();
		SignalParameterDescriptor spd = osd.getParameters();
		
		getStepTwoPanel().getSignalOptionsPanel().getPagingSignalParamersPanel().fillPanelFromModel(spd);
		
		RawSignalDescriptor rawSignalDescriptor = osd.getRawSignalDescriptor();
		if( rawSignalDescriptor != null ) {
			getStepTwoPanel().getSignalOptionsPanel().getRawSignalOptionsPanel().fillPanelFromModel(rawSignalDescriptor);
		}
		
	}

	@Override
	public void fillModelFromDialog(Object model) throws SignalMLException {
		
		OpenDocumentDescriptor ofd = (OpenDocumentDescriptor) model;
		ofd.setFile(getStepOnePanel().getFileChooser().getSelectedFile());
		if( targetType == null ) {
			logger.error("Target type null");
			throw new SignalMLException("error.invalidValue");			
		}
		ofd.setType(targetType);
		if( targetType.equals(ManagedDocumentType.SIGNAL) ) {
			
			OpenSignalDescriptor osd = ofd.getSignalOptions();
			
			OpenSignalOptionsPanel signalOptionsPanel = getStepTwoPanel().getSignalOptionsPanel();
			
			int method = signalOptionsPanel.getMethodComboBox().getSelectedIndex();
			if( method == 0 ) {
				
				osd.setMethod(OpenSignalMethod.USE_SIGNALML);
			
				SignalParameterDescriptor spd = osd.getParameters();
				
				osd.setCodec((SignalMLCodec) signalOptionsPanel.getSignalMLOptionsPanel().getSignalMLDriverComboBox().getSelectedItem());
				signalOptionsPanel.getPagingSignalParamersPanel().fillModelFromPanel(spd);
				
			} else if( method == 1 ) {
				
				osd.setMethod(OpenSignalMethod.RAW);
				
				RawSignalDescriptor descriptor = currentRawSignalDescriptor;
				if( descriptor == null ) {
					descriptor = new RawSignalDescriptor();
				}
				signalOptionsPanel.getRawSignalOptionsPanel().fillModelFromPanel(descriptor);
				signalOptionsPanel.getPagingSignalParamersPanel().fillModelFromPanel(descriptor);
				
				osd.setRawSignalDescriptor(descriptor);
				
			} else {
				throw new SignalMLException("error.invalidValue");			
			}
									
		} else if( targetType.equals(ManagedDocumentType.BOOK) ) {

			// nothing to do so far
		
		} else if( targetType.equals(ManagedDocumentType.TAG) ) {
			
			OpenTagDescriptor otd = ofd.getTagOptions();
			int index = getStepTwoPanel().getTagOptionsPanel().getSignalDocumentComboBox().getSelectedIndex();

			SignalDocument signalDocument =  (SignalDocument) documentManager.getDocumentAt(ManagedDocumentType.SIGNAL, index);

			otd.setParent(signalDocument);			

			boolean legTag = true;
			LegacyTagImporter importer = new LegacyTagImporter();
			StyledTagSet tagSet = null;
			try {
				tagSet = importer.importLegacyTags(ofd.getFile(), signalDocument.getSamplingFrequency());
			} catch (SignalMLException ex) {
				legTag = false;
				logger.info("Failed to import tags, not a legacy tag");
			}
			
			TagDocument tagDocument = null;
			try {
				 tagDocument = new TagDocument(tagSet);
			} catch (SignalMLException ex) {
				legTag = false;
				logger.info("Failed to create document, not a legacy tag");
			}

			if (legTag) {
				ofd.getTagOptions().setExistingDocument(tagDocument);
				ofd.setFile(null);
			}
			
		} else {
			logger.error("Unsupported type [" + targetType + "]");
			throw new SignalMLException("error.invalidValue");
		}
		
		applicationConfig.setLastOpenDocumentPath(getStepOnePanel().getFileChooser().getCurrentDirectory().getAbsolutePath());

	}

	@Override
	public boolean supportsModelClass(Class<?> clazz) {
		return OpenDocumentDescriptor.class.isAssignableFrom(clazz);
	}
	
	@Override
	protected URL getContextHelpURL() {
		if( contextHelpURL == null ) {
			 try {
				 contextHelpURL = (new ClassPathResource("org/signalml/help/contents.html")).getURL();
				 contextHelpURL = new URL( contextHelpURL.toExternalForm() + "#opendoc" );
			} catch (IOException ex) {
				logger.error("Failed to get help URL", ex);
			}				
		}
		return contextHelpURL;
	}
	
	public SignalMLCodecManager getCodecManager() {
		return codecManager;
	}

	public void setCodecManager(SignalMLCodecManager codecManager) {
		this.codecManager = codecManager;
	}

	protected RegisterCodecDialog getRegisterCodecDialog() {
		if( registerCodecDialog == null ) {
			registerCodecDialog = new RegisterCodecDialog(messageSource,this,true);
			registerCodecDialog.setCodecManager(codecManager);
			registerCodecDialog.setProfileDir(profileDir);
		}
		return registerCodecDialog;
	}

	public DocumentDetector getDocumentDetector() {
		return documentDetector;
	}

	public void setDocumentDetector(DocumentDetector documentDetector) {
		this.documentDetector = documentDetector;
	}
	
	public DocumentManager getDocumentManager() {
		return documentManager;
	}

	public void setDocumentManager(DocumentManager documentManager) {
		this.documentManager = documentManager;
	}

	public ApplicationConfiguration getApplicationConfig() {
		return applicationConfig;
	}

	public void setApplicationConfig(ApplicationConfiguration applicationConfig) {
		this.applicationConfig = applicationConfig;
	}
	
	public File getProfileDir() {
		return profileDir;
	}

	public void setProfileDir(File profileDir) {
		this.profileDir = profileDir;
	}

	protected PleaseWaitDialog getPleaseWaitDialog() {
		if( pleaseWaitDialog == null ) {
			pleaseWaitDialog = new PleaseWaitDialog(messageSource,this);
			pleaseWaitDialog.initializeNow();			
		}		
		return pleaseWaitDialog;
	}

	public ViewerFileChooser getFileChooser() {
		return fileChooser;
	}

	public void setFileChooser(ViewerFileChooser fileChooser) {
		this.fileChooser = fileChooser;
	}
	
	protected class ReadXMLManifestAction extends AbstractAction {

		private static final long serialVersionUID = 1L;
		private RawSignalDescriptorReader reader;
		
		public ReadXMLManifestAction() {
			super(messageSource.getMessage("openSignal.options.raw.readXMLManifest"));
			putValue(AbstractAction.SMALL_ICON, IconUtils.loadClassPathIcon("org/signalml/app/icon/script_load.png") );
			putValue(AbstractAction.SHORT_DESCRIPTION,messageSource.getMessage("openSignal.options.raw.readXMLManifestToolTip"));
			//putValue(AbstractAction.ACCELERATOR_KEY, KeyStroke.getKeyStroke("F1"));
		}
		
		public void actionPerformed(ActionEvent ev) {						
			
			File selectedFile = getStepOnePanel().getFileChooser().getSelectedFile();
			File directory = null;
			File fileSuggestion = null;
			if( selectedFile != null ) {
				directory = selectedFile.getParentFile();
				
				fileSuggestion = Util.changeOrAddFileExtension(selectedFile, "xml");
							
			}
			
			if( directory == null ) {
				directory = new File( System.getProperty("user.dir") );
			}
						
			File xmlFile = fileChooser.chooseReadXMLManifest(directory, fileSuggestion, OpenDocumentDialog.this);
			if( xmlFile == null ) {
				return;
			} 
			
			if( reader == null ) {
				reader = new RawSignalDescriptorReader();
			}
			
			try {
				currentRawSignalDescriptor = reader.readDocument(xmlFile);
			} catch (IOException ex) {
				logger.error("Failed to read document", ex);
				getErrorsDialog().showException(ex);
				return;			
			} catch (SignalMLException ex) {
				logger.error("Failed to read document", ex);
				getErrorsDialog().showException(ex);
				return;			
			}
			
			OpenSignalOptionsPanel signalOptionsPanel = getStepTwoPanel().getSignalOptionsPanel();
			signalOptionsPanel.getRawSignalOptionsPanel().fillPanelFromModel(currentRawSignalDescriptor);
			signalOptionsPanel.getPagingSignalParamersPanel().fillPanelFromModel(currentRawSignalDescriptor); 
			
		}
				
	}
	
}

