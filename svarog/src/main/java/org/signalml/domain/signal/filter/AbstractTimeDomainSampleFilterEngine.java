package org.signalml.domain.signal.filter;

import java.util.logging.Level;

import org.apache.log4j.Logger;
import org.signalml.domain.montage.filter.TimeDomainSampleFilter;
import org.signalml.domain.signal.samplesource.RoundBufferSampleSource;
import org.signalml.domain.signal.samplesource.SampleSource;
import org.signalml.math.iirdesigner.BadFilterParametersException;
import org.signalml.math.iirdesigner.FilterCoefficients;
import org.signalml.math.iirdesigner.IIRDesigner;

/**
 * This class represents a Time Domain (IIR or FIR) filter of samples.
 * Allows to return the filtered samples based on the given source.
 * There are two subclasses for this engine: {@link OfflineTimeDomainSampleFilterEngine}
 * for filtering offline signals and {@link OnlineTimeDomainSampleFilterEngine}
 * for filtering online signals.
 *
 * @author Piotr Szachewicz
 */
public abstract class AbstractTimeDomainSampleFilterEngine extends SampleFilterEngine {

	protected static final Logger logger = Logger.getLogger(AbstractTimeDomainSampleFilterEngine.class);

	/**
	 * Round buffer used to store filtered samples of the signal.
	 */
	protected RoundBufferSampleSource filtered = null;

	/**
	 * a Coefficients of the Time Domain filter (feedback filter coefficients).
	 */
	protected double aCoefficients[];

	/**
	 * b Coefficients of the Time Domain filter (feedforward filter coefficients).
	 */
	protected double bCoefficients[];

	/**
	 * the order of the {@link TimeDomainSampleFilter filter}.
	 */
	protected int filterOrder;

	/**
	 * Constructor. Creates an engine of a filter for provided
	 * {@link SampleSource source} of samples.
	 * @param source the source of samples
	 * @param definition the {@link TimeDomainSampleFilter definition} of the
	 * filter
	 */
	public AbstractTimeDomainSampleFilterEngine(SampleSource source, TimeDomainSampleFilter definition) {

		super(source);

		this.definition = definition;

		FilterCoefficients coeffs = null;
		try {
			coeffs = IIRDesigner.designDigitalFilter(definition);
			aCoefficients = coeffs.getACoefficients();
			bCoefficients = coeffs.getBCoefficients();
			filterOrder = coeffs.getFilterOrder();
			filtered = null;

		} catch (BadFilterParametersException ex) {
			java.util.logging.Logger.getLogger(AbstractTimeDomainSampleFilterEngine.class.getName()).log(Level.SEVERE, null, ex);
		}

	}

	/**
	 * Constructor. Creates an engine of a filter for provided
	 * {@link FilterCoefficients filter coefficients}.
	 * @param source the source of samples
	 * @param definition the definition of the filter for which this engine will
	 * be created
	 * @param coefficients the {@link FilterCoefficients coefficients} for which
	 * the engine will operate
	 */
	public AbstractTimeDomainSampleFilterEngine(SampleSource source, TimeDomainSampleFilter definition, FilterCoefficients coefficients) {
		this(source, coefficients);
		this.definition = definition;
	}

	/**
	 * Constructor. Creates an engine of a filter for provided
	 * {@link FilterCoefficients filter coefficients}.
	 * @param source the source of samples
	 * @param coefficients the {@link FilterCoefficients coefficients} for which
	 * the engine will operate
	 */
	public AbstractTimeDomainSampleFilterEngine(SampleSource source, FilterCoefficients coefficients) {
		super(source);
		aCoefficients = coefficients.getACoefficients();
		bCoefficients = coefficients.getBCoefficients();
		filterOrder = coefficients.getFilterOrder();
		filtered = null;
	}

	/**
	 * Filters the input data using the specified digital filter coefficients.
	 * @param bCoefficients feedforward coefficients of the digital filter
	 * @param aCoefficients feedback coefficients of the digital filter
	 * @param input the input data
	 * @return filtered input data
	 */
	public static double[] filter(double[] bCoefficients, double[] aCoefficients, double[] input) {
		int size = Math.max(bCoefficients.length, aCoefficients.length) - 1;
		double[] initialConditions = new double[size];

		for (int i = 0; i < initialConditions.length; i++) {
			initialConditions[i] = 0;
		}
		return filter(bCoefficients, aCoefficients, input, initialConditions);
	}

	/**
	 * Filters the given data using the specified digital filter assuming
	 * the given initial conditions of the filter delays.
	 * @param bCoefficients feedforward coefficients of the filter
	 * @param aCoefficients feedback coeffcients of the filter
	 * @param input the input signal to be filtered
	 * @param initialConditions initial conditions of the filter delays
	 * @return the input signal after filtering
	 */
	public static double[] filter(double[] bCoefficients, double[] aCoefficients, double[] input, double[] initialConditions) {
		/**
		 * The filter function is implemented as a direct II transposed structure.
		 * It is implemented as the lfilter function in the Scipy library.
		 * Compare with Scipy source code: scipy/signal/lfilter.c#@NAME@_filt
		 */

		int bi, ai, zi;
		double[] filtered = new double[input.length];

		for (int n = 0; n < input.length; n++) {
			bi = 0;
			ai = 0;
			zi = 0;

			if (bCoefficients.length > 1) {
				filtered[n] = initialConditions[zi] + bCoefficients[bi] / aCoefficients[0] * input[n];
				bi++;
				ai++;

				for (; zi < bCoefficients.length - 2; zi++) {
					initialConditions[zi] = initialConditions[zi + 1]
											+ input[n] * bCoefficients[bi] / aCoefficients[0]
											- filtered[n] * aCoefficients[ai] / aCoefficients[0];

					bi++;
					ai++;
				}
				initialConditions[zi] = input[n] * bCoefficients[bi] / aCoefficients[0]
										- filtered[n] * aCoefficients[ai] / aCoefficients[0];
			} else {
				filtered[n] = input[n] * bCoefficients[bi] / aCoefficients[0];
			}
		}

		return filtered;
	}

	/**
	 * Return the {@link TimeDomainSampleFilter definition} of the filter.
	 * @return the {@link TimeDomainSampleFilter definition} of the filter
	 */
	@Override
	public TimeDomainSampleFilter getFilterDefinition() {
		return (TimeDomainSampleFilter) definition.duplicate();
	}

}
