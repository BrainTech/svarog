/* TagStyleTreeModel.java created 2007-11-10
 *
 */

package org.signalml.app.model;

import org.apache.log4j.Logger;
import org.signalml.app.view.tag.TagIconProducer;
import org.signalml.domain.tag.StyledTagSet;
import org.signalml.domain.tag.TagStyleEvent;
import org.signalml.domain.tag.TagStyleListener;
import org.signalml.plugin.export.signal.SignalSelectionType;
import org.signalml.plugin.export.signal.TagStyle;
import org.signalml.plugin.export.view.AbstractTreeModel;

/** TagStyleTreeModel
 *
 *
 * @author Michal Dobaczewski &copy; 2007-2008 CC Otwarte Systemy Komputerowe Sp. z o.o.
 */
public class TagStyleTreeModel extends AbstractTreeModel implements TagStyleListener {

	protected static final Logger logger = Logger.getLogger(TagStyleTreeModel.class);

	private static final String ROOT_NODE = "tagStyleTree.root";

	private StyledTagSet tagSet;
	private TagIconProducer iconProducer;

	private TagTypeTreeNode[] tagTypeTreeNodes;

	public TagStyleTreeModel() {
		super();
		iconProducer = new TagIconProducer();
	}

	public StyledTagSet getTagSet() {
		return tagSet;
	}

	public void setTagSet(StyledTagSet tagSet) {
		if (this.tagSet != tagSet) {
			this.tagSet = tagSet;
			if (this.tagSet != null) {
				this.tagSet.removeTagStyleListener(this);
			}
			if (tagSet != null) {
				tagSet.addTagStyleListener(this);
			}
			tagTypeTreeNodes = null;
			fireTreeStructureChanged(this, new Object[] { ROOT_NODE });
		}
	}

	public TagIconProducer getIconProducer() {
		return iconProducer;
	}

	@Override
	public int getChildCount(Object parent) {
		if (tagSet != null) {
			if (parent == ROOT_NODE) {
				return SignalSelectionType.values().length;
			}
			if (parent instanceof TagTypeTreeNode) {
				return tagSet.getTagStyleCount(((TagTypeTreeNode) parent).getType());
			}
		}
		return 0;
	}

	@Override
	public Object getChild(Object parent, int index) {
		if (parent == ROOT_NODE) {
			return getTagTypeTreeNode(index);
		}
		if (parent instanceof TagTypeTreeNode) {
			return tagSet.getStyleAt(((TagTypeTreeNode) parent).getType(), index);
		}
		return null;
	}

	@Override
	public int getIndexOfChild(Object parent, Object child) {
		if (parent == ROOT_NODE) {
			SignalSelectionType type = ((TagTypeTreeNode) child).getType();
			return type.ordinal();
		}
		if (parent instanceof TagTypeTreeNode) {
			return tagSet.indexOfStyle((TagStyle) child);
		}
		return -1;
	}

	@Override
	public Object getRoot() {
		return ROOT_NODE;
	}

	@Override
	public boolean isLeaf(Object node) {
		return (node instanceof TagStyle);
	}

	public Object[] getTagStylePath(TagStyle style) {
		if (style == null) {
			return new Object[] { ROOT_NODE };
		} else {
			return new Object[] { ROOT_NODE, getTagTypeTreeNode(style.getType().ordinal()), style };
		}
	}

	private TagTypeTreeNode[] getTagTypeTreeNodes() {
		if (tagTypeTreeNodes == null) {
			tagTypeTreeNodes = new TagTypeTreeNode[3];
			SignalSelectionType[] types = SignalSelectionType.values();
			for (int i=0; i<types.length; i++) {
				tagTypeTreeNodes[i] = new TagTypeTreeNode(tagSet, types[i]);
			}
		}
		return tagTypeTreeNodes;
	}

	private TagTypeTreeNode getTagTypeTreeNode(int index) {
		return getTagTypeTreeNodes()[index];
	}

	public Object[] getTagStyleParentPath(SignalSelectionType type) {
		return new Object[] { ROOT_NODE, getTagTypeTreeNode(type.ordinal()) };
	}

	@Override
	public void tagStyleAdded(TagStyleEvent e) {
		TagStyle style = e.getTagStyle();
		fireTreeNodesInserted(
		        this,
		        getTagStyleParentPath(style.getType()),
		        new int[] { e.getInTypeIndex() },
		        new Object[] { style }
		);
	}

	@Override
	public void tagStyleChanged(TagStyleEvent e) {
		TagStyle style = e.getTagStyle();
		iconProducer.reset(style);

		// this may rearange styles if name is changed
		fireTreeStructureChanged(this, getTagStyleParentPath(style.getType()));
	}

	@Override
	public void tagStyleRemoved(TagStyleEvent e) {
		TagStyle style = e.getTagStyle();
		iconProducer.reset(style);
		fireTreeNodesRemoved(
		        this,
		        getTagStyleParentPath(style.getType()),
		        new int[] { e.getInTypeIndex() },
		        new Object[] { style }
		);
	}

}
