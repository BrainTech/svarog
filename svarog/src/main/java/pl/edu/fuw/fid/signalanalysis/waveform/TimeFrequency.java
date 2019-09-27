package pl.edu.fuw.fid.signalanalysis.waveform;

import org.apache.commons.math.complex.Complex;

/**
 * Time-frequency map unit. Consists of:<ul>
 * <li>time (s),</li>
 * <li>frequency (Hz),</li>
 * <li>time-frequency map value.</li>
 * </ul>
 *
 * @author ptr@mimuw.edu.pl
 */
public class TimeFrequency {

	public final double t;
	public final double f;
	public final Complex v;

	public TimeFrequency(double t, double f, Complex v) {
		this.t = t;
		this.f = f;
		this.v = v;
	}

}
