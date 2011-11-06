/* OpenSignalAction.java created 2011-03-06
 *
 */
package org.signalml.app.action;

import java.awt.event.ActionEvent;

import org.signalml.app.document.DocumentFlowIntegrator;
import org.signalml.app.document.ManagedDocumentType;
import org.signalml.app.document.SignalDocument;
import org.signalml.app.model.OpenDocumentDescriptor;
import org.signalml.app.view.opensignal.FileOpenSignalMethod;
import org.signalml.app.view.opensignal.OpenSignalAndSetMontageDialog;
import org.signalml.app.view.opensignal.SignalSource;
import org.signalml.domain.montage.Montage;
import org.signalml.plugin.export.view.AbstractSignalMLAction;

/**
 * An action performed when the user chooses an option to open signal allowing
 * also to set montage for this signal in the same dialog. Shows a dialog
 * similar to the one for editing montage but having an extra tab for choosing
 * a signal to be opened.
 *
 * @author Piotr Szachewicz
 */
public class OpenSignalAndSetMontageAction extends AbstractSignalMLAction {

	private static final long serialVersionUID = 1L;

	/**
	 * DocumentFlowIntegrator used by this action to open the selected
	 * signal file.
	 */
	private DocumentFlowIntegrator documentFlowIntegrator;

	/**
	 * A dialog opened after performing this action.
	 */
	private OpenSignalAndSetMontageDialog openSignalAndSetMontageDialog;

	/**
	 * Constructor.
	 * @param viewerElementManager ViewerElementManager to be used by
	 * this action.
	 */
	public OpenSignalAndSetMontageAction(DocumentFlowIntegrator documentFlowIntegrator) {
		super(documentFlowIntegrator.getMessageSource());
		this.documentFlowIntegrator = documentFlowIntegrator;
		setText("action.openSignalAndSetMontageLabel");
		setIconPath("org/signalml/app/icon/fileopen.png");
		setToolTip("action.openSignalAndSetMontageToolTip");
	}

	@Override
	public void actionPerformed(ActionEvent ev) {

		OpenDocumentDescriptor openDocumentDescriptor = new OpenDocumentDescriptor();
		openDocumentDescriptor.setMakeActive(true);
		openDocumentDescriptor.setType(ManagedDocumentType.SIGNAL);

		boolean ok = openSignalAndSetMontageDialog.showDialog(openDocumentDescriptor, true);
		if (!ok) {
			return;
		}

		documentFlowIntegrator.maybeOpenDocument(openDocumentDescriptor);

	}

	@Override
	public void setEnabledAsNeeded() {
		setEnabled(true);
	}

	/**
	 * Sets the dialog to be opened after performing this action.
	 * @param openSignalAndSetMontageDialog dialog to be used
	 */
	public void setOpenSignalAndSetMontageDialog(OpenSignalAndSetMontageDialog openSignalAndSetMontageDialog) {
		if (openSignalAndSetMontageDialog == null)
			throw new NullPointerException();
		this.openSignalAndSetMontageDialog = openSignalAndSetMontageDialog;
	}

}
