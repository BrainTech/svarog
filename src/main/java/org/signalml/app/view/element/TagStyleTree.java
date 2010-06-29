/* TagStyleTree.java created 2007-11-10
 *
 */
package org.signalml.app.view.element;

import javax.swing.tree.TreePath;

import org.signalml.app.model.TagStyleTreeModel;
import org.signalml.app.view.AbstractViewerTree;
import org.signalml.app.view.TagTreeCellRenderer;
import org.springframework.context.support.MessageSourceAccessor;

/** TagStyleTree
 *
 *
 * @author Michal Dobaczewski &copy; 2007-2008 CC Otwarte Systemy Komputerowe Sp. z o.o.
 */
public class TagStyleTree extends AbstractViewerTree {

	private static final long serialVersionUID = 1L;

	public TagStyleTree(TagStyleTreeModel model, MessageSourceAccessor messageSource) {
		super(model,messageSource);
		setCellRenderer(new TagTreeCellRenderer(model.getIconProducer()));
		expandPath(new TreePath(new Object[] {model.getRoot()}));
	}

	@Override
	public TagStyleTreeModel getModel() {
		return (TagStyleTreeModel) super.getModel();
	}

}
