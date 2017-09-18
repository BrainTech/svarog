package org.signalml.app.model.document.opensignal;

import org.signalml.app.config.preset.Preset;
import org.signalml.app.model.document.opensignal.elements.Amplifier;
import org.signalml.app.model.document.opensignal.elements.ExperimentStatus;
import org.signalml.app.model.document.opensignal.elements.SignalParameters;
import org.signalml.app.model.monitor.MonitorRecordingDescriptor;
import org.signalml.domain.tag.StyledTagSet;
import org.signalml.peer.Peer;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamOmitField;
import java.util.List;

@XStreamAlias(value="experiment")
public class ExperimentDescriptor extends AbstractOpenSignalDescriptor implements Preset {

	private String id;
	private String name;

	private String path;
	private String origin_machine;
	private Amplifier amplifier = new Amplifier();
	private ExperimentStatus status;

	private List<String> experimentRepUrls;
	
	private String multiplexerAddress;
	private int multiplexerPort;
	private Peer peer;

	private Float backupFrequency;
	private MonitorRecordingDescriptor monitorRecordingDescriptor = new MonitorRecordingDescriptor();

	@XStreamOmitField
	private StyledTagSet tagStyles;
	private String tagStylesName;

	private String peerId;

	private String recommendedScenario;
	private boolean hasVideoSaver;

	/**
	 * This value holds information whether Svarog has connected to this experiment or not.
	 */
	private transient boolean connected;

	public ExperimentDescriptor(ExperimentDescriptor other) {
		this();

		this.id = other.id;
		this.name = other.name;
		this.path = other.path;
		this.amplifier = new Amplifier(other.getAmplifier());
		this.status = other.status;
		this.experimentRepUrls = other.experimentRepUrls;
		this.signalParameters = new SignalParameters(other.signalParameters);

		this.backupFrequency = other.backupFrequency;

		this.eegSystemName = other.eegSystemName;
		this.tagStylesName = other.tagStylesName;

		//other fields should be filled after sending "join_experiment" request
	}

	public ExperimentDescriptor() {
		setBackupFrequency(10.0F);
		signalParameters = new SignalParameters();
	}

	public void copyFromPreset(ExperimentDescriptor other) {
		this.amplifier.copyFromPreset(other.getAmplifier());
		this.signalParameters = new SignalParameters(other.signalParameters);
		this.eegSystemName = other.eegSystemName;
		this.tagStylesName = other.tagStylesName;
	}

	@Override
	public String getName() {
		return name;
	}
	@Override
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
	
	public String getOriginMachine() {
		return origin_machine;
	}
	public void setOriginMachine(String origin_machine) {
		this.origin_machine = origin_machine;
	}
	
	public List<String> getExperimentRepUrls(){
		return experimentRepUrls;
	}

	public List<String> setExperimentRepUrls(List<String> repUrls){
		return this.experimentRepUrls = repUrls;
	}
	
	public String getFirstRepHost(){
		String first = experimentRepUrls.get(0);
		return first.split(":")[1].substring(2);
	}
	
	public int getFirstRepPort(){
		String first = experimentRepUrls.get(0);
		return Integer.parseInt(first.split(":")[2]);
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

	public Peer getPeer() {
		return peer;
	}

	public void setPeer(Peer peer) {
		this.peer = peer;
	}

	public StyledTagSet getTagStyles() {
		return tagStyles;
	}

	public String getTagStylesName() {
		return tagStylesName;
	}

	public void setTagStyles(StyledTagSet tagStyles) {
		this.tagStyles = tagStyles;
		this.tagStylesName = tagStyles == null ? null : tagStyles.getName();
	}

	public MonitorRecordingDescriptor getMonitorRecordingDescriptor() {
		return monitorRecordingDescriptor;
	}

	public void setMonitorRecordingDescriptor(MonitorRecordingDescriptor monitorRecordingDescriptor) {
		this.monitorRecordingDescriptor = monitorRecordingDescriptor;
	}

	public String getPath() {
		return path;
	}

	public void setPath(String path) {
		this.path = path;
	}

	public String getPeerId() {
		return peerId;
	}

	public void setPeerId(String peerId) {
		this.peerId = peerId;
	}

	@Override
	public String[] getChannelLabels() {
		return amplifier.getSelectedChannelsLabels();
	}

	public String getRecommendedScenario() {
		return recommendedScenario;
	}
	public void setRecommendedScenario(String recommendedScenario) {
		this.recommendedScenario = recommendedScenario;
	}

	public boolean getHasVideoSaver() {
		return hasVideoSaver;
	}
	public void setHasVideoSaver(boolean hasVideoSaver) {
		this.hasVideoSaver = hasVideoSaver;
	}

	@Override
	public String toString() {
		return getName();
	}

	public boolean isConnected() {
		return connected;
	}

	public void setConnected(boolean connected) {
		this.connected = connected;
	}

}
