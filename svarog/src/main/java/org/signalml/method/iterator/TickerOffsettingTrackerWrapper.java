/* TickerOffsettingTrackerWrapper.java created 2007-12-05
 *
 */

package org.signalml.method.iterator;

import java.util.Arrays;

import org.signalml.method.MethodExecutionTracker;

/** TickerOffsettingTrackerWrapper
 *
 *
 * @author Michal Dobaczewski &copy; 2007-2008 CC Otwarte Systemy Komputerowe Sp. z o.o.
 */
public class TickerOffsettingTrackerWrapper implements MethodExecutionTracker {

	private MethodExecutionTracker wrappedTracker;
	private int offset;

	public TickerOffsettingTrackerWrapper(MethodExecutionTracker wrappedTracker) {
		this(wrappedTracker, 1);
	}

	public TickerOffsettingTrackerWrapper(MethodExecutionTracker wrappedTracker, int offset) {
		if (wrappedTracker == null) {
			throw new NullPointerException("No wrapped tracker");
		}
		if (offset <= 0) {
			throw new IllegalArgumentException("Bad offset [" + offset + "]");
		}
		this.wrappedTracker = wrappedTracker;
		this.offset = offset;
	}

	public MethodExecutionTracker getWrappedTracker() {
		return wrappedTracker;
	}

	public int getOffset() {
		return offset;
	}

	@Override
	public String getMessage() {
		return wrappedTracker.getMessage();
	}

	@Override
	public void setMessage(String message) {
		wrappedTracker.setMessage(message);
	}

	@Override
	public boolean isRequestingAbort() {
		return wrappedTracker.isRequestingAbort();
	}

	@Override
	public boolean isRequestingSuspend() {
		return wrappedTracker.isRequestingSuspend();
	}

	@Override
	public Integer getExpectedSecondsUntilComplete(int index) {
		return wrappedTracker.getExpectedSecondsUntilComplete(index+offset);
	}

	@Override
	public int[] getTickerLimits() {
		int[] limits = wrappedTracker.getTickerLimits();
		return Arrays.copyOfRange(limits, offset, limits.length);
	}

	@Override
	public int[] getTickers() {
		int[] tickers = wrappedTracker.getTickers();
		return Arrays.copyOfRange(tickers, offset, tickers.length);
	}

	@Override
	public void resetTickers() {
		int[] tickers = wrappedTracker.getTickers();
		Arrays.fill(tickers, offset, tickers.length, 0);
		wrappedTracker.setTickers(tickers);
	}

	@Override
	public void setTicker(int index, int value) {
		wrappedTracker.setTicker(index+offset, value);
	}

	@Override
	public void setTickerLimit(int index, int limit) {
		wrappedTracker.setTickerLimit(index+offset, limit);
	}

	@Override
	public void setTickerLimits(int[] initial) {
		int[] limits = wrappedTracker.getTickerLimits();
		for (int i=0; (i<initial.length && (i+offset)<limits.length); i++) {
			limits[offset+i] = initial[i];
		}
		wrappedTracker.setTickerLimits(limits);
	}

	@Override
	public void setTickers(int[] current) {
		int[] tickers = wrappedTracker.getTickers();
		for (int i=0; (i<current.length && (i+offset)<tickers.length); i++) {
			tickers[offset+i] = current[i];
		}
		wrappedTracker.setTickers(tickers);
	}

	@Override
	public void tick(int index, int step) {
		wrappedTracker.tick(index+offset, step);
	}

	@Override
	public void tick(int index) {
		wrappedTracker.tick(index+offset);
	}

}
