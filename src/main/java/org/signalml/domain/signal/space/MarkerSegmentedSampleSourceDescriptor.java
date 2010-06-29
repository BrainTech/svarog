/* MarkerSegmentedSampleSourceDescriptor.java created 2008-02-15
 *
 */

package org.signalml.domain.signal.space;

import org.signalml.domain.signal.MultichannelSampleSource;
import org.signalml.domain.signal.MultichannelSegmentedSampleSource;

import com.thoughtworks.xstream.annotations.XStreamAlias;

/** MarkerSegmentedSampleSourceDescriptor
 *
 *
 * @author Michal Dobaczewski &copy; 2007-2008 CC Otwarte Systemy Komputerowe Sp. z o.o.
 */
@XStreamAlias("markersegsource")
public class MarkerSegmentedSampleSourceDescriptor implements SegmentedSampleSourceDescriptor {

	private static final long serialVersionUID = 1L;

	private int segmentLength;
	private int[] offsets;

	private int channelCount;
	private int[] channelIndices;

	private int unusableSegmentCount;
	private int samplesBefore;
	private int samplesAfter;

	public MarkerSegmentedSampleSourceDescriptor() {
	}

	@Override
	public MultichannelSegmentedSampleSource createSegmentedSource(MultichannelSampleSource source) {
		return new MarkerSegmentedSampleSource(source, this);
	}

	public int getSegmentLength() {
		return segmentLength;
	}

	public void setSegmentLength(int segmentLength) {
		this.segmentLength = segmentLength;
	}

	public int[] getOffsets() {
		return offsets;
	}

	public void setOffsets(int[] offsets) {
		this.offsets = offsets;
	}

	public int getChannelCount() {
		return channelCount;
	}

	public void setChannelCount(int channelCount) {
		this.channelCount = channelCount;
	}

	public int[] getChannelIndices() {
		return channelIndices;
	}

	public void setChannelIndices(int[] channelIndices) {
		this.channelIndices = channelIndices;
	}

	public int getUnusableSegmentCount() {
		return unusableSegmentCount;
	}

	public void setUnusableSegmentCount(int unusableSegmentCount) {
		this.unusableSegmentCount = unusableSegmentCount;
	}

	public int getSamplesBefore() {
		return samplesBefore;
	}

	public void setSamplesBefore(int samplesBefore) {
		this.samplesBefore = samplesBefore;
	}

	public int getSamplesAfter() {
		return samplesAfter;
	}

	public void setSamplesAfter(int samplesAfter) {
		this.samplesAfter = samplesAfter;
	}

}
