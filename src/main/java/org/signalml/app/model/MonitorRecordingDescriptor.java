/* MonitorRecordingDescriptor.java created 2010-11-03
 *
 */

package org.signalml.app.model;

/**
 *
 * @author Piotr Szachewicz
 */
public class MonitorRecordingDescriptor {

	private boolean recordingEnabled = false;
	private String signalRecordingFilePath;
	private String tagsRecordingFilePath;
	private boolean tagsRecordingDisabled;

	public MonitorRecordingDescriptor() {
	}

	public MonitorRecordingDescriptor(String signalRecordingFilePath, String tagsRecordingFilePath, boolean isTagsRecordingDisabled) {
		this.signalRecordingFilePath = signalRecordingFilePath;
		this.tagsRecordingFilePath = tagsRecordingFilePath;
		this.tagsRecordingDisabled = isTagsRecordingDisabled;
	}

	public String getSignalRecordingFilePath() {
		return signalRecordingFilePath;
	}

	public void setSignalRecordingFilePath(String signalRecordingFilePath) {
		this.signalRecordingFilePath = signalRecordingFilePath;
	}

	public String getTagsRecordingFilePath() {
		return tagsRecordingFilePath;
	}

	public void setTagsRecordingFilePath(String tagsRecordingFilePath) {
		this.tagsRecordingFilePath = tagsRecordingFilePath;
	}

	public boolean isTagsRecordingDisabled() {
		return tagsRecordingDisabled;
	}

	public void setTagsRecordingDisabled(boolean tagsRecordingDisabled) {
		this.tagsRecordingDisabled = tagsRecordingDisabled;
	}

	public void setRecordingEnabled(boolean isRecordingEnabled) {
		this.recordingEnabled = isRecordingEnabled;
	}

	public boolean isRecordingEnabled() {
		return recordingEnabled;
	}

}
