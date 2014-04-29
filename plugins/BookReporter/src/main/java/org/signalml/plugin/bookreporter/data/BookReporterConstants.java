package org.signalml.plugin.bookreporter.data;

/**
 * @author piotr@develancer.pl
 * (based on NewStagerConstants)
 */
public class BookReporterConstants {

	public static final float MIN_AMPLITUDE = 0f;
	public static final float MAX_AMPLITUDE = 1000f;
	public static final float INCR_AMPLITUDE = 1f;

	public static final float MIN_FREQUENCY = 0f;
	public static final float MAX_FREQUENCY = 4096f;
	public static final float INCR_FREQUENCY = 0.01f;

	public static final float MIN_SCALE = 0f;
	public static final float MAX_SCALE = 1000f;
	public static final float INCR_SCALE = 0.1f;

	public static final float MIN_PHASE = -3.14f;
	public static final float MAX_PHASE = 3.14f;
	public static final float INCR_PHASE = 0.01f;
	
	// all structures will occupy time interval x₀ ± z s where
	public static final float TIME_OCCUPATION_SCALE = 1.5f; // = z

}
