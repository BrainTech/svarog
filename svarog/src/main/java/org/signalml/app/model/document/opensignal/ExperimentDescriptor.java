package org.signalml.app.model.document.opensignal;

import multiplexer.jmx.client.JmxClient;

public class ExperimentDescriptor extends AbstractOpenSignalDescriptor {

	private String experimentName;
	private Amplifier amplifier;
	private ExperimentStatus experimentStatus;

	public String getExperimentName() {
		return experimentName;
	}
	public void setExperimentName(String experimentName) {
		this.experimentName = experimentName;
	}
	public Amplifier getAmplifier() {
		return amplifier;
	}
	public void setAmplifier(Amplifier amplifier) {
		this.amplifier = amplifier;
	}
	public ExperimentStatus getExperimentStatus() {
		return experimentStatus;
	}
	public void setExperimentStatus(ExperimentStatus experimentStatus) {
		this.experimentStatus = experimentStatus;
	}
	
	/**
	 * Multiplexer client to get signal from it.
	 * może nie powinno tego tu być, tylko gdzie indziej.
	 */
	//private JmxClient jmxClient;

}
