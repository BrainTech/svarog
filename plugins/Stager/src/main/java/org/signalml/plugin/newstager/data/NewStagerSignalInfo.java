package org.signalml.plugin.newstager.data;

public class NewStagerSignalInfo {
	public final float samplingFrequency;
	public final float pointsPerMicrovolt;
	public final int numberOfChannels;
	
	public NewStagerSignalInfo(float samplingFreq, float pointsPerMicrovolt, int numberOfChannels) {
		this.samplingFrequency = samplingFreq;
		this.pointsPerMicrovolt = pointsPerMicrovolt;
		this.numberOfChannels = numberOfChannels;
	}
	
}
