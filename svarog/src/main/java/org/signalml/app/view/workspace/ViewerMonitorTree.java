package org.signalml.app.view.workspace;

import java.awt.event.ActionEvent;
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
import org.signalml.app.action.selector.SignalPageFocusSelector;
import org.signalml.app.document.DocumentFlowIntegrator;
import org.signalml.app.document.signal.SignalDocument;
import org.signalml.app.model.monitor.MonitorTreeModel;
import org.signalml.plugin.export.signal.Document;
import org.signalml.plugin.export.view.AbstractViewerTree;

/** MonitorSignalTree
 *
 */
public class ViewerMonitorTree extends AbstractViewerTree implements SignalPageFocusSelector {

	private static final long serialVersionUID = 1L;

	private ActionFocusSupport afSupport = new ActionFocusSupport(this);

	private JPopupMenu documentPopupMenu;

	private ActionFocusManager actionFocusManager;
	private DocumentFlowIntegrator documentFlowIntegrator;

	private ActivateDocumentAction activateDocumentAction;
	private CloseDocumentAction closeDocumentAction;

	private SignalDocument activeSignalDocument;
	private int activePage;

	public ViewerMonitorTree(MonitorTreeModel model) {
		super(model);
		setCellRenderer(new SignalTreeCellRenderer());
		expandPath(new TreePath(new Object[] {model.getRoot()}));
		addMouseListener(new MouseEventHandler());
	}

	@Override
	public MonitorTreeModel getModel() {
		return (MonitorTreeModel) super.getModel();
	}

	@Override
	public SignalDocument getActiveSignalDocument() {
		return activeSignalDocument;
	}

	@Override
	public Document getActiveDocument() {
		return activeSignalDocument;
	}

	@Override
	public int getSignalPage() {
		return activePage;
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

		activeSignalDocument = null;
		activePage = -1;

		if (path != null) {
			Object last = path.getLastPathComponent();
			if (last instanceof SignalDocument) {
				activeSignalDocument = (SignalDocument) last;
				popupMenu = getDocumentPopupMenu();
			}
		}

		afSupport.fireActionFocusChanged();

		return popupMenu;

	}

	private JPopupMenu getDocumentPopupMenu() {

		if (documentPopupMenu == null) {
			documentPopupMenu = new JPopupMenu();

			documentPopupMenu.add(getActivateDocumentAction());
			documentPopupMenu.addSeparator();
			documentPopupMenu.add(getCloseDocumentAction());
		}

		return documentPopupMenu;

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

		@Override
		public void mousePressed(MouseEvent e) {
			ViewerMonitorTree tree = (ViewerMonitorTree) e.getSource();
			if (SwingUtilities.isRightMouseButton(e) && (e.getClickCount() == 1)) {
				TreePath selPath = tree.getPathForLocation(e.getX(), e.getY());
				tree.setSelectionPath(selPath);
			}
		}

		@Override
		public void mouseClicked(MouseEvent e) {
			ViewerMonitorTree tree = (ViewerMonitorTree) e.getSource();
			if (SwingUtilities.isLeftMouseButton(e) && (e.getClickCount() % 2) == 0) {
				int selRow = tree.getRowForLocation(e.getX(), e.getY());
				TreePath selPath = tree.getPathForLocation(e.getX(), e.getY());
				focus(selPath);
				if (selRow >= 0) {
					Object target = selPath.getLastPathComponent();
					if (target instanceof Document) {
						getActivateDocumentAction().actionPerformed(new ActionEvent(tree,0,"activate"));
					}
					// ignore dbl clicks on other tree nodes
				}
			}
		}

	}

}
