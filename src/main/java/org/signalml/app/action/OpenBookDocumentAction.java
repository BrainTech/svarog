/* OpenBookAction.java created 2011-03-26
 *
 */
package org.signalml.app.action;

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
import org.springframework.context.support.MessageSourceAccessor;

/**
 *
 * @author Piotr Szachewicz
 */
public class OpenBookDocumentAction extends AbstractSignalMLAction {

	private DocumentFlowIntegrator documentFlowIntegrator;
	private JFileChooser fileChooser;

	public OpenBookDocumentAction(MessageSourceAccessor messageSource) {
		super(messageSource);
		setText("action.openBookDocument");
		setIconPath("org/signalml/app/icon/fileopen.png");
		setToolTip("action.openBookDocumentToolTip");
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

	protected JFileChooser getFileChooser() {
		if (fileChooser == null) {
			String lastOpenDocumentPath = documentFlowIntegrator.getApplicationConfig().getLastOpenDocumentPath();
			fileChooser = new JFileChooser(lastOpenDocumentPath);

			FileFilter[] fileFilters = ManagedDocumentType.BOOK.getFileFilters(messageSource);

			for (FileFilter filter : fileFilters) {
				fileChooser.addChoosableFileFilter(filter);
			}
			if (fileFilters.length > 0) {
				fileChooser.setFileFilter(fileFilters[0]);
			}
		}
		return fileChooser;
	}

	public DocumentFlowIntegrator getDocumentFlowIntegrator() {
		return documentFlowIntegrator;
	}

	public void setDocumentFlowIntegrator(DocumentFlowIntegrator documentFlowIntegrator) {
		if (documentFlowIntegrator == null) {
			throw new NullPointerException();
		}
		this.documentFlowIntegrator = documentFlowIntegrator;
	}
}
