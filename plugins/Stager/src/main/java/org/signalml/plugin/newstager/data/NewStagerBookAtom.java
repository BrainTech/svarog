package org.signalml.plugin.newstager.data;

public class NewStagerBookAtom {

	public final float modulus;
	public final float amplitude;
	public final float position;
	public final float scale;
	public final float frequency;
	public final float phase;

	public NewStagerBookAtom(float modulus, float amplitude, float position,
							 float scale, float frequency, float phase) {
		this.modulus = modulus;
		this.amplitude = amplitude;
		this.position = position;
		this.scale = scale;
		this.frequency = frequency;
		this.phase = phase;
	}

	public static NewStagerBookAtom CreateGaborWave(float modulus,
			float amplitude, float position, float scale, float frequency,
			float phase) {
		return new NewStagerBookAtom(modulus, amplitude, position, scale,
									 frequency, phase);
	}

	public static NewStagerBookAtom CreateDiracDelta(float modulus,
			float amplitude, float position) {
		return new NewStagerBookAtom(modulus, amplitude, position, 0.0F, 0.0F,
									 0.0F);
	}

	public static NewStagerBookAtom CreateGaussFunction(float modulus,
			float amplitude, float position, float scale) {
		return new NewStagerBookAtom(modulus, amplitude, position, scale, 0.0F,
									 0.0F);
	}

	public static NewStagerBookAtom CreateSinCosWave(float modulus,
			float amplitude, float frequency, float phase) {
		return new NewStagerBookAtom(modulus, amplitude, 0.0F, 0.0F, frequency,
									 phase);
	}

}
