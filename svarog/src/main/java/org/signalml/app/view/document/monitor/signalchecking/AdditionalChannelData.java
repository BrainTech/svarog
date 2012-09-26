package org.signalml.app.view.document.monitor.signalchecking;

/**
 * Object containing some additional channel data - maximum value of some
 * variable, the limit value, and current value. Also, the method which was
 * used to determine the state is kept here.
 *
 * @author Tomasz Sawicki
 */
class AdditionalChannelData {

	/**
	 * Maximum value.
	 */
	private double max;
	/**
	 * Minimum value.
	 */
	private double min;
	/**
	 * Limit value.
	 */
	private double limit;
	/**
	 * Current value.
	 */
	private double current;
	/**
	 * {@link SignalCheckingMethod}.
	 */
	private SignalCheckingMethod method;

	/**
	 * Returns the current value.
	 * @return the current value
	 */
	public double getCurrent() {
		return current;
	}

	/**
	 * Returns the limit value.
	 * @return the limit value
	 */
	public double getLimit() {
		return limit;
	}

	/**
	 * Returns the maximum value.
	 * @return the maximum value
	 */
	public double getMax() {
		return max;
	}

	/**
	 * Returns the minimum value.
	 * @return the minimum value
	 */
	public double getMin() {
		return min;
	}

	/**
	 * Returns method.
	 * @return method
	 */
	public SignalCheckingMethod getMethod() {
		return method;
	}

	/**
	 * Default constructor.
	 * @param max {@link #max}
	 * @param limit {@link #limit}
	 * @param current {@link #current}
	 * @param method {@link #method}
	 */
	public AdditionalChannelData(double max, double min, double limit, double current, SignalCheckingMethod method) {
		this.max = max;
		this.min = min;
		this.limit = limit;
		this.current = current;
		this.method = method;
	}
}

