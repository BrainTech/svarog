package org.signalml.psychopy;

import java.io.File;
import javax.swing.JOptionPane;
import org.signalml.app.SvarogApplication;
import org.signalml.app.document.DocumentFlowIntegrator;
import org.signalml.app.document.ManagedDocumentType;
import org.signalml.app.document.MonitorSignalDocument;
import org.signalml.app.model.document.OpenDocumentDescriptor;
import org.signalml.app.model.document.opensignal.ExperimentDescriptor;
import static org.signalml.app.util.i18n.SvarogI18n._;
import org.signalml.app.view.common.dialogs.errors.Dialogs;
import org.signalml.app.worker.monitor.messages.BaseMessage;
import org.signalml.app.worker.monitor.messages.MessageType;
import static org.signalml.app.worker.monitor.messages.MessageType.PSYCHOPY_EXPERIMENT_STARTED;
import org.signalml.domain.signal.raw.RawSignalDescriptor;
import org.signalml.domain.signal.raw.RawSignalDescriptorReader;
import org.signalml.peer.Peer;
import org.signalml.psychopy.messages.FinishPsychopyExperiment;
import org.signalml.psychopy.messages.PsychopyExperimentError;
import org.signalml.psychopy.messages.RunPsychopyExperiment;
import org.signalml.util.Util;

public class PsychopyExperiment {
	private final Peer peer;
	private final String peerId;
	private final MonitorSignalDocument document;
	public String experimentPath;
	public String outputDirectoryPath;
	public boolean isRunning = false;
	

	public PsychopyExperiment(ExperimentDescriptor experimentDescriptor,
		MonitorSignalDocument document) {
		this(
			experimentDescriptor.getPeer(),
			experimentDescriptor.getPeerId(),
			"",
			"",
			document
		);
	}

	private PsychopyExperiment(
		Peer peer,
		String peerId,
		String experimentPath,
		String outputDirectoryPath,
		MonitorSignalDocument document
	) {
		this.peer = peer;
		this.peerId = peerId;
		this.experimentPath = experimentPath;
		this.outputDirectoryPath = outputDirectoryPath;
		this.document = document;
	}
	public void updateStatus(BaseMessage msg){		
		if (msg.getType() == MessageType.PSYCHOPY_EXPERIMENT_FINISHED) {
			JOptionPane.showMessageDialog(null,
				_("Psychopy Experiment finished"),
				_("Psychopy Experiment finished"),
				JOptionPane.INFORMATION_MESSAGE
			);
			DocumentFlowIntegrator integrator = SvarogApplication.getSharedInstance().getViewerElementManager().getDocumentFlowIntegrator();

			OpenDocumentDescriptor doc_descriptor = new OpenDocumentDescriptor();
			File signal_file = new File(this.outputDirectoryPath + ".raw");
			doc_descriptor.setFile(signal_file);
			doc_descriptor.setType(ManagedDocumentType.SIGNAL);
			doc_descriptor.setMakeActive(true);
			RawSignalDescriptorReader reader = new RawSignalDescriptorReader();
			RawSignalDescriptor openSignalDescriptor = new RawSignalDescriptor();
			try {
				openSignalDescriptor = reader.readDocument(Util.changeOrAddFileExtension(signal_file, "xml"));
				openSignalDescriptor.setCorrectlyRead(true);
			} catch (Exception e) {
				Dialogs.showError(_("There was an error while reading the XML manifest."));
				return;
			}
			openSignalDescriptor.setMontage(document.getMontage());
			doc_descriptor.setOpenSignalDescriptor(openSignalDescriptor);
			integrator.maybeOpenDocument(doc_descriptor);

		} else if (msg.getType() == MessageType.PSYCHOPY_EXPERIMENT_ERROR) {
			JOptionPane.showMessageDialog(null,
				"Psychopy Error: " + ((PsychopyExperimentError) msg).details,
				"Psychopy Error",
				JOptionPane.ERROR_MESSAGE
			);
		}
		isRunning = msg.getType() == PSYCHOPY_EXPERIMENT_STARTED;
	}

	public void run() {
		this.peer.publish(
			new RunPsychopyExperiment(
				this.peerId,
				this.experimentPath,
				this.outputDirectoryPath
			)
		);
	}

	public void finish() {
		peer.publish(new FinishPsychopyExperiment(peerId));
	}

}