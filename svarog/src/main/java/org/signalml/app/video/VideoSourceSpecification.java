package org.signalml.app.video;

import java.util.Collections;
import java.util.List;

/**
 * Simple immutable PDO (Plain Data Object) specifying a single video source,
 * (e.g  video camera) capable of displaying one or more video streams.
 *
 * @author piotr.rozanski@braintech.pl
 */
public class VideoSourceSpecification {

	public final String description;
	public final List<VideoStreamSpecification> streams;

	/**
	 * Create a new video source specification.
	 * Given list of streams should not be modified
	 * after the object is constructed.
	 *
	 * @param description  human-readable description for this source
	 * @param streams  list of video stream specifications
	 */
	public VideoSourceSpecification(String description, List<VideoStreamSpecification> streams) {
		this.description = description;
		this.streams = Collections.unmodifiableList(streams);
	}

	@Override
	public String toString() {
		return description;
	}

}
