/* AggregateTaskProgressInfo.java created 2007-10-06
 *
 */

package org.signalml.task;

/** This class provides an aggregate view of task's tickers (i.e. the total limit of smallest ticks and the total
 *  current count of smallest ticks.
 *
 * @author Michal Dobaczewski &copy; 2007-2008 CC Otwarte Systemy Komputerowe Sp. z o.o.
 */
public class AggregateTaskProgressInfo {

	private Task task;

	private int maxValue;
	private int value;

	/**
	 * Creates new instance of AggregateTaskProgressInfo for specified Task.
	 * @param task {@link Task} to create an aggregate view of its tickers
	 */
	public AggregateTaskProgressInfo(Task task) {
		this.task = task;
		update();
	}

	/**
	 * Computes the total limit of smallest ticks and the total current count of smallest ticks.
	 */
	public void update() {

		int[] limits;
		int[] values;
		int[] multipliers;
		double[] corrections;
		int i;

		synchronized (task) {
			limits = task.getTickerLimits();
			values = task.getTickers();
		}

		int cnt = Math.min(3, Math.min(limits.length, values.length));

		corrections = new double[cnt];

		maxValue = 0;
		value = 0;
		if (cnt > 0) {
			multipliers = new int[cnt];

			for (i=0; i<cnt; i++) {
				if (limits[i] > 100) {
					corrections[i] = 100.0 / ((double) limits[i]);
					limits[i] = (int) Math.round(limits[i] * corrections[i]);
				} else {
					corrections[i] = 1.0;
				}
			}

			maxValue = limits[cnt-1];
			for (i=(cnt-2); i>=0; i--) {
				multipliers[i+1] = maxValue;
				maxValue *= limits[i];
			}

			value = (int) Math.round(values[cnt-1] * corrections[cnt-1]);
			for (i=(cnt-2); i>=0; i--) {
				value += ((int) Math.round(values[i] * corrections[i])) * multipliers[i+1];
			}

		}

	}

	/**
	 * Returns the total limit of smallest ticks.
	 * @return the total limit of smallest ticks
	 */
	public int getMaxValue() {
		return maxValue;
	}

	/**
	 * Returns the total current count of smallest ticks.
	 * @return the total current count of smallest ticks
	 */
	public int getValue() {
		return value;
	}

	/**
	 * Checks if two instances of class AggregateTaskProgressInfo are equal.
	 * It compares the result of division of total current count of smallest ticks by total limit of smallest ticks.
	 * @param obj object to be compared for equality with this AggregateTaskProgressInfo
	 * @return true if obj is an instance of AggregateTaskProgressInfo equal to this AggregateTaskProgressInfo, false otherwise
	 */
	@Override
	public boolean equals(Object obj) {
		if (!(obj instanceof AggregateTaskProgressInfo)) {
			return false;
		}
		AggregateTaskProgressInfo o = (AggregateTaskProgressInfo) obj;
		float test = (((float) value) / maxValue) - (((float) o.value) / o.maxValue);
		return (test == 0);
	}

	/**
	 * Compares two instances of class AggregateTaskProgressInfo.
	 * It computes the result of division of total current count of smallest ticks by the total limit of smallest ticks for both objects.
	 * @param o AggregateTaskProgressInfo to be compared with this AggregateTaskProgressInfo
	 * @return 1 if the result of division of total current count of smallest ticks by the total limit of smallest ticks is greater for this AggregateTaskProgressInfo than for o, -1 if it is smaller and 0 if they are equal
	 */
	public int compareTo(AggregateTaskProgressInfo o) {
		float test = (((float) value) / maxValue) - (((float) o.value) / o.maxValue);
		return (test < 0 ? -1 : (test > 0 ? 1 : 0));
	}

}
