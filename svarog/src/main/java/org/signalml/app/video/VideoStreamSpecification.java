package org.signalml.app.video;

import java.util.Objects;

/**
 * Simple immutable PDO (Plain Data Object) specifying a single video stream,
 * associated with a RTSP URL.
 *
 * @author piotr.rozanski@braintech.pl
 */
public class VideoStreamSpecification {

	public final String cameraID;
	public final String streamID;
	public final int width;
	public final int height;
	public final float fps;

	/**
	 * Create a new video stream specification.
	 *
	 * @param cameraID  camera ID
	 * @param streamID  stream ID
	 * @param width  video width in pixels
	 * @param height  video height in pixels
	 * @param fps  video frames per second
	 */
	public VideoStreamSpecification(String cameraID, String streamID, int width, int height, float fps) {
		this.cameraID = cameraID;
		this.streamID = streamID;
		this.width = width;
		this.height = height;
		this.fps = fps;
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
