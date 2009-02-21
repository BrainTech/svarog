/* BookFilterProcessor.java created 2008-02-28
 * 
 */

package org.signalml.domain.book;

import java.beans.PropertyChangeEvent;
import java.util.LinkedHashMap;
import java.util.Map.Entry;

import org.signalml.domain.book.filter.AtomFilterChain;

/** BookFilterProcessor
 *
 * 
 * @author Michal Dobaczewski &copy; 2007-2008 CC Otwarte Systemy Komputerowe Sp. z o.o.
 */
public class BookFilterProcessor extends BookProcessor {

	private static int CACHE_SIZE = 16;

	public static final String FILTER_CHAIN_PROPERTY = "filterChain";
		
	private AtomFilterChain filterChain;
	
	private LinkedHashMap<Integer, StandardBookSegment[]> segmentCache;
	
	public BookFilterProcessor(StandardBook source) {
		super(source);
		segmentCache = new LinkedHashMap<Integer, StandardBookSegment[]>() {
			
			private static final long serialVersionUID = 1L;

			@Override
			protected boolean removeEldestEntry(Entry<Integer, StandardBookSegment[]> eldest) {
				return( size() > CACHE_SIZE );
			}
			
		};
	}

	public AtomFilterChain getFilterChain() {
		return filterChain;
	}

	public void setFilterChain(AtomFilterChain filterChain) {
		if( this.filterChain != filterChain ) {
			AtomFilterChain oldChain = this.filterChain;
			this.filterChain = filterChain;
			segmentCache.clear();
			pcSupport.firePropertyChange(FILTER_CHAIN_PROPERTY, oldChain, filterChain);
		}
	}
	
	@Override
	public StandardBookSegment[] getSegmentAt(int segmentIndex) {

		StandardBookSegment[] segments = segmentCache.get( new Integer( segmentIndex ) );
		if( segments == null ) {

			if( filterChain == null || !filterChain.isFiltered() ) {
				segments = source.getSegmentAt(segmentIndex);
			} else {
				int channelCount = source.getChannelCount();
				segments = new FilteredBookSegment[ channelCount ];
				for( int i=0; i<channelCount; i++ ) {
					segments[i] = new FilteredBookSegment( source.getSegmentAt(segmentIndex, i), filterChain );
				}			
			}
			
			segmentCache.put( segmentIndex, segments );
			
		}
		
		return segments;
	
	}
	
	@Override
	public StandardBookSegment getSegmentAt(int segmentIndex, int channelIndex) {
		return getSegmentAt(segmentIndex)[channelIndex];
	}
	
	@Override
	public void propertyChange(PropertyChangeEvent evt) {
		segmentCache.clear();
		super.propertyChange(evt);
	}
	
	@Override
	protected void onAnyBookEvent(BookEvent ev) {
		segmentCache.clear();
		super.onAnyBookEvent(ev);
	}
	
}
