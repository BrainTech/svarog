package org.signalml.app.view.document.opensignal.elements;

import static org.signalml.app.util.i18n.SvarogI18n._;
import static org.signalml.app.util.i18n.SvarogI18n._R;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.io.IOException;

import javax.swing.JFileChooser;
import javax.swing.JTabbedPane;
import javax.swing.event.DocumentEvent.EventType;

import org.signalml.app.SvarogApplication;
import org.signalml.app.document.ManagedDocumentType;
import org.signalml.app.document.SignalMLDocument;
import org.signalml.app.model.document.opensignal.AbstractOpenSignalDescriptor;
import org.signalml.app.model.document.opensignal.ExperimentDescriptor;
import org.signalml.app.model.document.opensignal.SignalMLDescriptor;
import org.signalml.app.model.document.opensignal.elements.FileOpenSignalMethod;
import org.signalml.app.model.document.opensignal.elements.SignalSource;
import org.signalml.app.view.components.FileChooserPanel;
import org.signalml.app.view.components.dialogs.errors.Dialogs;
import org.signalml.app.view.workspace.ViewerElementManager;
import org.signalml.app.worker.document.OpenSignalMLDocumentWorker;
import org.signalml.codec.SignalMLCodec;
import org.signalml.codec.SignalMLCodecManager;
import org.signalml.domain.signal.raw.RawSignalDescriptor;
import org.signalml.domain.signal.raw.RawSignalDescriptorReader;
import org.signalml.plugin.export.SignalMLException;
import org.signalml.util.Util;

import org.apache.log4j.Logger;

public class SignalSourceTabbedPane extends JTabbedPane implements PropertyChangeListener, ItemListener {
	protected static final Logger log = Logger.getLogger(SignalSourceTabbedPane.class);

	public static final String OPEN_SIGNAL_DESCRIPTOR_PROPERTY = "openSignalDescriptorProperty";

	private ViewerElementManager viewerElementManager;
	/**
	 * The panel for choosing a file to be opened.
	 */
	private FileChooserPanel fileChooserPanel;
	private ChooseExperimentPanel chooseExperimentPanel;

	private AbstractOpenSignalDescriptor openSignalDescriptor;
	private Object fileTypeMethod = FileOpenSignalMethod.AUTODETECT;

	public SignalSourceTabbedPane(ViewerElementManager viewerElementManager) {
		this.viewerElementManager = viewerElementManager;
		addTab(_("FILE"), getFileChooserPanel());
		addTab(_("ONLINE"), getChooseExperimentPanel());
	}

	/**
	 * Returns the panel for choosing which signal file should be opened.
	 * @return the panel for choosing which signal file should be opened
	 */
	public FileChooserPanel getFileChooserPanel() {
		if (fileChooserPanel == null) {
			fileChooserPanel = new FileChooserPanel( ManagedDocumentType.SIGNAL);
			
			String lastFileChooserPath = SvarogApplication.getApplicationConfiguration().getLastFileChooserPath();
			getFileChooserPanel().getFileChooser().setCurrentDirectory(new File(lastFileChooserPath));
			fileChooserPanel.getFileChooser().addPropertyChangeListener(this);
		}
		return fileChooserPanel;
	}
	
	protected ChooseExperimentPanel getChooseExperimentPanel() {
		if (chooseExperimentPanel == null) {
			chooseExperimentPanel = new ChooseExperimentPanel();
			chooseExperimentPanel.addPropertyChangeListener(this);
		}
		return chooseExperimentPanel;
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
				||JFileChooser.DIRECTORY_CHANGED_PROPERTY.equals(propertyName)) {
			updatedSelectedFile();
		}
		else if (ChooseExperimentPanel.EXPERIMENT_SELECTED_PROPERTY.equals(propertyName)) {
			updateSelectedExperiment();
		}
	}
	
	protected void updateSelectedExperiment() {
		openSignalDescriptor = chooseExperimentPanel.getSelectedExperiment();
		fireOpenSignalDescriptorChanged();
	}

	protected void updatedSelectedFile() {
		File file = fileChooserPanel.getSelectedFile();
		String extension = Util.getFileExtension(file, false);

		if (file == null || extension == null) {
			openSignalDescriptor = null;
			fireOpenSignalDescriptorChanged();
			return;
		}

		if (fileTypeMethod == FileOpenSignalMethod.AUTODETECT)
			autodetectFileTypeAndReadMetadata(file);
		else if (fileTypeMethod == FileOpenSignalMethod.RAW)
			readRawFileMetadata(file);
		else if (fileTypeMethod instanceof SignalMLCodec){
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
		if (extension.equalsIgnoreCase("raw") || extension.equalsIgnoreCase("bin")) {
			readRawFileMetadata(file);
		}
		else {
			String formatName = null;
			if (extension.equalsIgnoreCase("edf")) {
				formatName = "EDF";
			}
			else if (extension.equalsIgnoreCase("d")) {
				formatName = "EASYS";
			}
			else
				return;

			SignalMLCodecManager codecManager = viewerElementManager.getCodecManager();
			SignalMLCodec codec = codecManager.getCodecForFormat(formatName);
			
			if (codec == null) {
				Dialogs.showError(_("No SignalML codec was found to open this file!"));
				fireOpenSignalDescriptorChanged();
				fileChooserPanel.getFileChooser().setSelectedFile(null);
				return;
			}
			
			readSignalMLMetadata(file, codec);
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
			Dialogs.showError(_R("There was an error while loading the file - did you select a correct SignalML file?"));
			e.printStackTrace();
		}
	}

	protected void readRawFileMetadata(File signalFile) {

		File xmlManifestFile = Util.changeOrAddFileExtension(signalFile, "xml");

		if (!xmlManifestFile.exists())
			xmlManifestFile = Util.changeOrAddFileExtension(signalFile, "svarog.info");
		
		RawSignalDescriptorReader reader = new RawSignalDescriptorReader();
		openSignalDescriptor = null;
		if (!xmlManifestFile.exists()) {
			if (Dialogs.DIALOG_OPTIONS.YES == Dialogs.showWarningYesNoDialog(_("XML manifest not found - would you like to choose the XML file manualy?"))) {
				JFileChooser fileChooser = new JFileChooser(signalFile);
				fileChooser.showOpenDialog(null);
				xmlManifestFile =  fileChooser.getSelectedFile();
				if(xmlManifestFile == null)
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
			e.printStackTrace();
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
		}
	}

	public void onDialogCloseWithOK() {
		log.debug("onDialogCloseWithOK");
		if(getSelectedSignalSource() == SignalSource.FILE)
			getFileChooserPanel().getFileChooser().lastDirectoryChanged();
	}
}
