package org.signalml.plugin.bookreporter.logic.intervals;

/**
 * @author piotr@develancer.pl
 */
public class BookReporterTimeInterval implements Comparable<BookReporterTimeInterval> {

	final double start;

	final double end;

	private BookReporterTimeInterval(double start, double end) {
		this.start = start;
		this.end = end;
	}

	@Override
	public int compareTo(BookReporterTimeInterval ti) {
		return this.start != ti.start
			? Double.compare(this.start, ti.start)
			: Double.compare(this.end, ti.end);
	}
	
	public static BookReporterTimeInterval create(double start, double end) {
		if (start <= end) {
			return new BookReporterTimeInterval(start, end);
		} else {
			throw new RuntimeException("invalid endpoints for time interval");
		}
	}

	@Override
	public boolean equals(Object o) {
		if (o instanceof BookReporterTimeInterval) {
			BookReporterTimeInterval ti = (BookReporterTimeInterval) o;
			return this.start == ti.start && this.end == ti.end;
		} else {
			return false;
		}
	}

	@Override
	public int hashCode() { // auto-generated with NetBeans 7.4
		int hash = 3;
		hash = 59 * hash + (int) (Double.doubleToLongBits(this.start) ^ (Double.doubleToLongBits(this.start) >>> 32));
		hash = 59 * hash + (int) (Double.doubleToLongBits(this.end) ^ (Double.doubleToLongBits(this.end) >>> 32));
		return hash;
	}

	public BookReporterTimeInterval intersection(BookReporterTimeInterval o) {
		double startI = Math.max(this.start, o.start);
		double endI = Math.min(this.end, o.end);
		return (startI <= endI)
			? new BookReporterTimeInterval(startI, endI) : null;
	}

	public boolean intersects(BookReporterTimeInterval o) {
		return Math.max(this.start, o.start) <= Math.min(this.end, o.end);
	}

	public double length() {
		return end - start;
	}

	public BookReporterTimeInterval sum(BookReporterTimeInterval o) {
		return this.intersects(o)
			? new BookReporterTimeInterval(
				Math.min(this.start, o.start),
				Math.max(this.end, o.end)
			) : null;
	}

	@Override
	public String toString() {
		return "[" + this.start + ";" + this.end + "]";
	}

}
