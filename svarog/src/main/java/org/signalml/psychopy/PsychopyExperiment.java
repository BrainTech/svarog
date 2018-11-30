package org.signalml.psychopy;

import org.signalml.app.SvarogApplication;
import org.signalml.app.document.DocumentFlowIntegrator;
import org.signalml.app.document.ManagedDocumentType;
import org.signalml.app.document.MonitorSignalDocument;
import org.signalml.app.model.document.OpenDocumentDescriptor;
import org.signalml.app.model.document.opensignal.ExperimentDescriptor;
import org.signalml.app.view.common.dialogs.errors.Dialogs;
import org.signalml.app.worker.monitor.messages.BaseMessage;
import org.signalml.app.worker.monitor.messages.MessageType;
import org.signalml.domain.signal.raw.RawSignalDescriptor;
import org.signalml.domain.signal.raw.RawSignalDescriptorReader;
import org.signalml.peer.Peer;
import org.signalml.psychopy.messages.FinishPsychopyExperiment;
import org.signalml.psychopy.messages.PsychopyExperimentError;
import org.signalml.psychopy.messages.PsychopyExperimentFinished;
import org.signalml.psychopy.messages.RunPsychopyExperiment;
import org.signalml.util.Util;

import javax.swing.*;
import java.io.File;
import org.signalml.app.document.signal.SignalDocument;
import org.signalml.app.model.document.OpenTagDescriptor;

import static org.signalml.app.util.i18n.SvarogI18n._;
import static org.signalml.app.worker.monitor.messages.MessageType.PSYCHOPY_EXPERIMENT_STARTED;

public class PsychopyExperiment {

	private final Peer peer;
	private final String peerId;
	private final MonitorSignalDocument document;
	public String experimentPath;
	public String outputPathPrefix;
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
		String outputPathPrefix,
		MonitorSignalDocument document
	) {
		this.peer = peer;
		this.peerId = peerId;
		this.experimentPath = experimentPath;
		this.outputPathPrefix = outputPathPrefix;
		this.document = document;
	}

	public void updateStatus(BaseMessage msg) {
		if (msg.getType() == MessageType.PSYCHOPY_EXPERIMENT_FINISHED) {
			String[] createdFiles = ((PsychopyExperimentFinished) msg).createdFiles;
			String rawSignalFile = getRawSignalFilePath(createdFiles);

			showFinishedDialog(createdFiles);
			maybeOpenDocument(rawSignalFile);
		} else if (msg.getType() == MessageType.PSYCHOPY_EXPERIMENT_ERROR) {
			showErrorDialog(((PsychopyExperimentError) msg).details);
		}
		isRunning = msg.getType() == PSYCHOPY_EXPERIMENT_STARTED;
	}

	private String getRawSignalFilePath(String[] files) {
		for (String filePath : files) {
			if (filePath.endsWith(".raw")) {
				return filePath;
			}
		}
		assert false : "Raw file should be always generated";
		return null;
	}

	private void maybeOpenDocument(String rawSignalFile) {
		DocumentFlowIntegrator integrator = SvarogApplication.getSharedInstance().getViewerElementManager().getDocumentFlowIntegrator();
		OpenDocumentDescriptor documentDescriptor = getOpenDocumentDescriptor(rawSignalFile);
		if (documentDescriptor != null) {
			SignalDocument signalDocument = (SignalDocument) integrator.maybeOpenDocument(documentDescriptor);
			OpenDocumentDescriptor tagDocumentDescriptor = getTagDocumentDescriptor(rawSignalFile);
			if (tagDocumentDescriptor != null) {
				OpenTagDescriptor openTagDescriptor = new OpenTagDescriptor();
				openTagDescriptor.setParent(signalDocument);
				tagDocumentDescriptor.setTagOptions(openTagDescriptor);
				integrator.maybeOpenDocument(tagDocumentDescriptor);
			}
		}
	}

	private OpenDocumentDescriptor getTagDocumentDescriptor(String rawSignalFile) {
		boolean tagFileExists = false;
		File tagFile = null;
		for (String ext : ManagedDocumentType.TAG.getAllFileExtensions()) {
			tagFile = Util.changeOrAddFileExtension(new File(rawSignalFile), ext);
			if (tagFile.exists()) {
				tagFileExists = true;
				break;
			}
		}
		if (!tagFileExists) {
			return null;
		}

		OpenDocumentDescriptor tagDocumentDescriptor = new OpenDocumentDescriptor();
		tagDocumentDescriptor.setType(ManagedDocumentType.TAG);
		tagDocumentDescriptor.setFile(tagFile);
		return tagDocumentDescriptor;
	}

	private OpenDocumentDescriptor getOpenDocumentDescriptor(String rawSignalFilePath) {
		File signalFile = new File(rawSignalFilePath);
		RawSignalDescriptor openSignalDescriptor = getOpenSignalDescriptor(signalFile);
		if (openSignalDescriptor != null) {
			OpenDocumentDescriptor documentDescriptor = new OpenDocumentDescriptor();
			documentDescriptor.setFile(signalFile);
			documentDescriptor.setType(ManagedDocumentType.SIGNAL);
			documentDescriptor.setMakeActive(true);
			documentDescriptor.setOpenSignalDescriptor(openSignalDescriptor);
			return documentDescriptor;
		} else {
			return null;
		}
	}

	private RawSignalDescriptor getOpenSignalDescriptor(File signalFile) {
		RawSignalDescriptorReader reader = new RawSignalDescriptorReader();
		RawSignalDescriptor openSignalDescriptor = new RawSignalDescriptor();
		try {
			openSignalDescriptor = reader.readDocument(Util.changeOrAddFileExtension(signalFile, "xml"));
			openSignalDescriptor.setCorrectlyRead(true);
			openSignalDescriptor.setMontage(document.getMontage());
			openSignalDescriptor.setTryToOpenTagDocument(true);
			return openSignalDescriptor;
		} catch (Exception e) {
			Dialogs.showError(_("There was an error while reading the XML manifest."));
			return null;
		}
	}

	private void showFinishedDialog(String[] filePaths) {
		String message = String.join(
			"\n",
			_("Psychopy Experiment finished. Created files:"),
			String.join(
				"\n",
				filePaths
			)
		);
		JOptionPane.showMessageDialog(
			null,
			message,
			_("Psychopy Experiment finished"),
			JOptionPane.INFORMATION_MESSAGE
		);
	}

	private void showErrorDialog(String details) {
		String errorMsg = _("Psychopy Error");
		JOptionPane.showMessageDialog(
			null,
			errorMsg + ": " + details,
			errorMsg,
			JOptionPane.ERROR_MESSAGE
		);
	}

	public void run() {
		this.peer.publish(
			new RunPsychopyExperiment(
				this.peerId,
				this.experimentPath,
				this.outputPathPrefix
			)
		);
	}

	public void finish() {
		peer.publish(new FinishPsychopyExperiment(peerId));
	}

}
