/* ReadXMLManifestAction.java created 2011-03-12
 *
 */

package org.signalml.app.view.opensignal;

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.io.File;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.AbstractAction;
import javax.swing.JFileChooser;
import org.signalml.app.util.IconUtils;
import org.signalml.app.view.ViewerFileChooser;
import org.signalml.domain.signal.raw.RawSignalDescriptor;
import org.signalml.domain.signal.raw.RawSignalDescriptorReader;
import org.signalml.plugin.export.SignalMLException;
import org.springframework.context.support.MessageSourceAccessor;

/**
 * The actions which {@link RawSignalDescriptorReader#readDocument(File)
 * reads} the parameters of a (RAW) signal from an XML file.
 */
public class ReadXMLManifestAction extends AbstractAction {

	private static final long serialVersionUID = 1L;

	protected MessageSourceAccessor messageSource;
	ViewerFileChooser fileChooser;

	protected RawSignalDescriptor rawSignalDescriptor;

	/**
	 * the {@link RawSignalDescriptorReader reader} used to read the
	 * parameters of the RAW signal
	 */
	private RawSignalDescriptorReader reader;

	private SignalParametersPanelForRawSignalFile parentSignalParametersPanel;

	/**
	 * Constructor. Sets the icon and description.
	 */
	public ReadXMLManifestAction(MessageSourceAccessor messageSource, SignalParametersPanelForRawSignalFile parentSignalParametersPanel) {
		super(messageSource.getMessage("openSignal.options.raw.readXMLManifest"));
		this.messageSource = messageSource;
		this.parentSignalParametersPanel = parentSignalParametersPanel;
		putValue(AbstractAction.SMALL_ICON, IconUtils.loadClassPathIcon("org/signalml/app/icon/script_load.png"));
		putValue(AbstractAction.SHORT_DESCRIPTION,messageSource.getMessage("openSignal.options.raw.readXMLManifestToolTip"));
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

		/*File selectedFile = getStepOnePanel().getFileChooser().getSelectedFile();
		File directory = null;
		File fileSuggestion = null;
		if (selectedFile != null) {
			directory = selectedFile.getParentFile();

			fileSuggestion = Util.changeOrAddFileExtension(selectedFile, "xml");

		}

		if (directory == null) {
			directory = new File(System.getProperty("user.dir"));
		}*/

		//File xmlFile = fileChooser.chooseReadXMLManifest(null, null, this);
		/*fileChooser = new ViewerFileChooser();
		fileChooser.setMessageSource(messageSource);
		fileChooser.initialize();
		 *
		 */

		JFileChooser fileChooser = new JFileChooser();
		int showOpenDialog = fileChooser.showOpenDialog(parentSignalParametersPanel);
		File xmlFile = fileChooser.getSelectedFile();

		if (xmlFile == null) {
			return;
		}

		if (reader == null) {
			reader = new RawSignalDescriptorReader();
		}
		try {
			rawSignalDescriptor = reader.readDocument(xmlFile);
			parentSignalParametersPanel.fillPanelFromModel(rawSignalDescriptor);
		} catch (IOException ex) {
			Logger.getLogger(ReadXMLManifestAction.class.getName()).log(Level.SEVERE, null, ex);
		} catch (SignalMLException ex) {
			Logger.getLogger(ReadXMLManifestAction.class.getName()).log(Level.SEVERE, null, ex);
		}

	}

}