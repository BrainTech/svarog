/* SegmentedSampleSource.java created 2008-01-26
 *
 */

package org.signalml.domain.signal.space;

import org.signalml.domain.signal.MultichannelSampleProcessor;
import org.signalml.domain.signal.MultichannelSampleSource;
import org.signalml.domain.signal.MultichannelSegmentedSampleSource;
import org.signalml.domain.signal.SignalSelection;
import org.signalml.domain.signal.SignalSelectionType;
import org.signalml.exception.SanityCheckException;

/** SegmentedSampleSource
 *
 *
 * @author Michal Dobaczewski &copy; 2007-2008 CC Otwarte Systemy Komputerowe Sp. z o.o.
 */
public class SelectionSegmentedSampleSource extends MultichannelSampleProcessor implements MultichannelSegmentedSampleSource {

	private int firstSample;
	private int segmentLength;
	private int segmentCount;

	private int channelCount;
	private int[] channelIndices;

	private int unusableSegmentCount;
	private float firstPosition;

	public SelectionSegmentedSampleSource(MultichannelSampleSource source) {
		super(source);
	}

	public SelectionSegmentedSampleSource(MultichannelSampleSource source, SignalSelection selection, ChannelSpace channelSpace, float pageSize, float blockSize) {
		super(source);

		float samplingFrequency = source.getSamplingFrequency();

		SignalSelectionType selectionType = selection.getType();

		// determine channel count & indices
		if (channelSpace != null) {

			channelCount = channelSpace.size();
			channelIndices = channelSpace.getSelectedChannels();

		}
		else if (selectionType.isChannel()) {

			channelCount = 1;
			channelIndices = new int[] { selection.getChannel() };

		}
		else { // all channels

			channelCount = source.getChannelCount();
			channelIndices = new int[channelCount];
			for (int i=0; i<channelCount; i++) {
				channelIndices[i] = i;
			}

		}

		// determine segmentation

		firstPosition = selection.getPosition();
		firstSample = (int)(firstPosition * samplingFrequency);

		if (selectionType.isChannel()) {

			segmentCount = 1;
			segmentLength = (int)(selection.getLength() * samplingFrequency);

		} else {

			float segmentSize;

			if (selectionType.isPage()) {
				segmentSize = pageSize;
				segmentLength = (int)(pageSize * samplingFrequency);
			} else if (selectionType.isBlock()) {
				segmentSize = blockSize;
				segmentLength = (int)(blockSize * samplingFrequency);
			} else {
				throw new SanityCheckException("Unsupported type [" + selectionType + "]");
			}

			segmentCount = selection.getSegmentLength(segmentSize);
			if (segmentCount * segmentSize < selection.getLength()) {
				unusableSegmentCount = 1;
			} else {
				unusableSegmentCount = 0;
			}

		}

	}

	@Override
	public float getSegmentTime(int segment) {
		return firstPosition + (segmentLength*segment);
	}

	public SelectionSegmentedSampleSource(MultichannelSampleSource source, SelectionSegmentedSampleSourceDescriptor descriptor) {
		this(source);

		firstSample = descriptor.getFirstSample();
		segmentLength = descriptor.getSegmentLength();
		segmentCount = descriptor.getSegmentCount();

		channelCount = descriptor.getChannelCount();
		channelIndices = descriptor.getChannelIndices();

		unusableSegmentCount = descriptor.getUnusableSegmentCount();

	}

	@Override
	public SegmentedSampleSourceDescriptor createDescriptor() {

		SelectionSegmentedSampleSourceDescriptor descriptor = new SelectionSegmentedSampleSourceDescriptor();

		descriptor.setFirstSample(firstSample);
		descriptor.setSegmentLength(segmentLength);
		descriptor.setSegmentCount(segmentCount);

		descriptor.setChannelCount(channelCount);
		descriptor.setChannelIndices(channelIndices);

		descriptor.setUnusableSegmentCount(unusableSegmentCount);

		return descriptor;
	}

	@Override
	public void getSamples(int channel, double[] target, int signalOffset, int count, int arrayOffset) {
		source.getSamples(channelIndices[channel], target, firstSample+signalOffset, count, arrayOffset);
	}

	@Override
	public int getDocumentChannelIndex(int channel) {
		return source.getDocumentChannelIndex(channelIndices[channel]);
	}

	@Override
	public int getChannelCount() {
		return channelCount;
	}

	@Override
	public int getSegmentCount() {
		return segmentCount;
	}

	@Override
	public int getSegmentLength() {
		return segmentLength;
	}

	@Override
	public int getSampleCount(int channel) {
		return (segmentCount * segmentLength);
	}

	@Override
	public String getLabel(int channel) {
		return source.getLabel(channelIndices[channel]);
	}

	@Override
	public int getUnusableSegmentCount() {
		return unusableSegmentCount;
	}

	@Override
	public void getSegmentSamples(int channel, double[] target, int segment) {
		source.getSamples(channelIndices[channel], target, firstSample+(segment*segmentLength), segmentLength, 0);
	}

}
