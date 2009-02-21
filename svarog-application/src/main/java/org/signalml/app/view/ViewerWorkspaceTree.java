/* ViewerWorkspaceTree.java created 2007-09-11
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
import org.signalml.app.action.CloseDocumentAction;
import org.signalml.app.action.OpenDocumentAction;
import org.signalml.app.action.OpenMRUDAction;
import org.signalml.app.action.SaveDocumentAction;
import org.signalml.app.action.SaveDocumentAsAction;
import org.signalml.app.action.selector.ActionFocusEvent;
import org.signalml.app.action.selector.ActionFocusListener;
import org.signalml.app.action.selector.ActionFocusManager;
import org.signalml.app.action.selector.ActionFocusSupport;
import org.signalml.app.action.selector.DocumentFocusSelector;
import org.signalml.app.action.selector.MRUDFocusSelector;
import org.signalml.app.document.Document;
import org.signalml.app.document.DocumentFlowIntegrator;
import org.signalml.app.document.MRUDEntry;
import org.signalml.app.model.WorkspaceTreeModel;
import org.signalml.app.view.dialog.OpenDocumentDialog;
import org.springframework.context.support.MessageSourceAccessor;

/** ViewerWorkspaceTree
 *
 * 
 * @author Michal Dobaczewski &copy; 2007-2008 CC Otwarte Systemy Komputerowe Sp. z o.o.
 */
public class ViewerWorkspaceTree extends AbstractViewerTree implements ActionFocusListener, DocumentFocusSelector, MRUDFocusSelector {

	private static final long serialVersionUID = 1L;

	private ActionFocusSupport afSupport = new ActionFocusSupport(this);
	
	private ActionFocusManager actionFocusManager;
	private DocumentFlowIntegrator documentFlowIntegrator;
	private OpenDocumentDialog openDocumentDialog;
	
	private JPopupMenu documentPopupMenu;
	private JPopupMenu mrudPopupMenu;
	private JPopupMenu otherPopupMenu;
	
	private ActivateDocumentAction activateDocumentAction;
	private OpenDocumentAction openDocumentAction;	
	private CloseDocumentAction closeDocumentAction;
	private SaveDocumentAction saveDocumentAction;
	private SaveDocumentAsAction saveDocumentAsAction;
	private OpenMRUDAction openMRUDAction;
	
	private Document activeDocument;
	private MRUDEntry activeMRUDEntry;
	
	public ViewerWorkspaceTree(WorkspaceTreeModel model, MessageSourceAccessor messageSource) {
		super(model, messageSource);
		setCellRenderer(new WorkspaceTreeCellRenderer());
		expandPath( new TreePath(new Object[] {model.getRoot(), model.getChild(model.getRoot(), 0)}) );
		expandPath( new TreePath(new Object[] {model.getRoot(), model.getChild(model.getRoot(), 1)}) );
		addMouseListener(new MouseEventHandler());
	}
		
	@Override
	public WorkspaceTreeModel getModel() {
		return (WorkspaceTreeModel) super.getModel();
	}

	@Override
	public Document getActiveDocument() {
		return activeDocument;
	}

	@Override
	public MRUDEntry getActiveMRUDEntry() {
		return activeMRUDEntry;
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
		
		activeDocument = null;
		activeMRUDEntry = null;
		
		if( path != null ) {
			Object last = path.getLastPathComponent();
			if( last instanceof Document ) {
				activeDocument = (Document) last;
				popupMenu = getDocumentPopupMenu();				
			}
			else if( last instanceof MRUDEntry ) {
				activeMRUDEntry = (MRUDEntry) last;
				popupMenu = getMRUDPopupMenu();				
			}
		}
		if( popupMenu == null ) {
			popupMenu = getOtherPopupMenu();
		}
		
		afSupport.fireActionFocusChanged();
		
		return popupMenu;
		
	}
	
	private JPopupMenu getDocumentPopupMenu() {
	
		if( documentPopupMenu == null ) {
			documentPopupMenu = new JPopupMenu();
			
			documentPopupMenu.add(getActivateDocumentAction());
			documentPopupMenu.addSeparator();
			documentPopupMenu.add(getSaveDocumentAction());
			documentPopupMenu.add(getSaveDocumentAsAction());
			documentPopupMenu.addSeparator();
			documentPopupMenu.add(getCloseDocumentAction());
		}
				
		return documentPopupMenu;
		
	}

	private JPopupMenu getMRUDPopupMenu() {
		
		if( mrudPopupMenu == null ) {
			mrudPopupMenu = new JPopupMenu();
			
			mrudPopupMenu.add(getOpenMRUDAction());
		}
				
		return mrudPopupMenu;
		
	}
	
	private JPopupMenu getOtherPopupMenu() {
		
		if( otherPopupMenu == null ) {
			otherPopupMenu = new JPopupMenu();
			
			otherPopupMenu.add(getOpenDocumentAction());
		}
				
		return otherPopupMenu;
		
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
	
	public OpenDocumentDialog getOpenDocumentDialog() {
		return openDocumentDialog;
	}

	public void setOpenDocumentDialog(OpenDocumentDialog openDocumentDialog) {
		this.openDocumentDialog = openDocumentDialog;
	}

	public ActivateDocumentAction getActivateDocumentAction() {
		if( activateDocumentAction == null ) {
			activateDocumentAction = new ActivateDocumentAction(messageSource, actionFocusManager, this);
		}
		return activateDocumentAction;
	}

	public OpenDocumentAction getOpenDocumentAction() {
		if( openDocumentAction == null ) {
			openDocumentAction = new OpenDocumentAction(messageSource);
			openDocumentAction.setDocumentFlowIntegrator(documentFlowIntegrator);
			openDocumentAction.setOpenDocumentDialog(openDocumentDialog);
		}
		return openDocumentAction;
	}

	public CloseDocumentAction getCloseDocumentAction() {
		if( closeDocumentAction == null ) {
			closeDocumentAction = new CloseDocumentAction(messageSource,this);
			closeDocumentAction.setDocumentFlowIntegrator(documentFlowIntegrator);
		}
		return closeDocumentAction;
	}

	public SaveDocumentAction getSaveDocumentAction() {
		if( saveDocumentAction == null ) {
			saveDocumentAction = new SaveDocumentAction(messageSource,this);
			saveDocumentAction.setDocumentFlowIntegrator(documentFlowIntegrator);
		}
		return saveDocumentAction;
	}

	public SaveDocumentAsAction getSaveDocumentAsAction() {
		if( saveDocumentAsAction == null ) {
			saveDocumentAsAction = new SaveDocumentAsAction(messageSource,this);
			saveDocumentAsAction.setDocumentFlowIntegrator(documentFlowIntegrator);
		}
		return saveDocumentAsAction;
	}

	public OpenMRUDAction getOpenMRUDAction() {
		if( openMRUDAction == null ) {
			openMRUDAction = new OpenMRUDAction(messageSource,this);
			openMRUDAction.setDocumentFlowIntegrator(documentFlowIntegrator);
		}
		return openMRUDAction;
	}
	
	@Override
	public void actionFocusChanged(ActionFocusEvent e) {
		Document document = actionFocusManager.getActiveDocument();
		if( document != null ) {					
			setSelectionPath(getModel().getTreePathToRoot(document));
		}
	}
		
	private class MouseEventHandler extends MouseAdapter {
						
		@Override
		public void mousePressed(MouseEvent e) {
			ViewerWorkspaceTree tree = (ViewerWorkspaceTree) e.getSource();
			if( SwingUtilities.isRightMouseButton(e) && (e.getClickCount() == 1) ) {
				TreePath selPath = tree.getPathForLocation(e.getX(), e.getY());
				tree.setSelectionPath(selPath);
			}
		}
		
		@Override
		public void mouseClicked(MouseEvent e) {
			ViewerWorkspaceTree tree = (ViewerWorkspaceTree) e.getSource();
			if( SwingUtilities.isLeftMouseButton(e) && (e.getClickCount() % 2) == 0 ) {
				int selRow = tree.getRowForLocation(e.getX(), e.getY());
				TreePath selPath = tree.getPathForLocation(e.getX(), e.getY());
				focus(selPath);
				if( selRow >= 0 ) {
					Object target = selPath.getLastPathComponent();
					if( target instanceof Document ) {
						getActivateDocumentAction().actionPerformed(new ActionEvent(tree,0,"activate"));
					} else if( target instanceof MRUDEntry ) {
						getOpenMRUDAction().actionPerformed(new ActionEvent(tree,0,"open"));
					}
					// ignore dbl clicks on other tree nodes 
				}
			}
		}
					
	}
	
}
