/* ButterworthIIRDesignerTest.java created 2010-09-12
 *
 */

package org.signalml.math.iirdesigner;

import static org.signalml.math.iirdesigner.IIRDesignerAssert.*;

import org.junit.Test;
import org.signalml.math.iirdesigner.ApproximationFunctionType;
import org.signalml.math.iirdesigner.BadFilterParametersException;
import org.signalml.math.iirdesigner.FilterCoefficients;
import org.signalml.math.iirdesigner.FilterType;
import org.signalml.math.iirdesigner.IIRDesigner;

import java.lang.Math.*;

/**
 * This class performs unit tests on the {@link IIRDesigner} class.
 *
 * @author Piotr Szachewicz
 */
public class IIRDesignerTest {


	/**
	 * Test method for {@link IIRDesigner#designDigitalFilter(org.signalml.math.iirdesigner.ApproximationFunctionType, org.signalml.math.iirdesigner.FilterType, double[], double[], double, double, double) }.
	 */
	@Test
	public void testDesignDigitalFilter() throws BadFilterParametersException {

		double[] pythonB;
		double[] pythonA;
		FilterCoefficients coeffs;

		pythonB = new double[] {0.50611529,  -3.542807  ,  10.62842101, -17.71403501,
		                    17.71403501, -10.62842101,   3.542807  ,  -0.50611529
		                   };
		pythonA = new double[] {1.        ,  -5.64538   ,  13.76796824, -18.78803539,
		                    15.48341232,  -7.70174965,   2.14005833,  -0.25615268
		                   };

		coeffs = IIRDesigner.designDigitalFilter(ApproximationFunctionType.BUTTERWORTH, FilterType.HIGHPASS,
		                new double[] {10, 0.0}, new double[] {5, 0.0}, 3.0, 40.0, 200.0);

		assertEquals(new FilterCoefficients(pythonB, pythonA), coeffs, 1e-8);

	}

}