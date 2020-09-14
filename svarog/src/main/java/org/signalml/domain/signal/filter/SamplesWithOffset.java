package org.signalml.domain.signal.filter;

/**
 * Simple data structure consisting of an array of samples and a scalar offset value.
 */
public class SamplesWithOffset {

	public final double[] samples;
	public final long offset;

	public SamplesWithOffset(double[] samples, long offset) {
		this.samples = samples;
		this.offset = offset;
	}
}
