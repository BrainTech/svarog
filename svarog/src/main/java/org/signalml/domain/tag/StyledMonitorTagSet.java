package org.signalml.domain.tag;

import java.util.SortedSet;
import java.util.concurrent.Semaphore;
import org.signalml.plugin.export.signal.Tag;

/** StyledTagSet
 *
 * 
 * @author Michal Dobaczewski &copy; 2007-2008 CC Otwarte Systemy Komputerowe Sp. z o.o.
 */
public class StyledMonitorTagSet extends StyledTagSet {

	private static final long serialVersionUID = 1L;

	/**
	 * The timestamp of the first visible sample on the left (the 'oldest'
	 * visible sample).
	 */
	protected double firstSampleTimestamp;
	protected float samplingFrequency;
	protected Semaphore semaphore;

	public StyledMonitorTagSet(float pageSize, int blocksPerPage, float samplingFrequency) {
		super(pageSize, blocksPerPage);
		this.samplingFrequency = samplingFrequency;
		this.semaphore = new Semaphore(1);

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
}
