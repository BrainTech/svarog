/* MinMaxRangeInteger.java created 2008-02-25
 * 
 */

package org.signalml.util;

import java.io.Serializable;

/** MinMaxRangeInteger
 *
 * 
 * @author Michal Dobaczewski &copy; 2007-2008 CC Otwarte Systemy Komputerowe Sp. z o.o.
 */
public class MinMaxRangeInteger implements Serializable {

	private int unlimitedValue;

	private static final long serialVersionUID = 1L;
	
	private int min;
	private int max;
	
	private boolean minUnlimited;
	private boolean maxUnlimited;
	
	public MinMaxRangeInteger(int unlimitedValue) {
		this.unlimitedValue = unlimitedValue;
	}
		
	public MinMaxRangeInteger(int unlimitedValue, int min, int max, boolean minUnlimited, boolean maxUnlimited) {
		this.unlimitedValue = unlimitedValue;
		this.min = min;
		this.max = max;
		this.minUnlimited = minUnlimited;
		this.maxUnlimited = maxUnlimited;
	}

	public MinMaxRangeInteger(int unlimitedValue, boolean unlimited) {
		this.unlimitedValue = unlimitedValue;
		if( unlimited ) {
			minUnlimited = true;
			maxUnlimited = true;
		}
	}
	
	public MinMaxRangeInteger( MinMaxRangeInteger template ) {
		this.unlimitedValue = template.unlimitedValue;
		this.min = template.min;
		this.max = template.max;
		this.minUnlimited = template.minUnlimited;
		this.maxUnlimited = template.maxUnlimited;
	}
	
	public int getMin() {
		return min;
	}
	
	public int getMinWithUnlimited() {
		return ( minUnlimited ? unlimitedValue : min );
	}
		
	public void setMin(int min) {
		this.min = min;
	}
	
	public void setMinWithUnlimited(int min) {
		if( min < 0 ) {
			minUnlimited = true;
		} else {
			minUnlimited = false;
			this.min = min; 
		}
	}

	public int getMax() {
		return max;
	}
	
	public int getMaxWithUnlimited() {
		return ( maxUnlimited ? unlimitedValue : max );
	}
	
	public void setMax(int max) {
		this.max = max;
	}
	
	public void setMaxWithUnlimited(int max) {
		if( max < 0 ) { 
			maxUnlimited = true;
		} else {
			maxUnlimited = false;
			this.max = max;
		}
	}
	
	public boolean isMinUnlimited() {
		return minUnlimited;
	}
	
	public void setMinUnlimited(boolean minUnlimited) {
		this.minUnlimited = minUnlimited;
	}
	
	public boolean isMaxUnlimited() {
		return maxUnlimited;
	}
	
	public void setMaxUnlimited(boolean maxUnlimited) {
		this.maxUnlimited = maxUnlimited;
	}
	
	public void normalize() {
		if( !minUnlimited && !maxUnlimited ) {
			if( min > max ) {
				int temp = min;
				min = max;
				max = temp;
			}	
		}
	}
	
	public boolean isInRangeInclusive( int value ) {
		if( !minUnlimited && (value < min) ) {
			return false;
		}
		if( !maxUnlimited && (value > max) ) {
			return false;
		}
		return true;
	}

	/**
	 * @return the unlimitedValue
	 */
	public int getUnlimitedValue() {
		return unlimitedValue;
	}

	/**
	 * @param unlimitedValue the unlimitedValue to set
	 */
	public void setUnlimitedValue(int unlimitedValue) {
		this.unlimitedValue = unlimitedValue;
	}
		
}
