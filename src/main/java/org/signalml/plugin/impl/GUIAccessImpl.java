/**
 * 
 */
package org.signalml.plugin.impl;

import java.awt.Component;
import java.awt.event.ActionListener;
import java.awt.event.MouseListener;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import javax.swing.Action;
import javax.swing.ButtonGroup;
import javax.swing.ButtonModel;
import javax.swing.Icon;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JToggleButton;
import javax.swing.JToolBar;

import org.apache.log4j.Logger;
import org.signalml.app.action.selector.ActionFocusManager;
import org.signalml.app.view.ViewerDocumentTabbedPane;
import org.signalml.app.view.ViewerElementManager;
import org.signalml.app.view.ViewerTabbedPane;
import org.signalml.app.view.signal.SignalView;
import org.signalml.plugin.export.NoActiveObjectException;
import org.signalml.plugin.export.signal.SignalTool;
import org.signalml.plugin.export.view.DocumentView;
import org.signalml.plugin.export.view.ExportedSignalPlot;
import org.signalml.plugin.export.view.SvarogAccessGUI;
import org.signalml.plugin.export.view.ViewerTreePane;

/**
 * The implementation of {@link SvarogAccessGUI}.
 * @author Marcin Szumski
 */
public class GUIAccessImpl implements SvarogAccessGUI {
	
	private static final Logger logger = Logger.getLogger(GUIAccessImpl.class);

	/**
	 * the manager of the elements of Svarog
	 */
	private ViewerElementManager manager;
	
	/**
	 * informs whether plug-in interface is in the initialization phase 
	 */
	private boolean initializationPhase = true;
	
	/**
	 * buttons that will be added to signal plot popup menu
	 */
	private ArrayList<Action> signalPlotPopupButtons = new ArrayList<Action>();
	/**
	 * menus that will be added to signal plot popup menu as submenus
	 */
	private ArrayList<JMenu> signalPlotPopupSubmenus = new ArrayList<JMenu>();
	/**
	 * Information about the order of elements that will be added to signal plot
	 * popup menu. If value is false next element from {@link #signalPlotPopupButtons}
	 * is taken, if true element from {@link #signalPlotPopupSubmenus} is used.
	 */
	private ArrayList<Boolean> signalPlotPopupIsSubmenu = new ArrayList<Boolean>();
	
	/**
	 * buttons that will be added to hypnogram plot popup menu
	 */
	private ArrayList<Action> hypnogramPlotPopupButtons = new ArrayList<Action>();
	private ArrayList<JMenu> hypnogramPlotPopupSubmenus = new ArrayList<JMenu>();
	private ArrayList<Boolean> hypnogramPlotPopupIsSubmenu = new ArrayList<Boolean>();
	
	/**
	 * buttons that will be added to column header popup menu
	 */
	private ArrayList<Action> columnHeaderPopupButtons = new ArrayList<Action>();
	private ArrayList<JMenu> columnHeaderPopupSubmenus = new ArrayList<JMenu>();
	private ArrayList<Boolean> columnHeaderPopupIsSubmenu = new ArrayList<Boolean>();
	
	
	private ArrayList<Action> workspaceTreeMRUDPopupButtons = new ArrayList<Action>();
	private ArrayList<JMenu> workspaceTreeMRUDPopupSubmenus = new ArrayList<JMenu>();
	private ArrayList<Boolean> workspaceTreeMRUDPopupIsSubmenu = new ArrayList<Boolean>();
	
	private ArrayList<Action> workspaceTreeDocumentPopupButtons = new ArrayList<Action>();
	private ArrayList<JMenu> workspaceTreeDocumentPopupSubmenus = new ArrayList<JMenu>();
	private ArrayList<Boolean> workspaceTreeDocumentPopupIsSubmenu = new ArrayList<Boolean>();
	
	private ArrayList<Action> workspaceTreeOtherPopupButtons = new ArrayList<Action>();
	private ArrayList<JMenu> workspaceTreeOtherPopupSubmenus = new ArrayList<JMenu>();
	private ArrayList<Boolean> workspaceTreeOtherPopupIsSubmenu = new ArrayList<Boolean>();
	
	
	private ArrayList<Action> tagTreeSignalDocumentPopupButtons = new ArrayList<Action>();
	private ArrayList<JMenu> tagTreeSignalDocumentPopupSubmenus = new ArrayList<JMenu>();
	private ArrayList<Boolean> tagTreeSignalDocumentPopupIsSubmenu = new ArrayList<Boolean>();
	
	private ArrayList<Action> tagTreeTagDocumentPopupButtons = new ArrayList<Action>();
	private ArrayList<JMenu> tagTreeTagDocumentPopupSubmenus = new ArrayList<JMenu>();
	private ArrayList<Boolean> tagTreeTagDocumentPopupIsSubmenu = new ArrayList<Boolean>();
	
	private ArrayList<Action> tagTreeTagStylePopupButtons = new ArrayList<Action>();
	private ArrayList<JMenu> tagTreeTagStylePopupSubmenus = new ArrayList<JMenu>();
	private ArrayList<Boolean> tagTreeTagStylePopupIsSubmenu = new ArrayList<Boolean>();

	private ArrayList<Action> tagTreeTagPopupButtons = new ArrayList<Action>();
	private ArrayList<JMenu> tagTreeTagPopupSubmenus = new ArrayList<JMenu>();
	private ArrayList<Boolean> tagTreeTagPopupIsSubmenu = new ArrayList<Boolean>();
	
	
	private ArrayList<Action> signalTreeDocumentPopupButtons = new ArrayList<Action>();
	private ArrayList<JMenu> signalTreeDocumentPopupSubmenus = new ArrayList<JMenu>();
	private ArrayList<Boolean> signalTreeDocumentPopupIsSubmenu = new ArrayList<Boolean>();
	
	private ArrayList<Action> signalTreeSignalPagePopupButtons = new ArrayList<Action>();
	private ArrayList<JMenu> signalTreeSignalPagePopupSubmenus = new ArrayList<JMenu>();
	private ArrayList<Boolean> signalTreeSignalPagePopupIsSubmenu = new ArrayList<Boolean>();
	
	
	private ArrayList<Action> bookTreeBookDocumentPopupButtons = new ArrayList<Action>();
	private ArrayList<JMenu> bookTreeBookDocumentPopupSubmenus = new ArrayList<JMenu>();
	private ArrayList<Boolean> bookTreeBookDocumentPopupIsSubmenu = new ArrayList<Boolean>();
	
	/**
	 * the list of signal tools
	 */
	private ArrayList<SignalTool> signalTools = new ArrayList<SignalTool>();
	
	/**
	 * for every signal tool the {@link ToolButtonParameters parameters} that
	 * will be used to create buttons for it
	 */
	private HashMap<SignalTool, ToolButtonParameters> parametersForToolButtons = new HashMap<SignalTool, ToolButtonParameters>();
	/**
	 * created buttons for signal tools. every view has its own button
	 */
	private HashMap<SignalTool, HashMap<SignalView, JToggleButton>> buttonsForTools = new HashMap<SignalTool, HashMap<SignalView,JToggleButton>>();
	
	/**
	 * the buttons that will be added to main toolbar in signalView
	 */
	private ArrayList<Action> actionsToMainSignalToolbar = new ArrayList<Action>();
	
	
	/* (non-Javadoc)
	 * @see org.signalml.plugin.export.PluginAccessGUI#addButtonToToolsMenu(javax.swing.AbstractAction)
	 */
	@Override
	public JMenuItem addButtonToToolsMenu(Action action) throws UnsupportedOperationException {
		if (!initializationPhase) throw new UnsupportedOperationException("operation can be performed only during initialization phase");
		JMenu toolsMenu = manager.getToolsMenu();
		return toolsMenu.add(action);
	}

	/* (non-Javadoc)
	 * @see org.signalml.plugin.export.PluginAccessGUI#addSubmenuToToolsMenu(javax.swing.JMenu)
	 */
	@Override
	public JMenuItem addSubmenuToToolsMenu(JMenu menu){
		if (!initializationPhase) throw new UnsupportedOperationException("operation can be performed only during initialization phase");
		JMenu toolsMenu = manager.getToolsMenu();
		return toolsMenu.add(menu);
	}

	/* (non-Javadoc)
	 * @see org.signalml.plugin.export.PluginAccessGUI#addButtonToEditMenu(javax.swing.AbstractAction)
	 */
	@Override
	public JMenuItem addButtonToEditMenu(Action action) throws UnsupportedOperationException {
		if (!initializationPhase) throw new UnsupportedOperationException("operation can be performed only during initialization phase");
		JMenu editMenu = manager.getEditMenu();
		return editMenu.add(action);
	}

	/* (non-Javadoc)
	 * @see org.signalml.plugin.export.PluginAccessGUI#addSubmenuToEditMenu(javax.swing.JMenu)
	 */
	@Override
	public JMenuItem addSubmenuToEditMenu(JMenu menu) throws UnsupportedOperationException {
		if (!initializationPhase) throw new UnsupportedOperationException("operation can be performed only during initialization phase");
		JMenu editMenu = manager.getEditMenu();
		return editMenu.add(menu);
	}

	/**
	 * Adds a button to the given list and adds information that it
	 * was a button to given order list.
	 * @param action the button to be added
	 * @param actions the list to which the button will be added
	 * @param isSubmenu the list in which order is stored
	 * @throws UnsupportedOperationException if it is not an initialization phase
	 */
	private void delayedAddButton(Action action, ArrayList<Action> actions, ArrayList<Boolean> isSubmenu) throws UnsupportedOperationException{
		if (!initializationPhase) throw new UnsupportedOperationException("operation can be performed only during initialization phase");
		actions.add(action);
		isSubmenu.add(false);
	}
	
	/**
	 * Adds a submenu to the given list and adds information that it
	 * was a submenu to given order list.
	 * @param menu the submenu to be added
	 * @param submenus the list to which the button will be added
	 * @param isSubmenu the list in which order is stored
	 * @throws UnsupportedOperationException if it is not an initialization phase
	 */
	private void delayedAddSubMenu(JMenu menu, ArrayList<JMenu> submenus, ArrayList<Boolean> isSubmenu) throws UnsupportedOperationException{
		if (!initializationPhase) throw new UnsupportedOperationException("operation can be performed only during initialization phase");
		submenus.add(menu);
		isSubmenu.add(true);
	}
	
	/**
	 * Adds buttons and submenus from given lists to the given menu.
	 * To determine the order of adding uses {@code isSubmenu} array.
	 * @param menu the menu to which elements are added
	 * @param actions the list with buttons
	 * @param submenus the list with submenus
	 * @param isSubmenu the list in which order is stored.
	 * If successive value is false next element from {@code actions}
	 * is taken, if true element from {@cod submenus} is used.
	 */
	private void addToPopupMenu(JPopupMenu menu, ArrayList<Action> actions, ArrayList<JMenu> submenus, ArrayList<Boolean> isSubmenu){
		try {
			int iSubmenu=0, iButton=0;
			if (isSubmenu.size() != (submenus.size() + actions.size())){
				//throw new RuntimeException("lists for submenus and buttons are invalid");
			}
			for (Boolean isMenu : isSubmenu){
				if (isMenu){
					menu.add(submenus.get(iSubmenu++));
				} else {
					menu.add(actions.get(iButton++));
				}
			}
		} catch (Exception e) {
			logger.error("Error in plug-in interface while adding elements to pop-up menu");
			e.printStackTrace();
		}
	}
	
	/* (non-Javadoc)
	 * @see org.signalml.plugin.export.PluginAccessGUI#addButtonToSignalPlotPopupMenu(javax.swing.AbstractAction)
	 */
	@Override
	public void addButtonToSignalPlotPopupMenu(Action action) throws UnsupportedOperationException {
		delayedAddButton(action, signalPlotPopupButtons, signalPlotPopupIsSubmenu);
	}

	/* (non-Javadoc)
	 * @see org.signalml.plugin.export.PluginAccessGUI#addSubMenuToSignalPlotPopupMenu(javax.swing.JMenu)
	 */
	@Override
	public void addSubMenuToSignalPlotPopupMenu(JMenu menu) throws UnsupportedOperationException {
		delayedAddSubMenu(menu, signalPlotPopupSubmenus, signalPlotPopupIsSubmenu);
	}
	
	/**
	 * Adds buttons and submenus to signal plot popup menu.
	 * @param menu the menu to which elements are added
	 */
	public void addToSignalPlotPopupMenu(JPopupMenu menu){
		addToPopupMenu(menu, signalPlotPopupButtons, signalPlotPopupSubmenus, signalPlotPopupIsSubmenu);
	}
	
	/* (non-Javadoc)
	 * @see org.signalml.plugin.export.PluginAccessGUI#addButtonToHypnogramPlotPopupMenu(javax.swing.AbstractAction)
	 */
	@Override
	public void addButtonToHypnogramPlotPopupMenu(Action action) throws UnsupportedOperationException {
		delayedAddButton(action, hypnogramPlotPopupButtons, hypnogramPlotPopupIsSubmenu);
	}

	/* (non-Javadoc)
	 * @see org.signalml.plugin.export.PluginAccessGUI#addSubMenuToHypnogramPlotPopupMenu(javax.swing.JMenu)
	 */
	@Override
	public void addSubMenuToHypnogramPlotPopupMenu(JMenu menu) throws UnsupportedOperationException {
		delayedAddSubMenu(menu, hypnogramPlotPopupSubmenus, hypnogramPlotPopupIsSubmenu);
	}

	/**
	 * Adds buttons and submenus to hypnogram plot popup menu.
	 * @param menu the menu to which elements are added
	 */
	public void addToHypnogramPlotPopupMenu(JPopupMenu menu){
		addToPopupMenu(menu, hypnogramPlotPopupButtons, hypnogramPlotPopupSubmenus, hypnogramPlotPopupIsSubmenu);
	}
	
	/* (non-Javadoc)
	 * @see org.signalml.plugin.export.PluginAccessGUI#addButtonToColumnHeaderPopupMenu(javax.swing.AbstractAction)
	 */
	@Override
	public void addButtonToColumnHeaderPopupMenu(Action action) throws UnsupportedOperationException {
		delayedAddButton(action, columnHeaderPopupButtons, columnHeaderPopupIsSubmenu);
	}

	/* (non-Javadoc)
	 * @see org.signalml.plugin.export.PluginAccessGUI#addSubMenuToColumnHeaderPopupMenu(javax.swing.JMenu)
	 */
	@Override
	public void addSubMenuToColumnHeaderPopupMenu(JMenu menu) throws UnsupportedOperationException {
		delayedAddSubMenu(menu, columnHeaderPopupSubmenus, columnHeaderPopupIsSubmenu);
	}
	
	/**
	 * Adds buttons and submenus to column header popup menu.
	 * @param menu the menu to which elements are added
	 */
	public void addToColumnHeaderPopupMenu(JPopupMenu menu){
		addToPopupMenu(menu, columnHeaderPopupButtons, columnHeaderPopupSubmenus, columnHeaderPopupIsSubmenu);
	}

	/* (non-Javadoc)
	 * @see org.signalml.plugin.export.PluginAccessGUI#addSignalTool(javax.swing.JToggleButton, org.signalml.plugin.export.SignalTool)
	 */
	@Override
	public void addSignalTool(SignalTool tool, Icon icon, String toolTipText, MouseListener buttonListener) throws UnsupportedOperationException {
		if (!initializationPhase) throw new UnsupportedOperationException("operation can be performed only during initialization phase");
		signalTools.add(tool);
		ToolButtonParameters parameters = new ToolButtonParameters(toolTipText, icon, buttonListener);
		parametersForToolButtons.put(tool, parameters);
		buttonsForTools.put(tool, new HashMap<SignalView, JToggleButton>());
	}

	/**
	 * Performs all operations necessary to register signal tools for a signal view:
	 * <ul>
	 * <li>adds buttons and associated tools to {@code toolMap},</li>
	 * <li>adds buttons to button group ({@code toolButtonGroup}),</li>
	 * <li>registers a listener ({@code toolSelectionListener}) in buttons,</li>
	 * <li>sets the {@code view} in tools.</li>
	 * </ul>
	 * @param toolMap the map associating buttons with signal tools
	 * @param toolButtonGroup the group of buttons associated with signal tools
	 * @param toolSelectionListener the listener which signal tool is selected
	 * @param view the view for which the tools are registered
	 */
	public void registerSignalTools(Map<ButtonModel, SignalTool> toolMap, ButtonGroup toolButtonGroup, ActionListener toolSelectionListener, SignalView view){
		try {
			for (SignalTool tool : signalTools){
				try{
					SignalTool signalTool = tool.createCopy();
					signalTool.setSignalView(view);
					ToolButtonParameters parameters = parametersForToolButtons.get(tool);
					JToggleButton button = new JToggleButton(parameters.getIcon());
					if (parameters.getToolTipText() != null)
						button.setToolTipText(parameters.getToolTipText());
					if (parameters.getListener() != null)
						button.addMouseListener(parameters.getListener());
					HashMap<SignalView, JToggleButton> buttonsForViews = buttonsForTools.get(tool);
					buttonsForViews.put(view, button);
					toolMap.put(button.getModel(), signalTool);
					toolButtonGroup.add(button);
					button.addActionListener(toolSelectionListener);
				} catch (Exception e) {
					logger.error("Failed to register signal tool " + (tool != null ? tool.toString() : ""));
					e.printStackTrace();
				}
			}
		} catch (Exception e) {
			logger.error("Unknown error in plug-in interface during the registration of signal tools");
			e.printStackTrace();		}
	}
	
	/**
	 * Adds buttons associated with signal tools to the main toolbar.
	 * @param mainToolBar the main toolbar.
	 * @param view the view for which the tools are registered
	 */
	public void toolsToMainMenu(JToolBar mainToolBar, SignalView view){
		try{
			for (HashMap<SignalView, JToggleButton> buttonsForViews : buttonsForTools.values()){
				JToggleButton button = buttonsForViews.get(view);
				mainToolBar.add(button);
			}
		} catch (Exception e) {
			logger.error("Unknown error in plug-in interface during the addition of buttons for signal tools");
			e.printStackTrace();
		}
	}
	
	/* (non-Javadoc)
	 * @see org.signalml.plugin.export.PluginAccessGUI#addButtonToMainToolbar(javax.swing.AbstractAction)
	 */
	@Override
	public void addButtonToMainToolbar(Action action) throws UnsupportedOperationException {
		if (!initializationPhase) throw new UnsupportedOperationException("operation can be performed only during initialization phase");
		actionsToMainSignalToolbar.add(action);
	}

	/**
	 * Adds buttons to main toolbar in the signal view.
	 * @param mainToolBar the main toolbar.
	 */
	public void addToMainSignalToolBar(JToolBar mainToolBar){
		try{
			for (Action action : actionsToMainSignalToolbar){
				mainToolBar.add(action);
			}
		} catch (Exception e) {
			logger.error("Unknown error in plug-in interface during the addition of buttons to main toolbar");
			e.printStackTrace();
		}
	}

	/* (non-Javadoc)
	 * @see org.signalml.plugin.export.PluginAccessGUI#addTreeTab(org.signalml.plugin.export.AbstractViewerTree)
	 */
	@Override
	public void addTreeTab(ViewerTreePane treePane, String title, Icon icon, String tip) {
		ViewerTabbedPane pane = manager.getTreeTabbedPane();
		String tabTitle = (title == null ? treePane.getName() : title);
		if (tip != null)
			pane.addTab(tabTitle, icon, treePane, tip);
		else pane.addTab(tabTitle, icon, treePane);

	}

	/* (non-Javadoc)
	 * @see org.signalml.plugin.export.PluginAccessGUI#addPropertyTab(javax.swing.JPanel)
	 */
	@Override
	public void addPropertyTab(JPanel panel) {
		ViewerTabbedPane pane = manager.getPropertyTabbedPane();
		pane.addTab(panel.getName(), null, panel, panel.getToolTipText());
	}

	/**
	 * @param manager the element manager to set
	 */
	public void setManager(ViewerElementManager manager) {
		this.manager = manager;
	}

	/**
	 * @param initializationPhase true if it is an initialization phase,
	 * false otherwise
	 */
	public void setInitializationPhase(boolean initializationPhase) {
		this.initializationPhase = initializationPhase;
	}


	/* (non-Javadoc)
	 * @see org.signalml.plugin.export.view.PluginAccessGUI#getActiveSignalPlot()
	 */
	@Override
	public ExportedSignalPlot getActiveSignalPlot() throws NoActiveObjectException {
		if (manager == null) return null;
		ActionFocusManager focusManager = manager.getActionFocusManager();
		if (focusManager == null) return null;
		return focusManager.getActiveSignalPlot();
	}

	/*
	@Override
	public boolean addComponentToActiveSignalPlotPane(JComponent component) {
		if (manager == null) return false;
		ActionFocusManager focusManager = manager.getActionFocusManager();
		if (focusManager == null) return false;
		SignalPlot plot = focusManager.getActiveSignalPlot();
		if (plot == null) return false;
		Component result = plot.add(component);
		return (result != null);
	}*/

	/* (non-Javadoc)
	 * @see org.signalml.plugin.export.view.PluginAccessGUI#addButtonToWorkspaceTreeMRUDPopupMenu(javax.swing.Action)
	 */
	@Override
	public void addButtonToWorkspaceTreeMRUDPopupMenu(Action action) throws UnsupportedOperationException {
		delayedAddButton(action, workspaceTreeMRUDPopupButtons, workspaceTreeMRUDPopupIsSubmenu);
	}
	
	/* (non-Javadoc)
	 * @see org.signalml.plugin.export.view.PluginAccessGUI#addSubMenuToWorkspaceTreeMRUDPopupMenu(javax.swing.JMenu)
	 */
	@Override
	public void addSubMenuToWorkspaceTreeMRUDPopupMenu(JMenu menu) throws UnsupportedOperationException {
		delayedAddSubMenu(menu, workspaceTreeMRUDPopupSubmenus, workspaceTreeMRUDPopupIsSubmenu);
	}

	/**
	 * Adds buttons and submenus to the popup menu in workspace tree tab, which appears after clicking
	 * on a MRUD entry node.
	 * @param menu the menu to which elements are added
	 */
	public void addToWorkspaceTreeMRUDPopupMenu(JPopupMenu menu){
		addToPopupMenu(menu, workspaceTreeMRUDPopupButtons, workspaceTreeMRUDPopupSubmenus, workspaceTreeMRUDPopupIsSubmenu);
	}
	
	/* (non-Javadoc)
	 * @see org.signalml.plugin.export.view.PluginAccessGUI#addButtonToWorkspaceTreeDocumentPopupMenu(javax.swing.Action)
	 */
	@Override
	public void addButtonToWorkspaceTreeDocumentPopupMenu(Action action) throws UnsupportedOperationException {
		delayedAddButton(action, workspaceTreeDocumentPopupButtons, workspaceTreeDocumentPopupIsSubmenu);
	}

	/* (non-Javadoc)
	 * @see org.signalml.plugin.export.view.PluginAccessGUI#addSubMenuToWorkspaceTreeDocumentPopupMenu(javax.swing.JMenu)
	 */
	@Override
	public void addSubMenuToWorkspaceTreeDocumentPopupMenu(JMenu menu) throws UnsupportedOperationException {
		delayedAddSubMenu(menu, workspaceTreeDocumentPopupSubmenus, workspaceTreeDocumentPopupIsSubmenu);
	}
	
	/**
	 * Adds buttons and submenus to the popup menu in workspace tree tab, which appears after clicking
	 * on a document node.
	 * @param menu the menu to which elements are added
	 */
	public void addToWorkspaceTreeDocumentPopupMenu(JPopupMenu menu){
		addToPopupMenu(menu, workspaceTreeDocumentPopupButtons, workspaceTreeDocumentPopupSubmenus, workspaceTreeDocumentPopupIsSubmenu);
	}
	
	/* (non-Javadoc)
	 * @see org.signalml.plugin.export.view.PluginAccessGUI#addButtonToWorkspaceTreeOtherPopupMenu(javax.swing.Action)
	 */
	@Override
	public void addButtonToWorkspaceTreeOtherPopupMenu(Action action) throws UnsupportedOperationException {
		delayedAddButton(action, workspaceTreeOtherPopupButtons, workspaceTreeOtherPopupIsSubmenu);
	}

	/* (non-Javadoc)
	 * @see org.signalml.plugin.export.view.PluginAccessGUI#addSubMenuToWorkspaceTreeOtherPopupMenu(javax.swing.JMenu)
	 */
	@Override
	public void addSubMenuToWorkspaceTreeOtherPopupMenu(JMenu menu) throws UnsupportedOperationException {
		delayedAddSubMenu(menu, workspaceTreeOtherPopupSubmenus, workspaceTreeOtherPopupIsSubmenu);
	}

	/**
	 * Adds buttons and submenus to the popup menu in workspace tree tab, which appears after clicking
	 * not on a MRUD entry node and not on a document node.
	 * @param menu the menu to which elements are added
	 */
	public void addToWorkspaceTreeOtherPopupMenu(JPopupMenu menu){
		addToPopupMenu(menu, workspaceTreeOtherPopupButtons, workspaceTreeOtherPopupSubmenus, workspaceTreeOtherPopupIsSubmenu);
	}
	
	/* (non-Javadoc)
	 * @see org.signalml.plugin.export.view.PluginAccessGUI#addButtonToTagTreeSignalDocumentPopupMenu(javax.swing.Action)
	 */
	@Override
	public void addButtonToTagTreeSignalDocumentPopupMenu(Action action) throws UnsupportedOperationException {
		delayedAddButton(action, tagTreeSignalDocumentPopupButtons, tagTreeSignalDocumentPopupIsSubmenu);
	}

	/* (non-Javadoc)
	 * @see org.signalml.plugin.export.view.PluginAccessGUI#addButtonToTagTreeTagDocumentPopupMenu(javax.swing.Action)
	 */
	@Override
	public void addButtonToTagTreeTagDocumentPopupMenu(Action action) throws UnsupportedOperationException {
		delayedAddButton(action, tagTreeTagDocumentPopupButtons, tagTreeTagDocumentPopupIsSubmenu);
	}

	/* (non-Javadoc)
	 * @see org.signalml.plugin.export.view.PluginAccessGUI#addButtonToTagTreeTagStylePopupMenu(javax.swing.Action)
	 */
	@Override
	public void addButtonToTagTreeTagStylePopupMenu(Action action) throws UnsupportedOperationException {
		delayedAddButton(action, tagTreeTagStylePopupButtons, tagTreeTagStylePopupIsSubmenu);
	}

	/* (non-Javadoc)
	 * @see org.signalml.plugin.export.view.PluginAccessGUI#addButtonToTagTreePopuTagpMenu(javax.swing.Action)
	 */
	@Override
	public void addButtonToTagTreeTagPopupMenu(Action action) throws UnsupportedOperationException {
		delayedAddButton(action, tagTreeTagPopupButtons, tagTreeTagPopupIsSubmenu);
	}

	/* (non-Javadoc)
	 * @see org.signalml.plugin.export.view.PluginAccessGUI#addSubMenuToTagTreeSignalDocumentPopupMenu(javax.swing.JMenu)
	 */
	@Override
	public void addSubMenuToTagTreeSignalDocumentPopupMenu(JMenu menu) throws UnsupportedOperationException {
		delayedAddSubMenu(menu, tagTreeSignalDocumentPopupSubmenus, tagTreeSignalDocumentPopupIsSubmenu);
	}

	/* (non-Javadoc)
	 * @see org.signalml.plugin.export.view.PluginAccessGUI#addSubMenuToTagTreeTagDocumentPopupMenu(javax.swing.JMenu)
	 */
	@Override
	public void addSubMenuToTagTreeTagDocumentPopupMenu(JMenu menu) throws UnsupportedOperationException {
		delayedAddSubMenu(menu, tagTreeTagDocumentPopupSubmenus, tagTreeTagDocumentPopupIsSubmenu);
	}

	/* (non-Javadoc)
	 * @see org.signalml.plugin.export.view.PluginAccessGUI#addSubMenuToTagTreeTagStylePopupMenu(javax.swing.JMenu)
	 */
	@Override
	public void addSubMenuToTagTreeTagStylePopupMenu(JMenu menu) throws UnsupportedOperationException {
		delayedAddSubMenu(menu, tagTreeTagStylePopupSubmenus, tagTreeTagStylePopupIsSubmenu);
	}

	/* (non-Javadoc)
	 * @see org.signalml.plugin.export.view.PluginAccessGUI#addSubMenuToTagTreeTagPopupMenu(javax.swing.JMenu)
	 */
	@Override
	public void addSubMenuToTagTreeTagPopupMenu(JMenu menu) throws UnsupportedOperationException {
		delayedAddSubMenu(menu, tagTreeTagPopupSubmenus, tagTreeTagPopupIsSubmenu);
	}

	/**
	 * Adds buttons and submenus to the popup menu in tag tree tab, which appears after clicking
	 * on a signal document node.
	 * @param menu the menu to which elements are added
	 */
	public void addToTagTreeSignalDocumentPopupMenu(JPopupMenu menu){
		addToPopupMenu(menu, tagTreeSignalDocumentPopupButtons, tagTreeSignalDocumentPopupSubmenus, tagTreeSignalDocumentPopupIsSubmenu);
	}
	
	/**
	 * Adds buttons and submenus to the popup menu in tag tree tab, which appears after clicking
	 * on a tag document node.
	 * @param menu the menu to which elements are added
	 */
	public void addToTagTreeTagDocumentPopupMenu(JPopupMenu menu){
		addToPopupMenu(menu, tagTreeTagDocumentPopupButtons, tagTreeTagDocumentPopupSubmenus, tagTreeTagDocumentPopupIsSubmenu);
	}
	
	/**
	 * Adds buttons and submenus to the popup menu in tag tree tab, which appears after clicking
	 * on a tag style node.
	 * @param menu the menu to which elements are added
	 */
	public void addToTagTreeTagStylePopupMenu(JPopupMenu menu){
		addToPopupMenu(menu, tagTreeTagStylePopupButtons, tagTreeTagStylePopupSubmenus, tagTreeTagStylePopupIsSubmenu);
	}

	/**
	 * Adds buttons and submenus to the popup menu in tag tree tab, which appears after clicking
	 * on a tag node.
	 * @param menu the menu to which elements are added
	 */
	public void addToTagTreeTagPopupMenu(JPopupMenu menu){
		addToPopupMenu(menu, tagTreeTagPopupButtons, tagTreeTagPopupSubmenus, tagTreeTagPopupIsSubmenu);
	}
	
	/* (non-Javadoc)
	 * @see org.signalml.plugin.export.view.PluginAccessGUI#addButtonToSignalTreeDocumentPopupMenu(javax.swing.Action)
	 */
	@Override
	public void addButtonToSignalTreeDocumentPopupMenu(Action action) throws UnsupportedOperationException {
		delayedAddButton(action, signalTreeDocumentPopupButtons, signalTreeDocumentPopupIsSubmenu);
	}

	/* (non-Javadoc)
	 * @see org.signalml.plugin.export.view.PluginAccessGUI#addButtonToSignalTreeSignalPagePopupMenu(javax.swing.Action)
	 */
	@Override
	public void addButtonToSignalTreeSignalPagePopupMenu(Action action) throws UnsupportedOperationException {
		delayedAddButton(action, signalTreeSignalPagePopupButtons, signalTreeSignalPagePopupIsSubmenu);
	}

	/* (non-Javadoc)
	 * @see org.signalml.plugin.export.view.PluginAccessGUI#addSubMenuToSignalTreeDocumentPopupMenu(javax.swing.JMenu)
	 */
	@Override
	public void addSubMenuToSignalTreeDocumentPopupMenu(JMenu menu) throws UnsupportedOperationException {
		delayedAddSubMenu(menu, signalTreeDocumentPopupSubmenus, signalTreeDocumentPopupIsSubmenu);
	}

	/* (non-Javadoc)
	 * @see org.signalml.plugin.export.view.PluginAccessGUI#addSubMenuToSignalTreeSignalPagePopupMenu(javax.swing.JMenu)
	 */
	@Override
	public void addSubMenuToSignalTreeSignalPagePopupMenu(JMenu menu) throws UnsupportedOperationException {
		delayedAddSubMenu(menu, signalTreeSignalPagePopupSubmenus, signalTreeSignalPagePopupIsSubmenu);
	}
	
	/**
	 * Adds buttons and submenus to the popup menu in signal tree tab, which appears after clicking
	 * on a signal document node.
	 * @param menu the menu to which elements are added
	 */
	public void addToSignalTreeDocumentPopupMenu(JPopupMenu menu){
		addToPopupMenu(menu, signalTreeDocumentPopupButtons, signalTreeDocumentPopupSubmenus, signalTreeDocumentPopupIsSubmenu);
	}
	
	/**
	 * Adds buttons and submenus to the popup menu in signal tree tab, which appears after clicking
	 * on a signal page node.
	 * @param menu the menu to which elements are added
	 */
	public void addToSignalTreeSignalPagePopupMenu(JPopupMenu menu){
		addToPopupMenu(menu, signalTreeSignalPagePopupButtons, signalTreeSignalPagePopupSubmenus, signalTreeSignalPagePopupIsSubmenu);
	}

	/* (non-Javadoc)
	 * @see org.signalml.plugin.export.view.PluginAccessGUI#addButtonToBookTreeBookDocumentPopupMenu(javax.swing.Action)
	 */
	@Override
	public void addButtonToBookTreeBookDocumentPopupMenu(Action action) throws UnsupportedOperationException {
		delayedAddButton(action, bookTreeBookDocumentPopupButtons, bookTreeBookDocumentPopupIsSubmenu);
	}

	/* (non-Javadoc)
	 * @see org.signalml.plugin.export.view.PluginAccessGUI#addSubMenuToBookTreeBookDocumentPopupMenu(javax.swing.JMenu)
	 */
	@Override
	public void addSubMenuToBookTreeBookDocumentPopupMenu(JMenu menu) throws UnsupportedOperationException {
		delayedAddSubMenu(menu, bookTreeBookDocumentPopupSubmenus, bookTreeBookDocumentPopupIsSubmenu);
	}
	
	/**
	 * Adds buttons and submenus to the popup menu in book tree tab, which appears after clicking
	 * on a document node.
	 * @param menu the menu to which elements are added
	 */
	public void addToBookTreeBookDocumentPopupMenu(JPopupMenu menu){
		addToPopupMenu(menu, bookTreeBookDocumentPopupButtons, bookTreeBookDocumentPopupSubmenus, bookTreeBookDocumentPopupIsSubmenu);
	}

	@Override
	public void removePropertyTab(JPanel panel) throws IllegalArgumentException {
		ViewerTabbedPane pane = manager.getPropertyTabbedPane();
		if (pane.indexOfComponent(panel) == -1) throw new IllegalArgumentException("tab with this panel doesn't exist");
		pane.remove(panel);
	}

	@Override
	public void addMainTab(DocumentView tab, String title, Icon icon, String tip) {
		ViewerDocumentTabbedPane documentTabbedPane = manager.getDocumentTabbedPane();
		String tabTitle = (title == null ? tab.getName() : title);
		if (tip == null){
			documentTabbedPane.addTab(tabTitle, icon, tab);
		} else {
			documentTabbedPane.addTab(tabTitle, icon, tab, tip);
		}
	}

	@Override
	public void removeMainTab(DocumentView tab) throws IllegalArgumentException {
		ViewerDocumentTabbedPane documentTabbedPane = manager.getDocumentTabbedPane();
		if (documentTabbedPane.indexOfComponent(tab) != -1){
			documentTabbedPane.remove(tab);
		} else
			throw new IllegalArgumentException("tab with this view doesn't exist");
	}

	@Override
	public DocumentView getSelectedMainTab() throws NoActiveObjectException {
		ViewerDocumentTabbedPane documentTabbedPane = manager.getDocumentTabbedPane();
		Component component = documentTabbedPane.getSelectedComponent();
		if (component instanceof DocumentView)
			return (DocumentView) component;
		else
			throw new NoActiveObjectException("no active main tab");
	}

	@Override
	public void removeTreeTab(ViewerTreePane tab) throws IllegalArgumentException {
		ViewerTabbedPane viewerTreePane = manager.getTreeTabbedPane();
		if (viewerTreePane.indexOfComponent(tab) != -1){
			viewerTreePane.remove(tab);
		} else
			throw new IllegalArgumentException("tab with this panel doesn't exist");
	}

	@Override
	public ViewerTreePane getSelectedTreeTab() throws NoActiveObjectException {
		ViewerTabbedPane viewerTabbedPane = manager.getTreeTabbedPane();
		Component component = viewerTabbedPane.getSelectedComponent();
		if (component instanceof ViewerTreePane)
			return (ViewerTreePane) component;
		else
			throw new NoActiveObjectException("no active tree tab");
	}

	@Override
	public JPanel getSelectedPropertyTab() throws NoActiveObjectException {
		ViewerTabbedPane viewerTabbedPane = manager.getPropertyTabbedPane();
		Component component = viewerTabbedPane.getSelectedComponent();
		if (component instanceof JPanel)
			return (JPanel) component;
		else
			throw new NoActiveObjectException("no active property tab");
	}
	

}
