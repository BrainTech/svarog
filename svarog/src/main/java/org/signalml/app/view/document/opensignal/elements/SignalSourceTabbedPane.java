package org.signalml.app.view.document.opensignal.elements;

import static org.signalml.app.util.i18n.SvarogI18n._;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.io.IOException;

import javax.swing.JFileChooser;
import javax.swing.JTabbedPane;

import org.signalml.app.SvarogApplication;
import org.signalml.app.document.ManagedDocumentType;
import org.signalml.app.document.SignalMLDocument;
import org.signalml.app.model.document.opensignal.AbstractOpenSignalDescriptor;
import org.signalml.app.model.document.opensignal.ExperimentDescriptor;
import org.signalml.app.model.document.opensignal.SignalMLDescriptor;
import org.signalml.app.view.components.FileChooserPanel;
import org.signalml.app.view.components.dialogs.errors.Dialogs;
import org.signalml.app.view.document.opensignal_old.AbstractSignalParametersPanel;
import org.signalml.app.view.document.opensignal_old.SignalSource;
import org.signalml.app.view.document.opensignal_old.monitor.ChooseExperimentPanel;
import org.signalml.app.view.workspace.ViewerElementManager;
import org.signalml.app.worker.document.OpenSignalMLDocumentWorker;
import org.signalml.codec.SignalMLCodec;
import org.signalml.codec.SignalMLCodecManager;
import org.signalml.domain.signal.raw.RawSignalDescriptor;
import org.signalml.domain.signal.raw.RawSignalDescriptorReader;
import org.signalml.plugin.export.SignalMLException;
import org.signalml.util.Util;

public class SignalSourceTabbedPane extends JTabbedPane implements PropertyChangeListener {

	public static final String OPEN_SIGNAL_DESCRIPTOR_PROPERTY = "openSignalDescriptorProperty";
	
	private ViewerElementManager viewerElementManager;
	/**
	 * The panel for choosing a file to be opened.
	 */
	private FileChooserPanel fileChooserPanel;
	private ChooseExperimentPanel chooseExperimentPanel;
	
	private AbstractOpenSignalDescriptor openSignalDescriptor;
	
	public SignalSourceTabbedPane(ViewerElementManager viewerElementManager) {
		this.viewerElementManager = viewerElementManager;
		addTab(_("FILE"), getFileChooserPanel());
		addTab(_("MONITOR"), getChooseExperimentPanel());
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
		if (JFileChooser.SELECTED_FILE_CHANGED_PROPERTY.equals(propertyName)) {
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
		
		if (file == null)
			return;
		
		String extension = Util.getFileExtension(file, false);
		if (extension == null)
			return;

		if (extension.equalsIgnoreCase("raw") || extension.equalsIgnoreCase("bin")) {
			try {
				readRawFileMetadata(file);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (SignalMLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		else {
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
				Dialogs.showError(_("No SignalML codec was found to open this file!"));
				fireOpenSignalDescriptorChanged();
				return;
			}
			
			readSignalMLMetadata(file, codec);
		}
		fireOpenSignalDescriptorChanged();
	}
	
	protected void fireOpenSignalDescriptorChanged() {
		firePropertyChange(OPEN_SIGNAL_DESCRIPTOR_PROPERTY, null, openSignalDescriptor);
	}
	
	protected void readSignalMLMetadata(File signalFile, SignalMLCodec codec) {
		File file = getFileChooserPanel().getSelectedFile();

		if (codec == null) {
			Dialogs.showError(_("Please select a codec first!"));
			return;
		}
		if (file == null) {
			Dialogs.showError(_("Please select a signalML file first!"));
			return;
		}

		OpenSignalMLDocumentWorker worker = new OpenSignalMLDocumentWorker(codec, file);
		worker.execute();

		SignalMLDocument signalMLDocument = null;
		try {
			signalMLDocument = worker.get();

			int channelCount = signalMLDocument.getChannelCount();
			float samplingFrequency = signalMLDocument.getSamplingFrequency();

			String[] channelLabels = new String[channelCount];
			for (int i = 0; i < channelCount; i++) {
				channelLabels[i] = signalMLDocument.getSampleSource().getLabel(i);
			}
			
			openSignalDescriptor = new SignalMLDescriptor();
			SignalMLDescriptor signalMLDescriptor = (SignalMLDescriptor) openSignalDescriptor;
			signalMLDescriptor.setCodec(codec);
			signalMLDescriptor.setChannelLabels(channelLabels);
			signalMLDescriptor.getSignalParameters().setChannelCount(channelCount);
			signalMLDescriptor.getSignalParameters().setSamplingFrequency(samplingFrequency);

			signalMLDocument.closeDocument();
		} catch (Exception e) {
			Dialogs.showError(_("There was an error while loading the file - did you select a correct SignalML file?"));
			e.printStackTrace();
		}
	}

	protected void readRawFileMetadata(File signalFile) throws IOException, SignalMLException {

		File xmlManifestFile = Util.changeOrAddFileExtension(signalFile, "xml");

		if (!xmlManifestFile.exists())
			xmlManifestFile = Util.changeOrAddFileExtension(signalFile, "svarog.info");
		
		RawSignalDescriptorReader reader = new RawSignalDescriptorReader();
		openSignalDescriptor = reader.readDocument(xmlManifestFile);
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

}
