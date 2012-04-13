/* TagTreeModel.java created 2007-09-11
 *
 */
package org.signalml.app.model.tag;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.HashMap;

import org.apache.log4j.Logger;
import org.signalml.app.document.DocumentManager;
import org.signalml.app.document.DocumentManagerEvent;
import org.signalml.app.document.DocumentManagerListener;
import org.signalml.app.document.ManagedDocumentType;
import org.signalml.app.document.MonitorSignalDocument;
import org.signalml.app.document.SignalDocument;
import org.signalml.app.document.TagDocument;
import org.signalml.app.view.tag.TagIconProducer;
import org.signalml.domain.tag.StyledTagSet;
import org.signalml.domain.tag.TagEvent;
import org.signalml.domain.tag.TagListener;
import org.signalml.domain.tag.TagStyleEvent;
import org.signalml.domain.tag.TagStyleListener;
import org.signalml.plugin.export.signal.Document;
import org.signalml.plugin.export.signal.SignalSelectionType;
import org.signalml.plugin.export.signal.Tag;
import org.signalml.plugin.export.signal.TagStyle;
import org.signalml.plugin.export.view.AbstractTreeModel;

/** TagTreeModel
 *
 *
 * @author Michal Dobaczewski &copy; 2007-2008 CC Otwarte Systemy Komputerowe Sp. z o.o.
 */
public class TagTreeModel extends AbstractTreeModel implements DocumentManagerListener, PropertyChangeListener, TagStyleListener, TagListener {

	protected static final Logger logger = Logger.getLogger(TagTreeModel.class);

	private static final String ROOT_NODE = "tagTree.root";

	private DocumentManager documentManager;
	private TagIconProducer iconProducer;

	private HashMap<TagDocument,TagTypeTreeNode[]> tagTypeTreeNodeMap = new HashMap<TagDocument, TagTypeTreeNode[]>();
	private HashMap<StyledTagSet, TagDocument> tagDocumentMap = new HashMap<StyledTagSet, TagDocument>();

	public TagTreeModel() {
		super();
		iconProducer = new TagIconProducer();
	}

	public DocumentManager getDocumentManager() {
		return documentManager;
	}

	public void setDocumentManager(DocumentManager documentManager) {
		this.documentManager = documentManager;
	}

	@Override
	public Object getChild(Object parent, int index) {
		if (parent == ROOT_NODE) {
			return documentManager.getDocumentAt(ManagedDocumentType.SIGNAL, index);
		}
		else if (parent instanceof SignalDocument) {
			return ((SignalDocument) parent).getTagDocuments().get(index);
		}
		else if (parent instanceof TagDocument) {
			return getTagTypeTreeNode((TagDocument) parent, index);
		}
		else if (parent instanceof TagTypeTreeNode) {
			if (index == 0) {
				TagTypeTreeNode tagTypeTreeNode = (TagTypeTreeNode) parent;
				return new TagStylesTreeNode(tagTypeTreeNode.getTagSet(), tagTypeTreeNode.getType());
			} else {
				return ((TagTypeTreeNode) parent).getTag(index-1);
			}
		}
		else if (parent instanceof TagStylesTreeNode) {
			return ((TagStylesTreeNode) parent).getStyle(index);
		}
		return null;
	}

	@Override
	public int getChildCount(Object parent) {
		if (parent == ROOT_NODE) {
			return documentManager.getDocumentCount(ManagedDocumentType.SIGNAL);
		}
		else if (parent instanceof SignalDocument) {
			return ((SignalDocument) parent).getTagDocuments().size();
		}
		else if (parent instanceof TagDocument) {
			return SignalSelectionType.values().length;
		}
		else if (parent instanceof TagTypeTreeNode) {
			return 1+((TagTypeTreeNode) parent).getSize();
		}
		else if (parent instanceof TagStylesTreeNode) {
			return ((TagStylesTreeNode) parent).getSize();
		}
		return 0;
	}

	@Override
	public int getIndexOfChild(Object parent, Object child) {
		if (parent == ROOT_NODE && (child instanceof Document)) {
			return documentManager.getIndexOfDocument(ManagedDocumentType.SIGNAL, ((Document) child));
		}
		else if ((parent instanceof SignalDocument) && (child instanceof TagDocument)) {
			return ((SignalDocument) parent).getTagDocuments().indexOf(child);
		}
		else if ((parent instanceof TagDocument) && (child instanceof TagTypeTreeNode)) {
			return ((TagTypeTreeNode) child).getType().ordinal();
		}
		else if ((parent instanceof TagTypeTreeNode)) {
			if ((child instanceof TagStylesTreeNode)) {
				return 0;
			}
			else if ((child instanceof Tag)) {
				return ((TagTypeTreeNode) parent).indexOfTag((Tag) child) + 1;
			}
		}
		else if ((parent instanceof TagStylesTreeNode) && (child instanceof TagStyle)) {
			return ((TagStylesTreeNode) parent).indexOfStyle((TagStyle) child);
		}
		return -1;
	}

	@Override
	public Object getRoot() {
		return ROOT_NODE;
	}

	@Override
	public boolean isLeaf(Object node) {
		if (node == ROOT_NODE) {
			return false;
		}
		else if (node instanceof SignalDocument) {
			return false;
		}
		else if (node instanceof TagDocument) {
			return false;
		}
		else if (node instanceof TagTypeTreeNode) {
			return false;
		}
		else if (node instanceof TagStylesTreeNode) {
			return false;
		}
		return true;
	}

	@Override
	public void documentAdded(DocumentManagerEvent e) {

		Document document = e.getDocument();
		if (document instanceof SignalDocument && !(document instanceof MonitorSignalDocument)) {
			SignalDocument signalDocument = (SignalDocument) document;
			signalDocument.addPropertyChangeListener(this);

			fireTreeNodesInserted(this, new Object[] { ROOT_NODE }, new int[] { e.getInTypeIndex() }, new Object[] { signalDocument });
		}
		else if (document instanceof TagDocument) {
			TagDocument tagDocument = (TagDocument) document;
			StyledTagSet tagSet = tagDocument.getTagSet();

			tagDocument.addPropertyChangeListener(this);
			tagSet.addTagListener(this);
			tagSet.addTagStyleListener(this);

			SignalDocument parent = tagDocument.getParent();

			tagDocumentMap.put(tagSet, tagDocument);

			fireTreeNodesInserted(
				this,
				new Object[] { ROOT_NODE, parent },
				new int[] { parent.getTagDocuments().indexOf(tagDocument) },
				new Object[] { tagDocument }
			);
		}

	}

	@Override
	public void documentRemoved(DocumentManagerEvent e) {

		Document document = e.getDocument();
		if (document instanceof SignalDocument && !(document instanceof MonitorSignalDocument)) {
			SignalDocument signalDocument = (SignalDocument) document;
			signalDocument.removePropertyChangeListener(this);

			fireTreeNodesRemoved(this, new Object[] { ROOT_NODE }, new int[] { e.getInTypeIndex() }, new Object[] { signalDocument });
		}
		else if (document instanceof TagDocument) {
			TagDocument tagDocument = (TagDocument) document;
			StyledTagSet tagSet = tagDocument.getTagSet();

			tagTypeTreeNodeMap.remove(tagDocument);
			tagDocumentMap.remove(tagSet);

			tagDocument.removePropertyChangeListener(this);
			tagSet.removeTagListener(this);
			tagSet.removeTagStyleListener(this);

			SignalDocument parent = tagDocument.getParent();
			int index = parent.getTagDocuments().indexOf(tagDocument);

			fireTreeNodesRemoved(
				this,
				new Object[] { ROOT_NODE, parent },
				new int[] { index },
				new Object[] { tagDocument }
			);

		}

	}

	@Override
	public void documentPathChanged(DocumentManagerEvent e) {

		Document document = e.getDocument();
		if (document instanceof TagDocument) {
			TagDocument tagDocument = (TagDocument) document;
			SignalDocument parent = tagDocument.getParent();

			fireTreeNodesChanged(
				this,
				new Object[] { ROOT_NODE, parent },
				new int[] { parent.getTagDocuments().indexOf(tagDocument) },
				new Object[] { tagDocument }
			);
		}

	}

	@Override
	public void propertyChange(PropertyChangeEvent evt) {
		// ignored
	}

	@Override
	public void tagStyleAdded(TagStyleEvent e) {
		// this is too complicated to do more intelligently
		fireTreeStructureChanged(this, getTagStyleEventObjectPath(e));
	}

	@Override
	public void tagStyleChanged(TagStyleEvent e) {
		iconProducer.reset(e.getTagStyle());
		// this is too complicated to do more intelligently
		fireTreeStructureChanged(this, getTagStyleEventObjectPath(e));
	}

	@Override
	public void tagStyleRemoved(TagStyleEvent e) {
		iconProducer.reset(e.getTagStyle());
		// this is too complicated to do more intelligently
		fireTreeStructureChanged(this, getTagStyleEventObjectPath(e));
	}

	@Override
	public void tagAdded(TagEvent e) {
		// this is too complicated to do more intelligently
		fireTreeStructureChanged(this, getTagEventObjectPath(e));
	}

	@Override
	public void tagChanged(TagEvent e) {
		// this is too complicated to do more intelligently
		fireTreeStructureChanged(this, getTagEventObjectPath(e));
	}

	@Override
	public void tagRemoved(TagEvent e) {
		// this is too complicated to do more intelligently
		fireTreeStructureChanged(this, getTagEventObjectPath(e));
	}

	public TagIconProducer getIconProducer() {
		return iconProducer;
	}

	private TagTypeTreeNode[] getTagTypeTreeNodes(TagDocument tagDocument) {
		TagTypeTreeNode[] arr = tagTypeTreeNodeMap.get(tagDocument);
		if (arr == null) {
			arr = new TagTypeTreeNode[3];
			StyledTagSet tagSet = tagDocument.getTagSet();
			SignalSelectionType[] types = SignalSelectionType.values();
			for (int i=0; i<types.length; i++) {
				arr[i] = new TagTypeTreeNode(tagSet, types[i]);
			}
			tagTypeTreeNodeMap.put(tagDocument, arr);
		}
		return arr;
	}

	private TagTypeTreeNode getTagTypeTreeNode(TagDocument tagDocument, int index) {
		return getTagTypeTreeNodes(tagDocument)[index];
	}

	protected Object[] getTagStyleEventObjectPath(TagStyleEvent e) {

		Object source = e.getSource();
		if (source == null || !(source instanceof StyledTagSet)) {
			return null;
		}

		StyledTagSet tagSet = (StyledTagSet) e.getSource();
		TagDocument tagDocument = tagDocumentMap.get(tagSet);

		Object[] path = new Object[] {
			ROOT_NODE,
			tagDocument.getParent(),
			tagDocument,
			getTagTypeTreeNode(tagDocument, e.getTagStyle().getType().ordinal())
		};

		return path;

	}

	protected Object[] getTagEventObjectPath(TagEvent e) {

		Object source = e.getSource();
		if (source == null || !(source instanceof StyledTagSet)) {
			return null;
		}

		StyledTagSet tagSet = (StyledTagSet) e.getSource();
		TagDocument tagDocument = tagDocumentMap.get(tagSet);

		Object[] path = new Object[] {
			ROOT_NODE,
			tagDocument.getParent(),
			tagDocument,
			getTagTypeTreeNode(tagDocument, e.getTag().getType().ordinal())
		};

		return path;

	}

	public TagDocument getDocumentFromSet(StyledTagSet tagSet) {
		return tagDocumentMap.get(tagSet);
	}

	protected int getTagEventIndex(TagEvent e) {

		Object source = e.getSource();
		if (source == null || !(source instanceof StyledTagSet)) {
			return -1;
		}

		StyledTagSet tagSet = (StyledTagSet) e.getSource();

		return tagSet.indexOfTag(e.getTag());

	}

}
