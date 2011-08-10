package org.signalml.plugin.newstager.data.book;

import org.signalml.domain.book.StandardBookAtom;

public class NewStagerAdaptedAtom {

	public double amplitude;
	public double frequency;
	public double scale;
	public double position;
	public double phase;

	public NewStagerAdaptedAtom(double amplitude, double frequency, double scale, double position, double phase) {
		this.amplitude = amplitude;
		this.frequency = frequency;
		this.scale = scale;
		this.position = position;
		this.phase = phase;
	}

	public static NewStagerAdaptedAtom FromStandardBookAtom(StandardBookAtom atom) {
		return new NewStagerAdaptedAtom(atom.getAmplitude(),
						atom.getFrequency(), //TODO ?
						atom.getScale(),
						atom.getPosition(),
						atom.getPhase());
	}
}
