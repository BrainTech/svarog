/* ChannelSpaceSelection.java created 2008-01-18
 * 
 */

package org.signalml.domain.signal.space;

import java.util.Arrays;
import java.util.Iterator;
import java.util.LinkedHashSet;

/** ChannelSpaceSelection
 *
 * 
 * @author Michal Dobaczewski &copy; 2007-2008 CC Otwarte Systemy Komputerowe Sp. z o.o.
 */
public class ChannelSpace {

	private LinkedHashSet<Integer> channels;
	
	public ChannelSpace() {
		channels = new LinkedHashSet<Integer>();
	}
	
	public ChannelSpace( int[] array ) {
		this();
		replaceChannels(array);
	}
	
	public void replaceChannels( int[] array ) {
		channels.clear();
		for( int i=0; i<array.length; i++ ) {
			channels.add( array[i] );
		}
	}
	
	public void addChannel( int channel ) {
		channels.add( channel );
	}
	
	public void removeChannel( int channel ) {
		channels.remove( new Integer( channel ) );
	}
	
	public void clear() {
		channels.clear();
	}

	public int size() {
		return channels.size();
	}
	
	public boolean isChannelSelected( int channel ) {
		return channels.contains( channel );
	}
	
	public int[] getSelectedChannels() {
		int size = channels.size();
		int[] array = new int[size];
		int cnt = 0;
		Iterator<Integer> it = channels.iterator();		
		while( it.hasNext() ) {
			array[cnt] = it.next();
			cnt++;
		}
		Arrays.sort(array);
		return array;
	}
	
	public boolean[] getChannelSelection( int channelCount ) {
		
		boolean[] selection = new boolean[channelCount];
		
		for( int i=0; i<channelCount; i++ ) {
			if( channels.contains(i) ) {
				selection[i] = true;
			}
		}
		
		return selection;
		
	}
	
}
