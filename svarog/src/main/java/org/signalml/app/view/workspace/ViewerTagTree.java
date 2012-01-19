/* ViewerTagTree.java created 2007-09-11
 *
 */
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
import org.signalml.app.action.selector.TagFocusSelector;
import org.signalml.app.action.selector.TagStyleFocusSelector;
import org.signalml.app.action.tag.ActivateTagAction;
import org.signalml.app.action.tag.EditTagAnnotationAction;
import org.signalml.app.action.tag.EditTagDescriptionAction;
import org.signalml.app.action.tag.EditTagStylesAction;
import org.signalml.app.action.tag.RemoveTagAction;
import org.signalml.app.document.DocumentFlowIntegrator;
import org.signalml.app.document.SignalDocument;
import org.signalml.app.document.TagDocument;
import org.signalml.app.model.tag.TagTreeModel;
import org.signalml.app.view.TagTreeCellRenderer;
import org.signalml.app.view.components.dialogs.EditTagAnnotationDialog;
import org.signalml.app.view.components.dialogs.EditTagDescriptionDialog;
import org.signalml.app.view.components.dialogs.TagStylePaletteDialog;
import org.signalml.app.view.signal.PositionedTag;
import org.signalml.plugin.export.signal.Document;
import org.signalml.plugin.export.signal.Tag;
import org.signalml.plugin.export.signal.TagStyle;
import org.signalml.plugin.export.view.AbstractViewerTree;
import org.signalml.plugin.impl.PluginAccessClass;

/** ViewerTagTree
 *
 *
 * @author Michal Dobaczewski &copy; 2007-2008 CC Otwarte Systemy Komputerowe Sp. z o.o.
 */
public class ViewerTagTree extends AbstractViewerTree implements TagFocusSelector, TagStyleFocusSelector {

	private static final long serialVersionUID = 1L;

	private ActionFocusSupport afSupport = new ActionFocusSupport(this);

	private JPopupMenu signalDocumentPopupMenu;
	private JPopupMenu tagDocumentPopupMenu;
	private JPopupMenu tagStylePopupMenu;
	private JPopupMenu tagPopupMenu;

	private ActionFocusManager actionFocusManager;
	private DocumentFlowIntegrator documentFlowIntegrator;
	private TagStylePaletteDialog tagStylePaletteDialog;
	private EditTagAnnotationDialog editTagAnnotationDialog;
	private EditTagDescriptionDialog editTagDescriptionDialog;

	private ActivateDocumentAction activateDocumentAction;
	private ActivateTagAction activateTagAction;
	private CloseDocumentAction closeDocumentAction;
	private RemoveTagAction removeTagAction;
	private EditTagAnnotationAction editTagAnnotationAction;
	private EditTagStylesAction editTagStylesAction;
	private EditTagDescriptionAction editTagDescriptionAction;

	private SignalDocument activeSignalDocument;
	private TagDocument activeTagDocument;
	private PositionedTag activeTag;
	private TagStyle activeTagStyle;

	public ViewerTagTree(TagTreeModel model) {
		super(model);
		setCellRenderer(new TagTreeCellRenderer(model.getIconProducer()));
		expandPath(new TreePath(new Object[] {model.getRoot()}));
		addMouseListener(new MouseEventHandler());
	}

	@Override
	public TagTreeModel getModel() {
		return (TagTreeModel) super.getModel();
	}

	@Override
	public PositionedTag getActiveTag() {
		return activeTag;
	}

	@Override
	public TagDocument getActiveTagDocument() {
		return activeTagDocument;
	}

	@Override
	public Document getActiveDocument() {
		return (activeTagDocument != null ? activeTagDocument : activeSignalDocument);
	}

	@Override
	public TagStyle getActiveTagStyle() {
		return activeTagStyle;
	}

	@Override
	public SignalDocument getActiveSignalDocument() {
		return activeSignalDocument;
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
		activeTagDocument = null;
		activeTag = null;
		activeTagStyle = null;

		if (path != null) {
			Object last = path.getLastPathComponent();
			if (last instanceof SignalDocument) {
				activeSignalDocument = (SignalDocument) last;
				popupMenu = getSignalDocumentPopupMenu();
			}
			else if (last instanceof TagDocument) {
				activeSignalDocument = (SignalDocument) path.getPathComponent(1);
				activeTagDocument = (TagDocument) last;
				popupMenu = getTagDocumentPopupMenu();
			}
			else if (last instanceof TagStyle) {
				activeTagStyle = (TagStyle) last;
				activeTagDocument = (TagDocument) path.getPathComponent(2);
				activeSignalDocument = (SignalDocument) path.getPathComponent(1);
				popupMenu = getTagStylePopupMenu();
			}
			else if (last instanceof Tag) {
				activeTagDocument = (TagDocument) path.getPathComponent(2);
				activeSignalDocument = (SignalDocument) path.getPathComponent(1);
				int index = activeSignalDocument.getTagDocuments().indexOf(activeTagDocument);
				activeTag = new PositionedTag((Tag) last,index);
				popupMenu = getTagPopupMenu();
			}
		}

		afSupport.fireActionFocusChanged();

		return popupMenu;

	}

	private JPopupMenu getSignalDocumentPopupMenu() {

		if (signalDocumentPopupMenu == null) {
			signalDocumentPopupMenu = new JPopupMenu();

			signalDocumentPopupMenu.add(getActivateDocumentAction());
			signalDocumentPopupMenu.addSeparator();
			signalDocumentPopupMenu.add(getCloseDocumentAction());
			
			PluginAccessClass.getGUIImpl().addToTagTreeSignalDocumentPopupMenu(signalDocumentPopupMenu);
		}

		return signalDocumentPopupMenu;

	}

	private JPopupMenu getTagDocumentPopupMenu() {

		if (tagDocumentPopupMenu == null) {
			tagDocumentPopupMenu = new JPopupMenu();

			tagDocumentPopupMenu.add(getActivateDocumentAction());
			tagDocumentPopupMenu.addSeparator();
			tagDocumentPopupMenu.add(getEditTagStylesAction());
			tagDocumentPopupMenu.add(getEditTagDescriptionAction());
			tagDocumentPopupMenu.addSeparator();
			tagDocumentPopupMenu.add(getCloseDocumentAction());
			
			PluginAccessClass.getGUIImpl().addToTagTreeTagDocumentPopupMenu(tagDocumentPopupMenu);
		}

		return tagDocumentPopupMenu;

	}

	private JPopupMenu getTagStylePopupMenu() {

		if (tagStylePopupMenu == null) {
			tagStylePopupMenu = new JPopupMenu();

			tagStylePopupMenu.add(getEditTagStylesAction());
			
			PluginAccessClass.getGUIImpl().addToTagTreeTagStylePopupMenu(tagStylePopupMenu);
		}

		return tagStylePopupMenu;

	}

	private JPopupMenu getTagPopupMenu() {

		if (tagPopupMenu == null) {
			tagPopupMenu = new JPopupMenu();

			tagPopupMenu.add(getActivateTagAction());
			tagPopupMenu.addSeparator();
			tagPopupMenu.add(getEditTagAnnotationAction());
			tagPopupMenu.addSeparator();
			tagPopupMenu.add(getRemoveTagAction());
			
			PluginAccessClass.getGUIImpl().addToTagTreeTagPopupMenu(tagPopupMenu);
		}

		return tagPopupMenu;

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

	public TagStylePaletteDialog getTagStylePaletteDialog() {
		return tagStylePaletteDialog;
	}

	public void setTagStylePaletteDialog(TagStylePaletteDialog tagStylePaletteDialog) {
		this.tagStylePaletteDialog = tagStylePaletteDialog;
	}

	public EditTagAnnotationDialog getEditTagAnnotationDialog() {
		return editTagAnnotationDialog;
	}

	public void setEditTagAnnotationDialog(EditTagAnnotationDialog editTagAnnotationDialog) {
		this.editTagAnnotationDialog = editTagAnnotationDialog;
	}

	public EditTagDescriptionDialog getEditTagDescriptionDialog() {
		return editTagDescriptionDialog;
	}

	public void setEditTagDescriptionDialog(EditTagDescriptionDialog editTagDescriptionDialog) {
		this.editTagDescriptionDialog = editTagDescriptionDialog;
	}

	public ActivateDocumentAction getActivateDocumentAction() {
		if (activateDocumentAction == null) {
			activateDocumentAction = new ActivateDocumentAction(actionFocusManager,this);
		}
		return activateDocumentAction;
	}

	public ActivateTagAction getActivateTagAction() {
		if (activateTagAction == null) {
			activateTagAction = new ActivateTagAction(actionFocusManager,this);
		}
		return activateTagAction;
	}

	public CloseDocumentAction getCloseDocumentAction() {
		if (closeDocumentAction == null) {
			closeDocumentAction = new CloseDocumentAction(this);
			closeDocumentAction.setDocumentFlowIntegrator(documentFlowIntegrator);
		}
		return closeDocumentAction;
	}

	public RemoveTagAction getRemoveTagAction() {
		if (removeTagAction == null) {
			removeTagAction = new RemoveTagAction(this);
		}
		return removeTagAction;
	}

	public EditTagAnnotationAction getEditTagAnnotationAction() {
		if (editTagAnnotationAction == null) {
			editTagAnnotationAction = new EditTagAnnotationAction(this);
			editTagAnnotationAction.setEditTagAnnotationDialog(editTagAnnotationDialog);
		}
		return editTagAnnotationAction;
	}

	public EditTagStylesAction getEditTagStylesAction() {
		if (editTagStylesAction == null) {
			editTagStylesAction = new EditTagStylesAction(this);
			editTagStylesAction.setTagStylePaletteDialog(tagStylePaletteDialog);
		}
		return editTagStylesAction;
	}

	public EditTagDescriptionAction getEditTagDescriptionAction() {
		if (editTagDescriptionAction == null) {
			editTagDescriptionAction = new EditTagDescriptionAction(this);
			editTagDescriptionAction.setEditTagDescriptionDialog(editTagDescriptionDialog);
		}
		return editTagDescriptionAction;
	}

	private class MouseEventHandler extends MouseAdapter {

		@Override
		public void mousePressed(MouseEvent e) {
			ViewerTagTree tree = (ViewerTagTree) e.getSource();
			if (SwingUtilities.isRightMouseButton(e) && (e.getClickCount() == 1)) {
				TreePath selPath = tree.getPathForLocation(e.getX(), e.getY());
				tree.setSelectionPath(selPath);
			}
		}

		@Override
		public void mouseClicked(MouseEvent e) {
			ViewerTagTree tree = (ViewerTagTree) e.getSource();
			if (SwingUtilities.isLeftMouseButton(e) && (e.getClickCount() % 2) == 0) {
				int selRow = tree.getRowForLocation(e.getX(), e.getY());
				TreePath selPath = tree.getPathForLocation(e.getX(), e.getY());
				focus(selPath);
				if (selRow >= 0) {
					Object target = selPath.getLastPathComponent();
					if (target instanceof Document) {
						getActivateDocumentAction().actionPerformed(new ActionEvent(tree,0,"activate"));
					}
					else if (target instanceof TagStyle) {
						getEditTagStylesAction().actionPerformed(new ActionEvent(tree,0,"edit"));
					}
					else if (target instanceof Tag) {
						getActivateTagAction().actionPerformed(new ActionEvent(tree,0,"activate"));
					}
					// ignore dbl clicks on other tree nodes
				}
			}
		}

	}


}

