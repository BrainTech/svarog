/* MonitorRecordingDescriptor.java created 2010-11-03
 *
 */
package org.signalml.app.model;

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
	private boolean tagsRecordingDisabled;

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
		this.tagsRecordingDisabled = isTagsRecordingDisabled;
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
	 * @return true if the tags should NOT be recorded, false otherwise
	 */
	public boolean isTagsRecordingDisabled() {
		return tagsRecordingDisabled;
	}

	/**
	 * Sets if the tag recording should be disabled.
	 * @param tagsRecordingDisabled
	 */
	public void setTagsRecordingDisabled(boolean tagsRecordingDisabled) {
		this.tagsRecordingDisabled = tagsRecordingDisabled;
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
