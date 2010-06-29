/* ExampleData.java created 2007-09-12
 *
 */
package org.signalml.method.example;

/** ExampleData - an example data object.
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

	public ExampleData() {
		this(100, true);
	}

	public ExampleData(Integer count, boolean noWait) {
		this.count = count;
		this.noWait = noWait;
	}

	public Integer getCount() {
		return count;
	}

	public void setCount(Integer count) {
		this.count = count;
	}

	public boolean isNoWait() {
		return noWait;
	}

	public void setNoWait(boolean noWait) {
		this.noWait = noWait;
	}

	public boolean isSuspended() {
		return suspended;
	}

	public void setSuspended(boolean suspended) {
		this.suspended = suspended;
	}

	public int[] getSuspendedCounters() {
		return suspendedCounters;
	}

	public void setSuspendedCounters(int[] suspendedCounters) {
		this.suspendedCounters = suspendedCounters;
	}

	public int getSuspendedProduct() {
		return suspendedProduct;
	}

	public void setSuspendedProduct(int suspendedProduct) {
		this.suspendedProduct = suspendedProduct;
	}

}
