package org.signalml.plugin.newstager.data.book;

import org.signalml.plugin.newstager.data.NewStagerBookAtom;
import org.signalml.plugin.newstager.data.NewStagerBookInfo;

public class NewStagerBookSample {
	public final int offset;
	public final NewStagerAdaptedAtom[] atoms;

	public NewStagerBookSample(int offset, NewStagerBookAtom atoms[],
			NewStagerBookInfo bookInfo) {
		this.offset = offset;

		this.atoms = this.rescaleWith(atoms, bookInfo);
	}

	private NewStagerAdaptedAtom[] rescaleWith(NewStagerBookAtom atoms[],
			NewStagerBookInfo bookInfo) {
		double pointsPerMicrovolt = bookInfo.pointsPerMicrovolt;
		double samplingFrequency = bookInfo.samplingFrequency;

		NewStagerAdaptedAtom result[] = new NewStagerAdaptedAtom[atoms.length];

		for (int i = 0; i < atoms.length; ++i) {
			NewStagerBookAtom atom = atoms[i];
			double amplitude = 2.0d * (double) atom.amplitude
					/ pointsPerMicrovolt;
			double frequency = 0.5d * (double) atom.frequency * samplingFrequency;
			double scale = (double) atom.scale / samplingFrequency;
			double position = (double) atom.position / samplingFrequency;

			result[i] = new NewStagerAdaptedAtom(amplitude, frequency, scale,
					position, atom.phase);
		}

		return result;
	}
}
