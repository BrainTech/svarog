package org.signalml.domain.tag;

import java.util.SortedSet;
import java.util.concurrent.Semaphore;

import org.signalml.app.document.MonitorSignalDocument;
import org.signalml.domain.signal.RoundBufferSampleSource;
import org.signalml.plugin.export.signal.Tag;

/** StyledTagSet
 *
 * 
 * @author Michal Dobaczewski &copy; 2007-2008 CC Otwarte Systemy Komputerowe Sp. z o.o.
 */

public class StyledMonitorTagSet extends StyledTagSet {
    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	protected double lastSampleTimestamp;
    protected Semaphore semaphore;
    protected RoundBufferSampleSource timestamps_source;
    public StyledMonitorTagSet(float pageSize, int blocksPerPage) {
	super(pageSize, blocksPerPage);
	this.semaphore = new Semaphore(1);

	}
    public void setTs(RoundBufferSampleSource ts) {
    	this.timestamps_source = ts;
    }
    public void newSample(double newestSampleTimestamp) {
	this.lastSampleTimestamp = newestSampleTimestamp - this.getPageSize();
    }
    public SortedSet<Tag> getTagsBetween( double start, double end ) {
	start = (double) 0.0;
	end = Double.MAX_VALUE;
	Tag startMarker = new Tag(null, start-maxTagLength, 0);
	Tag endMarker = new Tag(null,end,Double.MAX_VALUE); // note that lengths matter, so that all tags starting at exactly end will be selected 
	return tags.subSet(startMarker, true, endMarker, true);
	}
    public double computePosition(double position) {
    	//return position - this.lastSampleTimestamp;
		
		double tag_position = position;

		
		int ts_count = timestamps_source.getSampleCount();
		double[] timestamps = new double[ts_count];
		timestamps_source.getSamples(timestamps, 0, ts_count, 0);
		double startingSample = -1000000.0;
		double samplingFrequency;
		samplingFrequency = 128;
		
		for (int i = 0; i < ts_count;i++) {
		    if (tag_position < timestamps[i]) {
			startingSample = i;
			break;
		    }
		}
		return  startingSample/samplingFrequency;
		/*logger.info("###################################################################");
		logger.info("sampling: "+samplingFrequency);
		logger.info("ts.count: "+timestamp_source.getSampleCount());
		logger.info("oldest ts: "+((int) timestamps[0]));
		logger.info("tag ts: "+ ((int)tag_position));
		logger.info("tag len: "+tag.getLength());
		logger.info("similar sample ts number: "+startingSample);
		logger.info("###################################################################");*/

		
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