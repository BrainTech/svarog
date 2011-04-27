/* Chebyshev1IIRDesignerTest.java created 2010-09-12
 *
 */

package org.signalml.domain.montage.filter.iirdesigner;

import org.apache.commons.math.complex.Complex;

import static org.signalml.domain.montage.filter.iirdesigner.IIRDesignerAssert.*;
import org.junit.Test;
import java.lang.Math.*;

/**
 * This class perfoms unit test on {@link Chebyshev1IIRDesigner} class.
 * @author Piotr Szachewicz
 */
public class Chebyshev1IIRDesignerTest {

	/**
	 * this Chebyshev1IIRDesigner is needed to call all the tested methods.
	 */
	Chebyshev1IIRDesigner iirdesigner = new Chebyshev1IIRDesigner();

	/**
	 * Test method for {@link Chebyshev1IIRDesigner#calculatePrototype(int, double) }.
	 */
	@Test
	public void testCalculateChebyshev1Prototype() {

		FilterZerosPolesGain zpk1 = iirdesigner.calculatePrototype(3, 3);
		Complex[] zeros = new Complex[0];
		Complex[] poles = new Complex[] {new Complex(-0.14931010, 9.03814429e-01), new Complex(-0.29862021, 6.39042136e-17),
		                                 new Complex(-0.14931010, -9.03814429e-01)
		                                };
		FilterZerosPolesGain zpk2 = new FilterZerosPolesGain(zeros, poles, 0.25059432325190018);

		assertEquals(zpk1, zpk2);

	}

	/**
	 * Test method for {@link Chebyshev1IIRDesigner#designDigitalFilter(org.signalml.domain.montage.filter.iirdesigner.FilterType, double[], double[], double, double) }.
	 */
	@Test
	public void testDesignDigitalFilter() throws BadFilterParametersException {

		double[] pythonB;
		double[] pythonA;
		FilterCoefficients coeffs;

		//lowpass test
		pythonB = new double[] {7.02019760e-05, 2.80807904e-04, 4.21211856e-04,
		                    2.80807904e-04, 7.02019760e-05
		                   };
		pythonA = new double[] {1.0, -3.72214229, 5.29043743,
		                    -3.39981985, 0.83311132
		                   };
		coeffs = iirdesigner.designDigitalFilter(FilterType.LOWPASS, new double[] {0.1}, new double[] {0.2}, 3.0, 40.0);
		assertEquals(new FilterCoefficients(pythonB, pythonA), coeffs, 1e-8);

		//highpass
		pythonB = new double[] {0.25733687, -0.25733687};
		pythonA = new double[] {1.0, 0.48532626};
		coeffs = iirdesigner.designDigitalFilter(FilterType.HIGHPASS, new double[] {0.7}, new double[] {0.2}, 5.0, 10.0);
		assertEquals(new FilterCoefficients(pythonB, pythonA), coeffs, 1e-4);

		//bandpass test
		pythonB = new double[] {0.18097462, 0.0, -0.18097462};
		pythonA = new double[] {1.00000000e+00, -1.81860167e-16, 6.38050764e-01};
		coeffs = iirdesigner.designDigitalFilter(FilterType.BANDPASS, new double[] {0.4, 0.6}, new double[] {0.2, 0.8}, 5.0, 10.0);
		assertEquals(new FilterCoefficients(pythonB, pythonA), coeffs, 1e-4);

		//bandstop test
		pythonB = new double[] {3.30701236e-01,  -1.60021832e-07,   3.30701236e-01};
		pythonA = new double[] {1.00000000e+00,  -1.60021832e-07,  -3.38597528e-01};
		coeffs = iirdesigner.designDigitalFilter(FilterType.BANDSTOP, new double[] {0.2, 0.8}, new double[] {0.4, 0.6}, 5.0, 10.0);
		assertEquals(new FilterCoefficients(pythonB, pythonA), coeffs, 1e-4);

	}

	/**
	 * Test method for {@link Chebyshev1IIRDesigner#calculateNaturalFrequency(org.signalml.domain.montage.filter.iirdesigner.FilterType, int, double[], double[], double, double, boolean) }.
	 */
	@Test
	public void testCalculateNaturalFrequency() {

		double[] wn = iirdesigner.calculateNaturalFrequency(FilterType.BANDPASS, 10, new double[] {0.6, 0.7}, new double[] {0.55, 0.8}, 0.5, 100, true);
		assertEquals(new double[] {0.6, 0.7}, wn, 1e-16);

		wn = iirdesigner.calculateNaturalFrequency(FilterType.BANDPASS, 10, new double[] {0.6, 0.7}, new double[] {0.55, 0.8}, 0.5, 100, false);
		assertEquals(new double[] {0.6, 0.7}, wn, 1e-16);

	}

	/**
	 * Test method for {@link Chebyshev1IIRDesigner#calculateFilterOrder(org.signalml.domain.montage.filter.iirdesigner.FilterType, double[], double[], double, double, boolean) }.
	 */
	@Test
	public void testCalculateFilterOrder() throws BadFilterParametersException {

		int filterOrder;

		//digital lowpass
		filterOrder = iirdesigner.calculateFilterOrder(FilterType.LOWPASS, 0.4, 0.7, 3, 40, false);
		assertEquals(4, filterOrder);

		//digital highpass
		filterOrder = iirdesigner.calculateFilterOrder(FilterType.HIGHPASS, 0.7, 0.3, 1, 80, false);
		assertEquals(6, filterOrder);

		//digital bandstop
		filterOrder = iirdesigner.calculateFilterOrder(FilterType.BANDSTOP, new double[] {0.5, 0.8}, new double[] {0.6, 0.7}, 0.5, 100, false);
		assertEquals(8, filterOrder);

		//analog bandpass
		filterOrder = iirdesigner.calculateFilterOrder(FilterType.BANDPASS, new double[] {0.6, 0.7}, new double[] {0.55, 0.8}, 0.5, 100, true);
		assertEquals(10, filterOrder);

	}

}