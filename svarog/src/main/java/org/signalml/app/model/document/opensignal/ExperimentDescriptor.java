package org.signalml.app.model.document.opensignal;

import org.signalml.app.model.monitor.MonitorRecordingDescriptor;
import org.signalml.domain.signal.raw.RawSignalByteOrder;
import org.signalml.domain.signal.raw.RawSignalSampleType;
import org.signalml.domain.tag.StyledTagSet;

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
	private JmxClient jmxClient;
	
	private Float backupFrequency;
	private MonitorRecordingDescriptor monitorRecordingDescriptor;
	
	private StyledTagSet tagStyles;
	
	public ExperimentDescriptor() {
        setBackupFrequency(10.0F);
        this.signalParameters = new SignalParameters();
	}

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

	public Float getBackupFrequency() {
		return backupFrequency;
	}

	public void setBackupFrequency(Float backupFrequency) {
		this.backupFrequency = backupFrequency;
	}

	public JmxClient getJmxClient() {
		return jmxClient;
	}

	public void setJmxClient(JmxClient jmxClient) {
		this.jmxClient = jmxClient;
	}

	public StyledTagSet getTagStyles() {
		return tagStyles;
	}

	public void setTagStyles(StyledTagSet tagStyles) {
		this.tagStyles = tagStyles;
	}

	public MonitorRecordingDescriptor getMonitorRecordingDescriptor() {
		return monitorRecordingDescriptor;
	}

	public void setMonitorRecordingDescriptor(MonitorRecordingDescriptor monitorRecordingDescriptor) {
		this.monitorRecordingDescriptor = monitorRecordingDescriptor;
	}
	
	/**
	 * Multiplexer client to get signal from it.
	 * może nie powinno tego tu być, tylko gdzie indziej.
	 */
	//private JmxClient jmxClient;

}
