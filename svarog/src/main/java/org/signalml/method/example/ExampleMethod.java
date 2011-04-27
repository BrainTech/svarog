/* ExampleMethod.java created 2007-09-12
 *
 */
package org.signalml.method.example;

import java.beans.IntrospectionException;

import org.apache.log4j.Logger;
import org.signalml.exception.SanityCheckException;
import org.signalml.method.AbstractMethod;
import org.signalml.method.ComputationException;
import org.signalml.method.InitializingMethod;
import org.signalml.method.MethodExecutionTracker;
import org.signalml.method.SuspendableMethod;
import org.signalml.method.TrackableMethod;
import org.signalml.method.iterator.IterableMethod;
import org.signalml.method.iterator.IterableNumericProperty;
import org.signalml.method.iterator.IterableParameter;
import org.signalml.plugin.export.SignalMLException;
import org.signalml.util.ResolvableString;
import org.springframework.context.support.MessageSourceAccessor;
import org.springframework.validation.Errors;

/**
 * ExampleMethod - an example implemetnation of a method.
 *
 * @author Michal Dobaczewski &copy; 2007-2008 CC Otwarte Systemy Komputerowe Sp. z o.o.
 */
public class ExampleMethod extends AbstractMethod implements InitializingMethod, TrackableMethod, SuspendableMethod, IterableMethod {

        /**
         * Logger to save history of execution at.
         */
	protected static final Logger logger = Logger.getLogger(ExampleMethod.class);

	private static final String UID = "65b7e4c7-2d3f-4e5c-a18a-7942392268ea";
	private static final String NAME = "meaningOfLife";
	private static final int[] VERSION = new int[] {1,0};

	private static final int COUNT = 10;

	private int[] numbers;
	private boolean initialized = false;

        /**
         * Creates new uninitialized ExampleMethod instance.
         */
	public ExampleMethod() throws SignalMLException {
		super();
	}

        /**
         * {@inheritDoc}
         */
	@Override
	public void initialize() throws SignalMLException {
		if (!initialized) {
			numbers = new int[COUNT];
			for (int i=0; i<COUNT; i++) {
				numbers[i] = i+1;
			}
			initialized = true;
		}
	}

        /**
         * {@inheritDoc}
         */
        @Override
	public String getUID() {
		return UID;
	}

        /**
         * {@inheritDoc}
         */
        @Override
	public String getName() {
		return NAME;
	}

        /**
         * {@inheritDoc}
         */
        @Override
	public int[] getVersion() {
		return VERSION;
	}

        /**
         * {@inheritDoc}
         */
	@Override
	public Object createData() {
		return new ExampleData();
	}

        /**
         * {@inheritDoc}
         */
        @Override
	public boolean supportsDataClass(Class<?> clazz) {
		return ExampleData.class.isAssignableFrom(clazz);
	}

        /**
         * {@inheritDoc}
         */
        @Override
	public Class<?> getResultClass() {
		return ExampleResult.class;
	}

        /**
         * {@inheritDoc}
         */
	@Override
	public void validate(Object data, Errors errors) {
		super.validate(data, errors);
		ExampleData eData = (ExampleData) data;
		int count = eData.getCount();
		if (count < 0 || count > 10000) {
			errors.rejectValue("count", "exampleMethod.error.badCount");
		}
	}

        /**
         * Executes this ExampleMethod and returns null it it was aborted
         * or ExampleResult containing result of computation if no error occured.
         *
         * @param data the data object
         * @param tracker the tracker used to monitor the execution
         * @return the result object
         * @throws ComputationException when computation fails for reasons other than bad input data
         */
	@Override
	public Object doComputation(Object data, MethodExecutionTracker tracker) throws ComputationException {

		int product = 1;
		ExampleData eData = (ExampleData) data;
		int count = eData.getCount();

		// initialized task's tickers
		tracker.resetTickers();
		tracker.setTickerLimits(new int[] {COUNT,count});

		int i, e;
		// initialize computation based on whether it has been suspended before or not
		if (eData.isSuspended()) {
			int[] counters = eData.getSuspendedCounters();
			i = counters[0];
			e = counters[1];
			product = eData.getSuspendedProduct();
			synchronized (tracker) {
				tracker.setTickers(counters);
				if (i/2 == 0) {
					tracker.setMessage(new ResolvableString("exampleMethod.start"));
				} else {
					tracker.setMessage(new ResolvableString("exampleMethod.processed"+(i/2-1)));
				}
			}
			eData.setSuspended(false);
			eData.setSuspendedCounters(null);
		} else {
			i = 0;
			e = 0;
			tracker.setMessage(new ResolvableString("exampleMethod.start"));
		}

		// the main "computation" loop
		while (i<COUNT) {

			tracker.setTicker(1, e);

			while (e<count) {
				if (!eData.isNoWait()) {
					try {
						Thread.sleep(25);
					} catch (InterruptedException ex) {}
				}
				tracker.tick(1);
				e++;

				// check for abort request
				if (tracker.isRequestingAbort()) {
					return null;
				}

				// check for suspend request, if requested save state to the data
				if (tracker.isRequestingSuspend()) {
					eData.setSuspendedCounters(new int[] {i,e});
					eData.setSuspendedProduct(product);
					eData.setSuspended(true);
					return null;
				}
			}

			product *= numbers[i];
			synchronized (tracker) {
				tracker.tick(0);
				tracker.setMessage(new ResolvableString("exampleMethod.processed"+(i/2)));
			}
			i++;
			e=0;
		}

		product += count;

		ExampleResult result = new ExampleResult();
		result.setResult(product);
		return result;

	}

        /**
         * {@inheritDoc}
         */
	@Override
	public int getTickerCount() {
		return 2;
	}

        /**
         * {@inheritDoc}
         */
	@Override
	public String getTickerLabel(MessageSourceAccessor messageSource, int ticker) {
		return messageSource.getMessage("exampleMethod.ticker"+ticker);
	}

        /**
         * {@inheritDoc}
         */
	@Override
	public boolean isDataSuspended(Object data) {
		return ((ExampleData) data).isSuspended();
	}

        /**
         * {@inheritDoc}
         */
	@Override
	public IterableParameter[] getIterableParameters(Object data) {

		IterableNumericProperty countProperty;
		try {
			countProperty = new IterableNumericProperty(data, "count");
		} catch (IntrospectionException ex) {
			logger.error("Failed to get count property", ex);
			throw new SanityCheckException(ex);
		}
		countProperty.setDefaultStartValue(5);
		countProperty.setDefaultEndValue(10);
		countProperty.setMinimum(1);
		countProperty.setMaximum(100);
		countProperty.setStepSize(1);

		return new IterableParameter[] { countProperty };

	}

        /**
         * {@inheritDoc}
         */
	@Override
	public Object digestIterationResult(int iteration, Object result) {
		return result;
	}

}
