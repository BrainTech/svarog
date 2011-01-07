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
 * This class performs unit tests on the {@link ButterworthIIRDesigner} class.
 *
 * @author Piotr Szachewicz
 */
public class ButterworthIIRDesignerTest {

	/**
	 * an instance of ButterworthIIRDesigner needed to call the tested methods
	 */
	ButterworthIIRDesigner iirdesigner = new ButterworthIIRDesigner();

	/**
	 * Test method for {@link ButterworthIIRDesigner#designAnalogFilter(org.signalml.domain.montage.filter.iirdesigner.FilterType, double[], double[], double, double) }.
	 */
	@Test
	public void testDesignAnalogFilter() throws BadFilterParametersException {

		double[] pythonA = new double[] {1.00000000e+00, 1.79338716e+01, 1.60811875e+02,
		                             9.14184873e+02, 3.46464456e+03, 8.32444329e+03, 1.00005000e+04
		                            };
		double[] pythonB = new double[] {10000.5000375};

		FilterCoefficients coeffs = iirdesigner.designFilter(FilterType.LOWPASS, new double[] {4.0, 0.0}, new double[] {10.0, 0.0}, 3.0, 40.0, true);
		FilterCoefficients pyCoeffs = new FilterCoefficients(pythonB, pythonA);

		assertEquals(pyCoeffs, coeffs, 1e-4);

	}

	/**
	 * Test method for {@link ButterworthIIRDesigner#designDigitalFilter(org.signalml.domain.montage.filter.iirdesigner.FilterType, double[], double[], double, double) }.
	 */
	@Test
	public void testDesignDigitalFilter() throws BadFilterParametersException {

		double[] pythonB;
		double[] pythonA;
		FilterCoefficients coeffs;

		//lowpass
		pythonB = new double[] {1.79924818e-06, 1.25947373e-05, 3.77842118e-05,
		                    6.29736863e-05, 6.29736863e-05, 3.77842118e-05,
		                    1.25947373e-05, 1.79924818e-06
		                   };
		pythonA = new double[] {1.0, -5.50239773, 13.10636815,
		                    -17.49826739, 14.12933064, -6.89514162,
		                    1.88178747, -0.22144923
		                   };

		coeffs = iirdesigner.designDigitalFilter(FilterType.LOWPASS, new double[] {0.1, 0.0}, new double[] {0.2, 0.0}, 3.0, 40.0);
		assertEquals(new FilterCoefficients(pythonB, pythonA), coeffs, 1e-8);

		//highpass
		pythonB = new double[] {0.0030623, -0.02449839, 0.08574436,
		                    -0.17148872, 0.2143609, -0.17148872,
		                    0.08574436, -0.02449839, 0.0030623
		                   };
		pythonA = new double[] {1.00000000e+00, 1.28289711e+00, 1.72596336e+00,
		                    1.13734282e+00, 6.51093826e-01, 2.22784566e-01,
		                    5.74976483e-02, 8.15653354e-03, 5.74611669e-04
		                   };
		coeffs = iirdesigner.designDigitalFilter(FilterType.HIGHPASS, new double[] {0.6, 0.0}, new double[] {0.4, 0.0}, 3.0, 40.0);
		assertEquals(new FilterCoefficients(pythonB, pythonA), coeffs, 1e-8);

		//bandpass
		pythonB = new double[] {4.06633016e-04, 0.00000000e+00, -2.43979810e-03,
		                    -9.59124377e-19, 6.09949524e-03, -9.11168158e-18,
		                    -8.13266032e-03, 9.11168158e-18, 6.09949524e-03,
		                    9.59124377e-19, -2.43979810e-03, 0.00000000e+00,
		                    4.06633016e-04
		                   };
		pythonA = new double[] {1.00000000e+00, 2.99246806e-16, 3.49439866e+00,
		                    2.70089425e-15, 5.42502143e+00, 4.91071681e-16,
		                    4.68892114e+00, -2.94643009e-15, 2.35792769e+00,
		                    -3.68303761e-16, 6.50028902e-01, 1.61132895e-16,
		                    7.64240933e-02
		                   };
		coeffs = iirdesigner.designDigitalFilter(FilterType.BANDPASS, new double[] {0.4, 0.6}, new double[] {0.3, 0.7}, 3.0, 40.0);
		assertEquals(new FilterCoefficients(pythonB, pythonA), coeffs, 1e-8);

		//bandstop
		pythonB = new double[] {7.65853414e-02, 5.23150264e-07, 4.59512048e-01,
		                    2.61575132e-06, 1.14878012e+00, 5.23150264e-06,
		                    1.53170683e+00, 5.23150264e-06, 1.14878012e+00,
		                    2.61575132e-06, 4.59512048e-01, 5.23150264e-07,
		                    7.65853414e-02
		                   };
		pythonA = new double[] {1.00000000e+00, 4.16730153e-06, 1.32074661e+00,
		                    5.38773302e-06, 1.43047982e+00, 4.58932829e-06,
		                    7.80068865e-01, 2.02131353e-06, 3.02664538e-01,
		                    5.19781746e-07, 6.15555007e-02, 5.53503535e-08,
		                    5.94651712e-03
		                   };
		coeffs = iirdesigner.designDigitalFilter(FilterType.BANDSTOP, new double[] {0.3, 0.7}, new double[] {0.4, 0.6}, 3.0, 40.0);
		assertEquals(new FilterCoefficients(pythonB, pythonA), coeffs, 1e-5);

		//bandstop 2
		pythonB = new double[] {5.06390694e-01, -2.37380515e-07, 5.06390694e-01};
		pythonA = new double[] {1.00000000e+00, -2.37380515e-07, 1.27813882e-02};
		coeffs = iirdesigner.designDigitalFilter(FilterType.BANDSTOP, new double[] {0.2, 0.8}, new double[] {0.4, 0.6}, 5.0, 10.0);
		assertEquals(new FilterCoefficients(pythonB, pythonA), coeffs, 1e-6);

	}

	/**
	 * Test method for {@link ButterworthIIRDesigner#calculatePrototype(int) }.
	 */
	@Test
	public void testCalculatePrototype() {

		FilterZerosPolesGain zpk1 = iirdesigner.calculatePrototype(3);

		Complex[] poles = new Complex[] {new Complex(-0.5, 8.66025404e-1), new Complex(-1.0, 0.0), new Complex(-0.5, -8.66025404e-1)};
		FilterZerosPolesGain zpk2 = new FilterZerosPolesGain(new Complex[0], poles, 1.0);

		assertEquals(zpk1, zpk2);

	}

	/**
	 * Test method for {@link ButterworthIIRDesigner#calculateNaturalFrequency(org.signalml.domain.montage.filter.iirdesigner.FilterType, int, double[], double[], double, double, boolean) }.
	 */
	@Test
	public void testCalculateNaturalFrequency() {

		//lowpass
		double[] result = iirdesigner.calculateNaturalFrequency(FilterType.LOWPASS, 7, new double[] {10}, new double[] {20}, 5, 40, true);
		assertEquals(10.359023354921998, result[0], 1e-4);

		//digital lowpass
		result = iirdesigner.calculateNaturalFrequency(FilterType.LOWPASS, 7, new double[] {0.1}, new double[] {0.2}, 3.0, 40.0, false);
		assertEquals(0.10614373313957487, result[0], 1e-4);

		//analog highpass
		result = iirdesigner.calculateNaturalFrequency(FilterType.HIGHPASS, 4, new double[] {20}, new double[] {10}, 3, 20, true);
		assertEquals(17.760467745895387, result[0], 1e-4);

		//analog bandstop
		result = iirdesigner.calculateNaturalFrequency(FilterType.BANDSTOP, 3, new double[] {1, 4}, new double[] {2, 3}, 3, 20, true);
		assertEquals(1.59974742, result[0], 1e-4);
		assertEquals(3.75058475, result[1], 1e-4);

		//analog bandpass
		result = iirdesigner.calculateNaturalFrequency(FilterType.BANDPASS, 11, new double[] {2, 4}, new double[] {1, 5}, 5.0, 50.0, true);
		assertEquals(1.99511416, result[0], 1e-8);
		assertEquals(4.00979561, result[1], 1e-8);

		//digital bandpass
		result = iirdesigner.calculateNaturalFrequency(FilterType.BANDPASS, 9, new double[] {0.2, 0.4}, new double[] {0.1, 0.5}, 5.0, 50.0, false);
		assertEquals(0.19975973, result[0], 1e-8);
		assertEquals(0.40038904, result[1], 1e-8);

	}

	/**
	 * Test method for {@link ButterworthIIRDesigner#calculateFilterOrder(org.signalml.domain.montage.filter.iirdesigner.FilterType, double[], double[], double, double, boolean) }.
	 */
	@Test
	public void testCalculateFilterOrder() throws BadFilterParametersException {

		//lowpass
		int filterOrder = iirdesigner.calculateFilterOrder(FilterType.LOWPASS, new double[] {10, 0.0}, new double[] {20, 0.0}, 5, 40, true);
		assertEquals(7, filterOrder);

		//digital lowpass
		filterOrder = iirdesigner.calculateFilterOrder(FilterType.LOWPASS, 0.1, 0.2, 3.0, 40.0, false);
		assertEquals(7, filterOrder);

		//highpass
		filterOrder = iirdesigner.calculateFilterOrder(FilterType.HIGHPASS, new double[] {20, 0.0}, new double[] {10, 0.0}, 3, 20, true);
		assertEquals(4, filterOrder);

		//bandstop
		filterOrder = iirdesigner.calculateFilterOrder(FilterType.BANDSTOP, new double[] {1, 4}, new double[] {2, 3}, 3, 20, true);
		assertEquals(3, filterOrder);

		//bandpass
		filterOrder = iirdesigner.calculateFilterOrder(FilterType.BANDPASS, new double[] {2, 4}, new double[] {1, 5}, 5.0, 50.0, true);
		assertEquals(11, filterOrder);

		//digital bandpass
		filterOrder = iirdesigner.calculateFilterOrder(FilterType.BANDPASS, new double[] {0.2, 0.4}, new double[] {0.1, 0.5}, 5.0, 50.0, false);
		assertEquals(9, filterOrder);

	}

	/**
	 * Test method for {@link ButterworthIIRDesigner#calculateBandstopObjectiveFunctionValue(double, int, double[], double[], double, double) }.
	 */
	@Test
	public void testBandstopObjectiveFunction() {

		//butterworth test
		AbstractIIRDesigner.BandstopObjectiveFunction bo = iirdesigner.new BandstopObjectiveFunction(0,
		                new double[] {0.5, 0.8},
		                new double[] {0.6, 0.7},
		                3, 40);

		assertEquals(-3.546182557211913, bo.function(new double[] {1.0}), 1e-8);

		//butterworth test 2
		bo = iirdesigner.new BandstopObjectiveFunction(0,
		                new double[] {1.0, 4.0},
		                new double[] {2.0, 3.0},
		                3, 20);

		assertEquals(7.9947088019182813, bo.function(new double[] {0.0}), 1e-8);
		assertEquals(14.920040569147393, bo.function(new double[] {1.9}), 1e-8);

		//butterworth test 3
		bo = iirdesigner.new BandstopObjectiveFunction(0, new double[] {1.0, 4.0}, new double[] {2.0, 3.0}, 3, 20);

		//double wp0 = SpecialMath.minimizeFunctionConstrained(objectiveFunction, 1.0, 2.0-1e-12);
		assertEquals(2.5100487397489273, bo.function(new double[] {1.5}), 1e-4);

	}

}