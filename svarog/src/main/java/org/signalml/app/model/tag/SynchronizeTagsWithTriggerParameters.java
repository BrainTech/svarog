package org.signalml.app.model.tag;

import java.util.List;

import org.signalml.domain.signal.samplesource.MultichannelSampleSource;
import org.signalml.domain.tag.StyledTagSet;

/**
 * The parameters for synchronizing tags with a trigger channel.
 * @author Piotr Szachewicz
 */
public class SynchronizeTagsWithTriggerParameters {

	private MultichannelSampleSource sampleSource;
	private transient List<String> channelLabels;
	private StyledTagSet tagSet;

	private double thresholdValue;
	private int triggerChannel;
	private SlopeType slopeType;

	public MultichannelSampleSource getSampleSource() {
		return sampleSource;
	}
	public void setSampleSource(MultichannelSampleSource sampleSource) {
		this.sampleSource = sampleSource;
	}
	public double getThresholdValue() {
		return thresholdValue;
	}
	public void setThresholdValue(double thresholdValue) {
		this.thresholdValue = thresholdValue;
	}
	public int getTriggerChannel() {
		return triggerChannel;
	}
	public void setTriggerChannel(int triggerChannel) {
		this.triggerChannel = triggerChannel;
	}
	public void setSlopeType(SlopeType slopeType) {
		this.slopeType = slopeType;
	}
	public SlopeType getSlopeType() {
		return slopeType;
	}
	public StyledTagSet getTagSet() {
		return tagSet;
	}
	public void setTagSet(StyledTagSet tagSet) {
		this.tagSet = tagSet;
	}
	public List<String> getChannelLabels() {
		return channelLabels;
	}
	public void setChannelLabels(List<String> channelLabels) {
		this.channelLabels = channelLabels;
	}

}
