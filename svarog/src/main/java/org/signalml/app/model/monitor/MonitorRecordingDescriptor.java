/* MonitorRecordingDescriptor.java created 2010-11-03
 *
 */
package org.signalml.app.model.monitor;

import java.io.File;
import org.signalml.app.video.VideoStreamSpecification;

/**
 * Describes the parameters of the monitor recording - files to which the signal
 * and tags is recorded etc.
 *
 * @author Piotr Szachewicz
 */
public class MonitorRecordingDescriptor {

	private boolean recordingEnabled = false;
	private String signalRecordingFilePath;
	private boolean tagsRecordingEnabled = true;
	private boolean saveImpedance = false;
	private boolean appendTimestamps = false;
	private String videoRecordingFilePath;
	private boolean displayVideoPreviewWhileSaving = true;
	private VideoStreamSpecification videoStreamSpecification;

	/**
	 * Creates an empty {@link MonitorRecordingDescriptor} - all values are
	 * null and recording is disabled.
	 */
	public MonitorRecordingDescriptor() {
            String default_path = System.getProperty("user.home") + File.separator;
            this.signalRecordingFilePath = default_path;
            this.videoRecordingFilePath = default_path;

	}

	/**
	 * Creates a {@link MonitorRecordingDescriptor} with the given parameters.
	 *
	 * @param signalRecordingFilePath a file path to record signal to
	 * @param isTagsRecordingEnabled determines if the tags should be recorded
	 * @param videoRecordingFilePath a file path to record video to
	 * @param videoStreamSpecification video stream selected by the user
	 */
	public MonitorRecordingDescriptor(String signalRecordingFilePath,
									  boolean isTagsRecordingEnabled,
									  String videoRecordingFilePath, VideoStreamSpecification videoStreamSpecification) {
		this.signalRecordingFilePath = signalRecordingFilePath;
		this.tagsRecordingEnabled = isTagsRecordingEnabled;
		this.videoRecordingFilePath = videoRecordingFilePath;
		this.videoStreamSpecification = videoStreamSpecification;
	}

	/**
	 * Returns the file path to which the signal will be recorded.
	 * @return the file path to record signal to
	 */
	public String getSignalRecordingFilePath() {
		return signalRecordingFilePath;
	}

	/**
	 * Sets the file path to which the signal will be recorded.
	 * @param signalRecordingFilePath the new file path to which the signal
	 * will be recorded.
	 */
	public void setSignalRecordingFilePath(String signalRecordingFilePath) {
		this.signalRecordingFilePath = signalRecordingFilePath;
	}

	/**
	 * Returns if the tags should be recorded.
	 * @return true if the tags should be recorded, false otherwise
	 */
	public boolean isTagsRecordingEnabled() {
		return tagsRecordingEnabled;
	}

	/**
	 * Sets if the tag recording should be enabled.
	 * @param tagsRecordingEnabled
	 */
	public void setTagsRecordingEnabled(boolean tagsRecordingEnabled) {
		this.tagsRecordingEnabled = tagsRecordingEnabled;
	}

	/**
	 * Returns if the impedance should be saved with the signal.
	 * @return true if the impedance should be recorded, false otherwise
	 */
	public boolean isSaveImpedanceEnabled() {
		return saveImpedance;
	}

	/**
	 * Sets if the impedance should be saved with the signal.
	 * @param saveImpedance
	 */
	public void setSaveImpedanceEnabled(boolean saveImpedance) {
		this.saveImpedance = saveImpedance;
	}

	/**
	 * Returns if the timestamps should be appended.
	 * @return true if the timestamps should be appended, false otherwise
	 */
	public boolean isAppendTimestampsEnabled() {
		return appendTimestamps;
	}

	/**
	 * Sets if the timestamps should be appended.
	 * @param appendTimestamps
	 */
	public void setAppendTimestampsEnabled(boolean appendTimestamps) {
		this.appendTimestamps = appendTimestamps;
	}

	/**
	 * Returns the relative file path (without an extension) to which the video will be recorded.
	 * @return the file path to record video to.
	 */
	public String getVideoRecordingFilePath() {
		return videoRecordingFilePath;
	}

	/**
	 * Returns the relative file path (WITH an extension) to which the video will be recorded.
	 * @return the file path to record video to.
	 */
	public String getVideoRecordingFilePathWithExtension() {
		return videoRecordingFilePath + ".mkv";
	}

	/**
	 * Sets the file path to which the tags will be recorded.
	 * @param videoRecordingFilePath the new file path to which the video
	 * will be recorded.
	 */
	public void setVideoRecordingFilePath(String videoRecordingFilePath) {
            this.videoRecordingFilePath = videoRecordingFilePath;
	}

	/**
	 * Returns if the video should be recorded.
	 * @return true if the video should be recorded, false otherwise
	 */
	public boolean isVideoRecordingEnabled() {
		return videoStreamSpecification != null;
	}

	/**
	 * @return video stream specification selected by the user
	 */
	public VideoStreamSpecification getVideoStreamSpecification() {
		return videoStreamSpecification;
	}

	/**
	 * @param videoStreamSpecification  video stream specification selected by the user
	 */
	public void setVideoStreamSpecification(VideoStreamSpecification videoStreamSpecification) {
		this.videoStreamSpecification = videoStreamSpecification;
	}

	/**
	 * @return whether video preview should be displayed while saving
	 */
	public boolean getDisplayVideoPreviewWhileSaving() {
		return displayVideoPreviewWhileSaving;
	}

	/**
	 * @param displayVideoPreviewWhileSaving whether video preview should be displayed while saving
	 */
	public void setDisplayVideoPreviewWhileSaving(boolean displayVideoPreviewWhileSaving) {
		this.displayVideoPreviewWhileSaving = displayVideoPreviewWhileSaving;
	}

	/**
	 * Sets if the recording should be performed at all.
	 * @param isRecordingEnabled
	 */
	public void setRecordingEnabled(boolean isRecordingEnabled) {
		this.recordingEnabled = isRecordingEnabled;
	}

	/**
	 * Returns if the recording should be performed or not.
	 * @return true if the recording should be performed, false otherwise.
	 */
	public boolean isRecordingEnabled() {
		return recordingEnabled;
	}
}
