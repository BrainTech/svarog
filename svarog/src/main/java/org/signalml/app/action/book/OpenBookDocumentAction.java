/* OpenBookAction.java created 2011-03-26
 *
 */
package org.signalml.app.action.book;

import static org.signalml.app.util.i18n.SvarogI18n._;

import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.io.File;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

import multiplexer.jmx.client.ConnectException;

import org.signalml.app.document.DocumentFlowIntegrator;
import org.signalml.app.document.ManagedDocumentType;
import org.signalml.app.model.document.OpenDocumentDescriptor;
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
		setText(_("Open book"));
		setIconPath("org/signalml/app/icon/fileopen.png");
		setToolTip(_("Open a book document from a file"));
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
		} catch (IOException ex) {
			Logger.getLogger(OpenBookDocumentAction.class.getName()).log(Level.SEVERE, null, ex);
		} catch (SignalMLException ex) {
			Logger.getLogger(OpenBookDocumentAction.class.getName()).log(Level.SEVERE, null, ex);
		} catch (ConnectException ex) {
			Logger.getLogger(OpenBookDocumentAction.class.getName()).log(Level.SEVERE, null, ex);
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
