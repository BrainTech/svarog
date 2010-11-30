/* FilterCoefficientsTest.java created 2010-09-12
 *
 */

package org.signalml.domain.montage.filter.iirdesigner;

import flanagan.complex.Complex;
import org.junit.Test;
import org.signalml.domain.montage.filter.iirdesigner.FilterCoefficients.ComplexFrequencyResponse;

import static org.signalml.domain.montage.filter.iirdesigner.IIRDesignerAssert.*;

/**
 * This method performs unit tests on {@link FilterCoefficients}
 *
 * @author Piotr Szachewicz
 */
public class FilterCoefficientsTest {

	@Test
	public void testGetComplexFrequencyResponse() {

		//b,a=signal.iirdesign(0.4,0.6,5,30, ftype='butter')
		FilterCoefficients coeffs = new FilterCoefficients(
		        new double[] {0.01875398, 0.0937699, 0.18753981, 0.18753981, 0.0937699, 0.01875398},
		        new double[] {1.0, -1.13877891, 1.08854509, -0.46710982, 0.13151932, -0.0140483});

		TransferFunction response = coeffs.getComplexFrequencyResponse(512);

		assertEquals(new Complex(1,0), response.getGain()[0], new Complex(1e-6, 1e-6));
		assertEquals(new Complex(9.89651405e-01, -1.43492499e-01), response.getGain()[10], new Complex(1e-6, 1e-6));
		assertEquals(new Complex(2.94416493e-16, -4.24775580e-14), response.getGain()[511], new Complex(1e-6, 1e-6));

		assertEquals(0, response.getFrequencies()[0], 1e-6);
		assertEquals(0.02454369, response.getFrequencies()[4], 1e-6);
		assertEquals(3.12932081, response.getFrequencies()[510], 1e-6);

	}


	/**
	 * Test method for {@link FilterCoefficients#normalize() }.
	 */
	@Test
	public void testNormalize() throws BadFilterParametersException {

		FilterCoefficients coeffs = new FilterCoefficients(new double[] {0.1, 0.2, 0.3}, new double[] {0.7, 0.5, 0.3});
		coeffs.normalize();
		assertEquals(new double[] {0.14285714, 0.28571429, 0.42857143}, coeffs.getBCoefficients(), 1e-7);
		assertEquals(new double[] {1.0, 0.71428571, 0.42857143}, coeffs.getACoefficients(), 1e-7);

		coeffs = new FilterCoefficients(new double[] {0.2, 0.3, 0.4}, new double[] {0.5, 0.3, 0.8, 0.1});
		coeffs.normalize();
		assertEquals(new double[] {0.4, 0.6, 0.8}, coeffs.getBCoefficients(), 1e-7);
		assertEquals(new double[] {1. , 0.6, 1.6, 0.2}, coeffs.getACoefficients(), 1e-7);

	}

	/**
	 * Test method for {@link FilterCoefficients#transformLowpassToLowpass(double) }.
	 */
	@Test
	public void testTransformLowpassToLowpass() throws BadFilterParametersException {

		double[] b = new double[] {0.06264858};
		double[] a = new double[] {1.00000000, 0.57450003, 1.41502514, 0.54893711,
		                           0.40796631, 0.06264858
		                          };
		FilterCoefficients coeffs = new FilterCoefficients(b, a);

		double[] pythonB = new double[] {47573.76605485};
		double[] pythonA = new double[] {1.00000000e+00, 8.61750040e+00, 3.18380657e+02,
		                             1.85266276e+03, 2.06532946e+04, 4.75737661e+04
		                            };
		coeffs.transformLowpassToLowpass(15.0);
		assertEquals(new FilterCoefficients(pythonB, pythonA), coeffs, 1e-3);

	}

	/**
	 * Test method for {@link FilterCoefficients#transformLowpassToHighpass(double) }.
	 */
	@Test
	public void testTransformLowpassToHighpass() throws BadFilterParametersException {

		double[] b = new double[] {0.06264858};
		double[] a = new double[] {1.00000000, 0.57450003, 1.41502514, 0.54893711,
		                           0.40796631, 0.06264858
		                          };
		FilterCoefficients coeffs = new FilterCoefficients(b, a);

		double[] pythonB = new double[] {1, 0, 0, 0, 0, 0};
		double[] pythonA = new double[] {1.00000000e+00, 1.95359404e+01, 7.88594723e+01,
		                             6.09841090e+02, 7.42786214e+02, 3.87877901e+03
		                            };

		coeffs.transformLowpassToHighpass(3.0);
		assertEquals(new FilterCoefficients(pythonB, pythonA), coeffs, 1e-4);

	}

	/**
	 * Test method for {@link FilterCoefficients#transformLowpassToBandpass(double, double) }.
	 */
	@Test
	public void testTransformLowpassToBandpass() throws BadFilterParametersException {

		double[] b = new double[] {0.06264858};
		double[] a = new double[] {1.00000000, 0.57450003, 1.41502514, 0.54893711,
		                           0.40796631, 0.06264858
		                          };
		FilterCoefficients coeffs = new FilterCoefficients(b, a);

		double[] pythonB = new double[] {2.00475456, 0, 0, 0, 0, 0};
		double[] pythonA = new double[] { 1.00000000e+00, 1.14900006e+00, 5.06601006e+01,
		                              4.57554990e+01, 9.69350176e+02, 6.39465728e+02,
		                              8.72415158e+03, 3.70619542e+03, 3.69312133e+04,
		                              7.53858939e+03, 5.90490000e+04
		                            };

		coeffs.transformLowpassToBandpass(3.0, 2.0);
		assertEquals(new FilterCoefficients(pythonB, pythonA), coeffs, 1e-5);

	}

	/**
	 * Test method for {@link FilterCoefficients#transformFromLowpassToBandstop(double, double) }.
	 */
	@Test
	public void testTransformLowpassToBandstop() throws BadFilterParametersException {

		double[] b = new double[] {0.06264858};
		double[] a = new double[] {1.00000000, 0.57450003, 1.41502514, 0.54893711,
		                           0.40796631, 0.06264858
		                          };
		FilterCoefficients coeffs = new FilterCoefficients(b, a);

		double[] pythonB = new double[] {1.00000000e+00, 0.00000000e+00, 4.50000000e+01,
		                             0.00000000e+00, 8.10000000e+02, 0.00000000e+00,
		                             7.29000000e+03, 0.00000000e+00, 3.28050000e+04,
		                             0.00000000e+00, 5.90490000e+04
		                            };
		double[] pythonA = new double[] {1.00000000e+00, 1.30239603e+01, 8.00486546e+01,
		                             6.49556230e+02, 1.90303688e+03, 1.00929163e+04,
		                             1.71273319e+04, 5.26140546e+04, 5.83554692e+04,
		                             8.54502037e+04, 5.90490000e+04
		                            };

		coeffs.transformFromLowpassToBandstop(3.0, 2.0);
		assertEquals(new FilterCoefficients(pythonB, pythonA), coeffs, 1e-3);

	}

	/**
	 * Test method for cooperation between {@link FilterCoefficients#transformFromLowpassToBandstop(double, double)
	 * and {@link FilterCoefficients#bilinearTransform(double)}.
	 */
	@Test
	public void testTransformLowpassToBandstopAndBilinearTransform() throws BadFilterParametersException {

		double[] b = new double[] {1.0};
		double[] a = new double[] {1.0, 1.0};
		FilterCoefficients coeffs = new FilterCoefficients(b, a);
		double bw = 7.798072709589556;
		double wo = 3.9999999999999862;

		coeffs.transformFromLowpassToBandstop(wo, bw);
		double[] pythonB = new double[] {1.0000000, 0.0000000, 15.9999925};
		double[] pythonA = new double[] {1.0000000, 7.7980768, 15.9999925};
		assertEquals(new FilterCoefficients(pythonB, pythonA), coeffs, 1e-5);

		coeffs.bilinearTransform(2.0);
		pythonB = new double[] {5.06390694e-01, -2.37380515e-07, 5.06390694e-01};
		pythonA = new double[] {1.00000000e+00, -2.37380515e-07, 1.27813882e-02};
		assertEquals(new FilterCoefficients(pythonB, pythonA), coeffs, 1e-5);

	}

	/**
	 * Test method for {@link FilterCoefficients#bilinearTransform(double) }.
	 */
	@Test
	public void testBilinearTransform() throws BadFilterParametersException {

		double[] b;
		double[] a;
		FilterCoefficients coeffs;
		double[] pythonB;
		double[] pythonA;

		//test 1
		b = new double[] {5.0, 6.0, 7.0};
		a = new double[] {1.0, 2.0, 3.0, 4.0};
		coeffs = new FilterCoefficients(b, a);

		pythonB = new double[] {
		        0.04959992, -0.04839636, -0.04957248, 0.0484238
		};
		pythonA = new double[] {
		        1.        , -2.95959636, 2.92041588, -0.96078816
		};

		coeffs.bilinearTransform(50.0);
		assertEquals(new FilterCoefficients(pythonB, pythonA), coeffs, 1e-4);

		//test 2
		b = new double[] {1.0000000, 0.0000000, 15.9999925};
		a = new double[] {1.0000000, 7.7980768, 15.9999925};
		pythonB = new double[] {5.06390694e-01, -2.37370693e-07, 5.06390694e-01};
		pythonA = new double[] {1.00000000e+00, -2.37370693e-07, 1.27813880e-02};

		coeffs = new FilterCoefficients(b, a);
		coeffs.print();
		coeffs.bilinearTransform(2.0);
		assertEquals(new FilterCoefficients(pythonB, pythonA), coeffs, 1e-4);
		coeffs.print();

	}

}