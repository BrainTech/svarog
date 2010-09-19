/* FFTSampleFilter.java created 2008-02-01
 *
 */

package org.signalml.domain.montage.filter;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;

import org.signalml.app.config.FFTWindowTypeSettings;
import org.signalml.app.config.preset.Preset;
import org.signalml.fft.WindowType;
import org.signalml.util.ResolvableString;
import org.springframework.context.MessageSourceResolvable;

import com.thoughtworks.xstream.annotations.XStreamAlias;

/** FFTSampleFilter
 *
 *
 * @author Michal Dobaczewski &copy; 2007-2008 CC Otwarte Systemy Komputerowe Sp. z o.o.
 */
@XStreamAlias("fftsamplefilter")
public class FFTSampleFilter extends SampleFilterDefinition implements Preset, FFTWindowTypeSettings {

	private static final long serialVersionUID = 1L;

	private static final Object[] ARGUMENTS = new Object[0];
	private static final String[] CODES = new String[] { "fftFilter" };
	private static final String[] EFFECT_CODES = new String[] { "fftFilter.effect" };

	private String name;
	private ArrayList<Range> ranges;

	private WindowType windowType = WindowType.RECTANGULAR;
	private double windowParameter = 0;

	private transient String effectString;

	protected FFTSampleFilter() {
	}

	public FFTSampleFilter(boolean initiallyPassing) {

		ranges = new ArrayList<Range>();
		if (initiallyPassing) {
			ranges.add(new Range(0,0,1));
		} else {
			ranges.add(new Range(0,0,0));
		}

	}

	public FFTSampleFilter(FFTSampleFilter filter) {
		this();
		copyFrom(filter);
	}

	@Override
	public String getName() {
		return name;
	}

	@Override
	public void setName(String name) {
		this.name = name;
	}

	public int getRangeCount() {
		return ranges.size();
	}

	public Range getRangeAt(int index) {
		return ranges.get(index);
	}

	public Iterator<Range> getRangeIterator() {
		return ranges.iterator();
	}

	public void removeRange(int index) {

		int size = ranges.size();
		if (size <= 1) {
			return;
		}

		Range range = ranges.get(index);
		if (index == 0) {
			// first range being removed
			Range nextRange = ranges.get(index+1);
			nextRange.lowFrequency = 0;
		}
		else if (index == size - 1) {
			// last range being removed
			Range previousRange = ranges.get(index-1);
			previousRange.highFrequency = 0;
		}
		else {
			// not first, not last, and size > 1 - must be at least 3
			Range previousRange = ranges.get(index-1);
			Range nextRange = ranges.get(index+1);
			previousRange.highFrequency = range.highFrequency;
			if (previousRange.highFrequency == nextRange.lowFrequency && previousRange.coefficient == nextRange.coefficient) {
				previousRange.highFrequency = nextRange.highFrequency;
				ranges.remove(index+1);
			}
		}

		ranges.remove(index);

	}

	public void setRange(Range range) {
		setRange(range, false);
	}

	public void setRange(Range newRange, boolean multiply) {

		ArrayList<Range> newRanges = new ArrayList<Range>();

		Iterator<Range> it = ranges.iterator();
		Range range;
		double newCoefficient;

		while (it.hasNext()) {

			range = it.next();
			if (range.intersects(newRange)) {

				if (multiply) {
					newCoefficient = range.coefficient * newRange.coefficient;
				} else {
					newCoefficient = newRange.coefficient;
				}

				if (range.lowFrequency < newRange.lowFrequency) {
					newRanges.add(new Range(range.lowFrequency, newRange.lowFrequency, range.coefficient));
				}

				if (newRange.isOpenEnded()) {
					if (!multiply || range.isOpenEnded()) {
						newRanges.add(new Range(Math.max(newRange.lowFrequency, range.lowFrequency), 0, newCoefficient));
						break;
					} else {
						newRanges.add(new Range(newRange.lowFrequency, range.highFrequency, newCoefficient));
						continue;
					}
				}

				// new is not open ended

				if (range.isOpenEnded()) {
					newRanges.add(new Range(
					                       Math.max(range.lowFrequency, newRange.lowFrequency),
					                       newRange.highFrequency,
					                       newCoefficient
					              ));

					// add the open ended rest and quit
					range.lowFrequency = newRange.highFrequency;
					range.highFrequency = 0;

					newRanges.add(range);
					break;

				} else {
					// current is also not open ended
					newRanges.add(new Range(
					                       Math.max(range.lowFrequency, newRange.lowFrequency),
					                       Math.min(range.highFrequency, newRange.highFrequency),
					                       newCoefficient
					              ));

					// add the rest of current if any, and continue
					if (range.highFrequency > newRange.highFrequency) {
						newRanges.add(new Range(newRange.highFrequency, range.highFrequency, range.coefficient));
					}

				}

			} else {
				newRanges.add(range);
			}

		}

		// now scan for adjacent ranges to merge
		Range lastRange = null;
		it = newRanges.iterator();
		while (it.hasNext()) {
			range = it.next();
			if (lastRange != null) {
				if (lastRange.highFrequency == range.lowFrequency && lastRange.coefficient == range.coefficient) {
					lastRange.highFrequency = range.highFrequency;
					it.remove();
				} else {
					lastRange = range;
				}
			} else {
				lastRange = range;
			}
		}

		ranges = newRanges;
		Collections.sort(ranges);

		effectString = null;


	}

	@Override
	public WindowType getWindowType() {
		return windowType;
	}

	@Override
	public void setWindowType(WindowType windowType) {
		if (windowType == null) {
			throw new NullPointerException("No window type");
		}
		this.windowType = windowType;
	}

	@Override
	public double getWindowParameter() {
		return windowParameter;
	}

	@Override
	public void setWindowParameter(double windowParameter) {
		this.windowParameter = windowParameter;
	}

	@Override
	public FFTSampleFilter duplicate() {

		FFTSampleFilter filter = new FFTSampleFilter();
		filter.copyFrom(this);

		return filter;

	}

	public void copyFrom(FFTSampleFilter filter) {

		ranges = new ArrayList<Range>();

		for (Range range : filter.ranges) {
			ranges.add(range.clone());
		}

		name = filter.name;
		windowType = filter.windowType;
		windowParameter = filter.windowParameter;
		description = filter.description;

		effectString = null;

	}

	public String getEffectString() {

		if (effectString == null) {

			StringBuilder sb = new StringBuilder("[");
			boolean first = true;

			Iterator<Range> it = ranges.iterator();
			Range range;
			while (it.hasNext()) {
				if (!first) {
					sb.append(", ");
				}
				range = it.next();
				sb.append('(').append(range.lowFrequency).append('-');
				if (range.highFrequency <= range.lowFrequency) {
					sb.append("Fn)");
				} else {
					sb.append(range.highFrequency).append(')');
				}
				sb.append('=').append(range.coefficient);
				first = false;
			}

			effectString = sb.append(']').toString();

		}

		return effectString;

	}

	@Override
	public String getDefaultEffectDescription() {
		return "FFT: " + getEffectString();
	}

	@Override
	public SampleFilterType getType() {
		return SampleFilterType.FFT;
	}

	@Override
	public Object[] getArguments() {
		return ARGUMENTS;
	}

	@Override
	public String[] getCodes() {
		return CODES;
	}

	@Override
	public String getDefaultMessage() {
		return "FFT filter";
	}

	@Override
	public MessageSourceResolvable getEffectDescription() {
		return new ResolvableString(EFFECT_CODES, new Object[] { getEffectString() }, getDefaultEffectDescription());
	}

	@Override
	public String toString() {
		return name;
	}

	@XStreamAlias("range")
	public class Range implements Comparable<Range>, Cloneable, Serializable {

		private static final long serialVersionUID = 1L;

		// note: high <= low denotes "up to sf/2" or "up to inf"
		private float lowFrequency; // inclusive
		private float highFrequency; // exclusive

		private double coefficient;

		public Range() {};

		public Range(float lowFrequency, float highFrequency, double coefficient) {
			this.lowFrequency = lowFrequency;
			this.highFrequency = highFrequency;
			this.coefficient = coefficient;
		}

		public float getLowFrequency() {
			return lowFrequency;
		}

		public float getHighFrequency() {
			return highFrequency;
		}

		public double getCoefficient() {
			return coefficient;
		}

		public boolean isOpenEnded() {
			return(highFrequency <= lowFrequency);
		}

		public boolean intersects(Range range) {
			if (lowFrequency <= range.lowFrequency) {
				if (highFrequency <= lowFrequency) {
					return true;
				} else {
					if (range.lowFrequency < highFrequency) {
						return true;
					}
				}
			} else {
				if (range.highFrequency <= range.lowFrequency) {
					return true;
				} else {
					if (lowFrequency < range.highFrequency) {
						return true;
					}
				}
			}

			return false;
		}

		@Override
		public int compareTo(Range o) {
			return (int) (this.lowFrequency - o.lowFrequency);
		}

		@Override
		protected Range clone() {
			return new Range(lowFrequency, highFrequency, coefficient);
		}

	}

	/**
	 * Checks if the object o is an instance of FFTSampleFilter and if so
	 * checks if ranges (and appriopriate coefficients) and window types are
	 * equal.
	 * @param o object to compare with the FFTSampleFilter
	 * @return true if the Object o is an FFTSampleFilter with equal ranges
	 * and window types as this FFTSamplesFilter, false otherwise.
	 */
	@Override
	public boolean equals(Object o) {

		if (!(o instanceof FFTSampleFilter))
			return false;

		FFTSampleFilter fft = (FFTSampleFilter)o;

		Iterator<Range> it1, it2;
		Range r1, r2;

		it1 = fft.getRangeIterator();
		it2 = getRangeIterator();
		while (it1.hasNext() && it2.hasNext()) {
			r1 = it1.next();
			r2 = it2.next();
			if (r1.compareTo(r2) != 0)
				return false;
		}
		if (it1.hasNext() || it2.hasNext())
			return false;

		if (!this.windowType.equals(windowType) || this.windowParameter!=fft.windowParameter)
			return false;

		return true;

	}

}
