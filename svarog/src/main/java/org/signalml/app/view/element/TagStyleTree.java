/* TagStyleTree.java created 2007-11-10
 *
 */
package org.signalml.app.view.element;

import javax.swing.tree.TreeModel;
import javax.swing.tree.TreePath;

import org.signalml.app.model.TagStyleTreeModel;
import org.signalml.app.view.TagTreeCellRenderer;
import org.signalml.plugin.export.signal.TagStyle;
import org.signalml.plugin.export.view.AbstractViewerTree;

/**
 * The tree with the {@link TagStyle tag styles}.
 * Contains three branches - for page, block and channels tags.
 *
 * @author Michal Dobaczewski &copy; 2007-2008 CC Otwarte Systemy Komputerowe Sp. z o.o.
 */
public class TagStyleTree extends AbstractViewerTree {

	private static final long serialVersionUID = 1L;

	/**
	 * Constructor. Sets the source of messages and creates this tree using
	 * the given {@link TagStyleTreeModel model}.
	 * @param model the model for this tree
	 * @param messageSource the source of messages (labels)
	 */
	public TagStyleTree(TagStyleTreeModel model) {
		super(model);
		setCellRenderer(new TagTreeCellRenderer(model.getIconProducer()));
		expandPath(new TreePath(new Object[] {model.getRoot()}));
	}

	/**
	 * Returns the model for this tree.
	 */
	@Override
	public TagStyleTreeModel getModel() {
		return (TagStyleTreeModel) super.getModel();
	}

}
