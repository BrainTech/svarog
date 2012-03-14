package org.signalml.app.action.document;

import static org.signalml.app.util.i18n.SvarogI18n._;

import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;

import org.signalml.app.document.DocumentFlowIntegrator;
import org.signalml.app.model.document.OpenDocumentDescriptor;
import org.signalml.app.view.document.opensignal.OpenSignalWizardDialog;
import org.signalml.plugin.export.view.AbstractSignalMLAction;

public class OpenSignalWizardAction extends AbstractSignalMLAction {

	private DocumentFlowIntegrator documentFlowIntegrator;
	private OpenSignalWizardDialog openSignalWizardDialog;
	
	/**
	 * Constructor.
	 * @param viewerElementManager ViewerElementManager to be used by
	 * this action.
	 */
	public OpenSignalWizardAction(DocumentFlowIntegrator documentFlowIntegrator) {
		super();
		this.documentFlowIntegrator = documentFlowIntegrator;
		setText(_("Open signal wizard"));
		setIconPath("org/signalml/app/icon/fileopen.png");
		setToolTip(_("Open signal and set montage for it"));
		setMnemonic(KeyEvent.VK_O);
	}

	public void setOpenSignalWizardDialog(OpenSignalWizardDialog openSignalWizardDialog) {
		this.openSignalWizardDialog = openSignalWizardDialog;
	}

	@Override
	public void actionPerformed(ActionEvent arg0) {
		OpenDocumentDescriptor openDocumentDescriptor = new OpenDocumentDescriptor();

		boolean ok = openSignalWizardDialog.showDialog(openDocumentDescriptor); 
		if (!ok) {
			return;
		}

		documentFlowIntegrator.maybeOpenDocument(openDocumentDescriptor);
	}

}
