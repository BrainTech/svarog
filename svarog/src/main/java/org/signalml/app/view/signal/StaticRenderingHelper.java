package org.signalml.app.view.signal;

import java.util.Arrays;
import java.util.WeakHashMap;
import org.signalml.domain.signal.samplesource.MultichannelSampleSource;
import org.signalml.domain.tag.MonitorTag;

/**
 * Helper object for SignalPlot to properly display signal and tags
 * in "static" (non-scrolling) rendering mode for on-line signals.
 */
class StaticRenderingHelper {

	private final int channelCount; // number of channels
	private final int pageSampleCount; // number of samples per page
	private final double samplingFrequency; // Hz
	private final double pageLength; // s

	private final WeakHashMap<MonitorTag, InitialTagTiming> tagTimingCache = new WeakHashMap<>();
	private final double[][] tmp;

	private int indexInCycle = 0;
	private long cycleNumber = 0;

	StaticRenderingHelper(int channelCount, int pageSampleCount, double samplingFrequency) {
		this.channelCount = channelCount;
		this.pageSampleCount = pageSampleCount;
		this.samplingFrequency = samplingFrequency;
		this.pageLength = pageSampleCount / samplingFrequency;
		this.tmp = new double[channelCount][pageSampleCount];
	}

	/**
	 * Compute the proper placement of a given tag.
	 * The outcome depends on the internal state of this object
	 * resulting from the last call to fetchSamples.
	 *
	 * @param tag on-line tag
	 * @return
	 */
	TagTiming computeTagTiming(MonitorTag tag) {
		final double stepPosition = indexInCycle / samplingFrequency;
		InitialTagTiming tagTiming = tagTimingCache.get(tag);
		if (tagTiming == null) {
			tagTiming = createTagTiming(tag, stepPosition);
			tagTimingCache.put(tag, tagTiming);
		}

		double position = tagTiming.position;
		double length = tag.getLength();
		long tagAge = (int) (cycleNumber - tagTiming.cycleNumber);
		if (tagAge > 0 && position + length > pageLength) {
			// showing the next part of the tag
			--tagAge;
			length -= pageLength - position;
			position = 0;

			if (tagAge > 0 && length > pageLength) {
				// in case of very long tags
				long cyclesToSkip = Math.min(tagAge, Math.round(length/pageLength - 0.5));
				tagAge -= cyclesToSkip;
				length -= cyclesToSkip * pageLength;
			}
		}

		if (tagAge == 0) {
			// first epoch visible
			if (position + length > stepPosition) {
				return new TagTiming(position, Math.max(0, stepPosition-position));
			}
		} else if (tagAge == 1) {
			// second epoch visible
			if (position < stepPosition) {
				return new TagTiming(stepPosition, Math.max(0, position+length-stepPosition));
			}
		} else {
			// for rare cases of future tags or lingering old tags
			return new TagTiming(position, 0.0);
		}

		// most common case
		return new TagTiming(position, length);
	}

	/**
	 * Fetch the new set of samples from the given source
	 * into the internal buffers of this object.
	 * The internal state will be updated accordingly.
	 *
	 * @param source
	 */
	void fetchSamples(MultichannelSampleSource source) {
		long totalSampleCount = 0;
		for (int channel=0; channel<channelCount; ++channel) {
			totalSampleCount = source.getSamples(channel, tmp[channel], 0, pageSampleCount, 0);
		}
		setState(totalSampleCount);
	}

	/**
	 * Mainly for testing. In production code, use fetchSamples
	 * to set the state based on value from sample source.
	 * @param totalSampleCount total number of samples received so far
	 */
	void setState(long totalSampleCount) {
		indexInCycle = (int) (totalSampleCount % pageSampleCount);
		cycleNumber = totalSampleCount / pageSampleCount;
	}

	public int visibleSampleNumberToRealSampleNumber(int signalOffset)
	{
		
		return (pageSampleCount-indexInCycle+signalOffset) % pageSampleCount;
	}	
	
	/**
	 * Copy the properly shifted samples from the internal buffer
	 * (as fetched in the last call to fetchSamples) to the given array.
	 *
	 * @param samplesMargin number of samples to be made blank for visual aid between epochs
	 * @param channel channel number
	 * @param target array for new samples
	 * @param signalOffset the position (in samples) in the signal starting
	 * from which samples will be returned
	 * @param count the number of samples to be returned
	 */
	void getSamples(int samplesMargin, int channel, double[] target, int signalOffset, int count) {
		if (indexInCycle > signalOffset) {
			System.arraycopy(tmp[channel], pageSampleCount-indexInCycle+signalOffset, target, 0, Math.min(indexInCycle-signalOffset, count));
		}
		int sourceOffset = Math.max(0, signalOffset-indexInCycle);
		int targetOffset = Math.max(0, indexInCycle-signalOffset);
		int copyLength = Math.min(pageSampleCount-indexInCycle-sourceOffset, count-targetOffset);
		if (copyLength > 0) {
			System.arraycopy(tmp[channel], sourceOffset, target, targetOffset, copyLength);
		}

		int fillFrom = Math.max(0, indexInCycle-signalOffset);
		int fillTo = Math.min(count, indexInCycle-signalOffset+samplesMargin);
		if (fillFrom < fillTo) {
			Arrays.fill(target, fillFrom, fillTo, 0.0);
		}
	}

	private InitialTagTiming createTagTiming(MonitorTag tag, double stepPosition) {
		double position = stepPosition - pageLength + tag.getPosition();
		long cycleDelta = (long) Math.floor(position / pageLength);
		long tagCycleNumber = cycleNumber + cycleDelta;
		position -= cycleDelta * pageLength;
		return new InitialTagTiming(position, tagCycleNumber);
	}
}
