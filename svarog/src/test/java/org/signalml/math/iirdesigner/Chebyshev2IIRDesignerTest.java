/* Chebyshev2IIRDesignerTest.java created 2010-09-12
 *
 */

package org.signalml.math.iirdesigner;

import org.apache.commons.math.complex.Complex;
import org.junit.Test;
import org.signalml.BaseTestCase;
import static org.signalml.math.iirdesigner.IIRDesignerAssert.*;

/**
 * This class performs unit tests on {@link Chebyshev2IIRDesigner}
 *
 * @author Piotr Szachewicz
 */
public class Chebyshev2IIRDesignerTest extends BaseTestCase {

	/**
	 * an instance of {@link Chebyshev2IIRDesigner} needed to all the tested methods
	 */
	Chebyshev2IIRDesigner iirdesigner = new Chebyshev2IIRDesigner();

	/**
	 * Test method for {@link Chebyshev2IIRDesigner#calculatePrototype(int, double) }.
	 */
	@Test
	public void testCalculateChebyshev2Prototype() {

		FilterZerosPolesGain zpk1 = iirdesigner.calculatePrototype(3, 3);
		Complex[] zeros = new Complex[] {new Complex(0.0, -1.15470054), new Complex(0.0, 1.15470054)};
		Complex[] poles = new Complex[] {new Complex(-0.17737632,-1.07757832), new Complex(-3.36188451, -1.44406328e-15),
					new Complex(-0.17737632, 1.07757832)
		};

		FilterZerosPolesGain zpk2 = new FilterZerosPolesGain(zeros, poles, 3.0071318790228005);

		assertEquals(zpk1, zpk2);

	}

	/**
	 * Test method for {@link Chebyshev2IIRDesigner#designDigitalFilter(org.signalml.math.iirdesigner.FilterType, double[], double[], double, double) }.
	 */
	@Test
	public void testDesignDigitalFilter() throws BadFilterParametersException {

		//lowpass
		double[] pythonB = new double[] {0.01234417, -0.02387048, 0.03168035, -0.02387048, 0.01234417};
		double[] pythonA = new double[] {1.0, -3.14621235, 3.78629474, -2.05495604, 0.42350137};

		FilterCoefficients coeffs = iirdesigner.designFilter(FilterType.LOWPASS, new double[] {0.1}, new double[] {0.2}, 3.0, 40.0, false);
		assertEquals(new FilterCoefficients(pythonB, pythonA), coeffs, 1e-8);

		//highpass
		pythonB = new double[] {0.07021058, -0.14279096, 0.14279096, -0.07021058};
		pythonA = new double[] {1.0, 1.12978612, 0.68657296, 0.13078377};

		coeffs = iirdesigner.designDigitalFilter(FilterType.HIGHPASS, new double[] {0.7}, new double[] {0.2}, 3.0, 40.0);
		assertEquals(new FilterCoefficients(pythonB, pythonA), coeffs, 1e-8);

		//bandpass
		pythonB = new double[] {3.49030352e-02, 1.99241721e-02, -3.69150532e-02,
								-4.45893421e-18, 3.69150532e-02, -1.99241721e-02,
								-3.49030352e-02
							   };
		pythonA = new double[] {1.0, 1.53977953, 2.54604688, 2.03168879,
								1.68294636, 0.64377119, 0.27144655
							   };

		coeffs = iirdesigner.designDigitalFilter(FilterType.BANDPASS, new double[] {0.5, 0.7}, new double[] {0.3, 0.9}, 3.0, 40.0);
		assertEquals(new FilterCoefficients(pythonB, pythonA), coeffs, 1e-8);

		//bandstop
		pythonB = new double[] {0.16237092, 0.2964514, 0.61138715,
								0.59303041, 0.61138715, 0.2964514,
								0.16237092
							   };
		pythonA = new double[] {1.0, 0.91835019, 0.14443493,
								0.16130349, 0.41398049, 0.10627954,
								-0.0108993
							   };

		coeffs = iirdesigner.designDigitalFilter(FilterType.BANDSTOP, new double[] {0.3, 0.9}, new double[] {0.5, 0.7}, 3.0, 40.0);
		assertEquals(new FilterCoefficients(pythonB, pythonA), coeffs, 1e-4);

	}

	/**
	 * Test method for {@link Chebyshev2IIRDesigner#calculateNaturalFrequency(org.signalml.math.iirdesigner.FilterType, int, double[], double[], double, double, boolean) }.
	 */
	@Test
	public void testCalculateNaturalFrequency() throws BadFilterParametersException {

		double[] wna;
		double wn;

		//digital lowpass
		wn = iirdesigner.calculateNaturalFrequency(FilterType.LOWPASS, 7, 0.6, 0.9, 0.5, 100.0, false);
		assertEquals(0.86590513063084507, wn, 1e-8);

		//digital highpass
		wn = iirdesigner.calculateNaturalFrequency(FilterType.HIGHPASS, 4, 0.9, 0.1, 0.5, 100.0, false);
		assertEquals(0.27365521962463824, wn, 1e-8);

		//digital bandstop
		wna = iirdesigner.calculateNaturalFrequency(FilterType.BANDSTOP, 11, new double[] {0.5, 0.7}, new double[] {0.55, 0.65}, 0.5, 100, true);
		assertEquals(new double[] {0.54813224, 0.6522109}, wna, 1e-4);

		//digital bandpass
		wna = iirdesigner.calculateNaturalFrequency(FilterType.BANDPASS, 10, new double[] {0.6, 0.7}, new double[] {0.55, 0.8}, 0.5, 100, true);
		assertEquals(new double[] {0.55509301, 0.75662996}, wna, 1e-8);

	}

}