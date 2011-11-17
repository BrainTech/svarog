/* TagComparisonResults.java created 2007-11-14
 *
 */

package org.signalml.domain.tag;

import static org.signalml.app.SvarogI18n._;
import org.signalml.domain.montage.SourceMontage;
import org.signalml.domain.signal.MultichannelSampleSource;
import org.signalml.plugin.export.signal.Tag;

/**
 * This class contains {@link TagComparisonResult results} of comparison
 * between two sets of tags (two files with tags).
 * Results are divided into different types of tags (page-, block-, channel-).
 *
 * @author Michal Dobaczewski &copy; 2007-2008 CC Otwarte Systemy Komputerowe Sp. z o.o.
 */
public class TagComparisonResults {

        /**
         * the {@link TagComparisonResult result} of comparison between page tags
         */
	private TagComparisonResult pageTagResult;

        /**
         * the {@link TagComparisonResult result} of comparison between block tags
         */
	private TagComparisonResult blockTagResult;

        /**
         * an array of {@link TagComparisonResult results} of comparison
         * between channel tags (one entry for each channel)
         */
	private TagComparisonResult[] channelTagResults;

        /**
         * the length of the signal
         */
	private float totalSignalTime;

        /**
         * the lengths of channels
         */
	private float[] totalChannelTimes;

        /**
         * the names of channels
         */
	private String[] channelNames;

        /**
         * Constructor. Creates a new object with given
         * {@link TagComparisonResult results} of comparison of {@link Tag tags}
         * of different types.
         * @param pageTagResult the result of comparison between page tags
         * @param blockTagResult the result of comparison between block tags
         * @param channelTagResults an array with results of comparison
         * between channel tags (one channel - one result)
         */
	public TagComparisonResults(TagComparisonResult pageTagResult, TagComparisonResult blockTagResult, TagComparisonResult[] channelTagResults) {

		this.pageTagResult = pageTagResult;
		this.blockTagResult = blockTagResult;
		this.channelTagResults = channelTagResults;
		this.totalChannelTimes = new float[channelTagResults.length];
		this.channelNames = new String[channelTagResults.length];

	}

        /**
         * Returns the number of channels.
         * @return the number of channels
         */
	public int getChannelCount() {
		return channelTagResults.length;
	}

        /**
         * Returns the {@link TagComparisonResult result} of comparison between
         * tags for channel of a given index.
         * @param index the index of the channel
         * @return the result of comparison between tags for channel of a given
         * index
         */
	public TagComparisonResult getChannelResult(int index) {
		return channelTagResults[index];
	}

        /**
         * Returns the {@link TagComparisonResult result} of comparison between
         * page tags.
         * @return the result of comparison between page tags
         */
	public TagComparisonResult getPageTagResult() {
		return pageTagResult;
	}

        /**
         * Returns the {@link TagComparisonResult result} of comparison between
         * block tags.
         * @return the result of comparison between block tags
         */
	public TagComparisonResult getBlockTagResult() {
		return blockTagResult;
	}

        /**
         * Returns an array of {@link TagComparisonResult results} of comparison
         * between channel tags.
         * @return an array of result of comparison between channel tags
         */
	public TagComparisonResult[] getChannelTagResults() {
		return channelTagResults;
	}

        /**
         * Returns the length of the signal (in seconds).
         * @return the length of the signal (in seconds)
         */
	public float getTotalSignalTime() {
		return totalSignalTime;
	}

        /**
         * Sets the length of the signal to a given value.
         * @param totalSignalTime the length of the signal (in seconds)
         */
	public void setTotalSignalTime(float totalSignalTime) {
		this.totalSignalTime = totalSignalTime;
	}

        /**
         * Returns an array of lengths of channels.
         * @return an array of lengths of channels
         */
	public float[] getTotalChannelTimes() {
		return totalChannelTimes;
	}

        /**
         * Returns the length of a given channel.
         * @param index the index of a channel
         * @return the length of a given channel
         */
	public float getTotalChannelTime(int index) {
		return totalChannelTimes[index];
	}

        /**
         * Sets lengths of channels to given values.
         * @param totalChannelTimes an array of lengths of channels to be set
         */
	public void setTotalChannelTimes(float[] totalChannelTimes) {
		if (totalChannelTimes.length < channelTagResults.length) {
			throw new IndexOutOfBoundsException("Array too short");
		}
		for (int i=0; i<this.totalChannelTimes.length; i++) {
			this.totalChannelTimes[i] = totalChannelTimes[i];
		}
	}

        /**
         * Sets channels names to given values.
         * @param channelNames an array of channels names to be set
         */
	public void setChannelNames(String[] channelNames) {
		if (channelNames.length < channelTagResults.length) {
			throw new IndexOutOfBoundsException("Array too short");
		}
		for (int i=0; i<this.channelNames.length; i++) {
			this.channelNames[i] = channelNames[i];
		}
	}

	/**
	 * Returns an array of channels names.
	 * @return an array of channels names
	 */
	public String[] getChannelNames() {
		return channelNames;
	}

        /**
         * Sets the attributes of this TagComparisonResults using given
         * parameters. (A {@link SvarogI18n} must be set before using
	 * this method).
         * @param source the {@link MultichannelSampleSource source} of samples
         * @param montage the {@link SourceMontage montage} of source channels
         */
	public void getParametersFromSampleSource(MultichannelSampleSource source, SourceMontage montage) {

		int channelCount = source.getChannelCount();
		if (channelCount < channelTagResults.length - 1) { // -1 for SignalSelection.CHANNEL_NULL
			throw new IllegalArgumentException("Source not compatible - not enough channels");
		}
		if (channelCount > channelTagResults.length) {
			channelCount = channelTagResults.length;
		}

		float samplingFrequency = source.getSamplingFrequency();

		totalSignalTime = 0F;
		for (int i=0; i<channelCount; i++) {
			totalChannelTimes[i] = source.getSampleCount(i) / samplingFrequency;
			if (totalChannelTimes[i] > totalSignalTime) {
				totalSignalTime = totalChannelTimes[i];
			}
			channelNames[i] = montage.getSourceChannelLabelAt(i);
		}

		channelNames[channelCount] = _("MULTICHANNEL TAGS");
	}

}
