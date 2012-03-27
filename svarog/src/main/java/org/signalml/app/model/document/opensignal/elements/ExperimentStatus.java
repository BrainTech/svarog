package org.signalml.app.model.document.opensignal.elements;

public enum ExperimentStatus {

	NEW("New"),
	LAUNCHING("Launching"),
	RUNNING("Running");
	
	private String displayName;
	
	ExperimentStatus(String displayName) {
		this.displayName = displayName;
	}
	
	@Override
	public String toString() {
		return displayName;
	}
}
