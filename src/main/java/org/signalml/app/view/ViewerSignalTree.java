/* ViewerSignalTree.java created 2007-09-11
 * 
 */
package org.signalml.app.view;

import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.JPopupMenu;
import javax.swing.SwingUtilities;
import javax.swing.tree.TreePath;

import org.signalml.app.action.ActivateDocumentAction;
import org.signalml.app.action.ActivateSignalPageAction;
import org.signalml.app.action.CloseDocumentAction;
import org.signalml.app.action.selector.ActionFocusListener;
import org.signalml.app.action.selector.ActionFocusManager;
import org.signalml.app.action.selector.ActionFocusSupport;
import org.signalml.app.action.selector.SignalPageFocusSelector;
import org.signalml.app.document.Document;
import org.signalml.app.document.DocumentFlowIntegrator;
import org.signalml.app.document.SignalDocument;
import org.signalml.app.model.SignalPageTreeNode;
import org.signalml.app.model.SignalTreeModel;
import org.springframework.context.support.MessageSourceAccessor;

/** ViewerSignalTree
 *
 * 
 * @author Michal Dobaczewski &copy; 2007-2008 CC Otwarte Systemy Komputerowe Sp. z o.o.
 */
public class ViewerSignalTree extends AbstractViewerTree implements SignalPageFocusSelector {

	private static final long serialVersionUID = 1L;
	
	private ActionFocusSupport afSupport = new ActionFocusSupport(this);
	
	private JPopupMenu documentPopupMenu;
	private JPopupMenu signalPagePopupMenu;

	private ActionFocusManager actionFocusManager;
	private DocumentFlowIntegrator documentFlowIntegrator;
	
	private ActivateDocumentAction activateDocumentAction;
	private ActivateSignalPageAction activateSignalPageAction;
	private CloseDocumentAction closeDocumentAction;

	private SignalDocument activeSignalDocument;
	private int activePage;
	
	public ViewerSignalTree(SignalTreeModel model, MessageSourceAccessor messageSource) {
		super(model,messageSource);
		setCellRenderer(new SignalTreeCellRenderer());
		expandPath( new TreePath(new Object[] {model.getRoot()}) );
		addMouseListener(new MouseEventHandler());
	}
	
	@Override
	public SignalTreeModel getModel() {
		return (SignalTreeModel) super.getModel();
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
		
		if( path != null ) {
			Object last = path.getLastPathComponent();
			if( last instanceof SignalDocument ) {
				activeSignalDocument = (SignalDocument) last;
				popupMenu = getDocumentPopupMenu();				
			}
			else if( last instanceof SignalPageTreeNode ) {
				activePage = ((SignalPageTreeNode) last).getPage();
				activeSignalDocument = (SignalDocument) path.getPathComponent(1);
				popupMenu = getSignalPagePopupMenu();
			}
		}
		
		afSupport.fireActionFocusChanged();
		
		return popupMenu;
		
	}
	
	private JPopupMenu getDocumentPopupMenu() {
		
		if( documentPopupMenu == null ) {
			documentPopupMenu = new JPopupMenu();
			
			documentPopupMenu.add(getActivateDocumentAction());
			documentPopupMenu.addSeparator();
			documentPopupMenu.add(getCloseDocumentAction());
		}
				
		return documentPopupMenu;
		
	}

	private JPopupMenu getSignalPagePopupMenu() {
		
		if( signalPagePopupMenu == null ) {
			signalPagePopupMenu = new JPopupMenu();
			
			signalPagePopupMenu.add(getActivateSignalPageAction());
		}
				
		return signalPagePopupMenu;
		
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
		if( activateDocumentAction == null ) {
			activateDocumentAction = new ActivateDocumentAction(messageSource,actionFocusManager,this);
		}
		return activateDocumentAction;
	}

	public ActivateSignalPageAction getActivateSignalPageAction() {
		if( activateSignalPageAction == null ) {
			activateSignalPageAction = new ActivateSignalPageAction(messageSource,actionFocusManager,this);			
		}
		return activateSignalPageAction;
	}

	public CloseDocumentAction getCloseDocumentAction() {
		if( closeDocumentAction == null ) {
			closeDocumentAction = new CloseDocumentAction(messageSource,this);
			closeDocumentAction.setDocumentFlowIntegrator(documentFlowIntegrator);
		}
		return closeDocumentAction;
	}
	
	private class MouseEventHandler extends MouseAdapter {

		@Override
		public void mousePressed(MouseEvent e) {
			ViewerSignalTree tree = (ViewerSignalTree) e.getSource();
			if( SwingUtilities.isRightMouseButton(e) && (e.getClickCount() == 1) ) {
				TreePath selPath = tree.getPathForLocation(e.getX(), e.getY());
				tree.setSelectionPath(selPath);
			}
		}
		
		@Override
		public void mouseClicked(MouseEvent e) {
			ViewerSignalTree tree = (ViewerSignalTree) e.getSource();
			if( SwingUtilities.isLeftMouseButton(e) && (e.getClickCount() % 2) == 0 ) {
				int selRow = tree.getRowForLocation(e.getX(), e.getY());
				TreePath selPath = tree.getPathForLocation(e.getX(), e.getY());
				focus(selPath);
				if( selRow >= 0 ) {
					Object target = selPath.getLastPathComponent();
					if( target instanceof Document ) {
						getActivateDocumentAction().actionPerformed(new ActionEvent(tree,0,"activate"));
					} else if( target instanceof SignalPageTreeNode ) {
						getActivateSignalPageAction().actionPerformed(new ActionEvent(tree,0,"activate"));
					}
					// ignore dbl clicks on other tree nodes 
				}
			}
		}
				
	}
	
}
