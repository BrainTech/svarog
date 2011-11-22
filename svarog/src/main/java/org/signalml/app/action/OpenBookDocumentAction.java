/* OpenBookAction.java created 2011-03-26
 *
 */
package org.signalml.app.action;

import static org.signalml.app.SvarogI18n._;
import java.awt.event.ActionEvent;
import java.io.File;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JFileChooser;
import javax.swing.filechooser.FileFilter;
import multiplexer.jmx.client.ConnectException;
import org.signalml.app.document.DocumentFlowIntegrator;
import org.signalml.app.document.ManagedDocumentType;
import org.signalml.app.model.OpenDocumentDescriptor;
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
	private JFileChooser fileChooser;

	/**
	 * Creates this action.
	 */
	public OpenBookDocumentAction() {
		super();
		setText(_("Open book"));
		setIconPath("org/signalml/app/icon/fileopen.png");
		setToolTip(_("Open a book document from a file"));
	}

	@Override
	public void actionPerformed(ActionEvent e) {

		getFileChooser().showOpenDialog(null);
		File selectedFile = getFileChooser().getSelectedFile();

		if (selectedFile == null) {
			return;
		}

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

	/**
	 * Returns the file chooser used in this action.
	 * @return the file chooser to be used.
	 */
	protected JFileChooser getFileChooser() {
		if (fileChooser == null) {
			String lastOpenDocumentPath = documentFlowIntegrator.getApplicationConfig().getLastOpenDocumentPath();
			fileChooser = new JFileChooser(lastOpenDocumentPath);

			FileFilter[] fileFilters = ManagedDocumentType.BOOK.getFileFilters();

			for (FileFilter filter : fileFilters) {
				fileChooser.addChoosableFileFilter(filter);
			}
			if (fileFilters.length > 0) {
				fileChooser.setFileFilter(fileFilters[0]);
			}
		}
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
