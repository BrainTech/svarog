package org.signalml.app.video;

import java.util.Objects;

/**
 * Simple immutable PDO (Plain Data Object) specifying a single video stream,
 * associated with a RTSP URL.
 *
 * @author piotr.rozanski@braintech.pl
 */
public class VideoStreamSpecification {

	public static final int FEATURE_PAN = 1;
	public static final int FEATURE_TILT = 2;
	public static final int FEATURE_ZOOM = 4;
	public static final int FEATURE_HOME = 8;
	public static final int FEATURE_NIGHT_MODE = 16;

	public final String cameraName;
	public final String cameraID;
	public final String streamID;
	public final int width;
	public final int height;
	public final float fps;
	public final int features;  // bitmask of FEATURE_*

	/**
	 * Create a new video stream specification.
	 *
	 * @param cameraName  human-readable camera name
	 * @param cameraID  camera ID
	 * @param streamID  stream ID
	 * @param width  video width in pixels
	 * @param height  video height in pixels
	 * @param fps  video frames per second
	 */
	public VideoStreamSpecification(String cameraName, String cameraID, String streamID, int width, int height, float fps, int features) {
		this.cameraName = cameraName;
		this.cameraID = cameraID;
		this.streamID = streamID;
		this.width = width;
		this.height = height;
		this.fps = fps;
		this.features = features;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == null || !(obj instanceof VideoStreamSpecification)) {
			return false;
		}
		VideoStreamSpecification v = (VideoStreamSpecification) obj;
		return cameraID.equals(v.cameraID) && streamID.equals(v.streamID);
	}

	@Override
	public int hashCode() {
		int hash = 7;
		hash = 67 * hash + Objects.hashCode(this.cameraID);
		hash = 67 * hash + Objects.hashCode(this.streamID);
		return hash;
	}

	@Override
	public String toString() {
		return String.format("(%d x %d) @ %.1f", width, height, fps);
	}

}
