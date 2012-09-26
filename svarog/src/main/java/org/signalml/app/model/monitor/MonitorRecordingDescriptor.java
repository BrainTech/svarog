/* MonitorRecordingDescriptor.java created 2010-11-03
 *
 */
package org.signalml.app.model.monitor;

/**
 * Describes the parameters of the monitor recording - files to which the signal
 * and tags is recorded etc.
 *
 * @author Piotr Szachewicz
 */
public class MonitorRecordingDescriptor {

	private boolean recordingEnabled = false;
	private String signalRecordingFilePath;
	private String tagsRecordingFilePath;
	private boolean tagsRecordingEnabled = true;

	/**
	 * Creates an empty {@link MonitorRecordingDescriptor} - all values are
	 * null and recording is disabled.
	 */
	public MonitorRecordingDescriptor() {
	}

	/**
	 * Creates a {@link MonitorRecordingDescriptor} with the given parameters.
	 *
	 * @param signalRecordingFilePath a file path to record signal to
	 * @param tagsRecordingFilePath a file path to record tags to
	 * @param isTagsRecordingDisabled determines if the tags should be recorded
	 */
	public MonitorRecordingDescriptor(String signalRecordingFilePath,
									  String tagsRecordingFilePath, boolean isTagsRecordingDisabled) {
		this.signalRecordingFilePath = signalRecordingFilePath;
		this.tagsRecordingFilePath = tagsRecordingFilePath;
		this.tagsRecordingEnabled = isTagsRecordingDisabled;
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
	 * Returns the file path to which the tags will be recorded.
	 * @return the file path to record tags to.
	 */
	public String getTagsRecordingFilePath() {
		return tagsRecordingFilePath;
	}

	/**
	 * Sets the file path to which the tags will be recorded.
	 * @param tagsRecordingFilePath the new file path to which the tags
	 * will be recorded.
	 */
	public void setTagsRecordingFilePath(String tagsRecordingFilePath) {
		this.tagsRecordingFilePath = tagsRecordingFilePath;
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
