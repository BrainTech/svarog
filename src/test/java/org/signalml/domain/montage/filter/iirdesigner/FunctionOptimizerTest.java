/* OptimizerTest.java created 2011-02-21
 *
 */

package org.signalml.domain.montage.filter.iirdesigner;

import org.junit.Test;
import org.signalml.domain.montage.filter.iirdesigner.EllipticIIRDesigner.KRatio;
import org.signalml.domain.montage.filter.iirdesigner.EllipticIIRDesigner.VRatio;
import org.signalml.domain.montage.filter.iirdesigner.math.FunctionOptimizer;

import static org.junit.Assert.*;

/**
 * This class performes unit tests on {@link FunctionOptimizer}
 *
 * @author Piotr Szachewicz
 */
public class FunctionOptimizerTest {

	/**
	 * Test method for {@link FunctionOptimizer#minimizeFunction(org.apache.commons.math.analysis.UnivariateRealFunction, double, int) }.
	 */
	@Test
	public void testMinimizeFunction() {

		VRatio vRatio = new VRatio(1.0023772930076005, 0.9999004638148846);
		double startValue = 1.688482411280818;
		double r = FunctionOptimizer.minimizeFunction(vRatio, startValue, 250);
		assertEquals(0.8830402459180449, r, 1e-3);

	}

	/**
	 * Test method for {@link FunctionOptimizer#minimizeFunctionConstrained(org.apache.commons.math.analysis.UnivariateRealFunction, double, double, int) }.
	 */
	@Test
	public void testMinimizeFunctionConstrained() {

		EllipticIIRDesigner.KRatio kRatio = new KRatio();

		kRatio.setKRatio(0.5);
		double m = FunctionOptimizer.minimizeFunctionConstrained(kRatio, 0.0, 1.0, 250);

		assertEquals(0.02943873, m, 1e-4);

	}
}
