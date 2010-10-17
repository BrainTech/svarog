/* PositionedTag.java created 2007-10-15
 *
 */

package org.signalml.app.view.signal;

import org.signalml.plugin.export.signal.Tag;
import org.signalml.plugin.export.view.ExportedPositionedTag;

/** PositionedTag
 *
 *
 * @author Michal Dobaczewski &copy; 2007-2008 CC Otwarte Systemy Komputerowe Sp. z o.o.
 */
public class PositionedTag implements Comparable<PositionedTag>, ExportedPositionedTag {

	Tag tag;
	int tagPositionIndex;

	public PositionedTag(Tag tag, int tagPositionIndex) {
		this.tag = tag;
		this.tagPositionIndex = tagPositionIndex;
	}
	
	public PositionedTag(ExportedPositionedTag positionedTag){
		Tag tag;
		if (positionedTag.getTag() instanceof Tag){
			tag = (Tag) positionedTag.getTag();
		} else {
			tag = new Tag(positionedTag.getTag());
		}
		this.tag = tag;
		this.tagPositionIndex = positionedTag.getTagPositionIndex();
	}

	@Override
	public Tag getTag() {
		return tag;
	}

	@Override
	public int getTagPositionIndex() {
		return tagPositionIndex;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj instanceof PositionedTag) {
			PositionedTag otherTag = (PositionedTag) obj;
			return (otherTag.tagPositionIndex == this.tagPositionIndex) && otherTag.tag.equals(this.tag);
		}
		return false;
	}

	@Override
	public int compareTo(PositionedTag o) {
		if (tagPositionIndex != o.tagPositionIndex) {
			return tagPositionIndex - o.tagPositionIndex;
		}
		return tag.compareTo(o.tag);
	}

	/* (non-Javadoc)
	 * @see org.signalml.plugin.export.view.ExportedPositionedTag#compareTo(org.signalml.plugin.export.view.ExportedPositionedTag)
	 */
	@Override
	public int compareTo(ExportedPositionedTag o) {
		if (getTagPositionIndex() != o.getTagPositionIndex()) {
			return getTagPositionIndex() - o.getTagPositionIndex();
		}
		return getTag().compareTo(o.getTag());
	}


}
