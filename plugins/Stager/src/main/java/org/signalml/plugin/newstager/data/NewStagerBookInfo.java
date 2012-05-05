package org.signalml.plugin.newstager.data;

public class NewStagerBookInfo {

	public final int segmentCount;
	public final int offsetDimension;
	public final float samplingFrequency;
	public final float pointsPerMicrovolt;

	public NewStagerBookInfo(int segmentCount, int offsetDimension,
			float samplingFrequency, float pointsPerMicrovolt) {
		this.segmentCount = segmentCount;
		this.offsetDimension = offsetDimension;
		this.samplingFrequency = samplingFrequency;
		this.pointsPerMicrovolt = pointsPerMicrovolt;
	}

}
