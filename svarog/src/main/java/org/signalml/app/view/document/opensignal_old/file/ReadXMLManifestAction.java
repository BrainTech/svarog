/* ReadXMLManifestAction.java created 2011-03-12
 *
 */

package org.signalml.app.view.document.opensignal_old.file;

import java.awt.event.ActionEvent;
import java.io.File;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.AbstractAction;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import org.signalml.app.util.IconUtils;
import org.signalml.app.view.components.EmbeddedFileChooser;
import org.signalml.app.view.components.SignalMLFileChooser;
import org.signalml.app.view.components.dialogs.errors.Dialogs;
import org.signalml.app.view.workspace.ViewerFileChooser;
import org.signalml.app.config.ApplicationConfiguration;
import org.signalml.domain.montage.system.EegSystemName;
import org.signalml.domain.signal.raw.RawSignalDescriptor;
import org.signalml.domain.signal.raw.RawSignalDescriptorReader;
import org.signalml.plugin.export.SignalMLException;
import org.signalml.plugin.export.view.AbstractSignalMLAction;
import org.signalml.util.Util;

import static org.signalml.app.util.i18n.SvarogI18n._;


/**
 * The actions which {@link RawSignalDescriptorReader#readDocument(File)
 * reads} the parameters of a (RAW) signal from an XML file.
 */
public class ReadXMLManifestAction extends AbstractSignalMLAction {

	private static final long serialVersionUID = 1L;

	/**
	 * the {@link RawSignalDescriptorReader reader} used to read the
	 * parameters of the RAW signal
	 */
	private RawSignalDescriptorReader reader;

	/**
	 * The panel containing parameters for the signal. It is filled with
	 * the data from the manifest that will be read in response to this action.
	 */
	private SignalParametersPanelForRawSignalFile parentSignalParametersPanel;

	/**
	 * The file chooser for a signal file. Used to open
	 * the same directory as in the fileChooser opened by this action.
	 */
	private EmbeddedFileChooser signalFileChooser;

	private ApplicationConfiguration applicationConfig;

	public void setApplicationConfiguration(ApplicationConfiguration applicationConfig){
		this.applicationConfig = applicationConfig;
	}

	/**
	 * Constructor.
	 * @param parentSignalParametersPanel the signal parameters panel which
	 * should be filled after calling this action.
	 */
	public ReadXMLManifestAction(
		SignalParametersPanelForRawSignalFile parentSignalParametersPanel) {

		super();
		this.setText(_("Read manifest..."));
		this.parentSignalParametersPanel = parentSignalParametersPanel;

		putValue(AbstractAction.SMALL_ICON, IconUtils.loadClassPathIcon("org/signalml/app/icon/script_load.png"));
		putValue(AbstractAction.SHORT_DESCRIPTION,_("Read signal parameters from XML manifest"));
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
	@Override
	public void actionPerformed(ActionEvent ev) {

		File selectedFile = signalFileChooser.getSelectedFile();
		File directory = null;
		File fileSuggestion = null;
		if (selectedFile != null) {
			directory = selectedFile.getParentFile();

			fileSuggestion = Util.changeOrAddFileExtension(selectedFile, "xml");

			if (!fileSuggestion.exists())
				fileSuggestion = Util.changeOrAddFileExtension(selectedFile, "svarog.info");
		}

		if (directory == null) {
			directory = signalFileChooser.getCurrentDirectory();
		}

		JFileChooser fileChooser = new SignalMLFileChooser();
		fileChooser.setCurrentDirectory(directory);
		fileChooser.setSelectedFile(fileSuggestion);
		int buttonPressed = fileChooser.showOpenDialog(parentSignalParametersPanel);
		if (buttonPressed != JFileChooser.APPROVE_OPTION)
			return;

		File xmlFile = fileChooser.getSelectedFile();

		if (xmlFile == null) {
			return;
		}
		if (xmlFile.exists() && applicationConfig != null)
			((SignalMLFileChooser)fileChooser).lastDirectoryChanged(xmlFile.getParentFile().getPath());
		if (reader == null) {
			reader = new RawSignalDescriptorReader();
		}
		try {
			RawSignalDescriptor rawSignalDescriptor = reader.readDocument(xmlFile);

                        if (rawSignalDescriptor.isBackup())
                        {
                                String msg = _("The signal you are about to load is only a backup!");
                                String title = _("Warning!");
                                JOptionPane.showMessageDialog(null, msg, title, JOptionPane.WARNING_MESSAGE);
                        }

			parentSignalParametersPanel.fillPanelFromModel(rawSignalDescriptor);

			EegSystemName eegSystemName = rawSignalDescriptor.getEegSystemName();
			parentSignalParametersPanel.fireEegSystemChanged(eegSystemName);

		} catch (Exception ex) {
			Logger.getLogger(ReadXMLManifestAction.class.getName()).log(Level.SEVERE, null, ex);
			Dialogs.showError(_("Could not read manifest from file. Did you select a correct XML manifest file?"));
		}

	}

	/**
	 * Sets the fileChooser for the signal file which should be used to check
	 * its currently opened directory.
	 * @param fileChooser file chooser to be used by this panel
	 */
	public void setSignalFileChooser(EmbeddedFileChooser fileChooser) {
		this.signalFileChooser = fileChooser;
	}

}
