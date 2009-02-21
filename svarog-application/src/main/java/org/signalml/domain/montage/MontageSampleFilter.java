/* MontageSampleFilter.java created 2008-02-01
 * 
 */

package org.signalml.domain.montage;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;

import org.signalml.domain.montage.filter.SampleFilterDefinition;

import com.thoughtworks.xstream.annotations.XStreamAlias;

/** MontageSampleFilter
 *
 * 
 * @author Michal Dobaczewski &copy; 2007-2008 CC Otwarte Systemy Komputerowe Sp. z o.o.
 */
@XStreamAlias("filter")
public class MontageSampleFilter {

	private SampleFilterDefinition definition;

	private boolean enabled = true;
	private ArrayList<MontageChannel> excludedChannels = new ArrayList<MontageChannel>();	
	
	protected MontageSampleFilter() {		
	}
	
	public MontageSampleFilter(SampleFilterDefinition definition) {
		this.definition = definition;
	}
	
	public MontageSampleFilter( MontageSampleFilter filter, ArrayList<MontageChannel> montageChannels, ArrayList<MontageChannel> filterMontageChannels ) {
		this();
		
		this.definition = filter.definition.duplicate();
		this.enabled = filter.enabled;
		
		MontageChannel filterChannel;
		MontageChannel channel;
		int index;
		int size = montageChannels.size();
		
		Iterator<MontageChannel> it = filter.excludedChannels.iterator();		
		while( it.hasNext() ) {
			
			filterChannel = it.next();
			index = filterMontageChannels.indexOf( filterChannel );
			if( index >= 0 && index < size ) {
				
				channel = montageChannels.get(index);
				if( channel != null ) {
					excludedChannels.add( channel );
				}
			
			}
					
		}
				
	}
	
	public SampleFilterDefinition getDefinition() {
		return definition;
	}

	public void setDefinition(SampleFilterDefinition definition) {
		this.definition = definition;
	}
	
	public boolean clearExclusion() {
		boolean done = !excludedChannels.isEmpty();
		excludedChannels.clear();
		return done;
	}
	
	public int getExcludedChannelCount() {
		return excludedChannels.size();
	}
	
	public MontageChannel getExcludedChannelAt( int index ) {
		return excludedChannels.get(index);
	}
	
	public Iterator<MontageChannel> getExcludedChannelIterator() {
		return excludedChannels.iterator();
	}
	
	public boolean isChannelExcluded( MontageChannel channel ) {
		return excludedChannels.contains(channel);
	}
	
	public boolean addExcludedChannel( MontageChannel channel ) {
		return excludedChannels.add( channel );		
	}
	
	public boolean removeExcludedChannel( MontageChannel channel ) {
		return excludedChannels.remove(channel);
	}
	
	public MontageChannel removeExcludedChannel( int index ) {
		return excludedChannels.remove(index);
	}
	
	public boolean[] getExclusionArray( ArrayList<MontageChannel> montageChannels ) {
		
		boolean[] result = new boolean[montageChannels.size()];
		
		Arrays.fill(result, false);
		
		Iterator<MontageChannel> it = excludedChannels.iterator();
		int index;
		while( it.hasNext() ) {
			
			index = montageChannels.indexOf( it.next() );
			if( index >= 0 ) {
				result[index] = true;
			}
			
		}
		
		return result;
		
	}

	public boolean isEnabled() {
		return enabled;
	}

	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
	}
	
}
