/* TagTypeTreeNode.java created 2007-10-13
 * 
 */

package org.signalml.app.model;

import org.signalml.domain.signal.SignalSelectionType;
import org.signalml.domain.tag.StyledTagSet;
import org.signalml.domain.tag.Tag;
import org.springframework.context.MessageSourceResolvable;

/** TagTypeTreeNode
 *
 * 
 * @author Michal Dobaczewski &copy; 2007-2008 CC Otwarte Systemy Komputerowe Sp. z o.o.
 */
public class TagTypeTreeNode implements MessageSourceResolvable {

	private StyledTagSet tagSet;
	private SignalSelectionType type;

	public TagTypeTreeNode(StyledTagSet tagSet, SignalSelectionType type) {
		this.tagSet = tagSet;
		this.type = type;
	}
	
	public StyledTagSet getTagSet() {
		return tagSet;
	}

	public SignalSelectionType getType() {
		return type;
	}
	
	public int getSize() {
		return tagSet.getTagCount(type);
	}
	
	public Tag getTag(int index) {
		return tagSet.getTagAt(type, index);
	}
	
	public int indexOfTag(Tag tag) {
		return tagSet.indexOfTag(tag);
	}

	@Override
	public Object[] getArguments() {
		return new Object[0];
	}

	@Override
	public String[] getCodes() {
		return new String[] { "signalSelectionType." + type.getName() };
	}

	@Override
	public String getDefaultMessage() {
		return type.getName();
	}	
	
}
