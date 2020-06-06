/* OpenBookAction.java created 2011-03-26
 *
 */
package org.signalml.app.action.book;

import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.io.File;
import java.io.IOException;
import org.apache.log4j.Logger;
import org.signalml.app.document.DocumentFlowIntegrator;
import org.signalml.app.document.ManagedDocumentType;
import org.signalml.app.model.document.OpenDocumentDescriptor;
import static org.signalml.app.util.i18n.SvarogI18n._;
import org.signalml.app.view.workspace.ViewerFileChooser;
import org.signalml.plugin.export.SignalMLException;
import org.signalml.plugin.export.view.AbstractSignalMLAction;

/**
 * An action performed when the user chooses an option to open a book document.
 * Opens a new dialog for selecting a book file and after that opens
 * the file.
 *
 * @author Piotr Szachewicz
 */
public class OpenBookDocumentAction extends AbstractSignalMLAction {

	private static final Logger logger = Logger.getLogger(OpenBookDocumentAction.class);

	/**
	 * Used for opening documents.
	 */
	private DocumentFlowIntegrator documentFlowIntegrator;

	/**
	 * A component used to choose a book file to be opened.
	 */
	private ViewerFileChooser fileChooser;

	/**
	 * Creates this action.
	 */
	public OpenBookDocumentAction() {
		super();
		setText(_("Load results"));
		setIconPath("org/signalml/app/icon/fileopen.png");
		setToolTip(_("Open MP decomposition from a file"));
		setMnemonic(KeyEvent.VK_B);
	}

	@Override
	public void actionPerformed(ActionEvent e) {

		String lastFileChooserPath = documentFlowIntegrator.getApplicationConfig().getLastFileChooserPath();
		getFileChooser().setCurrentDirectory(new File(lastFileChooserPath));

		File selectedFile = getFileChooser().chooseOpenBook(null);
		//File selectedFile = getFileChooser().getSelectedFile();

		if (selectedFile == null) {
			return;
		}

		documentFlowIntegrator.getApplicationConfig().setLastFileChooserPath(selectedFile.getParentFile().getPath());

		OpenDocumentDescriptor openDocumentDescriptor = new OpenDocumentDescriptor();
		openDocumentDescriptor.setType(ManagedDocumentType.BOOK);
		openDocumentDescriptor.setMakeActive(true);
		openDocumentDescriptor.setFile(selectedFile);

		try {
			documentFlowIntegrator.openDocument(openDocumentDescriptor);
		} catch (IOException | SignalMLException ex) {
			logger.error(ex, ex);
		}
	}

	public void setFileChooser(ViewerFileChooser fileChooser) {
		this.fileChooser = fileChooser;
	}

	/**
	 * Returns the file chooser used in this action.
	 * @return the file chooser to be used.
	 */
	protected ViewerFileChooser getFileChooser() {
		return fileChooser;
	}

	/**
	 * Returns the DocumentFlowIntegrator used by this action.
	 * @return the DocumentFlowIntegrator used by this action
	 */
	public DocumentFlowIntegrator getDocumentFlowIntegrator() {
		return documentFlowIntegrator;
	}

	/**
	 * Sets the DocumentFlowIntegrator to be used by this action to
	 * open documents.
	 * @param documentFlowIntegrator DocumentFlowIntegrator to be used.
	 */
	public void setDocumentFlowIntegrator(DocumentFlowIntegrator documentFlowIntegrator) {
		if (documentFlowIntegrator == null) {
			throw new NullPointerException();
		}
		this.documentFlowIntegrator = documentFlowIntegrator;
	}

}
