package org.signalml.app.method.ep.helpers.minmax;

import java.util.ArrayList;
import java.util.List;

import org.signalml.app.method.ep.model.minmax.ChannelStatistics;
import org.signalml.method.ep.EvokedPotentialResult;

/**
 * Calculates the min/max values and times for a given {@link EvokedPotentialResult}
 * for a given tag group number.
 * @author Piotr Szachewicz
 */
public class ChannelStatisticsCalculator {

	private EvokedPotentialResult result;
	private int tagGroupNumber;

	private List<ChannelStatistics> statistics;

	/**
	 * Constructor.
	 * @param result the result of averaging
	 * @param tagGroupNumber the number of tag group from the EvokedPotentialResult
	 * for which statistics will be calculated.
	 */
	public ChannelStatisticsCalculator(EvokedPotentialResult result, int tagGroupNumber) {
		this.tagGroupNumber = tagGroupNumber;
		this.result = result;

		calculateStatistics();
	}

	protected void calculateStatistics() {
		statistics = new ArrayList<ChannelStatistics>();
		double[][] samples = result.getAverageSamples().get(tagGroupNumber);

		for (int channelNumber = 0; channelNumber < result.getAverageSamples().get(tagGroupNumber).length; channelNumber++) {
			double[] channelSamples = samples[channelNumber];
			statistics.add(calculateChannelStatistics(channelNumber, channelSamples));
		}
	}

	protected ChannelStatistics calculateChannelStatistics(int channelNumber, double[] channelSamples) {

		double maxValue = Double.MIN_VALUE;
		double maxTime = 0.0;
		for (int i = 0; i < channelSamples.length; i++) {
			if (channelSamples[i] > maxValue) {
				maxValue = channelSamples[i];
				maxTime = sampleNumberToTime(i);
			}
		}

		double minValue = Double.MAX_VALUE;
		double minTime = 0.0;
		for (int i = 0; i < channelSamples.length; i++) {
			if (channelSamples[i] < minValue) {
				minValue = channelSamples[i];
				minTime = sampleNumberToTime(i);
			}
		}

		ChannelStatistics channelStatistics = new ChannelStatistics(result.getLabels()[channelNumber], minTime, minValue, maxTime, maxValue);
		return channelStatistics;
	}

	protected double sampleNumberToTime(int sampleNumber) {
		return ((double) sampleNumber) / result.getSamplingFrequency() + result.getStartTime();
	}

	public List<ChannelStatistics> getStatistics() {
		return statistics;
	}

}
