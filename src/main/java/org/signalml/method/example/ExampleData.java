/* ExampleData.java created 2007-09-12
 *
 */
package org.signalml.method.example;

import org.signalml.method.Method;
import org.signalml.method.MethodExecutionTracker;

/**
 * ExampleData - an example data object.
 *
 *
 * @author Michal Dobaczewski &copy; 2007-2008 CC Otwarte Systemy Komputerowe Sp. z o.o.
 */
public class ExampleData {

	private Integer count;
	private boolean noWait;

	private boolean suspended;

	private int[] suspendedCounters;
	private int suspendedProduct;

        /**
         * Construct new not waiting ExampleData with count set to 100.
         */
	public ExampleData() {
		this(100, true);
	}

        /**
         * Constructs new ExampleData with specified count and
         * boolean value saying if data should wait before execution
         * @param count number of iterations while executing in
	 * {@link Method#compute(Object, MethodExecutionTracker)} method in
	 * {@link ExampleMethod} class
         * @param noWait true if this {@link Method#compute(Object, MethodExecutionTracker)}
	 * method in {@link ExampleMethod} class should wait before execution, otherwise false
         */
	public ExampleData(Integer count, boolean noWait) {
		this.count = count;
		this.noWait = noWait;
	}

        /**
         * Returns the number of iterations while executing in
         * {@link Method#compute(Object, MethodExecutionTracker)} method in
	 * {@link ExampleMethod} class
         * @return count data count
         */
	public Integer getCount() {
		return count;
	}

        /**
         * Sets the number of iterations while executing in
         * {@link Method#compute(Object, MethodExecutionTracker)}
	 * method in {@link ExampleMethod} class
         * @param count Integer to be set as this EmapleData count
         */
	public void setCount(Integer count) {
		this.count = count;
	}

        /**
         * Checks if {@link Method#compute(Object, MethodExecutionTracker)} method
	 * in {@link ExampleMethod} class should wait before execution
         * @return false if this method should wait, otherwise true
         */
	public boolean isNoWait() {
		return noWait;
	}

        /**
         * Set boolean value saying if {@link Method#compute(Object, MethodExecutionTracker)}
	 * method in {@link ExampleMethod} class should wait before execution
         * @param noWait boolean value saying if this method should wait before execution
         */
	public void setNoWait(boolean noWait) {
		this.noWait = noWait;
	}

        /**
         * Checks if this ExampleData is suspended
         * @return true if this ExampleData is suspended, otherwise false
         */
	public boolean isSuspended() {
		return suspended;
	}

        /**
         * Sets this ExampleData as suspended if specified boolean
         * value is true, otherwise as not suspended.
         * @param suspended if true then this ExampleData becomes
         * suspended, otherwise it becomes not suspended
         */
	public void setSuspended(boolean suspended) {
		this.suspended = suspended;
	}

        /**
         * Returns array with two elements which say how much iterations
         * of two main loops in {@link Method#compute(Object, MethodExecutionTracker)}
         * method in {@link ExampleMethod} class hass been done.
         * @return progress of computation result
         */
	public int[] getSuspendedCounters() {
		return suspendedCounters;
	}

        /**
         * Sets array of counters which say how much iterations
         * of two main loops in {@link Method#compute(Object, MethodExecutionTracker)}
         * method in {@link ExampleMethod} class hass been done.
         *
         * @param suspendedCounters new counters
         */
	public void setSuspendedCounters(int[] suspendedCounters) {
		this.suspendedCounters = suspendedCounters;
	}

        /**
         * Returns product which is computed during execution of
         * {@link Method#compute(Object, MethodExecutionTracker)}
         * method in {@link ExampleMethod} class.
         *
         * @return product computed before suspension
         */
	public int getSuspendedProduct() {
		return suspendedProduct;
	}

        /**
         * Sets product which is computed during execution of
         * {@link Method#compute(Object, MethodExecutionTracker)}
         * method in {@link ExampleMethod} class.
         */
	public void setSuspendedProduct(int suspendedProduct) {
		this.suspendedProduct = suspendedProduct;
	}

}
