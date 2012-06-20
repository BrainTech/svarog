package org.signalml.util;

/** An <b>unsynchronized</b> mutable int wrapper. */
public class FastMutableInt {
	private int k;

	public FastMutableInt() {
		k = 0;
	}

	public FastMutableInt(int i) {
		this.k = i;
	}

	public boolean isZero() {
		return (0 == k);
	}

	public boolean isPositive() {
		return (1 <= k);
	}

	public boolean isGE2() {
		return (2 <= k);
	}

	public int getValue() {
		return k;
	}
	public void setValue(int i) {
		this.k = i;
	}

	public void inc() {
		++k;
	}
	public void dec() {
		--k;
	}

	public void add(int i) {
		k += i;
	}
	public void sub(int i) {
		k -= i;
	}

	public String toString() {
		return Integer.toString(k);
	}
}
