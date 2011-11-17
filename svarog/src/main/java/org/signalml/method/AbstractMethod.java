/* AbstractMethod.java created 2007-09-12
 *
 */
package org.signalml.method;

import org.signalml.plugin.export.method.SvarogMethod;
import org.springframework.validation.Errors;

/**
 *  This is a bare bones abstract implementation of the {@link Method} interface. The compute
 *  method is implemented and split into calling {@link #validate(Object, Errors)} which
 *  validates the input data, and later {@link #doComputation(Object, MethodExecutionTracker)},
 *  provided the validation
 *  returned no errors. Subclasses must implement doComputation, and may override validate to
 *  provide data validation.
 *
 * @author Michal Dobaczewski &copy; 2007-2008 CC Otwarte Systemy Komputerowe Sp. z o.o.
 */
public abstract class AbstractMethod implements SvarogMethod {

	/**
         * {@inheritDoc}
	 */
	@Override
	public final Object compute(Object data, MethodExecutionTracker tracker) throws InputDataException, ComputationException {

		InputDataException errors = new InputDataException(data, "data");
		validate(data, errors);
		if (errors.hasErrors()) {
			throw errors;
		}

		return doComputation(data,tracker);

	}

	/**
         * Returns the version as String.
	 *
	 * @return the version
	 */
	public final String getVersionString() {
		int[] versionNumbers = getVersion();
		StringBuilder sb = new StringBuilder(versionNumbers.length*8);
		for (int i=0; i<versionNumbers.length; i++) {
			if (i > 0) {
				sb.append('.');
			}
			sb.append(versionNumbers[i]);
		}
		return sb.toString();
	}

	/**
         *  Validates the data object received by the compute method. All problems should
	 *  be added to suppled Errors instance. The default implementation checks if the data
	 *  object has approprtiate class (using {@link Method#supportsDataClass(Class)}).
	 *
	 * @param data the data object
	 * @param errors the Errors instance where errors should be added
	 */
	public void validate(Object data, Errors errors) {
		if (!supportsDataClass(data.getClass())) {
			errors.reject("error.method.badDataClass");
		}
	}

	/**
         *  Subclasses must implement this to perform the actual computations all caveats for
	 *  {@link #compute(Object, MethodExecutionTracker)} apply, except the data successfully passed through
	 *  {@link #validate(Object, Errors)} before, and may be assumed to be ok.
	 *
	 * @param data the data object
	 * @param tracker the tracker used to monitor the execution
	 * @return the result object
	 * @throws ComputationException when computation fails for reasons other than bad input data
	 */
	public abstract Object doComputation(Object data, MethodExecutionTracker tracker) throws ComputationException;
}
