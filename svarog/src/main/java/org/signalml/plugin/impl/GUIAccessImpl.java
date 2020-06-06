/**
 *
 */
package org.signalml.plugin.impl;

import java.awt.Component;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import javax.swing.AbstractAction;

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
import org.signalml.app.view.signal.SignalView;
import org.signalml.app.view.workspace.ViewerDocumentTabbedPane;
import org.signalml.app.view.workspace.ViewerElementManager;
import org.signalml.app.view.workspace.ViewerTabbedPane;
import org.signalml.plugin.export.NoActiveObjectException;
import org.signalml.plugin.export.signal.SignalTool;
import org.signalml.plugin.export.view.AbstractSignalMLAction;
import org.signalml.plugin.export.view.DocumentView;
import org.signalml.plugin.export.view.ExportedSignalPlot;
import org.signalml.plugin.export.view.FileChooser;
import org.signalml.plugin.export.view.SvarogAccessGUI;
import org.signalml.plugin.export.view.ViewerTreePane;

/**
 * The implementation of {@link SvarogAccessGUI}.
 * Contains:
 * <ul>
 * <li>the {@link ViewerElementManager element manager} - allows to access
 * elements of Svarog. It is used to add/remove tabs, add elements to main menu,
 * get active tabs</li>
 * <li>array of buttons and sub-menus for every pop-up menu and for menus in
 * the signal view - these elements are added, when a specific menu is created.
 * Sub-menus are also copied (only actions are copied).</li>
 * <li>collection of {@link SignalTool signal tools} and buttons for them
 * (actually the {@link ToolButtonParameters parameters} of these buttons) -
 * when a new {@link SignalView signal view} is created, the new buttons and
 * the copies of tools are created and added to this view.</li>
 * <li>a boolean which indicates if currently is an initialization phase</li
 * </ul>
 * @author Marcin Szumski
 */
public class GUIAccessImpl extends AbstractAccess implements SvarogAccessGUI {

	private static final Logger logger = Logger.getLogger(GUIAccessImpl.class);

	/**
	 * informs whether plug-in interface is in the initialization phase
	 */
	private boolean initializationPhase = true;

	/**
	 * actions that will be used to create buttons that will be added to
	 * the pop-up menu that appears when a signal plot is right-clicked
	 */
	private ArrayList<Action> signalPlotPopupButtons = new ArrayList<Action>();
	/**
	 * menus which will be copied (only actions in them) and that copies will
	 * be added the pop-up menu that appears when
	 * a signal plot is right-clicked
	 */
	private ArrayList<JMenu> signalPlotPopupSubmenus = new ArrayList<JMenu>();
	/**
	 * Information about the order of elements that will be added to
	 * the pop-up menu that appears when a signal plot is right-clicked.
	 * If value is false next element from {@link #signalPlotPopupButtons}
	 * is taken, if true element from {@link #signalPlotPopupSubmenus} is used.
	 */
	private ArrayList<Boolean> signalPlotPopupIsSubmenu = new ArrayList<Boolean>();

	/**
	 * actions that will be used to create buttons that will be added to
	 * the pop-up menu that appears when a hypnogram plot is right-clicked
	 */
	private ArrayList<Action> hypnogramPlotPopupButtons = new ArrayList<Action>();
	/**
	 * menus which will be copied (only actions in them) and that copies will
	 * be added the pop-up menu that appears when
	 * a hypnogram plot is right-clicked
	 */
	private ArrayList<JMenu> hypnogramPlotPopupSubmenus = new ArrayList<JMenu>();
	/**
	 * Information about the order of elements that will be added to
	 * the pop-up menu that appears when a hypnogram plot is right-clicked.
	 * If value is false next element from {@link #hypnogramPlotPopupButtons}
	 * is taken, if true element from {@link #hypnogramPlotPopupSubmenus} is used.
	 */
	private ArrayList<Boolean> hypnogramPlotPopupIsSubmenu = new ArrayList<Boolean>();

	/**
	 * actions that will be used to create buttons that will be added to
	 * the pop-up menu that appears when a column header is right-clicked
	 */
	private ArrayList<Action> columnHeaderPopupButtons = new ArrayList<Action>();
	/**
	 * menus which will be copied (only actions in them) and that copies will
	 * be added the pop-up menu that appears when
	 * a column header is right-clicked
	 */
	private ArrayList<JMenu> columnHeaderPopupSubmenus = new ArrayList<JMenu>();
	/**
	 * Information about the order of elements that will be added to
	 * the pop-up menu that appears when a column header is right-clicked.
	 * If value is false next element from {@link #columnHeaderPopupButtons}
	 * is taken, if true element from {@link #columnHeaderPopupSubmenus} is used.
	 */
	private ArrayList<Boolean> columnHeaderPopupIsSubmenu = new ArrayList<Boolean>();

	/**
	 * actions that will be used to create buttons that will be added to
	 * the pop-up menu that appears when a MRUD entry node in the workspace tree
	 * tab (tab "workspace" in the pane on the left) is right-clicked
	 */
	private ArrayList<Action> workspaceTreeMRUDPopupButtons = new ArrayList<Action>();
	/**
	 * menus which will be copied (only actions in them) and that copies will
	 * be added the pop-up menu that appears when a MRUD entry node in the workspace tree
	 * tab (tab "workspace" in the pane on the left) is right-clicked
	 */
	private ArrayList<JMenu> workspaceTreeMRUDPopupSubmenus = new ArrayList<JMenu>();
	/**
	 * Information about the order of elements that will be added to
	 * the pop-up menu that appears when a MRUD entry node in the workspace tree
	 * tab (tab "workspace" in the pane on the left) is right-clicked.
	 * If value is false next element from {@link #workspaceTreeMRUDPopupButtons}
	 * is taken, if true element from {@link #workspaceTreeMRUDPopupSubmenus} is used.
	 */
	private ArrayList<Boolean> workspaceTreeMRUDPopupIsSubmenu = new ArrayList<Boolean>();

	/**
	 * actions that will be used to create buttons that will be added to
	 * the pop-up menu that appears when a document node in the workspace tree
	 * panel (tab "workspace" in the pane on the left) is right-clicked
	 */
	private ArrayList<Action> workspaceTreeDocumentPopupButtons = new ArrayList<Action>();
	/**
	 * menus which will be copied (only actions in them) and that copies will
	 * be added the pop-up menu that appears when a document node in the workspace tree
	 * panel (tab "workspace" in the pane on the left) is right-clicked
	 */
	private ArrayList<JMenu> workspaceTreeDocumentPopupSubmenus = new ArrayList<JMenu>();
	/**
	 * Information about the order of elements that will be added to
	 * the pop-up menu that appears when a document node in the workspace tree
	 * panel (tab "workspace" in the pane on the left) is right-clicked.
	 * If value is false next element from {@link #workspaceTreeDocumentPopupButtons}
	 * is taken, if true element from {@link #workspaceTreeDocumentPopupSubmenus} is used.
	 */
	private ArrayList<Boolean> workspaceTreeDocumentPopupIsSubmenu = new ArrayList<Boolean>();

	/**
	 * actions that will be used to create buttons that will be added to
	 * the pop-up menu that appears when a different place of a workspace tree tab
	 * (tab "workspace" in the pane on the left)
	 * then a MRUD entry or a document node is right-clicked
	 */
	private ArrayList<Action> workspaceTreeOtherPopupButtons = new ArrayList<Action>();
	/**
	 * menus which will be copied (only actions in them) and that copies will
	 * be added the pop-up menu that appears when a different place of
	 * a workspace tree tab (tab "workspace" in the pane on the left)
	 * then a MRUD entry or a document node is right-clicked
	 */
	private ArrayList<JMenu> workspaceTreeOtherPopupSubmenus = new ArrayList<JMenu>();
	/**
	 * Information about the order of elements that will be added to
	 * the pop-up menu that appears when a different place of
	 * a workspace tree tab (tab "workspace" in the pane on the left)
	 * then a MRUD entry or a document node is right-clicked.
	 * If value is false next element from {@link #workspaceTreeOtherPopupButtons}
	 * is taken, if true element from {@link #workspaceTreeOtherPopupSubmenus} is used.
	 */
	private ArrayList<Boolean> workspaceTreeOtherPopupIsSubmenu = new ArrayList<Boolean>();

	/**
	 * actions that will be used to create buttons that will be added to
	 * the pop-up menu that appears when a signal document node in the tag tree
	 * tab (tab "Tags" in the pane on the left) is right-clicked
	 */
	private ArrayList<Action> tagTreeSignalDocumentPopupButtons = new ArrayList<Action>();
	/**
	 * menus which will be copied (only actions in them) and that copies will
	 * be added the pop-up menu that appears when a signal document node
	 * in the tag tree tab (tab "Tags" in the pane on the left) is right-clicked
	 */
	private ArrayList<JMenu> tagTreeSignalDocumentPopupSubmenus = new ArrayList<JMenu>();
	/**
	 * Information about the order of elements that will be added to
	 * the pop-up menu that appears when a signal document node
	 * in the tag tree tab (tab "Tags" in the pane on the left) is right-clicked.
	 * If value is false next element from {@link #tagTreeSignalDocumentPopupButtons}
	 * is taken, if true element from {@link #tagTreeSignalDocumentPopupSubmenus} is used.
	 */
	private ArrayList<Boolean> tagTreeSignalDocumentPopupIsSubmenu = new ArrayList<Boolean>();

	/**
	 * actions that will be used to create buttons that will be added to
	 * the pop-up menu that appears when a tag document node in the tag tree
	 * tab (tab "Tags" in the pane on the left) is right-clicked
	 */
	private ArrayList<Action> tagTreeTagDocumentPopupButtons = new ArrayList<Action>();
	/**
	 * menus which will be copied (only actions in them) and that copies will
	 * be added the pop-up menu that appears when a tag document node in the tag tree
	 * tab (tab "Tags" in the pane on the left) is right-clicked
	 */
	private ArrayList<JMenu> tagTreeTagDocumentPopupSubmenus = new ArrayList<JMenu>();
	/**
	 * Information about the order of elements that will be added to
	 * the pop-up menu that appears when a tag document node in the tag tree
	 * tab (tab "Tags" in the pane on the left) is right-clicked.
	 * If value is false next element from {@link #tagTreeTagDocumentPopupButtons}
	 * is taken, if true element from {@link #tagTreeTagDocumentPopupSubmenus} is used.
	 */
	private ArrayList<Boolean> tagTreeTagDocumentPopupIsSubmenu = new ArrayList<Boolean>();

	/**
	 * actions that will be used to create buttons that will be added to
	 * the pop-up menu that appears when a tag style node in the tag tree
	 * tab (tab "Tags" in the pane on the left) is right-clicked
	 */
	private ArrayList<Action> tagTreeTagStylePopupButtons = new ArrayList<Action>();
	/**
	 * menus which will be copied (only actions in them) and that copies will
	 * be added the pop-up menu that appears when a tag style node in the tag tree
	 * tab (tab "Tags" in the pane on the left) is right-clicked
	 */
	private ArrayList<JMenu> tagTreeTagStylePopupSubmenus = new ArrayList<JMenu>();
	/**
	 * Information about the order of elements that will be added to
	 * the pop-up menu that appears when a tag style node in the tag tree
	 * tab (tab "Tags" in the pane on the left) is right-clicked.
	 * If value is false next element from {@link #tagTreeTagStylePopupButtons}
	 * is taken, if true element from {@link #tagTreeTagStylePopupSubmenus} is used.
	 */
	private ArrayList<Boolean> tagTreeTagStylePopupIsSubmenu = new ArrayList<Boolean>();

	/**
	 * actions that will be used to create buttons that will be added to
	 * the pop-up menu that appears when a tag node in the tag tree
	 * tab (tab "Tags" in the pane on the left) is right-clicked
	 */
	private ArrayList<Action> tagTreeTagPopupButtons = new ArrayList<Action>();
	/**
	 * menus which will be copied (only actions in them) and that copies will
	 * be added the pop-up menu that appears when a tag node in the tag tree
	 * tab (tab "Tags" in the pane on the left) is right-clicked
	 */
	private ArrayList<JMenu> tagTreeTagPopupSubmenus = new ArrayList<JMenu>();
	/**
	 * Information about the order of elements that will be added to
	 * the pop-up menu that appears when a tag node in the tag tree
	 * tab (tab "Tags" in the pane on the left) is right-clicked.
	 * If value is false next element from {@link #tagTreeTagPopupButtons}
	 * is taken, if true element from {@link #tagTreeTagPopupSubmenus} is used.
	 */
	private ArrayList<Boolean> tagTreeTagPopupIsSubmenu = new ArrayList<Boolean>();

	/**
	 * actions that will be used to create buttons that will be added to
	 * the pop-up menu that appears when a document node in the signal tree
	 * tab (tab "Signals" in the pane on the left) is right-clicked
	 */
	private ArrayList<Action> signalTreeDocumentPopupButtons = new ArrayList<Action>();
	/**
	 * menus which will be copied (only actions in them) and that copies will
	 * be added the pop-up menu that appears when a document node in the signal tree
	 * tab (tab "Signals" in the pane on the left) is right-clicked
	 */
	private ArrayList<JMenu> signalTreeDocumentPopupSubmenus = new ArrayList<JMenu>();
	/**
	 * Information about the order of elements that will be added to
	 * the pop-up menu that appears when a document node in the signal tree
	 * tab (tab "Signals" in the pane on the left) is right-clicked.
	 * If value is false next element from {@link #signalTreeDocumentPopupButtons}
	 * is taken, if true element from {@link #signalTreeDocumentPopupSubmenus} is used.
	 */
	private ArrayList<Boolean> signalTreeDocumentPopupIsSubmenu = new ArrayList<Boolean>();

	/**
	 * actions that will be used to create buttons that will be added to
	 * the pop-up menu that appears when a signal page node in the signal tree
	 * tab (tab "Signals" in the pane on the left) is right-clicked
	 */
	private ArrayList<Action> signalTreeSignalPagePopupButtons = new ArrayList<Action>();
	/**
	 * menus which will be copied (only actions in them) and that copies will
	 * be added the pop-up menu that appears when a signal page node in the signal tree
	 * tab (tab "Signals" in the pane on the left) is right-clicked
	 */
	private ArrayList<JMenu> signalTreeSignalPagePopupSubmenus = new ArrayList<JMenu>();
	/**
	 * Information about the order of elements that will be added to
	 * the pop-up menu that appears when a signal page node in the signal tree
	 * tab (tab "Signals" in the pane on the left) is right-clicked.
	 * If value is false next element from {@link #signalTreeSignalPagePopupButtons}
	 * is taken, if true element from {@link #signalTreeSignalPagePopupSubmenus} is used.
	 */
	private ArrayList<Boolean> signalTreeSignalPagePopupIsSubmenu = new ArrayList<Boolean>();

	/**
	 * actions that will be used to create buttons that will be added to
	 * the pop-up menu that appears when a book document node in the book tree
	 * tab (tab "Books" in the pane on the left) is right-clicked
	 */
	private ArrayList<Action> bookTreeBookDocumentPopupButtons = new ArrayList<Action>();
	/**
	 * menus which will be copied (only actions in them) and that copies will
	 * be added the pop-up menu that appears when a book document node in the book tree
	 * tab (tab "Books" in the pane on the left) is right-clicked
	 */
	private ArrayList<JMenu> bookTreeBookDocumentPopupSubmenus = new ArrayList<JMenu>();
	/**
	 * Information about the order of elements that will be added to
	 * the pop-up menu that appears when a book document node in the book tree
	 * tab (tab "Books" in the pane on the left) is right-clicked.
	 * If value is false next element from {@link #bookTreeBookDocumentPopupButtons}
	 * is taken, if true element from {@link #bookTreeBookDocumentPopupSubmenus} is used.
	 */
	private ArrayList<Boolean> bookTreeBookDocumentPopupIsSubmenu = new ArrayList<Boolean>();

	/**
	 * the list of signal tools that will be copied and added to {@link SignalView}s.
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

	private GUIAccessImpl() { }

	private static final GUIAccessImpl _instance = new GUIAccessImpl();

	protected static GUIAccessImpl getInstance() {
		return _instance;
	}

	/* (non-Javadoc)
	 * @see org.signalml.plugin.export.PluginAccessGUI#addButtonToAnalysisMenu(javax.swing.AbstractAction)
	 */
	@Override
	public JMenuItem addButtonToAnalysisMenu(Action action) throws UnsupportedOperationException {
		if (!initializationPhase) throw new UnsupportedOperationException("operation can be performed only during initialization phase");
		JMenu analysisMenu = getViewerElementManager().getAnalysisMenu();
		int itemCount = analysisMenu.getItemCount();
		for (int i=0; i<itemCount; ++i) {
			JMenuItem item = analysisMenu.getItem(i);
			if (item.getText().equals(action.getValue(AbstractAction.NAME))) {
				item.setAction(action);
				return item;
			}
		}
		return analysisMenu.add(action);
	}

	/* (non-Javadoc)
	 * @see org.signalml.plugin.export.PluginAccessGUI#addSubmenuToAnalysisMenu(javax.swing.JMenu)
	 */
	@Override
	public JMenu addSubmenuToAnalysisMenu(String label) {
		if (!initializationPhase) throw new UnsupportedOperationException("operation can be performed only during initialization phase");
		JMenu analysisMenu = getViewerElementManager().getAnalysisMenu();
		int itemCount = analysisMenu.getItemCount();
		for (int i=0; i<itemCount; ++i) {
			JMenuItem submenu = analysisMenu.getItem(i);
			if (submenu instanceof JMenu && submenu.getText().equals(label)) {
				return (JMenu) submenu;
			}
		}
		JMenu submenu = new JMenu(label);
		analysisMenu.add(submenu);
		return submenu;
	}

	/**
	 * Adds a button to the given list and adds information that it
	 * was a button to given order list.
	 * @param action the button to be added
	 * @param actions the list to which the button will be added
	 * @param isSubmenu the list in which order is stored
	 * @throws UnsupportedOperationException if it is not an initialization phase
	 */
	private void delayedAddButton(Action action, ArrayList<Action> actions, ArrayList<Boolean> isSubmenu) throws UnsupportedOperationException {
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
	private void delayedAddSubMenu(JMenu menu, ArrayList<JMenu> submenus, ArrayList<Boolean> isSubmenu) throws UnsupportedOperationException {
		if (!initializationPhase) throw new UnsupportedOperationException("operation can be performed only during initialization phase");
		submenus.add(menu);
		isSubmenu.add(true);
	}

	private JMenu copyMenu(JMenu menu) {
		JMenu copy = new JMenu(menu.getText());
		Component[] menuComponents = menu.getMenuComponents();
		for (Component component : menuComponents) {
			if (component instanceof JMenuItem) {
				JMenuItem item = (JMenuItem) component;
				Action action = item.getAction();
				copy.add(action);
			}
		}
		return copy;
	}

	/**
	 * Adds buttons and submenus from given lists to the given menu.
	 * To determine the order of adding uses {@code isSubmenu} array.
	 * @param menu the menu to which elements are added
	 * @param actions the list with buttons
	 * @param submenus the list with submenus
	 * @param isSubmenu the list in which order is stored.
	 * If successive value is false next element from {@code actions}
	 * is taken, if true element from {@code submenus} is used.
	 */
	private void addToPopupMenu(JPopupMenu menu, ArrayList<Action> actions, ArrayList<JMenu> submenus, ArrayList<Boolean> isSubmenu) {
		try {
			int iSubmenu=0, iButton=0;
			if (isSubmenu.size() != (submenus.size() + actions.size())) {
				//throw new RuntimeException("lists for submenus and buttons are invalid");
			}
			for (Boolean isMenu : isSubmenu) {
				if (isMenu) {
					menu.add(copyMenu(submenus.get(iSubmenu++)));
				} else {
					menu.add(actions.get(iButton++));
				}
			}
		} catch (Exception e) {
			logger.error("Error in plug-in interface while adding elements to pop-up menu");
			logger.error("", e);
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
	public void addToSignalPlotPopupMenu(JPopupMenu menu) {
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
	public void addToHypnogramPlotPopupMenu(JPopupMenu menu) {
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
	public void addToColumnHeaderPopupMenu(JPopupMenu menu) {
		addToPopupMenu(menu, columnHeaderPopupButtons, columnHeaderPopupSubmenus, columnHeaderPopupIsSubmenu);
	}

	/* (non-Javadoc)
	 * @see org.signalml.plugin.export.PluginAccessGUI#addSignalTool(javax.swing.JToggleButton, org.signalml.plugin.export.SignalTool)
	 */
	@Override
	public void addSignalTool(SignalTool tool, ToolButtonParameters toolButtonParameters) throws UnsupportedOperationException {
		if (!initializationPhase) throw new UnsupportedOperationException("operation can be performed only during initialization phase");
		signalTools.add(tool);
		parametersForToolButtons.put(tool, toolButtonParameters);
		buttonsForTools.put(tool, new HashMap<SignalView, JToggleButton>());
	}

	/**
	 * Returns the button for a given {@link SignalTool tool} and a given
	 * {@link SignalView view}.
	 * <p>
	 * If the button doesn't exist it is created with {@link
	 * ToolButtonParameters#getIcon() icon} and {@link
	 * ToolButtonParameters#getToolTipText() tool-tip} obtained from the
	 * {@link ToolButtonParameters parameters} for this tool.
	 * The created button is added to the {@link #buttonsForTools appropriate
	 * collection}.
	 * @param tool the signal tool
	 * @param view the signal view
	 * @return the button
	 */
	private JToggleButton getButtonForView(SignalTool tool, SignalView view) {
		HashMap<SignalView, JToggleButton> buttonsForViews = buttonsForTools.get(tool);
		JToggleButton button = buttonsForViews.get(view);
		if (button == null) {
			ToolButtonParameters parameters = parametersForToolButtons.get(tool);
			button = new JToggleButton(parameters.getIcon());
			addRightButtonAction(button, parameters.getRightButtonClickAction());

			if (parameters.getToolTipText() != null)
				button.setToolTipText(parameters.getToolTipText());
			buttonsForViews.put(view, button);
		}
		return button;
	}

	/**
	 * Adds an action to a button which is executed when the button is pressed
	 * with the right click of the mouse.
	 * @param button the button for which the action will be added
	 * @param action the action to be executed
	 */
	private void addRightButtonAction(JToggleButton button, final AbstractSignalMLAction action) {
		if (action == null)
			return;

		button.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				if (e.getButton() == MouseEvent.BUTTON3) {
					action.actionPerformed(null);
				}
			}
		});
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
	public void registerSignalTools(Map<ButtonModel, SignalTool> toolMap, ButtonGroup toolButtonGroup, ActionListener toolSelectionListener, SignalView view) {
		try {
			for (SignalTool tool : signalTools) {
				try {
					SignalTool signalTool = tool.createCopy();
					signalTool.setSignalView(view);
					JToggleButton button = getButtonForView(tool, view);
					ToolButtonParameters parameters = parametersForToolButtons.get(tool);
					if (parameters.getListener() != null) {
						button.addMouseListener(parameters.getListener().createCopy(signalTool, button));
					}
					toolMap.put(button.getModel(), signalTool);
					toolButtonGroup.add(button);
					button.addActionListener(toolSelectionListener);
				} catch (Exception e) {
					logger.error("Failed to register signal tool " + (tool != null ? tool.toString() : ""));
					logger.error("", e);
				}
			}
		} catch (Exception e) {
			logger.error("Unknown error in plug-in interface during the registration of signal tools");
			logger.error("", e);
		}
	}

	/**
	 * Adds buttons associated with signal tools to the main toolbar.
	 * @param mainToolBar the main toolbar.
	 * @param view the view for which the tools are registered
	 */
	public void toolsToMainMenu(JToolBar mainToolBar, SignalView view) {
		try {
			for (SignalTool tool: signalTools) {
				JToggleButton button = getButtonForView(tool, view);
				mainToolBar.add(button);
			}
		} catch (Exception e) {
			logger.error("Unknown error in plug-in interface during the addition of buttons for signal tools");
			logger.error("", e);
		}
	}

	/* (non-Javadoc)
	 * @see org.signalml.plugin.export.PluginAccessGUI#addTreeTab(org.signalml.plugin.export.AbstractViewerTree)
	 */
	@Override
	public void addTreeTab(ViewerTreePane treePane, String title, Icon icon, String tip) {
		ViewerTabbedPane pane = getViewerElementManager().getTreeTabbedPane();
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
		ViewerTabbedPane pane = getViewerElementManager().getPropertyTabbedPane();
		pane.addTab(panel.getName(), (String) null, panel, panel.getToolTipText());
	}

	/** Sets {@link #initializationPhase} to false. */
	public void setInitializationPhaseEnd() {
		this.initializationPhase = false;
	}

	/* (non-Javadoc)
	 * @see org.signalml.plugin.export.view.PluginAccessGUI#getActiveSignalPlot()
	 */
	@Override
	public ExportedSignalPlot getActiveSignalPlot() throws NoActiveObjectException {
		if (! hasViewerElementManager()) return null;
		ActionFocusManager focusManager = getViewerElementManager().getActionFocusManager();
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
	public void addToWorkspaceTreeMRUDPopupMenu(JPopupMenu menu) {
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
	public void addToWorkspaceTreeDocumentPopupMenu(JPopupMenu menu) {
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
	public void addToWorkspaceTreeOtherPopupMenu(JPopupMenu menu) {
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
	public void addToTagTreeSignalDocumentPopupMenu(JPopupMenu menu) {
		addToPopupMenu(menu, tagTreeSignalDocumentPopupButtons, tagTreeSignalDocumentPopupSubmenus, tagTreeSignalDocumentPopupIsSubmenu);
	}

	/**
	 * Adds buttons and submenus to the popup menu in tag tree tab, which appears after clicking
	 * on a tag document node.
	 * @param menu the menu to which elements are added
	 */
	public void addToTagTreeTagDocumentPopupMenu(JPopupMenu menu) {
		addToPopupMenu(menu, tagTreeTagDocumentPopupButtons, tagTreeTagDocumentPopupSubmenus, tagTreeTagDocumentPopupIsSubmenu);
	}

	/**
	 * Adds buttons and submenus to the popup menu in tag tree tab, which appears after clicking
	 * on a tag style node.
	 * @param menu the menu to which elements are added
	 */
	public void addToTagTreeTagStylePopupMenu(JPopupMenu menu) {
		addToPopupMenu(menu, tagTreeTagStylePopupButtons, tagTreeTagStylePopupSubmenus, tagTreeTagStylePopupIsSubmenu);
	}

	/**
	 * Adds buttons and submenus to the popup menu in tag tree tab, which appears after clicking
	 * on a tag node.
	 * @param menu the menu to which elements are added
	 */
	public void addToTagTreeTagPopupMenu(JPopupMenu menu) {
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
	public void addToSignalTreeDocumentPopupMenu(JPopupMenu menu) {
		addToPopupMenu(menu, signalTreeDocumentPopupButtons, signalTreeDocumentPopupSubmenus, signalTreeDocumentPopupIsSubmenu);
	}

	/**
	 * Adds buttons and submenus to the popup menu in signal tree tab, which appears after clicking
	 * on a signal page node.
	 * @param menu the menu to which elements are added
	 */
	public void addToSignalTreeSignalPagePopupMenu(JPopupMenu menu) {
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
	public void addToBookTreeBookDocumentPopupMenu(JPopupMenu menu) {
		addToPopupMenu(menu, bookTreeBookDocumentPopupButtons, bookTreeBookDocumentPopupSubmenus, bookTreeBookDocumentPopupIsSubmenu);
	}

	@Override
	public void removePropertyTab(JPanel panel) throws IllegalArgumentException {
		ViewerTabbedPane pane = getViewerElementManager().getPropertyTabbedPane();
		if (pane.indexOfComponent(panel) == -1) throw new IllegalArgumentException("tab with this panel doesn't exist");
		pane.remove(panel);
	}

	@Override
	public void addMainTab(DocumentView tab, String title, Icon icon, String tip) {
		ViewerDocumentTabbedPane documentTabbedPane = getViewerElementManager().getDocumentTabbedPane();
		String tabTitle = (title == null ? tab.getName() : title);
		if (tip == null) {
			documentTabbedPane.addTab(tabTitle, icon, tab);
		} else {
			documentTabbedPane.addTab(tabTitle, icon, tab, tip);
		}
	}

	@Override
	public void removeMainTab(DocumentView tab) throws IllegalArgumentException {
		ViewerDocumentTabbedPane documentTabbedPane = getViewerElementManager().getDocumentTabbedPane();
		if (documentTabbedPane.indexOfComponent(tab) != -1) {
			documentTabbedPane.remove(tab);
		} else
			throw new IllegalArgumentException("tab with this view doesn't exist");
	}

	@Override
	public DocumentView getSelectedMainTab() throws NoActiveObjectException {
		ViewerDocumentTabbedPane documentTabbedPane = getViewerElementManager().getDocumentTabbedPane();
		Component component = documentTabbedPane.getSelectedComponent();
		if (component instanceof DocumentView)
			return (DocumentView) component;
		else
			throw new NoActiveObjectException("no active main tab");
	}

	@Override
	public void removeTreeTab(ViewerTreePane tab) throws IllegalArgumentException {
		ViewerTabbedPane viewerTreePane = getViewerElementManager().getTreeTabbedPane();
		if (viewerTreePane.indexOfComponent(tab) != -1) {
			viewerTreePane.remove(tab);
		} else
			throw new IllegalArgumentException("tab with this panel doesn't exist");
	}

	@Override
	public ViewerTreePane getSelectedTreeTab() throws NoActiveObjectException {
		ViewerTabbedPane viewerTabbedPane = getViewerElementManager().getTreeTabbedPane();
		Component component = viewerTabbedPane.getSelectedComponent();
		if (component instanceof ViewerTreePane)
			return (ViewerTreePane) component;
		else
			throw new NoActiveObjectException("no active tree tab");
	}

	@Override
	public JPanel getSelectedPropertyTab() throws NoActiveObjectException {
		ViewerTabbedPane viewerTabbedPane = getViewerElementManager().getPropertyTabbedPane();
		Component component = viewerTabbedPane.getSelectedComponent();
		if (component instanceof JPanel)
			return (JPanel) component;
		else
			throw new NoActiveObjectException("no active property tab");
	}

	@Override
	public java.awt.Window getDialogParent() {
		return getViewerElementManager().getDialogParent();
	}

	@Override
	public FileChooser getFileChooser() {
		return getViewerElementManager().getFileChooser();
	}

}
