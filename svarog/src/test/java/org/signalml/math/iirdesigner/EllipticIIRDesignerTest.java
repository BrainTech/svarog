/* EllipticIIRDesignerTest.java created 2010-09-12
 *
 */

package org.signalml.math.iirdesigner;

import org.apache.commons.math.complex.Complex;

import static org.signalml.math.iirdesigner.IIRDesignerAssert.*;

import org.junit.Test;
import java.lang.Math.*;

import org.signalml.math.iirdesigner.BadFilterParametersException;
import org.signalml.math.iirdesigner.EllipticIIRDesigner;
import org.signalml.math.iirdesigner.FilterCoefficients;
import org.signalml.math.iirdesigner.FilterType;
import org.signalml.math.iirdesigner.FilterZerosPolesGain;
import org.signalml.math.iirdesigner.AbstractIIRDesigner.BandstopObjectiveFunction;
import org.signalml.math.iirdesigner.EllipticIIRDesigner.KRatio;
import org.signalml.math.iirdesigner.EllipticIIRDesigner.VRatio;

/**
 * This class performs unit tests on {@link EllipticIIRDesigner} class.
 * @author Piotr Szachewicz
 */
public class EllipticIIRDesignerTest {

	/**
	 * an instance of {@link EllipticIIRDesigner} needed to call all the tested methods.
	 */
	EllipticIIRDesigner iirdesigner = new EllipticIIRDesigner();

	/**
	 * Test method for {@link EllipticIIRDesigner#designDigitalFilter(org.signalml.math.iirdesigner.FilterType, double[], double[], double, double) }.
	 */
	@Test
	public void testDesignDigitalFilter() throws BadFilterParametersException {

		double[] pythonB;
		double[] pythonA;
		FilterCoefficients coeffs;

		//lowpass test
		pythonB = new double[] {0.00881574, -0.00485172, -0.00485172, 0.00881574};
		pythonA = new double[] {1.0, -2.74146002, 2.58002871, -0.83064064};

		coeffs = iirdesigner.designFilter(FilterType.LOWPASS, new double[] {0.1}, new double[] {0.2}, 3.0, 40.0, false);
		assertEquals(new FilterCoefficients(pythonB, pythonA), coeffs, 1e-5);

		//highpass
		pythonB = new double[] {0.04105714, -0.05209928, 0.05209928, -0.04105714};
		pythonA = new double[] {1.0, 1.85768178, 1.61734402, 0.57334941};

		coeffs = iirdesigner.designDigitalFilter(FilterType.HIGHPASS, new double[] {0.7}, new double[] {0.3}, 3.0, 40.0);
		assertEquals(new FilterCoefficients(pythonB, pythonA), coeffs, 1e-4);

		//bandpass test
		pythonB = new double[] {0.02114251, 0.01792455, -0.00243741,
								0.0, 0.00243741, -0.01792455, -0.02114251
							   };
		pythonA = new double[] {1.0, 1.73921561, 3.38814582,
								3.10556401, 3.01265939, 1.35579533, 0.69028645
							   };

		coeffs = iirdesigner.designDigitalFilter(FilterType.BANDPASS, new double[] {0.5, 0.7}, new double[] {0.3, 0.9}, 3.0, 40.0);
		assertEquals(new FilterCoefficients(pythonB, pythonA), coeffs, 1e-4);

		//bandstop test
		pythonB = new double[] {0.0987149, 0.16661581, 0.31652632,
								0.30320545, 0.31652632, 0.16661581,
								0.0987149
							   };
		pythonA = new double[] {1.0, 0.68543316, -0.66268689,
								0.05276535, 0.9103521, -0.10176143,
								-0.41718278
							   };

		coeffs = iirdesigner.designDigitalFilter(FilterType.BANDSTOP, new double[] {0.3, 0.9}, new double[] {0.5, 0.7}, 3.0, 40.0);
		assertEquals(new FilterCoefficients(pythonB, pythonA), coeffs, 1e-4);

	}

	/**
	 * Test method for {@link VRatio#function(double[]) }.
	 */
	@Test
	public void testVRatioFunction() {

		EllipticIIRDesigner.VRatio vRatio = new VRatio(0.1, 0.2);

		assertEquals(0.44117773230576596, vRatio.value(0.5), 1e-16);

	}

	/**
	 * Test method for {@link KRatio#function(double[]) }.
	 */
	@Test
	public void testKRatioFunction() {

		EllipticIIRDesigner.KRatio kRatio = new KRatio();

		kRatio.setKRatio(0.3);
		assertEquals(0.69999999999999996, kRatio.value(0.5), 1e-16);
		kRatio.setKRatio(-0.3);
		assertEquals(0.29999999999999999, kRatio.value(1.5), 1e-16);
		kRatio.setKRatio(0.6);
		assertEquals(0.40000000000000002, kRatio.value(0.5), 1e-16);

	}

	/**
	 * Test method for {@link EllipticIIRDesigner#calculatePrototype(int, double, double) }.
	 */
	@Test
	public void testCalculatePrototype() throws BadFilterParametersException {

		//filterOrder=1
		FilterZerosPolesGain zpk1 = iirdesigner.calculatePrototype(1, 2, 3);
		FilterZerosPolesGain zpk2 = new FilterZerosPolesGain(new Complex[0], new Complex[] {new Complex(-1.3075602715790791, 0.0)}, 1.3075602715790791);
		assertEquals(zpk1, zpk2);

		//another test (even filter order)
		zpk1 = iirdesigner.calculatePrototype(4, 2, 3);

		Complex[] zeros = new Complex[] {new Complex(0, 1.06863478), new Complex(0, -1.06863478), new Complex(0, 1.00007796), new Complex(0, -1.00007796)};
		Complex[] poles = new Complex[] {new Complex(-6.62726745e-02, -1.00672153), new Complex(-6.62726745e-02, +1.00672153), new Complex(-7.22990126e-05, -1.00001461), new Complex(-7.22990126e-05, 1.00001461)};
		double gain = 0.70791820787954829;
		zpk2 = new FilterZerosPolesGain(zeros, poles, gain);

		//assertEquals(zpk1, zpk2, 1e-1);

		//odd filter order
		zpk1 = iirdesigner.calculatePrototype(5, 3, 40);

		zeros = new Complex[] {new Complex(0, 1.58139008), new Complex(0, -1.58139008), new Complex(0, 1.16628458), new Complex(0, -1.16628458)};
		poles = new Complex[] {new Complex(-0.25129106, 0), new Complex(-0.13168110, -0.74196202), new Complex(-0.13168110, 0.74196202), new Complex(-0.02642856, -0.98568577), new Complex(-0.02642856, 0.98568577)};
		gain = 0.040785969129633996;
		zpk2 = new FilterZerosPolesGain(zeros, poles, gain);

	}

	/**
	 * Test method for {@link EllipticIIRDesigner#calculateBandstopObjectiveFunctionValue(double, org.signalml.math.iirdesigner.AbstractIIRDesigner.BandstopObjectiveFunction) }.
	 */
	@Test
	public void testBandstopObjectiveFunction() {

		//elliptic test
		BandstopObjectiveFunction bo = iirdesigner.new BandstopObjectiveFunction(0,
									   new double[] {0.1, 0.8}, new double[] {0.4, 0.6}, 3, 20);
		assertEquals(2.4835659582481822, bo.value(0.0), 1e-8);

		//elliptic test 2
		bo = iirdesigner.new BandstopObjectiveFunction(0,
				new double[] {0.5, 0.8}, new double[] {0.55, 0.7}, 0.5, 100);
		assertEquals(11.571836975873939, bo.value(0.00), 1e-4);

		//elliptic test 3
		bo = iirdesigner.new BandstopObjectiveFunction(0,
				new double[] {0.5, 0.8}, new double[] {0.55, 0.7}, 2.5, 130);
		assertEquals(12.802279159652183, bo.value(0.09), 1e-4);


	}

	/**
	 * Test method for {@link EllipticIIRDesigner#calculateNaturalFrequency(org.signalml.math.iirdesigner.FilterType, int, double, double, double, double, boolean) }.
	 */
	@Test
	public void testCalculateNaturalFrequency() throws BadFilterParametersException {

		double[] wn;

		//test 1
		wn = iirdesigner.calculateNaturalFrequency(FilterType.BANDPASS, 7, new double[] {0.6, 0.7}, new double[] {0.55, 0.8}, 0.5, 100, true);
		assertEquals(new double[] {0.6, 0.7}, wn, 1e-16);

		//test 2
		wn = iirdesigner.calculateNaturalFrequency(FilterType.BANDSTOP, 3, new double[] {0.3, 0.9}, new double[] {0.5, 0.7}, 3.0, 40.0, true);
		assertEquals(new double[] {0.38888824, 0.89999518}, wn, 1e-4);

		//test 3
		wn = iirdesigner.calculateNaturalFrequency(FilterType.BANDSTOP, 3, new double[] {0.3, 0.9}, new double[] {0.5, 0.7}, 3.0, 40.0, false);
		assertEquals(new double[] {0.30000218, 0.83829193}, wn, 1e-5);

		//test 4
		wn = iirdesigner.calculateNaturalFrequency(FilterType.BANDSTOP, 8, new double[] {0.5, 0.8}, new double[] {0.55, 0.7}, 0.5, 100, true);
		assertEquals(new double[] {0.50000531, 0.7699937}, wn, 1e-5);

	}

	/**
	 * Test method for {@link EllipticIIRDesigner#calculateFilterOrder(org.signalml.math.iirdesigner.FilterType, double[], double[], double, double, boolean) }.
	 */
	@Test
	public void testCalculateFilterOrder() throws BadFilterParametersException {

		int filterOrder;

		//digital lowpass
		filterOrder = iirdesigner.calculateFilterOrder(FilterType.LOWPASS, 0.4, 0.7, 3, 40, false);
		assertEquals(3, filterOrder);

		//digital highpass
		filterOrder = iirdesigner.calculateFilterOrder(FilterType.HIGHPASS, 0.7, 0.3, 1, 80, false);
		assertEquals(5, filterOrder);

		//digital bandstop
		filterOrder = iirdesigner.calculateFilterOrder(FilterType.BANDSTOP, new double[] {0.5, 0.8}, new double[] {0.6, 0.7}, 0.5, 100, false);
		assertEquals(6, filterOrder);

		//analog bandpass
		filterOrder = iirdesigner.calculateFilterOrder(FilterType.BANDPASS, new double[] {0.6, 0.7}, new double[] {0.55, 0.8}, 0.5, 100, true);
		assertEquals(7, filterOrder);

	}

}