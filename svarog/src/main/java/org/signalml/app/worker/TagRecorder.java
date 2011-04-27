/* TagRecorder.java created 2010-10-30
 *
 */

package org.signalml.app.worker;

import java.util.ArrayList;
import java.util.Date;
import org.apache.log4j.Logger;
import org.signalml.domain.tag.StyledTagSet;
import org.signalml.plugin.export.signal.Tag;

import javax.swing.JOptionPane;

import org.signalml.domain.tag.MonitorTag;

/**
 * This class allows to record tags from a {@link MonitorWorker}. To start recording
 * create a tag recorder and connect it to a {@link MonitorWorker} using {@link MonitorWorker#connectTagRecorderWorker(org.signalml.app.worker.TagRecorder)}.
 * To stop recording disconnect it from a the {@link MonitorWorker} using {@link MonitorWorker#disconnectTagRecorderWorker()}.
 * After disconnecting, the recorded tags can be read using {@link TagRecorder#getRecordedTagSet()}.
 *
 * @author Piotr Szachewicz
 */
public class TagRecorder {

	/**
	 * The timestamp of the first sample in the samples recording (recorded by the {@link SignalRecorderWorker}).
	 * Used to calculate the position of the tag relatively to the beginning of the recording.
	 */
	private double startRecordingTimestamp = Double.NaN;

	/**
	 * An ArrayList containing the recorded tags.
	 */
	private ArrayList<MonitorTag> tagList = new ArrayList<MonitorTag>();

	public TagRecorder() {
	}

	/**
	 * Records the given tag.
	 *
	 * @param tag {@link MonitorTag} to be recorded
	 */
	public void offer(MonitorTag tag) {	
		tagList.add(tag);
	}

	/**
	 * Returns the {@link StyledTagSet} containing the tags which were recorded by
	 * this {@link TagRecorder}.
	 *
	 * @return a {@link StyledTagSet} containing the recorded tags
	 */
	public StyledTagSet getRecordedTagSet() {

		if (getStartRecordingTimestamp() == Double.NaN)
			throw new UnsupportedOperationException("the startRecordingTimestamp was not set for the TagRecorder object");

		StyledTagSet styledTagSet = new StyledTagSet();
		Tag temporaryTag;

		for (MonitorTag monitorTag: tagList) {
			temporaryTag = monitorTag.clone();
			temporaryTag.setPosition(monitorTag.getRealPosition() - getStartRecordingTimestamp());
			styledTagSet.addTag(temporaryTag);
		}

		return styledTagSet;

	}

	/**
	 * Sets the timestamp relatively to which the positions of the recorded tags
	 * will be calculated.
	 *
	 * @param startRecordingTimestamp tags returned by the {@link TagRecorder#getRecordedTagSet()} will
	 * have their positions recalculated relatively to this timestamp.
	 */
	public void setStartRecordingTimestamp(double startRecordingTimestamp) {
		this.startRecordingTimestamp = startRecordingTimestamp;
	}

	/**
	 * Returns the timestamp relatively to which the positions of the recorded tags
	 * will be calculated.
	 *
	 * @return the timestamp relatively to which the positions of the tags will
	 * be calculated
	 */
	public double getStartRecordingTimestamp() {
		return startRecordingTimestamp;
	}

	/**
	 * Returns if the startRecordingTimestamp was set using {@link TagRecorder#setStartRecordingTimestamp(double)}.
	 *
	 * @return true if the startRecordingTimestamp was set, false otherwise.
	 */
	public boolean isStartRecordingTimestampSet() {
		if (Double.isNaN(startRecordingTimestamp))
			return false;
		return true;
	}

}