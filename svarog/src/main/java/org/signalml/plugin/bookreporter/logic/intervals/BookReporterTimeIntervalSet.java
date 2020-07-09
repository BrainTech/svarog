package org.signalml.plugin.bookreporter.logic.intervals;

import java.util.Iterator;
import java.util.TreeSet;

/**
 * @author piotr@develancer.pl
 */
public class BookReporterTimeIntervalSet {
	
	private final TreeSet<BookReporterTimeInterval> intervals; // sorted by start
	
	public BookReporterTimeIntervalSet() {
		intervals = new TreeSet<>();
	}
	
	public boolean add(BookReporterTimeInterval interval) {
		Iterator<BookReporterTimeInterval> it;
		it = intervals.tailSet(interval, true).iterator();
		while (it.hasNext()) {
			BookReporterTimeInterval sum = it.next().sum(interval);
			if (sum != null) {
				interval = sum;
				it.remove();
			} else {
				break;
			}
		}
		it = intervals.headSet(interval, false).descendingIterator();
		while (it.hasNext()) {
			BookReporterTimeInterval sum = it.next().sum(interval);
			if (sum != null) {
				interval = sum;
				it.remove();
			} else {
				break;
			}
		}
		intervals.add(interval);
		return true;
	}
	
	/**
	 * @param interval
	 * @return sum of lengths of this set's elements' parts covering given interval
	 */
	public double cover(BookReporterTimeInterval interval) {
		double sumLengths = 0.0;
		Iterator<BookReporterTimeInterval> it;
		it = intervals.tailSet(interval, true).iterator();
		while (it.hasNext()) {
			BookReporterTimeInterval intersection = it.next().intersection(interval);
			if (intersection != null) {
				sumLengths += intersection.length();
			} else {
				break;
			}
		}
		it = intervals.headSet(interval, false).descendingIterator();
		while (it.hasNext()) {
			BookReporterTimeInterval intersection = it.next().intersection(interval);
			if (intersection != null) {
				sumLengths += intersection.length();
			} else {
				break;
			}
		}
		return sumLengths;
	}

	public static void main(String[] args) {
		boolean assertsEnabled = false;
		assert assertsEnabled = true;
		
		BookReporterTimeIntervalSet test = new BookReporterTimeIntervalSet();
		BookReporterTimeInterval measure = BookReporterTimeInterval.create(0.0, 1.0);
		assert test.cover(measure) == 0.0;
		test.add(BookReporterTimeInterval.create(1.2, 1.5));
		assert test.cover(measure) == 0.0;
		test.add(BookReporterTimeInterval.create(-0.2, 0.3));
		assert test.cover(measure) == 0.3;
		test.add(BookReporterTimeInterval.create(0.6, 1.2));
		assert test.cover(measure) == 0.7;
		test.add(BookReporterTimeInterval.create(0.5, 0.8));
		assert test.cover(measure) == 0.8;
		test.add(BookReporterTimeInterval.create(-0.3, 0.4));
		assert test.cover(measure) == 0.9;
		test.add(BookReporterTimeInterval.create(-0.3, 0.5));
		assert test.cover(measure) == 1.0;
		
		if (assertsEnabled) {
			System.out.println("ALL OK");
		}
	}	
}
