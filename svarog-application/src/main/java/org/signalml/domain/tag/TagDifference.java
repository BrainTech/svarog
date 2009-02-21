/* TagDifference.java created 2007-11-13
 * 
 */

package org.signalml.domain.tag;

import org.signalml.domain.signal.SignalSelection;
import org.signalml.domain.signal.SignalSelectionType;

/** TagDifference
 *
 * 
 * @author Michal Dobaczewski &copy; 2007-2008 CC Otwarte Systemy Komputerowe Sp. z o.o.
 */
public class TagDifference extends SignalSelection implements Comparable<TagDifference> {

	private static final long serialVersionUID = 1L;
	
	private TagDifferenceType differenceType;

	public TagDifference(SignalSelectionType type, float position, float length, int channel, TagDifferenceType differenceType) {
		super(type, position, length, channel);
		this.differenceType = differenceType;
	}

	public TagDifference(SignalSelectionType type, float position, float length, TagDifferenceType differenceType) {
		super(type, position, length);
		this.differenceType = differenceType;
	}

	public TagDifferenceType getDifferenceType() {
		return differenceType;
	}	

	@Override
	public int compareTo(TagDifference t) {
		
		float test = position - t.position;
		if( test == 0 ) {
			test = length - t.length;
			if( test == 0 ) {
				test = channel - t.channel;
				if( ((int) test) == 0 ) {
					int itest = type.ordinal() - t.type.ordinal();
					if( itest == 0 ) {
						return differenceType.ordinal() - t.differenceType.ordinal();
					}
				}
			}
		}
		return (int) Math.signum(test);
		
	}
	
	@Override
	public boolean equals(Object obj) {
		if( obj == null || !(obj instanceof TagDifference) ) {
			return false;
		}
		return ( this.compareTo((TagDifference) obj) == 0 );
	}
	
}
