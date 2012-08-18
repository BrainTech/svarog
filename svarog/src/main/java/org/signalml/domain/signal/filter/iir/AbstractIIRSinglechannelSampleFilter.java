package org.signalml.domain.signal.filter.iir;

import java.util.logging.Level;

import org.apache.log4j.Logger;
import org.signalml.domain.montage.filter.TimeDomainSampleFilter;
import org.signalml.domain.signal.filter.SinglechannelSampleFilterEngine;
import org.signalml.domain.signal.samplesource.RoundBufferSampleSource;
import org.signalml.domain.signal.samplesource.SampleSource;
import org.signalml.math.iirdesigner.BadFilterParametersException;
import org.signalml.math.iirdesigner.FilterCoefficients;
import org.signalml.math.iirdesigner.IIRDesigner;

/**
 * This class represents a Time Domain (IIR or FIR) filter of samples.
 * Allows to return the filtered samples based on the given source.
 * There are two subclasses for this engine: {@link OfflineIIRSinglechannelSampleFilter}
 * for filtering offline signals and {@link OnlineIIRSinglechannelSampleFilter}
 * for filtering online signals.
 *
 * @author Piotr Szachewicz
 */
public abstract class AbstractIIRSinglechannelSampleFilter extends SinglechannelSampleFilterEngine {

	protected static final Logger logger = Logger.getLogger(AbstractIIRSinglechannelSampleFilter.class);

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
	public AbstractIIRSinglechannelSampleFilter(SampleSource source, TimeDomainSampleFilter definition) {

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
			java.util.logging.Logger.getLogger(AbstractIIRSinglechannelSampleFilter.class.getName()).log(Level.SEVERE, null, ex);
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
	public AbstractIIRSinglechannelSampleFilter(SampleSource source, TimeDomainSampleFilter definition, FilterCoefficients coefficients) {
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
	public AbstractIIRSinglechannelSampleFilter(SampleSource source, FilterCoefficients coefficients) {
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
		IIRFilterEngine iirFilter = new IIRFilterEngine(bCoefficients, aCoefficients);
		return iirFilter.filter(input);
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
		IIRFilterEngine iirFilter = new IIRFilterEngine(bCoefficients, aCoefficients, initialConditions);
		return iirFilter.filter(input);
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
