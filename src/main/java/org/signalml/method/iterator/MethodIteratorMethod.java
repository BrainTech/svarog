/* IteratorMethod.java created 2007-12-05
 *
 */

package org.signalml.method.iterator;

import org.apache.log4j.Logger;
import org.signalml.exception.SanityCheckException;
import org.signalml.exception.SignalMLException;
import org.signalml.method.AbstractMethod;
import org.signalml.method.ComputationException;
import org.signalml.method.InitializingMethod;
import org.signalml.method.InputDataException;
import org.signalml.method.MethodExecutionTracker;
import org.signalml.method.SuspendableMethod;
import org.signalml.method.TrackableMethod;
import org.springframework.context.support.MessageSourceAccessor;

/** IteratorMethod
 *
 *
 * @author Michal Dobaczewski &copy; 2007-2008 CC Otwarte Systemy Komputerowe Sp. z o.o.
 */
public class MethodIteratorMethod extends AbstractMethod implements InitializingMethod, SuspendableMethod, TrackableMethod {

	protected static final Logger logger = Logger.getLogger(MethodIteratorMethod.class);

	private IterableMethod subjectMethod;

	public MethodIteratorMethod(IterableMethod subjectMethod) {
		super();
		if (subjectMethod == null) {
			throw new NullPointerException("No method");
		}
		this.subjectMethod = subjectMethod;
	}

	public IterableMethod getSubjectMethod() {
		return subjectMethod;
	}

	@Override
	public Object doComputation(Object dataObj, MethodExecutionTracker tracker) throws ComputationException {

		MethodIteratorData data = (MethodIteratorData) dataObj;
		MethodIteratorResult results;

		int totalIterations = data.getTotalIterations();
		int completedIterations = data.getCompletedIterations();
		int i;
		Object result;

		tracker.setTickerLimit(0, totalIterations);
		tracker.setTicker(0, completedIterations);
		if (completedIterations > 0) {
			results = data.getCompletedResults();
			if (results.size() != completedIterations) {
				throw new SanityCheckException("Invalid number of suspended results");
			}
		} else {
			results = new MethodIteratorResult(data.getParameters());
			data.setCompletedResults(results);
		}

		TickerOffsettingTrackerWrapper trackerWrapper = new TickerOffsettingTrackerWrapper(tracker);
		Object[] parameterValues;

		for (i=completedIterations; i<totalIterations; i++) {

			parameterValues = data.setupForIteration(i);

			try {
				result = subjectMethod.compute(data.getSubjectMethodData(), trackerWrapper);
			} catch (InputDataException ex) {
				logger.error("Iteration caused the data to become invalid", ex);
				throw new ComputationException("error.iterationError", ex);
			}
			if (result != null) {
				results.add(subjectMethod.digestIterationResult(i, result), parameterValues);
				data.setCompletedIterations(i+1);
				tracker.tick(0);
			}

			if (tracker.isRequestingAbort()) {
				return null;
			}
			else if (tracker.isRequestingSuspend()) {
				return null;
			}

			if (result == null) {
				return null;
			}

		}

		return results;

	}

	@Override
	public Object createData() {
		return new MethodIteratorData();
	}

	@Override
	public String getName() {
		return "iteration(" + subjectMethod.getName() + ")";
	}

	@Override
	public Class<?> getResultClass() {
		return MethodIteratorResult.class;
	}

	@Override
	public String getUID() {
		return subjectMethod.getUID() + "-ITERATION";
	}

	@Override
	public int[] getVersion() {
		return subjectMethod.getVersion();
	}

	@Override
	public boolean supportsDataClass(Class<?> clazz) {
		return MethodIteratorData.class.isAssignableFrom(clazz);
	}

	@Override
	public int getTickerCount() {
		if (subjectMethod instanceof TrackableMethod) {
			return 1 + ((TrackableMethod) subjectMethod).getTickerCount();
		}
		return 1;
	}

	@Override
	public String getTickerLabel(MessageSourceAccessor messageSource, int ticker) {
		if (ticker > 0 && subjectMethod instanceof TrackableMethod) {
			return ((TrackableMethod) subjectMethod).getTickerLabel(messageSource, ticker-1);
		}
		return messageSource.getMessage("methodIteratorMethod.iterationTicker");
	}

	@Override
	public void initialize() throws SignalMLException {
		if (subjectMethod instanceof InitializingMethod) {
			((InitializingMethod) subjectMethod).initialize();
		}
	}

	@Override
	public boolean isDataSuspended(Object data) {
		return (((MethodIteratorData) data).getCompletedIterations() > 0);
	}

}
