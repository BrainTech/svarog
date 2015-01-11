package org.signalml.plugin.bookreporter.data.book;

import org.signalml.domain.book.StandardBookAtom;

/**
 * @author piotr@develancer.pl
 */
public class BookReporterAtom {

	public double amplitude;
	public double frequency;
	public double scale;
	public double position;
	public double phase;

	public BookReporterAtom(double amplitude, double frequency, double scale, double position, double phase) {
		this.amplitude = amplitude;
		this.frequency = frequency;
		this.scale = scale;
		this.position = position;
		this.phase = phase;
	}

	public static BookReporterAtom createFromStandardBookAtom(StandardBookAtom atom, double pointsPerMicrovolt, double timeOffset) {
		return new BookReporterAtom(
			2.0 * atom.getAmplitude() / pointsPerMicrovolt,
			atom.getHzFrequency(),
			atom.getTimeScale(),
			atom.getTimePosition() + timeOffset,
			atom.getPhase()
		);
	}
}
