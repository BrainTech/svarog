/* SignalDocumentListModel.java created 2007-10-10
 *
 */

package org.signalml.app.model.components;

import javax.swing.AbstractListModel;
import javax.swing.ComboBoxModel;

import org.signalml.app.document.DocumentManager;
import org.signalml.app.document.ManagedDocumentType;
import org.signalml.app.document.SignalDocument;

/** SignalDocumentListModel
 *
 *
 * @author Michal Dobaczewski &copy; 2007-2008 CC Otwarte Systemy Komputerowe Sp. z o.o.
 */
public class SignalDocumentListModel extends AbstractListModel implements ComboBoxModel {

	private static final long serialVersionUID = 1L;

	private DocumentManager documentManager;
	private SignalDocument selectedDocument;

	public SignalDocumentListModel(DocumentManager documentManager) {
		super();
		this.documentManager = documentManager;
	}

	@Override
	public SignalDocument getSelectedItem() {
		return selectedDocument;
	}

	@Override
	public void setSelectedItem(Object anItem) {
		selectedDocument = (SignalDocument) anItem;
	}

	@Override
	public Object getElementAt(int index) {
		return documentManager.getDocumentAt(ManagedDocumentType.SIGNAL, index);
	}

	@Override
	public int getSize() {
		return documentManager.getDocumentCount(ManagedDocumentType.SIGNAL);
	}

}
