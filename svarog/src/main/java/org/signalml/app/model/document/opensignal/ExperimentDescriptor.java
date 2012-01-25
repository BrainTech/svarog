package org.signalml.app.model.document.opensignal;

import com.thoughtworks.xstream.annotations.XStreamAlias;

import multiplexer.jmx.client.JmxClient;

@XStreamAlias(value="experiment")
public class ExperimentDescriptor extends AbstractOpenSignalDescriptor {

	private String name;
	private Amplifier amplifier;
	private ExperimentStatus status;

	public String getName() {
		return name;
	}
	public void setName(String experimentName) {
		this.name = experimentName;
	}
	public Amplifier getAmplifier() {
		return amplifier;
	}
	public void setAmplifier(Amplifier amplifier) {
		this.amplifier = amplifier;
	}
	public ExperimentStatus getStatus() {
		return status;
	}
	public void setStatus(ExperimentStatus experimentStatus) {
		this.status = experimentStatus;
	}
	
	/**
	 * Multiplexer client to get signal from it.
	 * może nie powinno tego tu być, tylko gdzie indziej.
	 */
	//private JmxClient jmxClient;

}
