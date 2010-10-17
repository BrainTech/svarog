/* TagStylesTreeNode.java created 2007-10-13
 *
 */

package org.signalml.app.model;

import org.signalml.domain.tag.StyledTagSet;
import org.signalml.plugin.export.signal.SignalSelectionType;
import org.signalml.plugin.export.signal.TagStyle;
import org.springframework.context.MessageSourceResolvable;

/** TagStylesTreeNode
 *
 *
 * @author Michal Dobaczewski &copy; 2007-2008 CC Otwarte Systemy Komputerowe Sp. z o.o.
 */
public class TagStylesTreeNode implements MessageSourceResolvable {

	private StyledTagSet tagSet;
	private SignalSelectionType type;

	public TagStylesTreeNode(StyledTagSet tagSet, SignalSelectionType type) {
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
		return tagSet.getTagStyleCount(type);
	}

	public TagStyle getStyle(int index) {
		return tagSet.getStyleAt(type, index);
	}

	public int indexOfStyle(TagStyle style) {
		return tagSet.indexOfStyle(style);
	}

	@Override
	public Object[] getArguments() {
		return new Object[0];
	}

	@Override
	public String[] getCodes() {
		return new String[] { "tagTree.styles" };
	}

	@Override
	public String getDefaultMessage() {
		return "Styles???";
	}



}
