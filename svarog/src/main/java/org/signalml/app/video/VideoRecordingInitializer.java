package org.signalml.app.video;

import org.apache.log4j.Logger;
import org.json.JSONException;
import org.json.JSONObject;
import org.signalml.peer.Converter;
import org.signalml.peer.Message;
import org.signalml.peer.Peer;

/**
 * Object responsible for initializing video recording.
 * This object may be reused to send multiple "start recording" requests.
 *
 * @author piotr.rozanski@braintech.pl
 */
public class VideoRecordingInitializer {

	private static final Logger logger = Logger.getLogger(VideoRecordingInitializer.class);

	private final Peer peer;
	private final String peerId;
	private final String rtspURL;
	private final String targetFilePath;

	public VideoRecordingInitializer(Peer peer, String peerId, String rtspURL, String targetFilePath) {
		this.peer = peer;
		this.peerId = peerId;
		this.rtspURL = rtspURL;
		this.targetFilePath = targetFilePath;
	}

	/**
	 * Send a single request to start recording video.
	 */
	public void startRecording() {
		try {
			JSONObject saveVideoJSON = new JSONObject();
			saveVideoJSON.put("URL", rtspURL);
			saveVideoJSON.put("PATH", targetFilePath);
			byte[] content = Converter.bytesFromString(saveVideoJSON.toString());
			peer.publish(new Message(Message.SAVE_VIDEO, peerId, content));
		} catch (JSONException ex) {
			// should not happen
			logger.error("JSON error in startRecording", ex);
		}
	}

}
