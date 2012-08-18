package org.signalml.app;

import org.signalml.method.MethodExecutionTracker;
import org.signalml.method.ep.EvokedPotentialMethod;

/**
 * This execution tracker is used for testing the {@link EvokedPotentialMethod}
 * which needs an execution tracker but does not care what it is really doing.
 *
 * @author Piotr Szachewicz
 */
public class DummyMethodExecutionTracker implements MethodExecutionTracker {

	@Override
	public boolean isRequestingAbort() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean isRequestingSuspend() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void setMessage(String message) {
		// TODO Auto-generated method stub

	}

	@Override
	public String getMessage() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public int[] getTickerLimits() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void setTickerLimits(int[] initial) {
		// TODO Auto-generated method stub

	}

	@Override
	public void setTickerLimit(int index, int limit) {
		// TODO Auto-generated method stub

	}

	@Override
	public int[] getTickers() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void resetTickers() {
		// TODO Auto-generated method stub

	}

	@Override
	public void setTickers(int[] current) {
		// TODO Auto-generated method stub

	}

	@Override
	public void setTicker(int index, int value) {
		// TODO Auto-generated method stub

	}

	@Override
	public void tick(int index) {
		// TODO Auto-generated method stub

	}

	@Override
	public void tick(int index, int step) {
		// TODO Auto-generated method stub

	}

	@Override
	public Integer getExpectedSecondsUntilComplete(int index) {
		// TODO Auto-generated method stub
		return null;
	}

}
