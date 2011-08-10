package org.signalml.plugin.newstager.data;

public class NewStagerConstants {

	public static final float DEFAULT_MUSCLE_THRESHOLD = 50.0f;
	public static final float DEFAULT_MUSCLE_THRESHOLD_RATE = 1.2f;

	public static final float DEFAULT_AMPLITUDE_A = 0.0499f;
	public static final float DEFAULT_AMPLITUDE_B = 28.181f;

	public static final float DEFAULT_ALPHA_OFFSET = 61f;
	public static final float DEFAULT_DELTA_OFFSET = 0f;
	public static final float DEFAULT_SPINDLE_OFFSET = 52f;

	public final float frequency;
	public final int blockLengthInSeconds; //offsetDimension
	public final float muscleThreshold;
	public final float muscleThresholdRate;
	public final float amplitudeA;
	public final float amplitudeB;
	public final float alphaOffset;
	public final float deltaOffset;
	public final float spindleOffset;

	public NewStagerConstants(float frequency, int blockLengthInSeconds,
				  float muscleThreshold,
				  float muscleThresholdRate,
				  float amplitudeA,
				  float amplitudeB,
				  float alphaOffset,
				  float deltaOffset,
				  float spindleOffset) {
		this.frequency = frequency;
		this.blockLengthInSeconds = blockLengthInSeconds;
		this.muscleThreshold = muscleThreshold;
		this.muscleThresholdRate = muscleThresholdRate;
		this.amplitudeA = amplitudeA;
		this.amplitudeB = amplitudeB;
		this.alphaOffset = alphaOffset;
		this.deltaOffset = deltaOffset;
		this.spindleOffset = spindleOffset;
	}

	public int getBlockLength() {
		return (int) this.getBlockLengthAsFloat();
	}

	public float getBlockLengthAsFloat() {
		return this.blockLengthInSeconds * frequency;
	}

}
