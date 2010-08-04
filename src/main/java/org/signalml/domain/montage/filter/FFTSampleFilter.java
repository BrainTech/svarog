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

/**
 * This class holds a representation of FFT sample filter.
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

        /**
         * the name of this filter
         */
	private String name;

        /**
         * an array of frequencies ranges with given coefficients
         */
	private ArrayList<Range> ranges;

        /**
         * the {@link WindowType type} of a window
         */
	private WindowType windowType = WindowType.RECTANGULAR;

        /**
         * parameter for Gaussian and Kaiser window
         */
	private double windowParameter = 0;

        /**
         * the string with the description of filters effect
         */
	private transient String effectString;

        /**
         * Constructor. Creates an empty filter.
         */
	protected FFTSampleFilter() {
	}

        /**
         * Constructor. Creates a filter which should initially pass or block
         * a signal.
         * @param initiallyPassing true if a signal should be passed,
         * false otherwise
         */
	public FFTSampleFilter(boolean initiallyPassing) {

		ranges = new ArrayList<Range>();
		if (initiallyPassing) {
			ranges.add(new Range(0,0,1));
		} else {
			ranges.add(new Range(0,0,0));
		}

	}

        /**
         * Copy constructor.
         * @param filter the filter to be copied
         */
	public FFTSampleFilter(FFTSampleFilter filter) {
		this();
		copyFrom(filter);
	}

        /**
         * Returns the name of this filter.
         * @return the name of this filter
         */
	@Override
	public String getName() {
		return name;
	}

        /**
         * Sets the name of this filter.
         * @param name the name to be set
         */
	@Override
	public void setName(String name) {
		this.name = name;
	}

        /**
         * Returns the number of ranges.
         * @return the number of ranges
         */
	public int getRangeCount() {
		return ranges.size();
	}

        /**
         * Returns a range of a given index.
         * @param index index of a range
         * @return a range of a given index
         */
	public Range getRangeAt(int index) {
		return ranges.get(index);
	}

        /**
         * Returns an iterator over a ranges list.
         * @return an iterator over a ranges list
         */
	public Iterator<Range> getRangeIterator() {
		return ranges.iterator();
	}

        /**
         * Removes a range of a specified index.
         * Adjusts other ranges to fill entire interval [0,Fn) by expanding
         * previous range (except removing first range, when second is expanded).
         * If ranges before and after has the same coefficient they are merged.
         * @param index index of a range to be removed
         */
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
		} else {
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

        /**
         * Adds a given range to ranges arrays.
         * It replaces all ranges that are included in a given range and
         * shortens ranges that intersect with given.
         * @param range a range to be added
         */
	public void setRange(Range range) {
		setRange(range, false);
	}

        /**
         * Adds a given range to ranges arrays.
         * If multiply is not set all ranges that are included in a given range
         * are replaced and all that intersect with given are shortened.
         * If multiply is set coefficients of parts of ranges that intersect
         * with given are multiplied by coefficient of given range.
         * @param newRange a range to be added
         * @param multiply true if coefficients should be multiplied, false if
         * they should be replaced
         */
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

        /**
         * Returns the type of the window.
         * @return the type of the window
         */
	@Override
	public WindowType getWindowType() {
		return windowType;
	}

        /**
         * Sets the type of the window.
         * @param windowType the type of the window to be set
         */
	@Override
	public void setWindowType(WindowType windowType) {
		if (windowType == null) {
			throw new NullPointerException("No window type");
		}
		this.windowType = windowType;
	}

        /**
         * Returns the parameter of a window.
         * @return the parameter of a window
         */
	@Override
	public double getWindowParameter() {
		return windowParameter;
	}

        /**
         * Sets the parameter of a window.
         * @param windowParameter  the parameter of a window to be set
         */
	@Override
	public void setWindowParameter(double windowParameter) {
		this.windowParameter = windowParameter;
	}

        /**
         * Creates a copy of this filter.
         * @return a copy of this filter.
         */
	@Override
	public FFTSampleFilter duplicate() {

		FFTSampleFilter filter = new FFTSampleFilter();
		filter.copyFrom(this);

		return filter;

	}

        /**
         * Sets all parameters of this filter to values of
         * parameters of a given filter.
         * @param filter a filter which parameters are to be copied
         * to this filter
         */
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

        /**
         * Creates a string with a description of an effect of this filter.
         * Consists of a list of ranges intervals and coefficients.
         * @return a string with a description of an effect of a filter.
         */
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

        /**
         * This class represents a left-closed interval (range) of frequencies.
         * Allows to compare ranges (using left end of interval) and
         * to check whether they intersect.
         */
	@XStreamAlias("range")
	public class Range implements Comparable<Range>, Cloneable, Serializable {

		private static final long serialVersionUID = 1L;

		// note: high <= low denotes "up to sf/2" or "up to inf"
                /**
                 * left end of frequencies interval - inclusive
                 */
		private float lowFrequency; // inclusive
                /**
                 * right end of frequencies interval - exclusive.
                 * If <= lowFrequency then interval is not right-bounded
                 * (or bounded by sf/2).
                 */
		private float highFrequency; // exclusive

		private double coefficient;

                /**
                 * Creates a range without any data
                 */
		public Range() {};

                /**
                 * Creates a range of frequencies with given ends and coefficient.
                 * @param lowFrequency the left end of frequencies interval
                 * @param highFrequency the right end of frequencies interval
                 * @param coefficient
                 */
		public Range(float lowFrequency, float highFrequency, double coefficient) {
			this.lowFrequency = lowFrequency;
			this.highFrequency = highFrequency;
			this.coefficient = coefficient;
		}

                /**
                 * Returns the left end of frequencies interval.
                 * @return the left end of frequencies interval
                 */
		public float getLowFrequency() {
			return lowFrequency;
		}

                /**
                 * Returns the right end of frequencies interval.
                 * @return the right end of frequencies interval
                 */
		public float getHighFrequency() {
			return highFrequency;
		}

                /**
                 * Returns the coefficient.
                 * @return the coefficient
                 */
		public double getCoefficient() {
			return coefficient;
		}

                /**
                 * Returns whether interval (range) is right-bounded.
                 * @return false if interval is right-bounded, true otherwise
                 */
		public boolean isOpenEnded() {
			return(highFrequency <= lowFrequency);
		}

                /**
                 * Returns whether this range intersects with given.
                 * @param range range to be intersected with this range
                 * @return true if intersection is nonempty, false otherwise
                 */
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

                /**
                 * Compares this range to given.
                 * @param o range to be compared with this range
                 * @return difference between left ends of interval
                 * (current - given)
                 */
		@Override
		public int compareTo(Range o) {
			return (int)(this.lowFrequency - o.lowFrequency);
		}

                /**
                 * Creates a copy of this range.
                 * @return a copy of this range
                 */
		@Override
		protected Range clone() {
			return new Range(lowFrequency, highFrequency, coefficient);
		}

	}

}
