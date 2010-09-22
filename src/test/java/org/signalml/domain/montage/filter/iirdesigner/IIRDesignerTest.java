/* ButterworthIIRDesignerTest.java created 2010-09-12
 *
 */

package org.signalml.domain.montage.filter.iirdesigner;

import flanagan.complex.*;

import static org.signalml.domain.montage.filter.iirdesigner.IIRDesignerAssert.*;
import org.junit.Test;
import java.lang.Math.*;
import org.signalml.domain.montage.filter.iirdesigner.AbstractIIRDesigner.BandstopObjectiveFunction;

/**
 * This class performs unit tests on the {@link IIRDesigner} class.
 *
 * @author Piotr Szachewicz
 */
public class IIRDesignerTest {

		
	/**
	 * Test method for {@link IIRDesigner#designDigitalFilter(org.signalml.domain.montage.filter.iirdesigner.ApproximationFunctionType, org.signalml.domain.montage.filter.iirdesigner.FilterType, double[], double[], double, double, double) }.
	 */
	@Test
	public void testDesignDigitalFilter() throws BadFilterParametersException {

		double[] pyb;
		double[] pya;
		FilterCoefficients coeffs;

		//IIRDesigner.designDigitalFilter(ApproximationFunctionType.BUTTERWORTH, FilterType.LOWPASS, pya, pya, passbandRipple, stopbandAttenuation, samplingFrequency)
		coeffs = IIRDesigner.designDigitalFilter(ApproximationFunctionType.BUTTERWORTH, FilterType.HIGHPASS,
						       new double[] {6.0, 1.5}, new double[] {3.0,1.5}, 3.0, 40.0, 128.0);

		coeffs.print();

		//lowpass
		/*pyb = new double[] {1.79924818e-06, 1.25947373e-05, 3.77842118e-05,
		                    6.29736863e-05, 6.29736863e-05, 3.77842118e-05,
		                    1.25947373e-05, 1.79924818e-06
		                   };
		pya = new double[] {1.0, -5.50239773, 13.10636815,
		                    -17.49826739, 14.12933064, -6.89514162,
		                    1.88178747, -0.22144923
		                   };

		coeffs = iirdesigner.designDigitalFilter(FilterType.LOWPASS, new double[] {0.1}, new double[] {0.2}, 3.0, 40.0);
		assertEquals(new FilterCoefficients(pyb, pya), coeffs, 1e-8);*/

	}

}