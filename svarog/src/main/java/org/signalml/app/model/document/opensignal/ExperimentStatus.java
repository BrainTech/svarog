package org.signalml.app.model.document.opensignal;

public enum ExperimentStatus {

	NEW("New"),
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
