package org.signalml.app.action.document;

import static org.signalml.app.util.i18n.SvarogI18n._;

import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.concurrent.ExecutionException;

import javax.swing.SwingWorker.StateValue;

import multiplexer.jmx.client.JmxClient;

import org.signalml.app.document.DocumentFlowIntegrator;
import org.signalml.app.model.document.OpenDocumentDescriptor;
import org.signalml.app.model.document.opensignal.AbstractOpenSignalDescriptor;
import org.signalml.app.model.document.opensignal.ExperimentDescriptor;
import org.signalml.app.view.document.opensignal.OpenSignalWizardDialog;
import org.signalml.app.worker.monitor.ConnectToExperimentWorker;
import org.signalml.plugin.export.view.AbstractSignalMLAction;

public class OpenSignalWizardAction extends AbstractSignalMLAction implements PropertyChangeListener {

	private DocumentFlowIntegrator documentFlowIntegrator;
	private OpenSignalWizardDialog openSignalWizardDialog;
	
	private OpenDocumentDescriptor openDocumentDescriptor;
	private ConnectToExperimentWorker worker;
	
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
		openDocumentDescriptor = new OpenDocumentDescriptor();

		boolean ok = openSignalWizardDialog.showDialog(openDocumentDescriptor); 
		if (!ok) {
			return;
		}

		AbstractOpenSignalDescriptor openSignalDescriptor = openDocumentDescriptor.getOpenSignalDescriptor();
		if (openSignalDescriptor instanceof ExperimentDescriptor) {
			ExperimentDescriptor experimentDescriptor = (ExperimentDescriptor) openSignalDescriptor;
			worker = new ConnectToExperimentWorker(experimentDescriptor);
			worker.addPropertyChangeListener(this);
			worker.execute();
		}
		else {
			documentFlowIntegrator.maybeOpenDocument(openDocumentDescriptor);
		}
	}

	@Override
	public void propertyChange(PropertyChangeEvent evt) {
		if (((StateValue) evt.getNewValue()) == StateValue.DONE) {
			JmxClient jmxClient;
			try {
				jmxClient = worker.get();
				
				if (jmxClient != null) {
					ExperimentDescriptor experimentDescriptor = (ExperimentDescriptor) openDocumentDescriptor.getOpenSignalDescriptor();
					experimentDescriptor.setJmxClient(jmxClient);
					documentFlowIntegrator.maybeOpenDocument(openDocumentDescriptor);
				}
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (ExecutionException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
		}
	}


}
