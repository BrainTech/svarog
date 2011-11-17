/* OpenDocumentDialog.java created 2007-09-17
 *
 */

package org.signalml.app.view.dialog;

import static org.signalml.app.SvarogI18n._;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.io.File;
import java.io.IOException;
import java.net.URL;

import javax.swing.AbstractAction;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JComboBox;
import javax.swing.JComponent;

import org.signalml.app.action.RegisterCodecAction;
import org.signalml.app.config.ApplicationConfiguration;
import org.signalml.app.document.BookDocument;
import org.signalml.app.document.DocumentDetector;
import org.signalml.app.document.DocumentManager;
import org.signalml.app.document.ManagedDocumentType;
import org.signalml.app.document.SignalDocument;
import org.signalml.app.document.TagDocument;
import org.signalml.app.model.OpenDocumentDescriptor;
import org.signalml.app.model.OpenFileSignalDescriptor;
import org.signalml.app.model.OpenTagDescriptor;
import org.signalml.app.model.SignalMLCodecListModel;
import org.signalml.app.model.SignalParameterDescriptor;
import org.signalml.app.util.IconUtils;
import org.signalml.app.view.ViewerFileChooser;
import org.signalml.app.view.element.EmbeddedFileChooser;
import org.signalml.app.view.element.OpenBookOptionsPanel;
import org.signalml.app.view.element.OpenDocumentStepOnePanel;
import org.signalml.app.view.element.OpenDocumentStepTwoPanel;
import org.signalml.app.view.element.OpenSignalOptionsPanel;
import org.signalml.app.view.element.OpenTagOptionsPanel;
import org.signalml.app.view.element.PagingParametersPanel;
import org.signalml.app.view.element.RawSignalOptionsPanel;
import org.signalml.app.view.opensignal.SignalMLOptionsPanel;
import org.signalml.app.view.opensignal.FileOpenSignalMethod;
import org.signalml.codec.SignalMLCodec;
import org.signalml.codec.SignalMLCodecManager;
import org.signalml.codec.SignalMLCodecSelector;
import org.signalml.domain.signal.raw.RawSignalDescriptor;
import org.signalml.domain.signal.raw.RawSignalDescriptorReader;
import org.signalml.domain.tag.LegacyTagImporter;
import org.signalml.domain.tag.StyledTagSet;
import org.signalml.plugin.export.SignalMLException;
import org.signalml.plugin.export.signal.Document;
import org.signalml.util.Util;
import org.springframework.context.MessageSourceResolvable;

import org.springframework.core.io.ClassPathResource;
import org.springframework.validation.Errors;

/**
 * {@link AbstractWizardDialog Wizard dialog} to open {@link Document documents}.
 * This dialog has two steps:
 * <ul>
 * <li>in the {@link OpenDocumentStepOnePanel first step} user selects the file
 * to open and the type of that file,</li>
 * <li>in the {@link OpenDocumentStepTwoPanel second step} user selects the
 * parameters of the opened document ({@link ManagedDocumentType type
 * specific}).</li>
 * </ul>
 *
 * @author Michal Dobaczewski &copy; 2007-2008 CC Otwarte Systemy Komputerowe Sp. z o.o.
 */
public class OpenDocumentDialog extends AbstractWizardDialog {

	private static final long serialVersionUID = 1L;

	/**
	 * the profile directory
	 */
	private File profileDir;
	/**
	 * the {@link RegisterCodecDialog dialog} to register codecs
	 */
	private RegisterCodecDialog registerCodecDialog;
	
	/**
	 * the {@link SignalMLCodecManager manager} of {@link SignalMLCodec codecs}
	 */
	private SignalMLCodecManager codecManager;
	
	/**
	 * {@link DocumentDetector detects} the {@link ManagedDocumentType type}
	 * of a {@link Document document} stored in the file
	 */
	private DocumentDetector documentDetector;
	
	/**
	 * the {@link DocumentManager manager} of {@link Document documents}
	 */
	private DocumentManager documentManager;
	
	/**
	 * the {@link ApplicationConfiguration configuration} of Svarog
	 */
	private ApplicationConfiguration applicationConfig;
	
	/**
	 * the {@link PleaseWaitDialog dialog} which tells the user to wait
	 */
	private PleaseWaitDialog pleaseWaitDialog;
	
	/**
	 * the {@link ViewerFileChooser chooser} for files
	 */
	ViewerFileChooser fileChooser;

	/**
	 * the {@link ManagedDocumentType type} of a {@link Document document}
	 * to open
	 */
	private ManagedDocumentType targetType = null;

	/**
	 * the URL for the help for this dialog
	 */
	private URL contextHelpURL = null;

	/**
	 * the {@link RawSignalDescriptor descriptor} of a raw signal
	 */
	RawSignalDescriptor currentRawSignalDescriptor;

	/**
	 * Constructor. Sets parent window and if this dialog
	 * blocks top-level windows.
	 * @param f the parent window or null if there is no parent
	 * @param dialog blocks top-level windows if true
	 */
	public OpenDocumentDialog(Window f, boolean isModal) {
		super(f, isModal);
	}

	/**
	 * Initializes this dialog:
	 * <ul>
	 * <li>creates the {@link SignalMLCodecListModel list} of
	 * {@link SignalMLCodec codecs},</li>
	 * <li>creates and initializes the {@link RegisterCodecAction action} to
	 * register a codec,</li>
	 * <li>sets a model to the
	 * {@link SignalMLOptionsPanel#getSignalMLDriverComboBox() combo-box} in
	 * {@link SignalMLOptionsPanel},</li>
	 * <li>sets the action registering a codec for a
	 * {@link SignalMLOptionsPanel#getRegisterCodecButton() button} in
	 * SignalMLOptionsPanel,</li>
	 * <li>sets the {@link ReadXMLManifestAction action} for a
	 * {@link RawSignalOptionsPanel#getReadXMLManifestButton() button} in
	 * {@link RawSignalOptionsPanel}.</li></ul>
	 */
	@Override
	protected void initialize() {

		setTitle(_("Open document"));
		setIconImage(IconUtils.loadClassPathImage("org/signalml/app/icon/fileopen.png"));

		super.initialize();

		SignalMLCodecListModel codecListModel = new SignalMLCodecListModel();
		codecListModel.setCodecManager(codecManager);

		RegisterCodecAction registerCodecAction = new RegisterCodecAction();
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

	}

	/**
	 * Returns the number of steps, namely 2.
	 */
	@Override
	public int getStepCount() {
		return 2;
	}

	/**
	 * Performs operations necessary when a step is changed (only for
	 * changing from first step to second):
	 * <ul>
	 * <li>checks the {@link ManagedDocumentType type} of a document:
	 * <ul><li>{@link DocumentDetector autodetects} it if such option is
	 * {@link OpenDocumentStepOnePanel#getAutodetectRadio() selected},</li>
	 * <li>otherwise uses the type
	 * {@link OpenDocumentStepOnePanel#getFileTypeCombo() selected}
	 * by user,</li></ul></li>
	 * <li>depending on the obtained type:<ul>
	 * <li>for {@link SignalDocument signal document}:
	 * <ul><li>initializes the
	 * {@link SignalMLOptionsPanel#getSignalMLDriverComboBox() combo-box}
	 * with the {@link SignalMLCodec codecs},</li>
	 * <li>initializes the {@link PagingParametersPanel#getPageSizeField()
	 * page size} and the {@link PagingParametersPanel#getBlocksPerPageField()
	 * number} of block in a page,</li></ul></li>
	 * <li>for {@link TagDocument tag document}:<ul>
	 * <li>initializes the
	 * {@link OpenTagOptionsPanel#getSignalDocumentComboBox() combo-box} with
	 * signal documents</li></ul></li></ul></ul>
	 */
	@Override
	protected boolean onStepChange(int toStep, int fromStep, Object model) {
		if (fromStep == 0) {
			EmbeddedFileChooser stepOneFileChooser = getStepOnePanel().getFileChooser();
			getStepTwoPanel().getSignalOptionsPanel().getRawSignalOptionsPanel().setSignalFileChooser(stepOneFileChooser);

			boolean autodetect = getStepOnePanel().getAutodetectRadio().isSelected();
			if (autodetect) {
				File file = stepOneFileChooser.getSelectedFile();
				try {
					targetType = documentDetector.detectDocumentType(file);
				} catch (IOException ex) {
					logger.error("Exception at detection", ex);
					// assume signal
					targetType = ManagedDocumentType.SIGNAL;
				}
			} else {
				switch (getStepOnePanel().getFileTypeCombo().getSelectedIndex()) {

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

			if (targetType == ManagedDocumentType.SIGNAL) {

				SignalMLOptionsPanel signalMLOptionsPanel = getStepTwoPanel().getSignalOptionsPanel().getSignalMLOptionsPanel();

				JComboBox signalMLDriverComboBox = signalMLOptionsPanel.getSignalMLDriverComboBox();
				if (signalMLDriverComboBox.getItemCount() > 0) {
					if (signalMLDriverComboBox.getSelectedIndex() < 0) {
						signalMLDriverComboBox.setSelectedIndex(0);
					}
				} else {
					signalMLDriverComboBox.setSelectedIndex(-1);
				}
				PagingParametersPanel pagingPanel = getStepTwoPanel().getSignalOptionsPanel().getPagingSignalParamersPanel();
				pagingPanel.getPageSizeField().setText(Float.toString(applicationConfig.getPageSize()));
				pagingPanel.getBlocksPerPageField().setText(Integer.toString(applicationConfig.getBlocksPerPage()));

			}
			else if (targetType == ManagedDocumentType.TAG) {

				int cnt = documentManager.getDocumentCount(ManagedDocumentType.SIGNAL);
				String[] labels = new String[cnt];
				for (int i=0; i<cnt; i++) {
					labels[i] = getSvarogI18n().getMessage((MessageSourceResolvable) documentManager.getDocumentAt(ManagedDocumentType.SIGNAL, i));
				}

				DefaultComboBoxModel documentListModel = new DefaultComboBoxModel(labels);

				JComboBox signalDocumentComboBox = getStepTwoPanel().getTagOptionsPanel().getSignalDocumentComboBox();
				signalDocumentComboBox.setModel(documentListModel);
				if (documentListModel.getSize() > 0) {
					if (signalDocumentComboBox.getSelectedIndex() < 0) {
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

	/**
	 * Creates the interface for the given step:
	 * <ul><li>for step {@code 0} - {@link OpenDocumentStepOnePanel},</li>
	 * <li>for step {@code 1} - {@link OpenDocumentStepTwoPanel}.</li></ul>
	 */
	@Override
	public JComponent createInterfaceForStep(int step) {
		switch (step) {
		case 0 :
			return new OpenDocumentStepOnePanel();
		case 1 :
			return new OpenDocumentStepTwoPanel();
		default :
			throw new IndexOutOfBoundsException();
		}
	}

	/**
	 * Validates the step of this dialog.
	 * The step is valid if:
	 * <ul><li>for step {@code 0} the file is
	 * {@link EmbeddedFileChooser#validateFile(Errors, String, boolean,
	 * boolean, boolean, boolean, boolean) valid},</li>
	 * <li>for step {@code 1} depending on a {@link ManagedDocumentType type}
	 * of a {@link Document document}:
	 * <ul><li>for {@link SignalDocument signal document} - the signal options
	 * are {@link #validateSignalOptions(Object, Errors) valid},</li>
	 * <li>for {@link BookDocument book document} - the book options
	 * are {@link #validateBookOptions(Object, Errors) valid},</li>
	 * <li>for {@link TagDocument tag document} - the tag options are
	 * {@link #validateTagOptions(Object, Errors) valid}.</li></ul></li></ul>
	 */
	@Override
	public void validateDialogStep(int step, Object model, Errors errors) throws SignalMLException {
		if (step == 0) {

			EmbeddedFileChooser fileChooser = getStepOnePanel().getFileChooser();

			fileChooser.forceApproveSelection();
			fileChooser.validateFile(errors, "file", false, false, false, true, true);

		} else if (step == 1) {
			if (targetType == null) {
				logger.error("Target type null");
				throw new SignalMLException("error.invalidValue");
			}
			if (targetType.equals(ManagedDocumentType.SIGNAL)) {
				validateSignalOptions(model, errors);
			} else if (targetType.equals(ManagedDocumentType.BOOK)) {
				validateBookOptions(model,errors);
			} else if (targetType.equals(ManagedDocumentType.TAG)) {
				validateTagOptions(model,errors);
			} else {
				logger.error("Unsupported type [" + targetType + "]");
				throw new SignalMLException("error.invalidValue");
			}
		}
	}

	/**
	 * Validates the {@link OpenTagOptionsPanel tag options} from the
	 * {@link OpenDocumentStepTwoPanel panel} for a second step.
	 * The options are valid if the index of a selected {@link SignalDocument
	 * signal document} is positive. 
	 * @param model the model for this dialog; not used
	 * @param errors the object in which the errors are stored
	 */
	private void validateTagOptions(Object model, Errors errors) {
		int selectedIndex = getStepTwoPanel().getTagOptionsPanel().getSignalDocumentComboBox().getSelectedIndex();
		if (selectedIndex < 0) {
			errors.rejectValue("tagOptions.signalDocument", "error.signalDocumentMustBeSet");
		}
	}

	/**
	 * Validates the {@link OpenBookOptionsPanel book options} from the
	 * {@link OpenDocumentStepTwoPanel panel} for a second step.
	 * The book options are always valid.
	 * @param model the model for this dialog; not used
	 * @param errors the object in which the errors are stored
	 */
	private void validateBookOptions(Object model, Errors errors) {
		// nothing so far
	}

	/**
	 * Validates the {@link OpenSignalOptionsPanel signal options} from the
	 * {@link OpenDocumentStepTwoPanel panel} for a second step.
	 * The options are valid if:
	 * <ul><li>for SignalML method - the selected {@link SignalMLCodec codec}
	 * exists,</li>
	 * <li>for RAW method - if the {@link RawSignalOptionsPanel} is
	 * {@link RawSignalOptionsPanel#validatePanel(Errors) valid}.</li></ul>
	 * @param model the model for this dialog; not used
	 * @param errors the object in which the errors are stored
	 * @throws SignalMLException if the method is unknown
	 */
	private void validateSignalOptions(Object model, Errors errors) throws SignalMLException {

		OpenSignalOptionsPanel optionsPanel = getStepTwoPanel().getSignalOptionsPanel();
		int method = optionsPanel.getMethodComboBox().getSelectedIndex();
		if (method == 0) {
			SignalMLCodec codec = (SignalMLCodec) optionsPanel.getSignalMLOptionsPanel().getSignalMLDriverComboBox().getSelectedItem();
			if (codec == null) {
				errors.rejectValue("signalOptions.codec", "error.codecMustBeSet");
			}
		} else if (method == 1) {
			// the panel for method 1 doesn't need any validation
		} else {
			throw new SignalMLException("error.invalidValue");
		}

		getStepTwoPanel().getSignalOptionsPanel().getPagingSignalParamersPanel().validatePanel(errors);

	}

	/**
	 * Dialog can be finished in the step {@code 1} (second step).
	 */
	@Override
	public boolean isFinishAllowedOnStep(int step) {
		return (step >= 1);
	}

	/**
	 * Returns the {@link OpenDocumentStepOnePanel panel} for the first
	 * step (step 0).
	 * @return the panel for the first step
	 */
	public OpenDocumentStepOnePanel getStepOnePanel() {
		return (OpenDocumentStepOnePanel) getInterfaceForStep(0);
	}

	/**
	 * Returns the {@link OpenDocumentStepTwoPanel panel} for the second
	 * step (step 1).
	 * @return the panel for the second step
	 */
	public OpenDocumentStepTwoPanel getStepTwoPanel() {
		return (OpenDocumentStepTwoPanel) getInterfaceForStep(1);
	}

	/**
	 * Fills the fields of this dialog from the given model:
	 * <ul>
	 * <li>sets the {@link OpenDocumentDescriptor#getFile() selected file}
	 * in the {@link OpenDocumentStepOnePanel#getFileChooser() file chooser},
	 * </li><li>{@link PagingParametersPanel#fillPanelFromModel(
	 * org.signalml.app.model.PagingParameterDescriptor) fills} the
	 * {@link PagingParametersPanel},</li>
	 * <li>{@link RawSignalOptionsPanel#fillPanelFromModel(RawSignalDescriptor)
	 * fills} the {@link RawSignalOptionsPanel}.</li></ul>
	 */
	@Override
	public void fillDialogFromModel(Object model) {

		String path = applicationConfig.getLastOpenDocumentPath();
		if (path != null) {
			getStepOnePanel().getFileChooser().setCurrentDirectory(new File(path));
		}

		OpenDocumentDescriptor ofd = (OpenDocumentDescriptor) model;
		File f = ofd.getFile();
		if (f == null) {
			f = new File("");
		}
		getStepOnePanel().getFileChooser().setSelectedFile(f);

		OpenFileSignalDescriptor osd = ofd.getOpenSignalDescriptor().getOpenFileSignalDescriptor();
		SignalParameterDescriptor spd = osd.getParameters();

		getStepTwoPanel().getSignalOptionsPanel().getPagingSignalParamersPanel().fillPanelFromModel(spd);

		RawSignalDescriptor rawSignalDescriptor = osd.getRawSignalDescriptor();
		if (rawSignalDescriptor != null) {
			getStepTwoPanel().getSignalOptionsPanel().getRawSignalOptionsPanel().fillPanelFromModel(rawSignalDescriptor);
		}

	}

	/**
	 * Fills the {@link OpenDocumentDescriptor model} with the data from this
	 * dialog (user input):
	 * <ul>
	 * <li>{@link OpenDocumentDescriptor#setFile(File) sets} the
	 * {@link EmbeddedFileChooser#getSelectedFile() selected file}
	 * in the model,</li>
	 * <li>depending on the {@link ManagedDocumentType type} of the
	 * {@link Document}:
	 * <ul><li>for a {@link BookDocument} does nothing</li>
	 * <li>for a {@link TagDocument}:
	 * <ul><li>sets the {@link OpenTagDescriptor#setParent(SignalDocument)
	 * parent} in the {@link OpenTagDescriptor descriptor},</li>
	 * <li>if it is a {@link LegacyTagImporter legacy tag}
	 * {@link OpenTagDescriptor#setExistingDocument(TagDocument) sets} the
	 * {@link TagDocument document} in OpenTagDescriptor</li></ul>
	 * <li>for a {@link SignalDocument signal document}:
	 * <ul><li>{@link OpenSignalDescriptor#setMethod(OpenSignalMethod) sets}
	 * the method in the {@link OpenSignalDescriptor descriptor},</li>
	 * <li>depending on the method:
	 * <ul><li>for SignalML:<ul><li>{@link OpenSignalDescriptor#setCodec(
	 * SignalMLCodec) sets} the {@link SignalMLCodec codec},</li>
	 * <li> {@link PagingParametersPanel#fillModelFromPanel(
	 * org.signalml.app.model.PagingParameterDescriptor) fills} the paging
	 * parameters,</li></ul></li>
	 * <li>for RAW:<ul><li>
	 * {@link RawSignalOptionsPanel#fillModelFromPanel(RawSignalDescriptor)
	 * fills} the {@link RawSignalDescriptor descriptor} from the
	 * {@link RawSignalOptionsPanel},</li>
	 * <li>{@link PagingParametersPanel#fillModelFromPanel(
	 * org.signalml.app.model.PagingParameterDescriptor) fills} the paging
	 * parameters</li></ul></li></ul></li></ul></li></ul></li></ul>
	 */
	@Override
	public void fillModelFromDialog(Object model) throws SignalMLException {

		OpenDocumentDescriptor ofd = (OpenDocumentDescriptor) model;
		File selectedFile = getStepOnePanel().getFileChooser().getSelectedFile();
		ofd.getOpenSignalDescriptor().getOpenFileSignalDescriptor().setFile(selectedFile);

		if (targetType == null) {
			logger.error("Target type null");
			throw new SignalMLException("error.invalidValue");
		}
		ofd.setType(targetType);
		if (targetType.equals(ManagedDocumentType.SIGNAL)) {

			OpenFileSignalDescriptor osd = ofd.getOpenSignalDescriptor().getOpenFileSignalDescriptor();

			OpenSignalOptionsPanel signalOptionsPanel = getStepTwoPanel().getSignalOptionsPanel();

			int method = signalOptionsPanel.getMethodComboBox().getSelectedIndex();
			if (method == 0) {

				osd.setMethod(FileOpenSignalMethod.SIGNALML);

				SignalParameterDescriptor spd = osd.getParameters();

				osd.setCodec((SignalMLCodec) signalOptionsPanel.getSignalMLOptionsPanel().getSignalMLDriverComboBox().getSelectedItem());
				signalOptionsPanel.getPagingSignalParamersPanel().fillModelFromPanel(spd);

			} else if (method == 1) {

				osd.setMethod(FileOpenSignalMethod.RAW);

				RawSignalDescriptor descriptor = currentRawSignalDescriptor;
				if (descriptor == null) {
					descriptor = new RawSignalDescriptor();
					descriptor.setCalibrationGain(1.0F);
					descriptor.setCalibrationOffset(0.0F);
				}
				signalOptionsPanel.getRawSignalOptionsPanel().fillModelFromPanel(descriptor);

				osd.setRawSignalDescriptor(descriptor);

			} else {
				throw new SignalMLException("error.invalidValue");
			}

		} else if (targetType.equals(ManagedDocumentType.BOOK)) {

			// nothing to do so far

		} else if (targetType.equals(ManagedDocumentType.TAG)) {

			OpenTagDescriptor otd = ofd.getTagOptions();
			int index = getStepTwoPanel().getTagOptionsPanel().getSignalDocumentComboBox().getSelectedIndex();

			SignalDocument signalDocument = (SignalDocument) documentManager.getDocumentAt(ManagedDocumentType.SIGNAL, index);

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

	/**
	 * Model must be of type {@link OpenDocumentDescriptor}.
	 */
	@Override
	public boolean supportsModelClass(Class<?> clazz) {
		return OpenDocumentDescriptor.class.isAssignableFrom(clazz);
	}

	@Override
	protected URL getContextHelpURL() {
		if (contextHelpURL == null) {
			try {
				contextHelpURL = (new ClassPathResource("org/signalml/help/contents.html")).getURL();
				contextHelpURL = new URL(contextHelpURL.toExternalForm() + "#opendoc");
			} catch (IOException ex) {
				logger.error("Failed to get help URL", ex);
			}
		}
		return contextHelpURL;
	}

	/**
	 * Returns the {@link SignalMLCodecManager manager} of
	 * {@link SignalMLCodec codecs}.
	 * @return the manager of codecs
	 */
	public SignalMLCodecManager getCodecManager() {
		return codecManager;
	}

	/**
	 * Sets the {@link SignalMLCodecManager manager} of
	 * {@link SignalMLCodec codecs}.
	 * @param codecManager the manager of codecs
	 */
	public void setCodecManager(SignalMLCodecManager codecManager) {
		this.codecManager = codecManager;
	}

	/**
	 * Returns the {@link RegisterCodecDialog dialog} to register
	 * {@link SignalMLCodec codecs}
	 * If the dialog doesn't exist it is created
	 * @return the dialog to register codecs
	 */
	protected RegisterCodecDialog getRegisterCodecDialog() {
		if (registerCodecDialog == null) {
			registerCodecDialog = new RegisterCodecDialog(this,true);
			registerCodecDialog.setCodecManager(codecManager);
			registerCodecDialog.setProfileDir(profileDir);
		}
		return registerCodecDialog;
	}

	/**
	 * Returns a {@link DocumentDetector detector} of the
	 * {@link ManagedDocumentType types} of a {@link Document documents}
	 * stored in the file.
	 * @return the document type detector
	 */
	public DocumentDetector getDocumentDetector() {
		return documentDetector;
	}

	/**
	 * Sets a {@link DocumentDetector detector} of the
	 * {@link ManagedDocumentType types} of a {@link Document documents}
	 * stored in the file.
	 * @param documentDetector the document type detector
	 */
	public void setDocumentDetector(DocumentDetector documentDetector) {
		this.documentDetector = documentDetector;
	}

	/**
	 * Returns the {@link DocumentManager manager} of {@link Document
	 * documents}.
	 * @return the manager of documents
	 */
	public DocumentManager getDocumentManager() {
		return documentManager;
	}

	/**
	 * Sets the {@link DocumentManager manager} of {@link Document
	 * documents}.
	 * @param documentManager the manager of documents
	 */
	public void setDocumentManager(DocumentManager documentManager) {
		this.documentManager = documentManager;
	}

	/**
	 * Returns the {@link ApplicationConfiguration configuration} of Svarog.
	 * @return the configuration of Svarog
	 */
	public ApplicationConfiguration getApplicationConfig() {
		return applicationConfig;
	}

	/**
	 * Sets the {@link ApplicationConfiguration configuration} of Svarog.
	 * @param applicationConfig the configuration of Svarog
	 */
	public void setApplicationConfig(ApplicationConfiguration applicationConfig) {
		this.applicationConfig = applicationConfig;
	}

	/**
	 * Returns the profile directory.
	 * @return the profile directory
	 */
	public File getProfileDir() {
		return profileDir;
	}

	/**
	 * Sets the profile directory.
	 * @param profileDir the profile directory
	 */
	public void setProfileDir(File profileDir) {
		this.profileDir = profileDir;
	}

	/**
	 * Returns the {@link PleaseWaitDialog dialog} which tells the user
	 * to wait.
	 * If the dialog doesn't exist it is created.
	 * @return the dialog which tells the user to wait
	 */
	protected PleaseWaitDialog getPleaseWaitDialog() {
		if (pleaseWaitDialog == null) {
			pleaseWaitDialog = new PleaseWaitDialog(this);
			pleaseWaitDialog.initializeNow();
		}
		return pleaseWaitDialog;
	}

	/**
	 * Returns the {@link ViewerFileChooser chooser} for files.
	 * @return the chooser for files
	 */
	public ViewerFileChooser getFileChooser() {
		return fileChooser;
	}

	/**
	 * Sets the {@link ViewerFileChooser chooser} for files.
	 * @param fileChooser the  chooser for files
	 */
	public void setFileChooser(ViewerFileChooser fileChooser) {
		this.fileChooser = fileChooser;
	}

	/**
	 * The actions which {@link RawSignalDescriptorReader#readDocument(File)
	 * reads} the parameters of a (RAW) signal from an XML file.
	 */
	protected class ReadXMLManifestAction extends AbstractAction {

		private static final long serialVersionUID = 1L;
		
		/**
		 * the {@link RawSignalDescriptorReader reader} used to read the
		 * parameters of the RAW signal
		 */
		private RawSignalDescriptorReader reader;

		/**
		 * Constructor. Sets the icon and description.
		 */
		public ReadXMLManifestAction() {
			super(_("Read manifest..."));
			putValue(AbstractAction.SMALL_ICON, IconUtils.loadClassPathIcon("org/signalml/app/icon/script_load.png"));
			putValue(AbstractAction.SHORT_DESCRIPTION,_("Read signal parameters from XML manifest"));
			//putValue(AbstractAction.ACCELERATOR_KEY, KeyStroke.getKeyStroke("F1"));
		}

		/**
		 * Called when this action is performed.
		 * <ul>
		 * <li>{@link ViewerFileChooser#chooseReadXMLManifest(File, File,
		 * java.awt.Component) Asks} the user for the XML file with the
		 * parameters of the raw signal.</li>
		 * <li>{@link RawSignalDescriptorReader Reads} the {@link RawSignalDescriptor
		 * parametrs}.</li>
		 * <li>Fills the {@link RawSignalOptionsPanel#fillPanelFromModel(
		 * RawSignalDescriptor) RawSignalOptionsPanel} and
		 * {@link PagingParametersPanel#fillModelFromPanel(RawSignalDescriptor)
		 * PagingParametersPanel}.</li><ul>
		 */
		public void actionPerformed(ActionEvent ev) {

			File selectedFile = getStepOnePanel().getFileChooser().getSelectedFile();
			File directory = null;
			File fileSuggestion = null;
			if (selectedFile != null) {
				directory = selectedFile.getParentFile();

				fileSuggestion = Util.changeOrAddFileExtension(selectedFile, "xml");

			}

			if (directory == null) {
				directory = new File(System.getProperty("user.dir"));
			}

			File xmlFile = fileChooser.chooseReadXMLManifest(directory, fileSuggestion, OpenDocumentDialog.this);
			if (xmlFile == null) {
				return;
			}

			if (reader == null) {
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

