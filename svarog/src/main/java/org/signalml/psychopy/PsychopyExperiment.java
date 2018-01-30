package org.signalml.psychopy;

import org.signalml.app.model.document.opensignal.ExperimentDescriptor;
import org.signalml.app.worker.monitor.messages.MessageType;
import org.signalml.peer.Peer;
import org.signalml.psychopy.messages.RunPsychopyExperiment;

public class PsychopyExperiment {
	private final Peer peer;
	private final String peerId;
	public String experimentPath;
	public String outputDirectoryPath;

	public PsychopyExperiment(ExperimentDescriptor experimentDescriptor) {
		this(
			experimentDescriptor.getPeer(),
			experimentDescriptor.getPeerId(),
			"",
			""
		);
	}

	private PsychopyExperiment(
		Peer peer,
		String peerId,
		String experimentPath,
		String outputDirectoryPath
	) {
		this.peer = peer;
		this.peerId = peerId;
		this.experimentPath = experimentPath;
		this.outputDirectoryPath = outputDirectoryPath;
		subscribeToMessages();
	}

	private void subscribeToMessages() {
		assert peer != null : "Peer should not be null";
		this.peer.subscribe(MessageType.PSYCHOPY_EXPERIMENT_STARTED.getMessageCode());
		this.peer.subscribe(MessageType.PSYCHOPY_EXPERIMENT_FINISHED.getMessageCode());
		this.peer.subscribe(MessageType.PSYCHOPY_EXPERIMENT_ERROR.getMessageCode());
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

}