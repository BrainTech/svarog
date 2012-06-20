/* TagRecorder.java created 2010-10-30
 *
 */

package org.signalml.app.worker.monitor;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.SortedSet;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.signalml.app.document.TagDocument;
import org.signalml.domain.tag.StyledTagSet;
import org.signalml.plugin.export.SignalMLException;
import org.signalml.plugin.export.signal.Tag;
import org.signalml.domain.tag.MonitorTag;
import org.signalml.domain.tag.StyledTagSetConverter;

/**
 * This class allows to record tags from a {@link MonitorWorker}. To start recording
 * create a tag recorder and connect it to a {@link MonitorWorker} using {@link MonitorWorker#connectTagRecorderWorker(org.signalml.app.worker.monitor.TagRecorder)}.
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

	/**
	 * Path to the output file.
	 */
	private String filePath;

	/**
	 * Whether the worker is finished.
	 */
	private volatile boolean finished;

	/**
	 * Ending of the file (everything after last tag).
	 */
	private String fileEnding;

	/**
	 * Length of {@link #fileEnding} in bytes.
	 */
	private int endingLength;

	/**
	 * Default constructor.
	 * @param filePath path to output file
	 */
	public TagRecorder(String filePath) {

		if (!filePath.endsWith(".tag")) {
			filePath += ".tag";
		}

		this.filePath = filePath;
		this.finished = false;
	}

	/**
	 * Records the given tag.
	 *
	 * @param tag {@link MonitorTag} to be recorded
	 */
	public void offerTag(MonitorTag tag) {

		synchronized (this) {

			if (!finished) {
				tagList.add(tag);
			}
		}
	}

	/**
	 * Saves tags received so far to the output file.
	 */
	public void doBackup() {

		synchronized (this) {

			doSave();
		}
	}

	/**
	 * Saves all received tags to the output file.
	 * @param styles styles to be saved
	 */
	public void save() {

		synchronized (this) {

			doSave();
			finished = true;
		}
	}

	/**
	 * Does the saving.
	 * @param tagSet tag set to savetagSet
	 */
	public void doSave() {

		File backingFile = new File(filePath);
		StyledTagSet tagSet = getRecordedTagSet();

		try {
			// if this is the first backup - create the file normally
			if (!backingFile.exists()) {
				TagDocument tagDocument = new TagDocument(tagSet);
				tagDocument.setBackingFile(backingFile);
				tagDocument.saveDocument();
				findEnding(backingFile);
				// else - add tags at the end
			} else {
				addTags(backingFile, tagSet.getTags());
			}

			removeAllTags();

		} catch (Exception ex) {
			Logger.getLogger(TagRecorder.class.getName()).log(Level.SEVERE, null, ex);
		}
	}

	/**
	 * Finds {@link #fileEnding}.
	 * @param backingFile the file containing the tag document
	 */
	private void findEnding(File backingFile) throws FileNotFoundException, IOException {

		// this is only called once, so we can load the entire file into a single String
		byte[] buffer = new byte[(int)backingFile.length()];
		BufferedInputStream stream = new BufferedInputStream(new FileInputStream(backingFile));
		stream.read(buffer);
		stream.close();
		String content = new String(buffer, TagDocument.CHAR_SET);

		// closing of the tag section
		String tagSectionClosing = "</" + StyledTagSetConverter.TAG_NODE_NAME + ">";

		// get position of tag section closing, and save everything from that point to fileEnding
		int start = content.indexOf(tagSectionClosing);
		fileEnding = content.substring(start);

		// length of ending in bytes
		int lengthOfSingleChar = (int)Charset.forName(TagDocument.CHAR_SET).newEncoder().averageBytesPerChar();
		endingLength = lengthOfSingleChar * fileEnding.length();

	}

	/**
	 * Adds given tag set to end of tag section of given file.
	 * @param backingFile file to add tags to
	 * @param tags tags to add
	 */
	private void addTags(File backingFile, SortedSet<Tag> tags) throws IOException {

		// get tags to save, and add fileEnding at the end
		String toSave = StyledTagSetConverter.marshalTagsToString(tags);
		if (!toSave.endsWith("\n")) {
			toSave += "\n";
		}
		toSave += fileEnding;

		// Add tags to the file
		int bytesToSkip = (int)backingFile.length() - endingLength;
		RandomAccessFile file = new RandomAccessFile(backingFile, "rwd");
		file.skipBytes(bytesToSkip);
		file.write(toSave.getBytes(TagDocument.CHAR_SET));
		file.close();
	}

	/**
	 * Returns the {@link StyledTagSet} containing the tags which were recorded by
	 * this {@link TagRecorder}.
	 *
	 * @return a {@link StyledTagSet} containing the recorded tags
	 */
	private StyledTagSet getRecordedTagSet() {

		if (getStartRecordingTimestamp() == Double.NaN)
			throw new UnsupportedOperationException("the startRecordingTimestamp was not set for the TagRecorder object");

		StyledTagSet styledTagSet = new StyledTagSet();
		Tag temporaryTag;

		for (MonitorTag monitorTag: tagList) {
			temporaryTag = monitorTag.clone();
			temporaryTag.setPosition(monitorTag.getTimestamp() - getStartRecordingTimestamp());
			styledTagSet.addTag(temporaryTag);
		}

		return styledTagSet;
	}

	/**
	 * Should be called to remove all tags after they were saved to a file.
	 */
	private void removeAllTags() {

		tagList.clear();
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