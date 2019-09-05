package org.signalml.app.worker.monitor.messages;

import org.codehaus.jackson.annotate.JsonIgnore;
import org.codehaus.jackson.annotate.JsonProperty;

/**
 * @author piotr.rozanski@braintech.pl
 */
public class StartSavingSignal extends LauncherMessage {

	@JsonProperty("experiment_id")
	public String experimentID;

	@JsonProperty("signal_source_id")
	public String signalSourceID;

	@JsonProperty("signal_filename")
	public String signalFileName;
        
	@JsonProperty("save_tags")
	public boolean saveTags = false;

	@JsonProperty("video_filename")
	public String videoFileName = "";

	@JsonProperty("video_stream_url")
	public String videoStreamURL = "";

	@JsonProperty("save_impedance")
	public boolean saveImpedance = false;

	@JsonProperty("append_timestamps")
	public boolean appendTimestamps = false;

	@JsonIgnore
	public StartSavingSignal(String experimentID, String signalSourceID, String signalFileName) {
		super(MessageType.START_SAVING_SIGNAL);
		this.experimentID = experimentID;
		this.signalSourceID = signalSourceID;
		this.signalFileName = signalFileName;
	}

}
