package org.signalml.plugin.newstager.data;

public class NewStagerConstants {

	public static final float DEFAULT_MUSCLE_THRESHOLD = 50.0f;
	public static final float DEFAULT_MUSCLE_THRESHOLD_RATE = 1.2f;

	public static final float DEFAULT_AMPLITUDE_A = 0.0499f;
	public static final float DEFAULT_AMPLITUDE_B = 28.181f;

	public static final float DEFAULT_ALPHA_OFFSET = 61f;
	public static final float DEFAULT_DELTA_OFFSET = 0f;
	public static final float DEFAULT_SPINDLE_OFFSET = 52f;

	public static final float MIN_AMPLITUDE = 0f;
	public static final float MAX_AMPLITUDE = 1000000f;
	public static final float INCR_AMPLITUDE = 1f;

	public static final float MIN_FREQUENCY = 0f;
	public static final float MAX_FREQUENCY = 4096f;
	public static final float INCR_FREQUENCY = 0.01f;

	public static final float MIN_SCALE = 0f;
	public static final float MAX_SCALE = 1000000f;
	public static final float INCR_SCALE = 0.1f;

	public static final float MIN_PHASE = -3.14f;
	public static final float MAX_PHASE = 3.14f;
	public static final float INCR_PHASE = 0.01f;

	public static final float MIN_EMG_TONE_THRESHOLD = 5f;
	public static final float MAX_EMG_TONE_THRESHOLD = 100f;

	public static final float MIN_MT_EEG_THRESHOLD = 10f;
	public static final float MAX_MT_EEG_THRESHOLD = 150f;

	public static final float MIN_MT_EMG_THRESHOLD = 100f;
	public static final float MAX_MT_EMG_THRESHOLD = 1000f;

	public static final float MIN_MT_TONE_EMG_THRESHOLD = 10f;
	public static final float MAX_MT_TONE_EMG_THRESHOLD = 150f;

	public static final float MIN_REM_EOG_DEFLECTION_THRESHOLD = 10f;
	public static final float MAX_REM_EOG_DEFLECTION_THRESHOLD = 1000f;

	public static final float MIN_SEM_EOG_DEFLECTION_THRESHOLD = 5f;
	public static final float MAX_SEM_EOG_DEFLECTION_THRESHOLD = 100f;


	public final float frequency;
	public final int blockLengthInSecondsINT; //offsetDimension
	public final int segmentCount;
	public final float muscleThreshold;
	public final float muscleThresholdRate;
	public final float amplitudeA;
	public final float amplitudeB;
	public final float alphaOffset;
	public final float deltaOffset;
	public final float spindleOffset;

	public NewStagerConstants(float frequency, int blockLengthInSecondsINT,
							  int segmentCount,
							  float muscleThreshold,
							  float muscleThresholdRate,
							  float amplitudeA,
							  float amplitudeB,
							  float alphaOffset,
							  float deltaOffset,
							  float spindleOffset) {
		this.frequency = frequency;
		this.blockLengthInSecondsINT = blockLengthInSecondsINT;
		this.segmentCount = segmentCount;
		this.muscleThreshold = muscleThreshold;
		this.muscleThresholdRate = muscleThresholdRate;
		this.amplitudeA = amplitudeA;
		this.amplitudeB = amplitudeB;
		this.alphaOffset = alphaOffset;
		this.deltaOffset = deltaOffset;
		this.spindleOffset = spindleOffset;
	}

	public int getBlockLengthInSamples() {
		return (int) (this.blockLengthInSecondsINT * this.frequency);
	}

}
