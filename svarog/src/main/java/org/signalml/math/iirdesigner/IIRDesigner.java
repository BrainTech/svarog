/* IIRDesigner.java created 2010-09-16
 *
 */

package org.signalml.math.iirdesigner;

import org.signalml.domain.montage.filter.TimeDomainSampleFilter;
import static org.signalml.app.util.i18n.SvarogI18n._;

/**
 * This class represents a designer capable of designing digital filters, i.e. calculating
 * the {@link FilterCoefficients} according to the given {@link TimeDomainSampleFilter
 * specification}.
 *
 * @author Piotr Szachewicz
 */
public class IIRDesigner {

	/**
	 * A variable which controls whether logging should be performed.
	 */
	protected static boolean enableLogger = true;

	/**
	 * Returs the {@link FilterCoefficients coefficients} of a digital filter
	 * which meets the given filter specification.
	 *
	 * @param approximationFunctionType the type of the approximation function which
	 * should be used to design the filter.
	 * @param type the type of the filter (lowpass, highpass, bandpass or bandstop)
	 * @param passbandEdgeFrequencies the passband edge frequencies
	 * @param stopbandEdgeFrequencies the stopband edge frequencies
	 * @param passbandRipple maximum ripple allowed for the filter to have in the passband [dB]
	 * @param stopbandAttenuation minimum attenuation required for the filter to have in the stopband [dB]
	 * @param quality relative bandwidth (df/f0) for -3 dB attenuation (for notch and peak filters only)
	 * @param samplingFrequency the sampling frequency at which the filter will operate
	 * @return the {@link FilterCoefficients} of the filter designed which meets given specification.
	 * @throws BadFilterParametersException thrown when the filter cannot
	 * design a filter for the given parameters.
	 */
	public static FilterCoefficients designDigitalFilter(ApproximationFunctionType approximationFunctionType, FilterType type, double[] passbandEdgeFrequencies, double[] stopbandEdgeFrequencies, double passbandRipple, double stopbandAttenuation, double quality, double samplingFrequency) throws BadFilterParametersException {

		if (type.isNotch() || type.isPeak()) {
			NotchIIRDesigner iirdesigner = new NotchIIRDesigner();
			return iirdesigner.designDigitalFilter(samplingFrequency, type, passbandEdgeFrequencies, stopbandEdgeFrequencies, quality);
		}

		if (approximationFunctionType.isButterworth()) {

			ButterworthIIRDesigner iirdesigner = new ButterworthIIRDesigner();
			iirdesigner.enableDebugger(enableLogger);
			return iirdesigner.designDigitalFilter(samplingFrequency, type, passbandEdgeFrequencies, stopbandEdgeFrequencies, passbandRipple, stopbandAttenuation);

		}
		else if (approximationFunctionType.isChebyshev1()) {

			Chebyshev1IIRDesigner iirdesigner = new Chebyshev1IIRDesigner();
			iirdesigner.enableDebugger(enableLogger);
			return iirdesigner.designDigitalFilter(samplingFrequency, type, passbandEdgeFrequencies, stopbandEdgeFrequencies, passbandRipple, stopbandAttenuation);

		}
		else if (approximationFunctionType.isChebyshev2()) {

			Chebyshev2IIRDesigner iirdesigner = new Chebyshev2IIRDesigner();
			iirdesigner.enableDebugger(enableLogger);
			return iirdesigner.designDigitalFilter(samplingFrequency, type, passbandEdgeFrequencies, stopbandEdgeFrequencies, passbandRipple, stopbandAttenuation);

		}
		else if (approximationFunctionType.isElliptic()) {

			EllipticIIRDesigner iirdesigner = new EllipticIIRDesigner();
			iirdesigner.enableDebugger(enableLogger);
			return iirdesigner.designDigitalFilter(samplingFrequency, type, passbandEdgeFrequencies, stopbandEdgeFrequencies, passbandRipple, stopbandAttenuation);

		}
		else
			throw new BadFilterParametersException(_("This approximation function type is not supported by the IIRFilterDesigner."));

	}

	/**
	 * Designs a digital filter according to the given {@link TimeDomainSampleFilter
	 * filter definition}.
	 * @param filterDefinition the definition containing parameters for which
	 * the filter should be designed.
	 * @return coefficients of the designed filter.
	 * @throws BadFilterParametersException thrown when the filter cannot
	 * design a filter for the given parameters.
	 */
	public static FilterCoefficients designDigitalFilter(TimeDomainSampleFilter filterDefinition) throws BadFilterParametersException {

		return IIRDesigner.designDigitalFilter(filterDefinition.getApproximationFunctionType(), filterDefinition.getFilterType(),
											   filterDefinition.getPassbandEdgeFrequencies(), filterDefinition.getStopbandEdgeFrequencies(),
											   filterDefinition.getPassbandRipple(), filterDefinition.getStopbandAttenuation(),
											   filterDefinition.getQualityParameter(), filterDefinition.getSamplingFrequency());

	}

}