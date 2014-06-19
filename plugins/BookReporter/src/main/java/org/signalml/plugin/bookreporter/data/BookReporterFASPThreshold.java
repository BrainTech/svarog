package org.signalml.plugin.bookreporter.data;

import java.io.Serializable;
import java.util.HashMap;

/**
 * @author piotr@develancer.pl
 * (based on NewStagerFASPThreshold)
 */
public class BookReporterFASPThreshold implements Serializable {
	
	private static class Predefined extends HashMap<String, BookReporterFASPThreshold> {
		public Predefined() {
			super();
			put("alpha waves", new BookReporterFASPThreshold(
				8.0,  12.0,  // f
				5.0,  null,  // a
				1.5,  null,  // s
				null, null   // p
			));
			put("beta waves", new BookReporterFASPThreshold(
				15.0, 25.0,  // f
				5.0,  null,  // a
				0.5,  null,  // s
				null, null   // p
			));
			put("delta waves", new BookReporterFASPThreshold(
				0.2,  4.0,   // f
				65.0, null,  // a
				0.5,  6.0,   // s
				null, null   // p
			));
			put("theta waves", new BookReporterFASPThreshold(
				4.0,  8.0,   // f
				15.0, null,  // a
				1.0,  null,  // s
				null, null   // p
			));
			put("sleep spindles", new BookReporterFASPThreshold(
				11.0, 15.0,  // f
				12.0, null,  // a
				0.4,  2.5,   // s
				null, null   // p
			));
			put("K-complexes", new BookReporterFASPThreshold(
				0.03, 25.0,  // f
				100.0, null, // a
				0.3, 1.5,    // s
				-0.5, +0.5   // p
			));
			put("SWA", new BookReporterFASPThreshold(
				0.2,  4.0,   // f
				70.0, null,  // a
				0.5,  null,  // s
				null, null   // p
			));
		}
	}

	public static BookReporterFASPThreshold getPredefinedThreshold(String name) {
		return predefined.get(name);
	}

	public static String[] getPredefinedThresholdNames() {
		return predefined.keySet().toArray(new String[predefined.size()]);
	}

	private static final Predefined predefined = new Predefined();
	
	public static final BookReporterFASPThreshold UNLIMITED = new BookReporterFASPThreshold();

	public final BookReporterMinMaxRange frequency;
	public final BookReporterMinMaxRange amplitude;
	public final BookReporterMinMaxRange scale;
	public final BookReporterMinMaxRange phase;
	
	public BookReporterFASPThreshold() {
		this(
			BookReporterMinMaxRange.UNLIMITED,
			BookReporterMinMaxRange.UNLIMITED,
			BookReporterMinMaxRange.UNLIMITED,
			BookReporterMinMaxRange.UNLIMITED
		);
	}

	public BookReporterFASPThreshold(BookReporterMinMaxRange frequency, BookReporterMinMaxRange amplitude, BookReporterMinMaxRange scale, BookReporterMinMaxRange phase) {
		this.frequency = (frequency != null) ? frequency : BookReporterMinMaxRange.UNLIMITED;
		this.amplitude = (amplitude != null) ? amplitude : BookReporterMinMaxRange.UNLIMITED;
		this.scale = (scale != null) ? scale : BookReporterMinMaxRange.UNLIMITED;
		this.phase = (phase != null) ? phase : BookReporterMinMaxRange.UNLIMITED;
	}

	public BookReporterFASPThreshold(
		Double frequencyMin, Double frequencyMax,
		Double amplitudeMin, Double amplitudeMax,
		Double scaleMin, Double scaleMax,
		Double phaseMin, Double phaseMax
	) {
		this(
			new BookReporterMinMaxRange(frequencyMin, frequencyMax),
			new BookReporterMinMaxRange(amplitudeMin, amplitudeMax),
			new BookReporterMinMaxRange(scaleMin, scaleMax),
			new BookReporterMinMaxRange(phaseMin, phaseMax)
		);
	}

}
