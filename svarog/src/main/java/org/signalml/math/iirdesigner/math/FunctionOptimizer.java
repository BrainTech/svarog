/* Optimizer.java created 2011-02-21
 *
 */

package org.signalml.math.iirdesigner.math;
import java.util.logging.Level;
import org.apache.commons.math.FunctionEvaluationException;
import org.apache.commons.math.MaxIterationsExceededException;
import org.apache.commons.math.analysis.UnivariateRealFunction;
import org.apache.commons.math.optimization.GoalType;
import org.apache.commons.math.optimization.OptimizationException;
import org.apache.commons.math.optimization.RealPointValuePair;
import org.apache.commons.math.optimization.direct.NelderMead;
import org.apache.commons.math.optimization.univariate.BrentOptimizer;

/**
 * This class represents an optimizer capable of finding minimum values in
 * univariate real functions.
 *
 * @author Piotr Szachewicz
 */
public class FunctionOptimizer {

	/**
	 * Returns the value of the parameter found by the Nelder and Mead simplex
	 * algorithm which minimizes the value of the given function.
	 *
	 * @param function the function to minimize
	 * @param start the initial estimate of the function parameter
	 * @param nmax the maximum number of iterations allowed by the simplex procedure
	 * @return the value of the parameter at which the value of the function is minimum
	 */
	public static double minimizeFunction(UnivariateRealFunction function, double start, int nmax) {
		UnivariateFunctionWrappedInAMultivariate wrappedFunction = new UnivariateFunctionWrappedInAMultivariate(function);
		NelderMead optimizer = new NelderMead();

		optimizer.setMaxEvaluations(nmax);
		RealPointValuePair result = null;
		try {
			result = optimizer.optimize(wrappedFunction, GoalType.MINIMIZE, new double[] {start});
		} catch (FunctionEvaluationException ex) {
			java.util.logging.Logger.getLogger(FunctionOptimizer.class.getName()).log(Level.SEVERE, null, ex);
		} catch (OptimizationException ex) {
			java.util.logging.Logger.getLogger(FunctionOptimizer.class.getName()).log(Level.SEVERE, null, ex);
		} catch (IllegalArgumentException ex) {
			java.util.logging.Logger.getLogger(FunctionOptimizer.class.getName()).log(Level.SEVERE, null, ex);
		}

		return result.getPoint()[0];
	}

	/**
	 * Returns the value of the parameter found by the Nelder and Mead simplex
	 * algorithm which minimizes the value of the given function with its
	 * parameter constrained.
	 *
	 * @param function the function to minimize
	 * @param lowerBounds the lower boundary value for the function parameter
	 * @param higherBounds the higher boundary value for the function parameter
	 * @param nmax the maximum number of iterations allowed by the simplex procedure
	 * @return the value of the parameter at which the value of the function is minimum
	 * (at the given constraints).
	 */
	public static double  minimizeFunctionConstrained(UnivariateRealFunction function, double lowerBounds, double higherBounds, int nmax) {
		BrentOptimizer optimizer = new BrentOptimizer();


		double result = 0;
		try {
			result = optimizer.optimize(function, GoalType.MINIMIZE, lowerBounds, higherBounds);
		} catch (MaxIterationsExceededException ex) {
			java.util.logging.Logger.getLogger(FunctionOptimizer.class.getName()).log(Level.SEVERE, null, ex);
		} catch (FunctionEvaluationException ex) {
			java.util.logging.Logger.getLogger(FunctionOptimizer.class.getName()).log(Level.SEVERE, null, ex);
		}

		return result;
	}

}
