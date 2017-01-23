package org.signalml.app.worker.monitor.messages;

import java.util.LinkedHashMap;
import java.util.Map;
import org.codehaus.jackson.annotate.JsonProperty;
import org.signalml.app.video.VideoStreamSpecification;

public class CameraControlRequest extends Message {

	@JsonProperty("action_name")
	private final String action_name;

	@JsonProperty("args")
	private final Map<String, String> args;

	public CameraControlRequest(String action_name) {
		super(MessageType.CAMERA_CONTROL_REQUEST);
		this.action_name = action_name;
		this.args = new LinkedHashMap<>();
	}

	public CameraControlRequest(String action_name, VideoStreamSpecification stream) {
		this(action_name);
		putArg("cam_id", stream.cameraID);
		putArg("preset_id", stream.streamID);
	}

	public final void putArg(String key, String value) {
		args.put(key, value);
	}

}
