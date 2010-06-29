/* SelectionSegmentedSampleSourceDescriptor.java created 2008-02-15
 *
 */

package org.signalml.domain.signal.space;

import org.signalml.domain.signal.MultichannelSampleSource;
import org.signalml.domain.signal.MultichannelSegmentedSampleSource;

import com.thoughtworks.xstream.annotations.XStreamAlias;

/** SelectionSegmentedSampleSourceDescriptor
 *
 *
 * @author Michal Dobaczewski &copy; 2007-2008 CC Otwarte Systemy Komputerowe Sp. z o.o.
 */
@XStreamAlias("selectionsegsource")
public class SelectionSegmentedSampleSourceDescriptor implements SegmentedSampleSourceDescriptor {

	private static final long serialVersionUID = 1L;

	private int firstSample;
	private int segmentLength;
	private int segmentCount;

	private int channelCount;
	private int[] channelIndices;

	private int unusableSegmentCount;

	public SelectionSegmentedSampleSourceDescriptor() {
	}

	@Override
	public MultichannelSegmentedSampleSource createSegmentedSource(MultichannelSampleSource source) {
		return new SelectionSegmentedSampleSource(source,this);
	}

	public int getFirstSample() {
		return firstSample;
	}

	public void setFirstSample(int firstSample) {
		this.firstSample = firstSample;
	}

	public int getSegmentLength() {
		return segmentLength;
	}

	public void setSegmentLength(int segmentLength) {
		this.segmentLength = segmentLength;
	}

	public int getSegmentCount() {
		return segmentCount;
	}

	public void setSegmentCount(int segmentCount) {
		this.segmentCount = segmentCount;
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

}
