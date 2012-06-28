/* ViewerBookTree.java created 2007-09-11
 *
 */
package org.signalml.app.view.workspace;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.JPopupMenu;
import javax.swing.SwingUtilities;
import javax.swing.tree.TreePath;

import org.signalml.app.action.document.ActivateDocumentAction;
import org.signalml.app.action.document.CloseDocumentAction;
import org.signalml.app.action.selector.ActionFocusListener;
import org.signalml.app.action.selector.ActionFocusManager;
import org.signalml.app.action.selector.ActionFocusSupport;
import org.signalml.app.action.selector.BookDocumentFocusSelector;
import org.signalml.app.document.BookDocument;
import org.signalml.app.document.DocumentFlowIntegrator;
import org.signalml.app.model.book.BookTreeModel;
import org.signalml.app.view.common.components.cellrenderers.BookTreeCellRenderer;
import org.signalml.plugin.export.signal.Document;
import org.signalml.plugin.export.view.AbstractViewerTree;
import org.signalml.plugin.impl.PluginAccessClass;

/** ViewerBookTree
 *
 *
 * @author Michal Dobaczewski &copy; 2007-2008 CC Otwarte Systemy Komputerowe Sp. z o.o.
 */
public class ViewerBookTree extends AbstractViewerTree  implements BookDocumentFocusSelector {

	private static final long serialVersionUID = 1L;

	private ActionFocusSupport afSupport = new ActionFocusSupport(this);

	private JPopupMenu bookDocumentPopupMenu;

	private ActionFocusManager actionFocusManager;
	private DocumentFlowIntegrator documentFlowIntegrator;

	private ActivateDocumentAction activateDocumentAction;
	private CloseDocumentAction closeDocumentAction;

	private BookDocument activeBookDocument;

	public ViewerBookTree(BookTreeModel model) {
		super(model);
		setCellRenderer(new BookTreeCellRenderer());
		expandPath(new TreePath(new Object[] {model.getRoot()}));
		addMouseListener(new MouseEventHandler());
	}

	@Override
	public BookTreeModel getModel() {
		return (BookTreeModel) super.getModel();
	}

	@Override
	public BookDocument getActiveBookDocument() {
		return activeBookDocument;
	}

	@Override
	public Document getActiveDocument() {
		return activeBookDocument;
	}

	@Override
	public void addActionFocusListener(ActionFocusListener listener) {
		afSupport.addActionFocusListener(listener);
	}

	@Override
	public void removeActionFocusListener(ActionFocusListener listener) {
		afSupport.removeActionFocusListener(listener);
	}

	@Override
	public JPopupMenu getComponentPopupMenu() {
		return focus(getSelectionPath());
	}

	private JPopupMenu focus(TreePath path) {

		JPopupMenu popupMenu = null;

		activeBookDocument = null;
		if (path != null) {
			Object last = path.getLastPathComponent();
			if (last instanceof BookDocument) {
				activeBookDocument = (BookDocument) last;
				popupMenu = getBookDocumentPopupMenu();
			}
		}

		afSupport.fireActionFocusChanged();

		return popupMenu;

	}

	private JPopupMenu getBookDocumentPopupMenu() {

		if (bookDocumentPopupMenu == null) {
			bookDocumentPopupMenu = new JPopupMenu();

			bookDocumentPopupMenu.add(getActivateDocumentAction());
			bookDocumentPopupMenu.addSeparator();
			bookDocumentPopupMenu.add(getCloseDocumentAction());

			PluginAccessClass.getGUIImpl().addToBookTreeBookDocumentPopupMenu(bookDocumentPopupMenu);
		}

		return bookDocumentPopupMenu;

	}

	public ActionFocusManager getActionFocusManager() {
		return actionFocusManager;
	}

	public void setActionFocusManager(ActionFocusManager actionFocusManager) {
		this.actionFocusManager = actionFocusManager;
	}

	public DocumentFlowIntegrator getDocumentFlowIntegrator() {
		return documentFlowIntegrator;
	}

	public void setDocumentFlowIntegrator(DocumentFlowIntegrator documentFlowIntegrator) {
		this.documentFlowIntegrator = documentFlowIntegrator;
	}

	public ActivateDocumentAction getActivateDocumentAction() {
		if (activateDocumentAction == null) {
			activateDocumentAction = new ActivateDocumentAction(actionFocusManager,this);
		}
		return activateDocumentAction;
	}

	public CloseDocumentAction getCloseDocumentAction() {
		if (closeDocumentAction == null) {
			closeDocumentAction = new CloseDocumentAction(this);
			closeDocumentAction.setDocumentFlowIntegrator(documentFlowIntegrator);
		}
		return closeDocumentAction;
	}

	private class MouseEventHandler extends MouseAdapter {

		// TODO finish

		@Override
		public void mousePressed(MouseEvent e) {
			ViewerBookTree tree = (ViewerBookTree) e.getSource();
			if (SwingUtilities.isRightMouseButton(e) && (e.getClickCount() == 1)) {
				TreePath selPath = tree.getPathForLocation(e.getX(), e.getY());
				tree.setSelectionPath(selPath);
			}
		}

	}

}
