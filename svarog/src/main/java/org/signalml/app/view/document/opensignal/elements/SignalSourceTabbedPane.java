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
import org.signalml.app.model.document.opensignal.AbstractOpenSignalDescriptor;
import org.signalml.app.view.components.FileChooserPanel;
import org.signalml.app.view.document.opensignal_old.SignalSource;
import org.signalml.app.view.document.opensignal_old.monitor.ChooseExperimentPanel;
import org.signalml.domain.signal.raw.RawSignalDescriptor;
import org.signalml.domain.signal.raw.RawSignalDescriptorReader;
import org.signalml.plugin.export.SignalMLException;
import org.signalml.util.Util;

public class SignalSourceTabbedPane extends JTabbedPane implements PropertyChangeListener {

	public static final String OPEN_SIGNAL_DESCRIPTOR_PROPERTY = "openSignalDescriptorProperty";
	
	/**
	 * The panel for choosing a file to be opened.
	 */
	private FileChooserPanel fileChooserPanel;
	private ChooseExperimentPanel chooseExperimentPanel;
	
	private AbstractOpenSignalDescriptor openSignalDescriptor;
	
	public SignalSourceTabbedPane() {
		chooseExperimentPanel = new ChooseExperimentPanel();
		
		addTab(_("FILE"), getFileChooserPanel());
		addTab(_("MONITOR"), chooseExperimentPanel);
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
	
	public SignalSource getSelectedSignalSource() {
		if (getSelectedComponent() == getFileChooserPanel())
			return SignalSource.FILE;
		return SignalSource.OPENBCI;
	}

	@Override
	public void propertyChange(PropertyChangeEvent event) {
		if (JFileChooser.SELECTED_FILE_CHANGED_PROPERTY.equals(event.getPropertyName())) {
			File file = fileChooserPanel.getSelectedFile();
			
			if (file == null)
				return;
			
			String extension = Util.getFileExtension(file, false);
			System.out.println(extension);
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
			firePropertyChange(OPEN_SIGNAL_DESCRIPTOR_PROPERTY, null, openSignalDescriptor);
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

}
