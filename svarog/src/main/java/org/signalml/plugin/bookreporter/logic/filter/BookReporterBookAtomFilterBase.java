package org.signalml.plugin.bookreporter.logic.filter;

import java.util.AbstractCollection;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.NoSuchElementException;
import org.signalml.plugin.bookreporter.data.BookReporterFASPThreshold;
import org.signalml.plugin.bookreporter.data.BookReporterMinMaxRange;
import org.signalml.plugin.bookreporter.data.book.BookReporterAtom;

/**
 * @author piotr@develancer.pl
 * (based on NewStagerBookAtomFilterBase)
 */
public class BookReporterBookAtomFilterBase implements IBookReporterBookAtomFilter {

	protected final BookReporterFASPThreshold threshold;
	protected IBookReporterBookAtomSelector selector;

	private BookReporterAtom atoms[];
	private Collection<BookReporterAtom> result;

	public BookReporterBookAtomFilterBase(BookReporterFASPThreshold threshold) {
		this.threshold = threshold;

		final BookReporterMinMaxRange amplitude = threshold.amplitude;
		final BookReporterMinMaxRange frequency = threshold.frequency;
		final BookReporterMinMaxRange phase = threshold.phase;
		final BookReporterMinMaxRange scale = threshold.scale;

		this.selector = new IBookReporterBookAtomSelector() {

			@Override
			public boolean matches(BookReporterAtom atom) {
				return compare(amplitude, atom.amplitude)
					&& compare(frequency, atom.frequency)
					&& compare(phase, atom.phase)
					&& compare(scale, atom.scale);
			}

			private boolean compare(BookReporterMinMaxRange thresholdRange, double atomValue) {
				return (thresholdRange.getMin() <= atomValue) && (thresholdRange.getMax() >= atomValue);
			}
		};
	}

	@Override
	public Collection<BookReporterAtom> filter(BookReporterAtom atoms[]) {
		if (this.atoms != atoms || this.result == null) {
			this.atoms = atoms;
			this.result = new ArrayList<>(this.filterWhen(atoms,
					this.selector));
		}
		return this.result;
	}

	protected Collection<BookReporterAtom> filterWhen(
			BookReporterAtom atoms[], IBookReporterBookAtomSelector selector) {

		final BookReporterAtom array[] = atoms;
		final IBookReporterBookAtomSelector atomSelector = selector;

		return new AbstractCollection<BookReporterAtom>() {

			private int collectionSize = 0;
			private boolean sizeInitialized = false;

			@Override
			public Iterator<BookReporterAtom> iterator() {
				return new Iterator<BookReporterAtom>() {

					private boolean needsAdvance = true;
					private int currentIdx = 0;

					@Override
					public void remove() {
						throw new UnsupportedOperationException();
					}

					@Override
					public BookReporterAtom next() {
						if (this.needsAdvance) {
							this.advance();
						}

						if (this.currentIdx >= array.length) {
							throw new NoSuchElementException();
						}

						this.needsAdvance = true;
						++collectionSize;

						return array[this.currentIdx++];
					}

					@Override
					public boolean hasNext() {
						if (needsAdvance) {
							this.advance();
						}

						boolean result = this.currentIdx < array.length;
						if (!result) {
							sizeInitialized = true;
						}
						return result;
					}

					private void advance() {
						while (this.currentIdx < array.length
								&& !atomSelector
										.matches(array[this.currentIdx])) {
							++this.currentIdx;
						}

						this.needsAdvance = false;
					}
				};
			}

			@Override
			public int size() {
				if (!this.sizeInitialized) {
					Iterator<BookReporterAtom> iterator = this.iterator();
					while (iterator.hasNext()) {
						iterator.next();
					}
					this.sizeInitialized = true;
				}
				return this.collectionSize;
			}
		};

	}
}
