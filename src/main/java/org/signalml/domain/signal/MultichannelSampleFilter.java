/* MultichannelSampleFilter.java created 2007-09-24
 *
 */

package org.signalml.domain.signal;

import java.beans.PropertyChangeEvent;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.ListIterator;
import java.util.Vector;
import java.util.concurrent.Semaphore;
import java.util.logging.Level;

import org.apache.log4j.Logger;
import org.signalml.domain.montage.Montage;
import org.signalml.domain.montage.MontageMismatchException;
import org.signalml.domain.montage.filter.FFTSampleFilter;
import org.signalml.domain.montage.filter.SampleFilterDefinition;
import org.signalml.domain.montage.filter.FFTSampleFilter.Range;
import org.signalml.domain.montage.filter.TimeDomainSampleFilter;

/**
 * This abstract class represents a filter of samples for multichannel signal.
 * It contains the list of {@link SampleFilterEngine engines} for every channel
 * and uses them to filter samples.
 *
 * @author Michal Dobaczewski &copy; 2007-2008 CC Otwarte Systemy Komputerowe Sp. z o.o.
 */
public class MultichannelSampleFilter extends MultichannelSampleProcessor {

	protected OriginalMultichannelSampleSource originalSource;

	protected static final Logger logger = Logger.getLogger(MultichannelSampleFilter.class);

        /**
         * a vector that at each index (being also the index of a channel) holds
         * a chain (list) of {@link SampleFilterEngine engines}.
         */
	protected Vector<LinkedList<SampleFilterEngine>> chains;

        /**
         * the {@link Montage montage} with which this filter is associated
         */
	private Montage currentMontage = null;

	/**
	 * A boolean indicating if montage has just changed.
	 * For the use of {@link MultichannelSampleFilter#updateTimeDomainSampleFilterEnginesCache()}.
	 */
	private boolean newMontage = false;

	/**
	 * A semaphore to prevent using getSamples() and
	 * applyMontage() simultaneously in a multithreading enviroment.
	 */
	private Semaphore semaphore = new Semaphore(1);

	/**
	 * Constructor. Creates an empty sample filter using a given source.
	 * @param source the source of samples
	 */
	public MultichannelSampleFilter(MultichannelSampleSource source) {
		super(source);
		reinitFilterChains();
	}

	/**
	 * Constructor. Creates an empty sample filter using a given source and
	 * a given original sample source. Original source of samples is used to
	 * detect changes in original source of signal (i.e. new samples) which
	 * should be used to update the cached filtered signal which is stored
	 * in {@link TimeDomainSampleFilterEngine TimeDomainSampleFilterEngines}.
	 * This is useful if the originalSource is an instance of
	 * {@link ChangeableMultichannelSampleSource}.
	 * @param source the source of samples
	 * @param originalSource the original source of samples
	 */
	public MultichannelSampleFilter(MultichannelSampleSource source, OriginalMultichannelSampleSource originalSource) {
		this(source);
		this.originalSource = originalSource;
	}

	/**
	 * Updates the filtered signal cache in all
	 * {@link TimeDomainSampleFilterEngine TimeDomainSampleFilterEngines}
	 * in the MultichannelSampleFilter.
	 *
	 * @param samplesAdded how many new samples were added to the originalSource
	 * since the last time this method was evoked.
	 */
	private void updateTimeDomainSampleFilterEnginesCache(int samplesAdded) {

		SampleFilterEngine eng;
		Iterator<SampleFilterEngine> it;
		for (int i = 0; i < chains.size(); i++) {

			it = (chains.get(i)).iterator();
			while (it.hasNext()) {
				eng = it.next();
				if (eng instanceof TimeDomainSampleFilterEngine) {
					((TimeDomainSampleFilterEngine)eng).updateCache(samplesAdded);
				}
			}

		}

	}

	/**
	 * Updates the filtered signal cache in all
	 * {@link TimeDomainSampleFilterEngine TimeDomainSampleFilterEngines}
	 * in the MultichannelSampleFilter. Automatically detects the number of
	 * new samples if originalSource is {@link ChangeableMultichannelSampleSource}
	 * and runs the {@link MultichannelSampleFilter#updateTimeDomainSampleFilterEnginesCache(int)}.
	 * Has no effect if the originalSource is not a {@link ChangeableMultichannelSampleSource}.
	 */
	private void updateTimeDomainSampleFilterEnginesCache() {

		if (originalSource instanceof ChangeableMultichannelSampleSource) {
			ChangeableMultichannelSampleSource os = (ChangeableMultichannelSampleSource)originalSource;
			try {
				semaphore.acquire();
				os.lock();

				int samplesAdded;
				if (newMontage) {
					newMontage = false;
					samplesAdded = source.getSampleCount(0);
					os.clearNewSamplesCount();
				}
				else
					samplesAdded = os.getNewSamplesCount();

				if (samplesAdded > 0)
					updateTimeDomainSampleFilterEnginesCache(samplesAdded);
			}
			catch (InterruptedException ex) {
				java.util.logging.Logger.getLogger(MultichannelSampleFilter.class.getName()).log(Level.SEVERE, null, ex);
			}
			finally {
				os.unlock();
				semaphore.release();
			}
		}

	}

	@Override
	public synchronized void getSamples(int channel, double[] target, int signalOffset, int count, int arrayOffset) {

		try {
			if (originalSource instanceof ChangeableMultichannelSampleSource)
				updateTimeDomainSampleFilterEnginesCache();
			semaphore.acquire();

			LinkedList<SampleFilterEngine> chain = chains.get(channel);
			if (chain.isEmpty()) {
				source.getSamples(channel, target, signalOffset, count, arrayOffset);
			} else {
				ListIterator<SampleFilterEngine> it = chain.listIterator(chain.size());
				SampleFilterEngine last = it.previous();
				last.getSamples(target, signalOffset, count, arrayOffset);
			}
		}
		catch (InterruptedException ex) {
			java.util.logging.Logger.getLogger(MultichannelSampleFilter.class.getName()).log(Level.SEVERE, null, ex);
		}
		finally {
			semaphore.release();
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
		else if (evt.getPropertyName().equals(OriginalMultichannelSampleSource.CALIBRATION_PROPERTY)) {
			updateTimeDomainSampleFilterEnginesCache(source.getSampleCount(0));
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

	public Montage getCurrentMontage() {
		return currentMontage;
	}

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

	/**
	 * Imports all {@link TimeDomainSampleFilter time domain filters} from the
	 * given montage to the {@link MultichannelSampleFilter}.
	 *
	 * @param montage the montage from which the filters will be imported
	 */
	private void importTimeDomainFiltersFromMontage(Montage montage) {

		int channelCount = montage.getMontageChannelCount();
		int filterCount = montage.getSampleFilterCount();
		SampleFilterDefinition[] definitions = new SampleFilterDefinition[filterCount];
		TimeDomainSampleFilter tdsFilter;
		LinkedList<SampleFilterEngine> chain;

		SampleSource input;//input for the next filter in the list

		int i, e;

		for (i = 0; i < filterCount; i++) {

			definitions[i] = montage.getSampleFilterAt(i);

			if (definitions[i] instanceof TimeDomainSampleFilter) {

				tdsFilter = (TimeDomainSampleFilter) definitions[i];
				tdsFilter.setSamplingFrequency(source.getSamplingFrequency());

				for (e = 0; e < channelCount; e++) {
					if (!montage.isFilteringExcluded(i, e)) {

						chain = chains.get(e);

						if (chain.isEmpty())
							input = new ChannelSelectorSampleSource(source, e);
						else
							input = chain.getLast();

						addFilter(new TimeDomainSampleFilterEngine(input, tdsFilter), e);

					}

				}

			}

		}

	}

	/**
	 * Imports all {@link FFTSampleFilter FFT filters} from the
	 * given montage to the {@link MultichannelSampleFilter}.
	 *
	 * @param montage the montage from which the filters will be imported
	 */
	private void importFFTSampleFiltersFromMontage(Montage montage) {

		int channelCount = montage.getMontageChannelCount();
		int filterCount = montage.getSampleFilterCount();
		SampleFilterDefinition[] definitions = new SampleFilterDefinition[filterCount];
		LinkedList<SampleFilterEngine> chain;

		FFTSampleFilter fftFilter;
		FFTSampleFilter[] summaryFFTFilters = new FFTSampleFilter[channelCount];
		Iterator<Range> rangeIterator;
		Range range;
		SampleSource input;//input for the next filter in the list

		int i, e;

		for (i = 0; i < filterCount; i++) {

			definitions[i] = montage.getSampleFilterAt(i);
			if (definitions[i] instanceof FFTSampleFilter) {

				fftFilter = (FFTSampleFilter) definitions[i];
				rangeIterator = fftFilter.getRangeIterator();
				while (rangeIterator.hasNext()) {
					range = rangeIterator.next();

					for (e = 0; e < channelCount; e++) {
						if (!montage.isFilteringExcluded(i, e)) {
							if (summaryFFTFilters[e] == null)
								summaryFFTFilters[e] = new FFTSampleFilter(true);
							summaryFFTFilters[e].setRange(range, true);
						}
					}
				}
			}

		}

		for (e = 0; e < channelCount; e++) {

			if (summaryFFTFilters[e] != null) {

				chain = chains.get(e);

				if (chain.isEmpty())
					input = new ChannelSelectorSampleSource(source, e);
				else
					input = chain.getLast();

				addFilter(new FFTSampleFilterEngine(input, summaryFFTFilters[e]), e);

			}
		}

	}

	/**
	 * Clears the filter {@link SampleFilterEngine engines} and initialises
	 * them creating {@link SampleFilterEngine filter engines}
	 * based on {@link SampleFilterDefinition sample filters} (both FFT and Time Domain) from a given montage.
	 * @param montage the montage used to create new engines
	 * @throws MontageMismatchException
	 */
	protected synchronized void applyMontage(Montage montage) throws MontageMismatchException {

		try {
			semaphore.acquire();

			reinitFilterChains();
			if (!montage.isFilteringEnabled()) {
				return;
			}

			importTimeDomainFiltersFromMontage(montage);
			importFFTSampleFiltersFromMontage(montage);

			newMontage = true;

		} catch (InterruptedException ex) {
			java.util.logging.Logger.getLogger(MultichannelSampleFilter.class.getName()).log(Level.SEVERE, null, ex);
		}
		finally {
			semaphore.release();
		}

		if (!(originalSource instanceof ChangeableMultichannelSampleSource)) {
			updateTimeDomainSampleFilterEnginesCache(source.getSampleCount(0));
		}

	}

}
