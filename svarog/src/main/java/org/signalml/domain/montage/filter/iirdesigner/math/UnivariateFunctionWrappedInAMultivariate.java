/* UnivariateFunctionWrappedInAMultivariate.java created 2011-02-20
 *
 */

package org.signalml.domain.montage.filter.iirdesigner.math;

import org.apache.commons.math.FunctionEvaluationException;
import org.apache.commons.math.analysis.MultivariateRealFunction;
import org.apache.commons.math.analysis.UnivariateRealFunction;

/**
 * This class represents an apache univariateRealFunction wrapped in a multivariate
 * function interface to use it with Nelder-Mead optimization algorithm which
 * is for multivariate functions only in the apache.commons.math library.
 *
 * @author Piotr Szachewicz
 */
class UnivariateFunctionWrappedInAMultivariate implements MultivariateRealFunction {

	/**
	 * A function to which all calls for values will be delegated.
	 */
	private UnivariateRealFunction function;

	/**
	 * Creates a new multivariate function out of a univariate function.
	 * @param function univariate function to be wrapped in a multivariate
	 * interface
	 */
	public UnivariateFunctionWrappedInAMultivariate(UnivariateRealFunction function) {
		this.function = function;
	}

	@Override
	public double value(double[] doubles) throws FunctionEvaluationException, IllegalArgumentException {
		return function.value(doubles[0]);
	}

}

