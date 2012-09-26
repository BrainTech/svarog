package org.signalml.domain.tag;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.SortedSet;
import java.util.concurrent.Semaphore;
import javax.swing.Timer;
import org.signalml.plugin.export.signal.Tag;

/** StyledTagSet
 *
 *
 * @author Michal Dobaczewski &copy; 2007-2008 CC Otwarte Systemy Komputerowe Sp. z o.o.
 */
public class StyledMonitorTagSet extends StyledTagSet implements ActionListener {

	private static final long serialVersionUID = 1L;

	/**
	 * The timestamp of the first visible sample on the left (the 'oldest'
	 * visible sample).
	 */
	protected double firstSampleTimestamp;
	protected float samplingFrequency;
	protected Semaphore semaphore;

	/**
	 * Number of milliseconds between each call for old tags cleanup action.
	 */
	private int oldTagsCleanupTimeInterval = 3000;
	/**
	 * The timer calling the old tags cleanup action at a specified intervals
	 * of time.
	 */
	private Timer oldTagsRemoverTimer;

	public StyledMonitorTagSet(float pageSize, int blocksPerPage, float samplingFrequency) {
		super(pageSize, blocksPerPage);
		this.samplingFrequency = samplingFrequency;
		this.semaphore = new Semaphore(1);

		this.oldTagsRemoverTimer = new Timer(oldTagsCleanupTimeInterval, this);
		oldTagsRemoverTimer.setInitialDelay(oldTagsCleanupTimeInterval);
		oldTagsRemoverTimer.start();

	}

	public void newSample(double newestSampleTimestamp) {
		this.firstSampleTimestamp = newestSampleTimestamp - this.getPageSize();
	}

	public SortedSet<Tag> getTagsBetween(double start, double end) {
		start = (double) 0.0;
		end = Double.MAX_VALUE;
		Tag startMarker = new Tag(null, start - maxTagLength, 0);
		Tag endMarker = new Tag(null, end, Double.MAX_VALUE); // note that lengths matter, so that all tags starting at exactly end will be selected
		return tags.subSet(startMarker, true, endMarker, true);
	}

	public double computePosition(double position) {
		return position - firstSampleTimestamp;
	}

	public void addTag(MonitorTag tag) {
		tag.setParent(this);
		super.addTag(tag);
	}

	public void lock() {
		try {
			this.semaphore.acquire();
		} catch (InterruptedException ex) {
			logger.error("An error occured while trying to acquire semaphore.");
		}
	}

	public void unlock() {
		this.semaphore.release();
	}

	public void stopTagsRemoving() {
		oldTagsRemoverTimer.stop();
	}

	/**
	 * Action performed when the oldTagsRemoverTimer decides to perform an
	 * action of deleting old tags.
	 * @param e
	 */
	@Override
	public void actionPerformed(ActionEvent e) {
		//search through all tags to find those that are not visible anymore
		Object[] tagArray = getTags().toArray();
		for (Object o: tagArray) {
			Tag tag = (MonitorTag) o;
			if (tag.getPosition() + tag.getLength() < 0)
				this.removeTag(tag);
			else
				break;
		}
		logger.debug("Old tags removing action performed - number of tags after removal: " + getTags().size());
	}
}