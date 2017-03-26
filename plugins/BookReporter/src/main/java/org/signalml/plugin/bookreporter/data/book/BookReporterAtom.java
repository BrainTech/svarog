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
	public int channel;  // starting from 1

	public BookReporterAtom(double amplitude, double frequency, double scale, double position, double phase, int channel) {
		this.amplitude = amplitude;
		this.frequency = frequency;
		this.scale = scale;
		this.position = position;
		this.phase = phase;
		this.channel = channel;
	}

	public static BookReporterAtom createFromStandardBookAtom(StandardBookAtom atom, double pointsPerMicrovolt, double timeOffset, int channel) {
		return new BookReporterAtom(
			2.0 * atom.getAmplitude() / pointsPerMicrovolt,
			atom.getHzFrequency(),
			atom.getTimeScale(),
			atom.getTimePosition() + timeOffset,
			atom.getPhase(),
			channel
		);
	}
}
