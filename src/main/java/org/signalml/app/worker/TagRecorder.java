/* TagRecorder.java created 2010-10-30
 *
 */

package org.signalml.app.worker;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.signalml.app.document.TagDocument;
import org.signalml.domain.tag.StyledTagSet;
import org.signalml.plugin.export.SignalMLException;
import org.signalml.plugin.export.signal.Tag;

import org.signalml.domain.tag.MonitorTag;
import org.signalml.plugin.export.signal.TagStyle;

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

        /**
         * Path to the output file.
         */
        private String filePath;

        /**
         * Whether the worker is finished.
         */
        private volatile boolean finished;

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

                        doSave(getRecordedTagSet());
                }
        }

        /**
         * Saves all received tags and styles to the output file.
         * @param styles styles to be saved
         */
        public void save(LinkedHashSet<TagStyle> styles) {

                synchronized (this) {

                        StyledTagSet tagSet = getRecordedTagSet();
                        for(TagStyle tagStyle: styles)
				tagSet.addStyle(tagStyle);

                        doSave(tagSet);
                        finished = true;
                }
        }

        /**
         * Does the saving.
         * @param tagSet tag set to savetagSet
         */
        public void doSave(StyledTagSet tagSet) {

                File backingFile = new File(filePath);
                if (backingFile.exists()) {
                        backingFile.delete();
                }
        
                try {
                        TagDocument tagDocument;
                        tagDocument = new TagDocument(tagSet);
                        tagDocument.setBackingFile(backingFile);
                        tagDocument.saveDocument();
                } catch (SignalMLException ex) {
                        Logger.getLogger(TagRecorder.class.getName()).log(Level.SEVERE, null, ex);
                } catch (IOException ex) {
                        Logger.getLogger(TagRecorder.class.getName()).log(Level.SEVERE, null, ex);
                }
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