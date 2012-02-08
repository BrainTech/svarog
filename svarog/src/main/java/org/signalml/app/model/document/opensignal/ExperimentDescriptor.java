package org.signalml.app.model.document.opensignal;

import com.thoughtworks.xstream.annotations.XStreamAlias;

import multiplexer.jmx.client.JmxClient;

@XStreamAlias(value="experiment")
public class ExperimentDescriptor extends AbstractOpenSignalDescriptor {

	private String id;
	private String name;
	private Amplifier amplifier;
	private ExperimentStatus status;
	
	private String experimentAddress;
	
	private String multiplexerAddress;
	private int multiplexerPort;

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
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getExperimentAddress() {
		return experimentAddress;
	}
	public void setExperimentAddress(String experimentAddress) {
		this.experimentAddress = experimentAddress;
	}
	public String getMultiplexerAddress() {
		return multiplexerAddress;
	}
	public void setMultiplexerAddress(String multiplexerAddress) {
		this.multiplexerAddress = multiplexerAddress;
	}
	public int getMultiplexerPort() {
		return multiplexerPort;
	}
	public void setMultiplexerPort(int multiplexerPort) {
		this.multiplexerPort = multiplexerPort;
	}
	
	/**
	 * Multiplexer client to get signal from it.
	 * może nie powinno tego tu być, tylko gdzie indziej.
	 */
	//private JmxClient jmxClient;

}
