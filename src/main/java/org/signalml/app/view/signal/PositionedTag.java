/* PositionedTag.java created 2007-10-15
 * 
 */

package org.signalml.app.view.signal;

import org.signalml.domain.tag.Tag;

/** PositionedTag
 *
 * 
 * @author Michal Dobaczewski &copy; 2007-2008 CC Otwarte Systemy Komputerowe Sp. z o.o.
 */
public class PositionedTag implements Comparable<PositionedTag> {

	Tag tag;
	int tagPositionIndex;

	public PositionedTag(Tag tag, int tagPositionIndex) {
		this.tag = tag;
		this.tagPositionIndex = tagPositionIndex;
	}

	public Tag getTag() {
		return tag;
	}

	public int getTagPositionIndex() {
		return tagPositionIndex;
	}
	
	@Override
	public boolean equals(Object obj) {
		if( obj instanceof PositionedTag ) {
			PositionedTag otherTag = (PositionedTag) obj;
			return (otherTag.tagPositionIndex == this.tagPositionIndex) && otherTag.tag.equals(this.tag);
		}
		return false;		
	}

	@Override
	public int compareTo(PositionedTag o) {
		if( tagPositionIndex != o.tagPositionIndex ) {
			return tagPositionIndex - o.tagPositionIndex;
		}
		return tag.compareTo(o.tag);
	}

	
}
