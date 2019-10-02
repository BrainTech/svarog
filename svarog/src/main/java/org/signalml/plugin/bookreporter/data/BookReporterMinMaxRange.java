package org.signalml.plugin.bookreporter.data;

/**
 * @author piotr@develancer.pl
 */
public class BookReporterMinMaxRange {
	
	private final double minValue;
	private final double maxValue;
	
	public static final BookReporterMinMaxRange UNLIMITED = new BookReporterMinMaxRange();
	
	public BookReporterMinMaxRange() {
		this.minValue = Double.NEGATIVE_INFINITY;
		this.maxValue = Double.POSITIVE_INFINITY;
	}
	
	public BookReporterMinMaxRange(Double minValue, Double maxValue) {
		this.minValue = (minValue == null) ? Double.NEGATIVE_INFINITY : minValue;
		this.maxValue = (maxValue == null) ? Double.POSITIVE_INFINITY : maxValue;
	}
	
	public double getMax() {
		return this.maxValue;
	}
	
	public BookReporterMinMaxRange withMax(double maxValue) {
		return new BookReporterMinMaxRange(minValue, maxValue);
	}
	
	public double getMin() {
		return this.minValue;
	}
	
	public BookReporterMinMaxRange withMin(double minValue) {
		return new BookReporterMinMaxRange(minValue, maxValue);
	}
	
}
