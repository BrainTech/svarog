package org.signalml.app.view.document.opensignal.elements;

import com.alee.laf.tabbedpane.WebTabbedPane;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;
import javax.swing.JFileChooser;
import org.apache.log4j.Logger;
import org.signalml.app.SvarogApplication;
import org.signalml.app.action.document.RegisterCodecAction;
import org.signalml.app.document.ManagedDocumentType;
import org.signalml.app.document.signal.SignalMLDocument;
import org.signalml.app.model.document.opensignal.AbstractOpenSignalDescriptor;
import org.signalml.app.model.document.opensignal.SignalMLDescriptor;
import org.signalml.app.model.document.opensignal.elements.FileOpenSignalMethod;
import org.signalml.app.model.document.opensignal.elements.SignalSource;
import static org.signalml.app.util.i18n.SvarogI18n._;
import static org.signalml.app.util.i18n.SvarogI18n._R;
import org.signalml.app.view.common.components.filechooser.FileChooserPanel;
import org.signalml.app.view.common.dialogs.errors.Dialogs;
import org.signalml.app.view.workspace.ViewerElementManager;
import org.signalml.app.worker.document.OpenSignalMLDocumentWorker;
import org.signalml.app.worker.monitor.ObciServerCapabilities;
import org.signalml.codec.SignalMLCodec;
import org.signalml.codec.SignalMLCodecManager;
import org.signalml.domain.signal.ascii.AsciiSignalDescriptorReader;
import org.signalml.domain.signal.raw.RawSignalDescriptor;
import org.signalml.domain.signal.raw.RawSignalDescriptorReader;
import org.signalml.util.Util;

public class SignalSourceTabbedPane extends WebTabbedPane implements PropertyChangeListener, ItemListener {
	protected static final Logger logger = Logger.getLogger(SignalSourceTabbedPane.class);

	public static final String TAB_AMPLIFIERS = "Amplifiers";
	public static final String TAB_EXPERIMENTS = "Experiments";

	public static final String OPEN_SIGNAL_DESCRIPTOR_PROPERTY = "openSignalDescriptorProperty";

	private ViewerElementManager viewerElementManager;
	/**
	 * The panel for choosing a file to be opened.
	 */
	private FileChooserPanel fileChooserPanel;
	private ChooseExperimentPanel chooseExperimentPanel;
	private ChooseAmplifierPanel chooseAmplifierPanel;


	private AbstractOpenSignalDescriptor openSignalDescriptor;
	private Object fileTypeMethod = FileOpenSignalMethod.AUTODETECT;

	public SignalSourceTabbedPane(ViewerElementManager viewerElementManager) {
		this(viewerElementManager, null);
	}

	public SignalSourceTabbedPane(ViewerElementManager viewerElementManager, String selectedSourceTab) {
		this.viewerElementManager = viewerElementManager;
		addTab(_("File"), getFileChooserPanel());
		int selectedIndex = 0;
		if (ObciServerCapabilities.getSharedInstance().hasOnlineExperiments()) {
			if (TAB_EXPERIMENTS.equals(selectedSourceTab)) {
				selectedIndex = getTabCount();
			}
			addTab(_("Online experiments"), getChooseExperimentPanel());
		}
		if (ObciServerCapabilities.getSharedInstance().hasOnlineAmplifiers()) {
			if (TAB_AMPLIFIERS.equals(selectedSourceTab)) {
				selectedIndex = getTabCount();
			}
			addTab(_("Online amplifiers"), getChooseAmplifierPanel());
		}
		setSelectedIndex(selectedIndex);
	}

	/**
	 * Returns the panel for choosing which signal file should be opened.
	 * @return the panel for choosing which signal file should be opened
	 */
	public FileChooserPanel getFileChooserPanel() {
		if (fileChooserPanel == null) {
			fileChooserPanel = new FileChooserPanel(ManagedDocumentType.SIGNAL);

			String lastFileChooserPath = SvarogApplication.getApplicationConfiguration().getLastFileChooserPath();
			getFileChooserPanel().getFileChooser().setCurrentDirectory(new File(lastFileChooserPath));
			fileChooserPanel.getFileChooser().addPropertyChangeListener(this);
		}
		return fileChooserPanel;
	}

	public ChooseExperimentPanel getChooseExperimentPanel() {
		if (chooseExperimentPanel == null) {
			chooseExperimentPanel = new ChooseExperimentPanel();
			chooseExperimentPanel.addPropertyChangeListener(this);
		}
		return chooseExperimentPanel;
	}
	
	public ChooseAmplifierPanel getChooseAmplifierPanel() {
		if (chooseAmplifierPanel == null) {
			chooseAmplifierPanel = new ChooseAmplifierPanel();
			chooseAmplifierPanel.addPropertyChangeListener(this);
		}
		return chooseAmplifierPanel;
	}

	public SignalSource getSelectedSignalSource() {
		if (getSelectedComponent() == getFileChooserPanel())
			return SignalSource.FILE;
		return SignalSource.OPENBCI;
	}

	@Override
	public void propertyChange(PropertyChangeEvent event) {
		String propertyName = event.getPropertyName();
		if (JFileChooser.SELECTED_FILE_CHANGED_PROPERTY.equals(propertyName)
				|| RegisterCodecAction.CODEC_REGISTERED.equals(propertyName)) {
			updatedSelectedFile();
		}
		else if (JFileChooser.DIRECTORY_CHANGED_PROPERTY.equals(propertyName)) {
			openSignalDescriptor = null;
			fireOpenSignalDescriptorChanged();
		}
		
		else if (ChooseAmplifierPanel.AMPLIFIER_SELECTED_PROPERTY.equals(propertyName)) {
			updateSelectedAmplifier();
		}
		
		else if (ChooseExperimentPanel.EXPERIMENT_SELECTED_PROPERTY.equals(propertyName)) {
			updateSelectedExperiment();
		}
	}

	protected void updateSelectedExperiment() {
		openSignalDescriptor = chooseExperimentPanel.getSelectedExperiment();
		fireOpenSignalDescriptorChanged();
	}
	
	protected void updateSelectedAmplifier() {
		openSignalDescriptor = chooseAmplifierPanel.getSelectedAmplifier();
		fireOpenSignalDescriptorChanged();
	}

	protected void updatedSelectedFile() {
		File file = fileChooserPanel.getSelectedFile();

		if (file == null || file.isDirectory()) {
			openSignalDescriptor = null;
			fireOpenSignalDescriptorChanged();
			return;
		}

		if (fileTypeMethod == FileOpenSignalMethod.AUTODETECT)
			autodetectFileTypeAndReadMetadata(file);
		else if (fileTypeMethod == FileOpenSignalMethod.CSV)
			readCsvFileMetadata(file);
		else if (fileTypeMethod == FileOpenSignalMethod.RAW)
			readRawFileMetadata(file);
		else if (fileTypeMethod instanceof SignalMLCodec) {
			SignalMLCodec codec = (SignalMLCodec) fileTypeMethod;
			readSignalMLMetadata(file, codec);
		}
		else {
			openSignalDescriptor = null;
		}

		fireOpenSignalDescriptorChanged();
	}

	protected void autodetectFileTypeAndReadMetadata(File file) {
		String extension = Util.getFileExtension(file, false);

		if ("edf".equalsIgnoreCase(extension) || "d".equals(extension)){
			String formatName = null;
			if (extension.equalsIgnoreCase("edf")) {
				formatName = "EDF";
			}
			else if (extension.equalsIgnoreCase("d")) {
				formatName = "EASYS";
			}

			SignalMLCodecManager codecManager = viewerElementManager.getCodecManager();
			SignalMLCodec codec = codecManager.getCodecForFormat(formatName);

			if (codec == null) {
				openSignalDescriptor = null;
				Dialogs.showError(_("No SignalML codec was found to open this file!"));
				fireOpenSignalDescriptorChanged();
				return;
			}

			readSignalMLMetadata(file, codec);
		} else if ("csv".equalsIgnoreCase(extension) || "txt".equalsIgnoreCase(extension)) {
			readCsvFileMetadata(file);
		} else {
			readRawFileMetadata(file);
		}
	}

	protected void fireOpenSignalDescriptorChanged() {
		firePropertyChange(OPEN_SIGNAL_DESCRIPTOR_PROPERTY, null, openSignalDescriptor);
	}

	protected void readSignalMLMetadata(File signalFile, SignalMLCodec codec) {
		File file = getFileChooserPanel().getSelectedFile();

		OpenSignalMLDocumentWorker worker = new OpenSignalMLDocumentWorker(codec, file);
		worker.execute();

		SignalMLDocument signalMLDocument = null;
		try {
			signalMLDocument = worker.get();
			openSignalDescriptor = new SignalMLDescriptor(signalMLDocument);
			openSignalDescriptor.setCorrectlyRead(true);
			signalMLDocument.closeDocument();
		} catch (Exception e) {
			Dialogs.showError(_R("There was an error while loading the file—did you select a correct SignalML file?"));
			logger.error("", e);
			openSignalDescriptor = null;
		}
	}

	protected void readCsvFileMetadata(File signalFile) {
		AsciiSignalDescriptorReader reader = new AsciiSignalDescriptorReader();
		try {
			openSignalDescriptor = reader.readDocument(signalFile);
			openSignalDescriptor.setCorrectlyRead(true);
		} catch (Exception ex) {
			logger.error("error while reading CSV file", ex);
			Dialogs.showError(_("Could not read contents of CSV signal file!"));
			openSignalDescriptor = new RawSignalDescriptor();
			openSignalDescriptor.setCorrectlyRead(false);
		}
	}

	protected void readRawFileMetadata(File signalFile) {

		File xmlManifestFile = Util.changeOrAddFileExtension(signalFile, "xml");

		if (!xmlManifestFile.exists())
			xmlManifestFile = Util.changeOrAddFileExtension(signalFile, "svarog.info");

		RawSignalDescriptorReader reader = new RawSignalDescriptorReader();
		openSignalDescriptor = null;
		if (!xmlManifestFile.exists()) {
			if (Dialogs.DIALOG_OPTIONS.YES == Dialogs.showWarningYesNoDialog(_("XML manifest not found—would you like to choose the XML file manualy?"))) {
				JFileChooser fileChooser = new JFileChooser(signalFile);
				fileChooser.showOpenDialog(null);
				xmlManifestFile =  fileChooser.getSelectedFile();
				if (xmlManifestFile == null)
					return;
			}
			else {
				openSignalDescriptor = new RawSignalDescriptor();
				openSignalDescriptor.setCorrectlyRead(false);
				return;
			}
		}

		try {
			openSignalDescriptor = reader.readDocument(xmlManifestFile);
			openSignalDescriptor.setCorrectlyRead(true);
		} catch (Exception e) {
			logger.error("", e);
			Dialogs.showError(_("There was an error while reading the XML manifest. Please input the signal parameters manually."));
			openSignalDescriptor = new RawSignalDescriptor();
			openSignalDescriptor.setCorrectlyRead(false);
		}
	}

	public AbstractOpenSignalDescriptor getOpenSignalDescriptor() {
		return openSignalDescriptor;
	}

	@Override
	protected void fireStateChanged() {
		if (this.getSelectedComponent() == fileChooserPanel) {
			updatedSelectedFile();
		}
		
		else if (this.getSelectedComponent() == chooseAmplifierPanel)
		{
			updateSelectedAmplifier();
		}
		
		else {
			updateSelectedExperiment();
		}

		super.fireStateChanged();
	}

	/**
	 * Invoked when the selected file type changed.
	 * @param
	 */
	@Override
	public void itemStateChanged(ItemEvent event) {
		if (event.getStateChange() == ItemEvent.SELECTED) {
			fileTypeMethod = event.getItem();
			fireStateChanged();
		}
	}

	public void onDialogCloseWithOK() {
		if (getSelectedSignalSource() == SignalSource.FILE)
			getFileChooserPanel().getFileChooser().lastDirectoryChanged();
	}
}
