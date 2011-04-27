package org.signalml.plugin.newartifact.data;

public class NewArtifactConstants {
	public static float DEFAULT_FREQUENCY = 128.0f;

	public final int channelCount;
	public final float frequency;
	public final float powerGridFrequency;
	public final int blockLengthInSeconds;
	public final int smallBlockLengthInSeconds;
	public final int tailLengthInSeconds;
	public final float smallTailLengthInSeconds;
	public final float slopeLengthInSeconds;

	private static final double FW1 = 0.0;
	private static final double FW2 = 0.625;

	private static final double FM1 = 38;

	public NewArtifactConstants(int channelCount,
				    float frequency, float powerGridFrequency,
				    int blockLengthInSeconds, int smallBlockLengthInSeconds,
				    int tailLengthInSeconds, float smallTailLengthInSeconds,
				    float slopeLengthInSeconds) {
		this.channelCount = channelCount;
		this.frequency = frequency;
		this.powerGridFrequency = powerGridFrequency;
		this.blockLengthInSeconds = blockLengthInSeconds;
		this.smallBlockLengthInSeconds = smallBlockLengthInSeconds;
		this.tailLengthInSeconds = tailLengthInSeconds;
		this.smallTailLengthInSeconds = smallTailLengthInSeconds;
		this.slopeLengthInSeconds = slopeLengthInSeconds;
	}

	public int getBlockLength() {
		return (int)(this.frequency * this.blockLengthInSeconds);
	}

	public int getBlockLengthWithPadding() {
		return (int)(this.frequency * (this.blockLengthInSeconds + 2 * this.tailLengthInSeconds));
	}

	public int getSlopeLength() {
		return (int)(this.frequency * this.slopeLengthInSeconds);
	}

	public int getBlockWithSlopesLength() {
		return this.getBlockLength() + 2 * this.getSlopeLength();
	}

	public int getSmallBlockLength() {
		return (int)(this.getBlockLength() / this.getBlockCapacity());
	}

	public int getSmallBlockLengthWithPadding() {
		return this.getSmallBlockLength() + 2 * this.getSmallPaddingLength();
	}

	public int getBlockCapacity() {
		return this.blockLengthInSeconds / this.smallBlockLengthInSeconds;
	}

	public int getPaddingLength() {
		return (int)(this.frequency * this.tailLengthInSeconds);
	}

	public int getSmallPaddingLength() {
		return (int)(this.frequency * this.smallTailLengthInSeconds);
	}

	public int getBlockPowerVectorLength() {
		return this.getBlockLengthWithPadding() / 2 + 1;
	}

	public int getSmallBlockPowerVectorLength() {
		return this.getSmallBlockLength() / 2 + 1;
	}

	public double getFw1() {
		return NewArtifactConstants.FW1;
	}

	public double getFw2() {
		return NewArtifactConstants.FW2;
	}

	public int getFs1() {
		return (int) Math.floor(Math.min(this.powerGridFrequency - 2, this.frequency / 2));
	}

	public int getFs2() {
		return (int) Math.floor(Math.min(this.powerGridFrequency + 2, this.frequency / 2));
	}

	public int getFm1() {
		return (int) NewArtifactConstants.FM1;
	}

	public int getFm2() {
		return (int) this.frequency / 2;
	}

	public double getFreqChangeCoefficient() {
		return this.getBlockLengthWithPadding() / this.frequency;
	}

	public double getFreqChangeCoefficientForSmallBlock() {
		return this.getSmallBlockLength() / this.frequency;
	}
}
