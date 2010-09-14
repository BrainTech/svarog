/* SampleFilter.java created 2007-09-24
 *
 */

package org.signalml.domain.signal;

import java.beans.PropertyChangeEvent;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.ListIterator;
import java.util.Vector;

import org.apache.log4j.Logger;
import org.signalml.domain.montage.Montage;
import org.signalml.domain.montage.MontageMismatchException;
import org.signalml.domain.montage.filter.FFTSampleFilter;
import org.signalml.domain.montage.filter.SampleFilterDefinition;
import org.signalml.domain.montage.filter.FFTSampleFilter.Range;

/**
 * This class represents a filter of samples for multichannel signal.
 * It contains the list of {@link SampleFilterEngine engines} for every channel
 * and uses them to filter samples.
 *
 * @author Michal Dobaczewski &copy; 2007-2008 CC Otwarte Systemy Komputerowe Sp. z o.o.
 */
public class MultichannelSampleFilter extends MultichannelSampleProcessor {

	protected static final Logger logger = Logger.getLogger(MultichannelSampleFilter.class);

        /**
         * a vector that at each index (being also the index of a channel) holds
         * a chain (list) of {@link SampleFilterEngine engines}.
         */
	private Vector<LinkedList<SampleFilterEngine>> chains;

        /**
         * the {@link Montage montage} with which this filter is associated
         */
	private Montage currentMontage = null;

        /**
         * Constructor. Creates an empty sample filter using a given source
         * @param source the source of samples
         */
	public MultichannelSampleFilter(MultichannelSampleSource source) {
		super(source);
		reinitFilterChains();
	}

        /**
         * Returns the given number of filtered samples for a given channel
         * starting from a given position in time.
         * Filtering is done by the last of {@link SampleFilterEngine engines}
         * associated with a given channel.
         * @param channel the number of channel
         * @param target the array to which results will be written starting
         * from position <code>arrayOffset</code>
         * @param signalOffset the position (in time) in the signal starting
         * from which samples will be returned
         * @param count the number of samples to be returned
         * @param arrayOffset the offset in <code>target</code> array starting
         * from which samples will be written
         */
	@Override
	public void getSamples(int channel, double[] target, int signalOffset, int count, int arrayOffset) {

		LinkedList<SampleFilterEngine> chain = chains.get(channel);
		if (chain.isEmpty()) {
			source.getSamples(channel, target, signalOffset, count, arrayOffset);
		} else {
			ListIterator<SampleFilterEngine> it = chain.listIterator(chain.size());
			SampleFilterEngine last = it.previous();
			last.getSamples(target, signalOffset, count, arrayOffset);
		}

	}

        /**
         * Adds the filter {@link SampleFilterEngine engine} for all
         * channels.
         * @param filter the filter engine to be added
         */
	public void addFilter(SampleFilterEngine filter) {
		int cnt = chains.size();
		LinkedList<SampleFilterEngine> chain;
		for (int i=0; i<cnt; i++) {
			chain = chains.get(i);
			chain.add(filter);
		}
	}

        /**
         * Adds the filter {@link SampleFilterEngine engine} for specified
         * channels.
         * @param filter the filter engine to be added
         * @param channels array of indexes of channels for which filter engine
         * is to be added
         */
	public void addFilter(SampleFilterEngine filter, int[] channels) {
		LinkedList<SampleFilterEngine> chain;
		for (int i=0; i<channels.length; i++) {
			chain = chains.get(channels[i]);
			chain.add(filter);
		}
	}

        /**
         * Adds the filter {@link SampleFilterEngine engine} for the
         * specified channel.
         * @param filter the filter engine to be added
         * @param channel the number of the channel for which filter engine
         * is to be added
         */
	public void addFilter(SampleFilterEngine filter, int channel) {
		LinkedList<SampleFilterEngine> chain;
		chain = chains.get(channel);
		chain.add(filter);
	}

	@Override
	public void propertyChange(PropertyChangeEvent evt) {
		if (evt.getSource() == source) {
			if (evt.getPropertyName().equals(CHANNEL_COUNT_PROPERTY)) {
				reinitFilterChains();
			}
		}
		super.propertyChange(evt);
	}

        /**
         * Clears the list of filter {@link SampleFilterEngine engines}.
         * It is done by creating new and replacing.
         */
	public void reinitFilterChains() {

		int cnt = source.getChannelCount();
		chains = new Vector<LinkedList<SampleFilterEngine>>(cnt);
		LinkedList<SampleFilterEngine> chain;
		for (int i=0; i<cnt; i++) {
			chain = new LinkedList<SampleFilterEngine>();
			chains.add(chain);
		}

	}

        /**
         * Returns the {@link Montage montage} associated with this sample filter
         * @return the montage associated with this sample filter
         */
	public Montage getCurrentMontage() {
		return currentMontage;
	}

        /**
         * Sets the {@link Montage montage} to be associated with this sample
         * filter
         * @param currentMontage the montage which will be associated with this
         * sample filter
         * @throws MontageMismatchException
         */
	public void setCurrentMontage(Montage currentMontage) throws MontageMismatchException {
		try {
			applyMontage(currentMontage);
		} catch (MontageMismatchException ex) {
			logger.error("Failed to apply montage", ex);
			this.currentMontage = null;
			reinitFilterChains();
			throw ex;
		}
		this.currentMontage = currentMontage;
	}

        //TODO when exception is thrown? I can't find the place....
        /**
         * Clears the filter {@link SampleFilterEngine engines} and initialises
         * them creating {@link FFTSampleFilterEngine FFT filter engines}
         * based on {@link FFTSampleFilter FFT filters} from a given montage.
         * @param montage the montage used to create new engines
         * @throws MontageMismatchException
         */
	private void applyMontage(Montage montage) throws MontageMismatchException {

		reinitFilterChains();
		if (!montage.isFilteringEnabled()) {
			return;
		}

		int channelCount = montage.getMontageChannelCount();

		FFTSampleFilter[] summaryFFTFilters = new FFTSampleFilter[channelCount];
		FFTSampleFilter fftFilter;

		int filterCount = montage.getSampleFilterCount();
		SampleFilterDefinition[] definitions = new SampleFilterDefinition[filterCount];
		int i;
		int e;
		Iterator<Range> it;
		Range range;

		for (i=0; i<filterCount; i++) {
			definitions[i] = montage.getSampleFilterAt(i);
			if (definitions[i] instanceof FFTSampleFilter) {

				fftFilter = (FFTSampleFilter) definitions[i];
				it = fftFilter.getRangeIterator();
				while (it.hasNext()) {
					range = it.next();

					for (e=0; e<channelCount; e++) {
						if (!montage.isFilteringExcluded(i, e)) {
							if (summaryFFTFilters[e] == null) {
								summaryFFTFilters[e] = new FFTSampleFilter(true);
							}
							summaryFFTFilters[e].setRange(range, true);
						}
					}

				}

			}
		}

		for (e=0; e<channelCount; e++) {
			if (summaryFFTFilters[e] != null) {
				addFilter(new FFTSampleFilterEngine(new ChannelSelectorSampleSource(source,e), summaryFFTFilters[e]), e);
			}
		}

	}

}
