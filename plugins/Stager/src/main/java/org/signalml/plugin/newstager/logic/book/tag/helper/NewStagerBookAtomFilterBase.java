package org.signalml.plugin.newstager.logic.book.tag.helper;

import java.util.AbstractCollection;
import java.util.Collection;
import java.util.Iterator;
import java.util.NoSuchElementException;

import org.signalml.plugin.newstager.data.book.NewStagerAdaptedAtom;
import org.signalml.plugin.newstager.data.tag.NewStagerBookAtomFilterData;
import org.signalml.util.MinMaxRange;

import com.google.common.collect.Lists;

public class NewStagerBookAtomFilterBase implements
		INewStagerBookAtomFilter {

	protected NewStagerBookAtomFilterData data;
	protected INewStagerBookAtomSelector selector;

	private NewStagerAdaptedAtom atoms[];
	private Collection<NewStagerAdaptedAtom> result;


	public NewStagerBookAtomFilterBase(NewStagerBookAtomFilterData data) {
		this.data = data;

		final MinMaxRange amplitude = data.threshold.amplitude;
		final MinMaxRange frequency = data.threshold.frequency;
		final MinMaxRange phase = data.threshold.phase;
		final MinMaxRange scale = data.threshold.scale;

		this.selector = new INewStagerBookAtomSelector() {

			@Override
			public boolean matches(NewStagerAdaptedAtom atom) {
				return (amplitude == null || (atom.amplitude >= amplitude
						.getMin() && atom.amplitude <= amplitude.getMax()))
						&& (frequency == null || (atom.frequency >= frequency
								.getMin() && atom.frequency <= frequency
								.getMax()))
						&& (phase == null || (atom.phase <= phase.getMin() || atom.phase >= phase
								.getMax()))
						&& (scale == null || (atom.scale >= scale.getMin() && atom.scale <= scale
								.getMax()));
			}
		};
	}

	@Override
	public Collection<NewStagerAdaptedAtom> filter(NewStagerAdaptedAtom atoms[]) {
		if (this.atoms != atoms || this.result == null) {
			this.atoms = atoms;
			this.result = Lists.newArrayList(this.filterWhen(atoms, this.selector));
		}
		return this.result;
	}

	protected Collection<NewStagerAdaptedAtom> filterWhen(
			NewStagerAdaptedAtom atoms[], INewStagerBookAtomSelector selector) {

		final NewStagerAdaptedAtom array[] = atoms;
		final INewStagerBookAtomSelector atomSelector = selector;

		return new AbstractCollection<NewStagerAdaptedAtom>() {

			private int collectionSize = 0;
			private boolean sizeInitialized = false;

			@Override
			public Iterator<NewStagerAdaptedAtom> iterator() {
				return new Iterator<NewStagerAdaptedAtom>() {

					private boolean needsAdvance = true;
					private int currentIdx = 0;

					@Override
					public void remove() {
						throw new UnsupportedOperationException();
					}

					@Override
					public NewStagerAdaptedAtom next() {
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
					Iterator<NewStagerAdaptedAtom> iterator = this.iterator();
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
