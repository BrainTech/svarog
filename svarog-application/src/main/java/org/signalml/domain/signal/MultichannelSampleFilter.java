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

/** SampleFilter
 *
 * 
 * @author Michal Dobaczewski &copy; 2007-2008 CC Otwarte Systemy Komputerowe Sp. z o.o.
 */
public class MultichannelSampleFilter extends MultichannelSampleProcessor {

	protected static final Logger logger = Logger.getLogger(MultichannelSampleFilter.class);
	
	private Vector<LinkedList<SampleFilterEngine>> chains;
	
	private Montage currentMontage = null;
	
	public MultichannelSampleFilter(MultichannelSampleSource source) {
		super(source);
		reinitFilterChains();
	}

	@Override
	public void getSamples(int channel, double[] target, int signalOffset, int count, int arrayOffset) {
		
		LinkedList<SampleFilterEngine> chain = chains.get(channel);
		if( chain.isEmpty() ) {
			source.getSamples(channel, target, signalOffset, count, arrayOffset);
		} else {
			ListIterator<SampleFilterEngine> it = chain.listIterator(chain.size());
			SampleFilterEngine last = it.previous();
			last.getSamples(target, signalOffset, count, arrayOffset);
		}
		
	}
	
	public void addFilter(SampleFilterEngine filter) {
		int cnt = chains.size();
		LinkedList<SampleFilterEngine> chain;
		for( int i=0; i<cnt; i++ ) {
			chain = chains.get(i);
			chain.add(filter);
		}
	}

	public void addFilter(SampleFilterEngine filter, int[] channels) {
		LinkedList<SampleFilterEngine> chain;
		for( int i=0; i<channels.length; i++ ) {
			chain = chains.get(channels[i]);
			chain.add(filter);
		}
	}

	public void addFilter(SampleFilterEngine filter, int channel) {
		LinkedList<SampleFilterEngine> chain;
		chain = chains.get(channel);
		chain.add(filter);
	}
	
	@Override
	public void propertyChange(PropertyChangeEvent evt) {
		if( evt.getSource() == source ) {
			if( evt.getPropertyName().equals(CHANNEL_COUNT_PROPERTY) ) {
				reinitFilterChains();
			}
		}
		super.propertyChange(evt);
	}
	
	public void reinitFilterChains() {
		
		int cnt = source.getChannelCount();
		chains = new Vector<LinkedList<SampleFilterEngine>>(cnt);
		LinkedList<SampleFilterEngine> chain;
		for( int i=0; i<cnt; i++ ) {
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
		} catch( MontageMismatchException ex ) {
			logger.error( "Failed to apply montage", ex );
			this.currentMontage = null;
			reinitFilterChains();
			throw ex;
		}
		this.currentMontage = currentMontage;
	}
	
	private void applyMontage(Montage montage) throws MontageMismatchException {

		reinitFilterChains();
		if( !montage.isFilteringEnabled() ) {
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
		
		for( i=0; i<filterCount; i++ ) {
			definitions[i] = montage.getSampleFilterAt(i);
			if( definitions[i] instanceof FFTSampleFilter ) {
				
				fftFilter = (FFTSampleFilter) definitions[i];
				it = fftFilter.getRangeIterator();
				while( it.hasNext() ) {
					range = it.next();
					
					for( e=0; e<channelCount; e++ ) {
						if( !montage.isFilteringExcluded(i, e) ) {
							if( summaryFFTFilters[e] == null ) {
								summaryFFTFilters[e] = new FFTSampleFilter(true);
							}
							summaryFFTFilters[e].setRange(range, true);
						}
					}					
					
				}
				
			}
		}
		
		for( e=0; e<channelCount; e++ ) {
			if( summaryFFTFilters[e] != null ) {
				addFilter( new FFTSampleFilterEngine( new ChannelSelectorSampleSource(source,e), summaryFFTFilters[e] ), e);
			}
		}
		
	}
					
}
